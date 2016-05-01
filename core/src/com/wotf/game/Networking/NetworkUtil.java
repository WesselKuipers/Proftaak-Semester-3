/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.Networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.wotf.game.GameStage;
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
    
    private String hostIP;
    private final int _PORT = 9021;
    private GameStage scene;
    
    /**
     * Object holds data to connect to the host.
     * @param hostIP IP address of the host of the game.
     */
    public NetworkUtil( String hostIP, GameStage gameStage ){
        this.hostIP = hostIP;
        this.scene = gameStage;
        
        initNetworkListener( this.hostIP );
    }
    
    /**
     * Add network listener to this client.
     * @param hostIP Ip Address of the host.
     */
    private void initNetworkListener( String hostIP ){
        // Listen for incoming socket connections
        new Thread(new Runnable(){
            @Override
            public void run() {
                ServerSocketHints serverSocketHint = new ServerSocketHints();
                // 0 means no timeout.  Probably not the greatest idea in production!
                serverSocketHint.acceptTimeout = 0;
                
                // Create the socket server using TCP protocol and listening on 9021
                // Only one app can listen to a port at a time, keep in mind many ports are reserved
                // especially in the lower numbers ( like 21, 80, etc )
                ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, _PORT, serverSocketHint);
                
                // Loop forever
                while(true){
                    // Create a socket
                    Socket socket = serverSocket.accept(null);
                    
                    // Read data from the socket into a BufferedReader
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
                    
                    try {
                        // Read to the next newline (\n) and display that text on labelMessage
                        System.out.println( buffer.readLine() );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); // And, start the thread running
    }
    
    /**
     * Send message to host.
     * @param message Message to send.
     */
    public void sendToHost( NetworkMessage nMsg ){
        //TODO: send nMsg.toString() to host and make host send it to all clients.

        // debug 
        System.out.println("Command: " + nMsg.getCommand().toString() + " was send to host.");
        receiveMessage( nMsg.toString() );
    } 
    
    /**
     * Send message to host.
     * @param message Message to send.
     */
    public void sendToClient( NetworkMessage nMsg, String clientIP ){
        //TODO: send nMsg.toString() to specified client.        
        
    } 
    
    /**
     * Receive message on client and put the command trough logic for each message.
     * NOTICE! the order does matter!
     * For example if you send unit movement before the impact of the bullet on the client the unit might not get hit!
     * @param message Message to send.
     */
    public void receiveMessage( String message ){
        //debug
        System.out.println("Received message: " + message);
        
        //check if host get this message if so make host send it to all clients.
        
        //split message up into networkMessages by splitting by end of line (;).        
        String[] splitMessages = message.split(";");
        
        for( String msg : splitMessages ){
            NetworkMessage nMsg = new NetworkMessage( msg );
            
            //debug
            System.out.println("Command: " + nMsg.getCommand().toString() + " was received and triggered.");
            
            commandLogic(nMsg);
        }
    }
    
    /**
     * handle the logic behind the messages.
     */
    public void commandLogic( NetworkMessage nMsg ){
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
            default: 
                System.out.println("Command was not processed");
                break;
        }
    }
    
    /**
     * function to get the networking interfaces of the client that calls this function.
     */
    public static void clientIPInfo(){
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
    public void fire( NetworkMessage nMsg ){
        try{
            String mousePosXStr = nMsg.getParameter("mousePosX");
            String mousePosYStr = nMsg.getParameter("mousePosY");
            
            int mousePosX = Integer.parseInt( mousePosXStr );
            int mousePosY = Integer.parseInt( mousePosYStr );
            
            scene.fire( mousePosX, mousePosY );
        }
        catch( InvalidParameterException ipe ){
            //TODO: what do we do when message went wrong ? ask host aggain ?
        }
    }
    
}
