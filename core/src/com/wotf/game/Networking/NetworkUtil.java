/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.Networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Jip Boesenkool, 25-04-'16
 * Utility class to make in game connection.
 */
public class NetworkUtil {
    
    private Player host;
    private final int _PORT = 9021;
    private GameStage scene;
    private Socket socket;
    private List<Vector2> spawnLocations = new ArrayList<>();
    
    /**
     * Object holds data to connect to the host.
     * @param hostIP IP address of the host of the game.
     */
    public NetworkUtil( Player host, GameStage gameStage ){
        this.host = host;
        this.scene = gameStage;
        
        initNetworkListener( this.host );
    }
    
    /**
     * Add network listener to this client.
     * @param hostIP Ip Address of the host.
     */
    private void initNetworkListener( Player host ) {
        if (scene.getGame().getPlayingPlayer().equals(host)) {
            ServerSocketHints serverSocketHint = new ServerSocketHints();

            // This prevents the host from dropping out, when in production set it to an appropiate value
            serverSocketHint.acceptTimeout = 0;

            // Create the socket server using TCP protocol and listening on 9021
            ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, _PORT, serverSocketHint);

            // Accept any incoming connections
            this.socket = serverSocket.accept(null);
            
            messageListener(true);
        } else {
            SocketHints socketHints = new SocketHints();
            this.socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host.getIp(), _PORT, socketHints);
            messageListener(false);
        }
    }
    
    /**
     * Used to check if host is receiving messages from any client
     */
    private void messageListener(Boolean isHost) {
        new Thread(new Runnable() {
            private String message;
            @Override
            public void run () {
                // Keep checking if client receives messages
                while(true) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        // Check if message is valid, and if it's and actual message received from the host
                        while ((message = bufferedReader.readLine()) != null) {    
                            receiveMessage(message);
                            
                            // If this is the host send the incoming message to other clients
                            if (isHost) {
                                for (Player player : scene.getGame().getPlayers()) {
                                    // Dont send the message to host else it will do the action twice!
                                    if (!player.equals(host)) {
                                        sendToClient(message, player.getIp());
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println(ex);
                        break;
                    }
                }
            } 
        }).start();
    }
    
    /**
     * Send message to host.
     * @param nMsg
     */
    public void sendToHost( NetworkMessage nMsg ) {
        try {
            socket.getOutputStream().write((nMsg.toString() + "\n").getBytes());
            
            // If its the host that is sending the message, you have to run the action for the host too
            if (scene.getGame().getPlayingPlayer().equals(host)) {
                receiveMessage(nMsg.toString());
            }
            
            // debug 
            System.out.println("Command: " + nMsg.getCommand().toString() + " was send to host.");
        } catch (IOException ex) {
            System.out.println("An error occured");
        }
    } 
    
    /**
     * Send message to client.
     * @param message
     * @param clientIP
     */
    public void sendToClient( String message, String clientIP ) {       
        try {
            socket.getOutputStream().write((message + "\n").getBytes());
        } catch (IOException e) {
            System.out.println("An error occured");
        }
    } 
    
    /**
     * Receive message on client and put the command trough logic for each message.
     * NOTICE! the order does matter!
     * For example if you send unit movement before the impact of the bullet on the client the unit might not get hit!
     * @param message Message to send.
     */
    public void receiveMessage( String message ) {
        //debug
        System.out.println("Received message: " + message);
        
        //check if host get this message if so make host send it to all clients.
        
        //split message up into networkMessages by splitting by end of line (;).        
        String[] splitMessages = message.split(";");
        
        for( String msg : splitMessages ) {
            NetworkMessage nMsg = new NetworkMessage( msg );
            
            //debug
            System.out.println("Command: " + nMsg.getCommand().toString() + " was received and triggered.");
            
            commandLogic(nMsg);
        }
    }
    
    /**
     * handle the logic behind the messages.
     */
    public void commandLogic( NetworkMessage nMsg ) {
        // TODO: handle message logic
        // the action can be checked based on the enum Command which can be retreived by nMsg.getCommand()
        // to retreive a parameter do nMsg.getParameter( parameterName).
        // will return null if specified parameter could not be found (message was created incorrect)
        
        switch ( nMsg.getCommand() ) {
            case FIRE:
                fire( nMsg );
                break;
            case MOVE:
                break;
            case BEGINTURN:
                beginTurn( nMsg );
            case ENDTURN:
                break;
            case INITGAME:
                initGame( nMsg );
            case SPAWNLOCATION:
                addSpawnLocation ( nMsg );
                break;
            default: 
                System.out.println("Command was not processed");
                break;
        }
    }
    
    /**
     * function to get the networking interfaces of the client that calls this function.
     */
    public static void clientIPInfo() {
        // The following code loops through the available network interfaces
        // Keep in mind, there can be multiple interfaces per device, for example
        // one per NIC, one per active wireless and the loopback
        // In this case we only care about IPv4 address ( x.x.x.x format )
        List<String> addresses = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface ni : list(interfaces)){
                for(InetAddress address : list(ni.getInetAddresses()))
                {
                    if(address instanceof Inet4Address){
                        addresses.add(address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        // Print the contents of our array to a string.  Yeah, should have used StringBuilder
        String ipAddress = new String("");
        for(String str:addresses)
        {
            ipAddress = ipAddress + str + "\n";
            System.out.println(ipAddress);
        }
    }
    
    /**
     * Fire function which retreives the mousePos parameter from the network message and call in scene fire function.
     * @param nMsg Network message which holds the data for the fire method.
     */
    public void fire( NetworkMessage nMsg ) {
        try {
            String mousePosXStr = nMsg.getParameter("mousePosX");
            String mousePosYStr = nMsg.getParameter("mousePosY");
            
            float mousePosX = Float.parseFloat(mousePosXStr );
            float mousePosY = Float.parseFloat( mousePosYStr );
            
            scene.fire( mousePosX, mousePosY );
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }
    
    public void initGame( NetworkMessage nMsg ) {
        try {
            scene.spawnUnits(spawnLocations);
            scene.getGame().beginTurn();
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }
    
    public void beginTurn( NetworkMessage nMsg ) {
        try {
            String windXStr = nMsg.getParameter("windX");
            String windYStr = nMsg.getParameter("windY");
            
            float windX = Float.parseFloat( windXStr );
            float windY = Float.parseFloat( windYStr );
            
            Vector2 windForce = new Vector2( windX, windY );
            if (!scene.getGame().getPlayingPlayer().equals(host)) {
                scene.getGame().getMap().setWind(windForce);
            }
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }

    public void addSpawnLocation( NetworkMessage nMsg ) {
        try {
            String locXStr = nMsg.getParameter("locX");
            String locYStr = nMsg.getParameter("locY");
            
            float locX = Float.parseFloat( locXStr );
            float locY = Float.parseFloat( locYStr );
            
            Vector2 spawnLocation = new Vector2( locX, locY );
            
            spawnLocations.add(spawnLocation);
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }
    
    public void move( NetworkMessage nMsg ) {
        try {
            String direction = nMsg.getParameter("direction");
            
            //scene.getGame().getActiveTeam().getActiveUnit()
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
        
    }
    
}
