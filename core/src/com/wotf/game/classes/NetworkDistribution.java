/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rensphilipsen
 */
public class NetworkDistribution {
    private Game game;
    private Socket socket;
    
    public NetworkDistribution(Game game) {
        this.game = game;
        
        initializeSocket();
    }
    
    private void initializeSocket() {
        // If its the host create the socket else try to connect as client to the socket
        if (game.getPlayingPlayer().equals(game.getHost())) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            Future<Socket> future = es.submit(new Callable<Socket>() {
                @Override
                public Socket call() throws Exception {
                    ServerSocketHints serverSocketHint = new ServerSocketHints();
                    
                    // This prevents the host from dropping out, when in production set it to an appropiate value
                    serverSocketHint.acceptTimeout = 0;

                    // Create the socket server using TCP protocol and listening on 9021
                    ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, 9021, serverSocketHint);

                    Socket socket = serverSocket.accept(null);

                    return socket;
                }
            });
                    
            try {
                this.socket = future.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(NetworkDistribution.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(NetworkDistribution.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            SocketHints socketHints = new SocketHints();
            this.socket = Gdx.net.newClientSocket(Net.Protocol.TCP, game.getHost().getIp(), 9021, socketHints);
        }
                    
        while (true) {
            try {
                System.out.println(socket.getInputStream().read());
            } catch (IOException ex) {
                Logger.getLogger(NetworkDistribution.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void sendCommand (String command) {
        try {
            socket.getOutputStream().write(command.getBytes());
        } catch (IOException e) {
            System.out.println("An error occured");
        }
    }
    
    public void receiveCommand () {
        try {
            String message = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            System.out.println("Message received:" + message);
        } catch (IOException e) {
            System.out.println("An error occured");
        }
    }
    
    public void getUserIpAddresses() {
        List<String> addresses = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface ni : Collections.list(interfaces)){
                for(InetAddress address : Collections.list(ni.getInetAddresses()))
                {
                    if(address instanceof Inet4Address){
                        addresses.add(address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        
        for(String addr:addresses)
        {
            System.out.println(addr);
        }
    }
}
