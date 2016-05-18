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
import com.wotf.game.classes.Team;
import com.wotf.game.classes.Unit;
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
    
    private final Player host;
    private final int _PORT = 9021;
    private final GameStage scene;
    private Socket socket;
    private final List<Vector2> unitPositions = new ArrayList<>();
    private boolean[][] terrain;
    
    /**
     * Object holds data to connect to the host.
     * @param host the host of the game.
     * @param gameStage stage to interact with the actors and all classes being used by the game
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
        if (scene.getGame().getPlayingPlayer().getID() == host.getID() ) {
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
        new Thread(() -> {
            String message;
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
                                if (player.getID() != host.getID()) {
                                    sendToClient(message, player.getIp());
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    Gdx.app.log("networkingUtil", "Error occured while reading message", ex);
                    break;
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
            socket.getOutputStream().write((nMsg.toString() + System.lineSeparator()).getBytes());
            
            // If its the host that is sending the message, you have to run the action for the host too
            if (scene.getGame().getPlayingPlayer().getID() == host.getID()) {
                receiveMessage(nMsg.toString());
            }
            
            // debug 
            System.out.println("Command: " + nMsg.getCommand().toString() + " was send to host.");
        } catch (IOException ex) {
            Gdx.app.log("networkingUtil", "Error occured while sending message to host", ex);
        }
    } 
    
    /**
     * Send message to client.
     * @param message
     * @param clientIP
     */
    public void sendToClient( String message, String clientIP ) {       
        try {
            socket.getOutputStream().write((message + System.lineSeparator()).getBytes());
        } catch (IOException ex) {
            Gdx.app.log("networkingUtil", "Error occured while sending message to client", ex);
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
        //System.out.println("Received message: " + message);
        
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
     * @param nMsg the network message being sent to the appropriate method
     */
    public void commandLogic( NetworkMessage nMsg ) {
        // the action can be checked based on the enum Command which can be retreived by nMsg.getCommand()
        // to retreive a parameter do nMsg.getParameter( parameterName).
        // will return null if specified parameter could not be found (message was created incorrect)
        
        switch ( nMsg.getCommand() ) {
            case FIRE:
                fire( nMsg );
                break;
            case MOVE:
                move ( nMsg );
                break;
            case BEGINTURN:
                beginTurn( nMsg );
            case ENDTURN:
                //endTurn ( nMsg );
                break;
            case INITGAME:
                initGame( nMsg );
                break;
            case SYNCUNITS:
                syncUnits( nMsg );
                break;
            case TERRAIN:
                addTerrainX ( nMsg );
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
        List<String> addresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : list(interfaces)){
                for (InetAddress address : list(ni.getInetAddresses()))
                {
                    if (address instanceof Inet4Address){
                        addresses.add(address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        // Print the contents of our array to a string.  Yeah, should have used StringBuilder
        String ipAddress = "";
        for(String str:addresses)
        {
            ipAddress = ipAddress + str + "\n";
            System.out.println(ipAddress);
        }
    }
    
    /**
     * Fire function which retrieves the mousePos parameter from the network message and call in scene fire function.
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
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }
    
    public void initGame( NetworkMessage nMsg ) {
        try {
            scene.spawnUnits(unitPositions);
            scene.getGame().beginTurn();
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }
    
    public void beginTurn( NetworkMessage nMsg ) {
        try {
            // host has already ran this action when sending this message, so we want to apply it only on the connected clients 
            if (scene.getGame().getPlayingPlayer().getID() != host.getID()) {
                // set the wind force
                String windXStr = nMsg.getParameter("windX");
                String windYStr = nMsg.getParameter("windY");

                float windX = Float.parseFloat( windXStr );
                float windY = Float.parseFloat( windYStr );

                Vector2 windForce = new Vector2( windX, windY );
                
                scene.getGame().beginTurnReceive(windForce);
            }
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }
    
    public void addTerrainX( NetworkMessage nMsg ) {
        try {
            boolean[][] terrain = scene.getGame().getMap().getTerrain();
            int yVal = 0;
            boolean val = false;
            
            String xStr = nMsg.getParameter("x");
            for (int y = 0; y < terrain[0].length; y++) {
                String yStr = nMsg.getParameter("y"+y);
                String valStr = nMsg.getParameter("val"+y);
                
                yVal = Integer.parseInt( yStr );
                val = Boolean.parseBoolean(valStr);
            }
            
            int x = Integer.parseInt( xStr );
            
            terrain[x][yVal] = val;
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }
    
    public void syncUnits( NetworkMessage nMsg ) {
        try {   
            List<Team> teams = scene.getGame().getTeams();
            int unitCount = 0;
            int unitAmount = scene.getGame().getTeam(0).getUnits().size() * scene.getGame().getTeams().size();
            
            for (Team team : teams) {
                for (Unit unit : team.getUnits()) {
                    String unitXStr = nMsg.getParameter("u" + unitCount + "x");
                    String unitYStr = nMsg.getParameter("u" + unitCount + "y");

                    float unitX = Float.parseFloat( unitXStr );
                    float unitY = Float.parseFloat( unitYStr );

                    Vector2 unitPosition = new Vector2( unitX, unitY );

                    // When no positions are yet added, add them to the array, else we are going to sync the current positions and the unit health
                    if (unitPositions == null || unitPositions.size() != unitAmount) {
                        unitPositions.add(unitPosition);
                    } else {
                        unit.setPosition(unitPosition);

                        // Sync the unit health
                        String unitHealthStr = nMsg.getParameter("u" + unitCount + "hp");
                        int unitHealth = Integer.parseInt(unitHealthStr);
                        int damageUnit = unit.getHealth() - unitHealth;
                        unit.decreaseHealth(damageUnit);
                    }
                    unitCount++;
                }
            }
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }
    
    public void move( NetworkMessage nMsg ) {
        try {
            String direction = nMsg.getParameter("direction");
            
            //scene.getGame().getActiveTeam().getActiveUnit().jump()
        }
        catch( InvalidParameterException ipe ) {
            //TODO: what do we do when message went wrong ? ask host aggain ?
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
        
    }

    private void endTurn(NetworkMessage nMsg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
