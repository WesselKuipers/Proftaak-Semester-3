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
 * @author Jip Boesenkool, 25-04-'16 Utility class to make in game connection.
 */
public class NetworkUtil {

    private final Player host;
    private final int port = 9021;
    private final GameStage scene;
    private Socket socket;
    private final List<Vector2> unitPositions;

    /**
     * Object holds data to connect to the host.
     * @param host the host of the game.
     * @param gameStage stage to interact with the actors and all classes being
     * used by the game
     */
    public NetworkUtil(Player host, GameStage gameStage) {
        this.host = host;
        this.scene = gameStage;
        this.unitPositions = new ArrayList<>();

        initNetworkListener(this.host);
    }

    /**
     * Network listener which will setup the socket connection. If it's the host
     * that is entering this method, create a socket connection. Host will wait
     * until it receives an client request for connection If it's the client
     * that is entering this method, try to connect to the host's socket
     * connection
     * @param hostIP Ip Address of the host.
     */
    private void initNetworkListener(Player host) {
        if (!scene.getGame().getGameSettings().getIsLocal()) {
            if (scene.getGame().getPlayingPlayer().getId() == host.getId()) {
                initHostNetworkListener();
            } else {
                initClientNetworkListener();
            }
        } else {
            initHostNetworkListener();
            initClientNetworkListener();
        }

        // Run another thread to check if there was a shutdown client side. if so dispose the socket, to prevent connection conflicts.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                socket.dispose();
                Gdx.app.log("networkingUtil", "Server was shut down");
            }
        });

    }

    private void initHostNetworkListener() {
        ServerSocketHints serverSocketHint = new ServerSocketHints();

        // This prevents the host from dropping out, when in production set it to an appropiate value
        serverSocketHint.acceptTimeout = 0;

        // Create the socket server using TCP protocol and listening on 9021
        ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, serverSocketHint);

        // Accept any incoming connections
        this.socket = serverSocket.accept(null);

        messageListener(true);
    }

    private void initClientNetworkListener() {
        SocketHints socketHints = new SocketHints();
        this.socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host.getIp(), port, socketHints);
        if(!scene.getGame().getGameSettings().getIsLocal())
            messageListener(false);
    }

    /**
     * messageListener used for retrieving messages sent, either by host or any
     * client, after its received, we are going to run the receiveMessage
     * method.
     * @param isHost this boolean is used to either start a message listener for
     * host or client. If it's the host, we want to send the message to all
     * clients by using the sendToClient method for all connected clients.
     */
    private void messageListener(boolean isHost) {
        new Thread(() -> {
            String message;
            // Keep checking if client receives messages
            while (true) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    // Check if message is valid, and if it's and actual message received from the host
                    while ((message = bufferedReader.readLine()) != null) {
                        receiveMessage(message);

                        // If this is the host send the incoming message to other clients
                        if (isHost) {
                            for (Player player : scene.getGame().getPlayers()) {
                                // Dont send the message to host else it will do the action twice!
                                if (player.getId() != host.getId()) {
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
     * Send message to host and receive the message for the host itself too,
     * else it will only be executed on the client sides.
     * @param nMsg
     */
    public void sendToHost(NetworkMessage nMsg) {
        try {
            socket.getOutputStream().write((nMsg.toString() + System.lineSeparator()).getBytes());

            // If its the host that is sending the message, you have to run the action for the host too
            if (scene.getGame().getPlayingPlayer().getId() == host.getId()) {
                receiveMessage(nMsg.toString());
            }

            // debug 
            Gdx.app.log("networkingUtil", "Command: " + nMsg.getCommand().toString() + " was send to host.");
        } catch (IOException ex) {
            Gdx.app.log("networkingUtil", "Error occured while sending message to host", ex);
        }
    }

    /**
     * Send message to client by using the output stream write method.
     * @param message
     * @param clientIP
     */
    public void sendToClient(String message, String clientIP) {
        try {
            socket.getOutputStream().write((message + System.lineSeparator()).getBytes());
        } catch (IOException ex) {
            Gdx.app.log("networkingUtil", "Error occured while sending message to client", ex);
        }
    }

    /**
     * Receive message on client and put the command trough logic for each
     * message. NOTICE! the order does matter! For example if you send unit
     * movement before the impact of the bullet on the client the unit might not
     * get hit!
     *
     * @param message Message to send.
     */
    public void receiveMessage(String message) {
        //split message up into networkMessages by splitting by end of line (;).        
        String[] splitMessages = message.split(";");

        for (String msg : splitMessages) {
            NetworkMessage nMsg = new NetworkMessage(msg);

            //debug
            Gdx.app.log("networkingUtil", "Command: " + nMsg.getCommand().toString() + " was received and triggered.");

            commandLogic(nMsg);
        }
    }

    /**
     * handle the logic behind the messages.
     * @param nMsg the network message being sent to the appropriate method
     */
    public void commandLogic(NetworkMessage nMsg) {
        // the action can be checked based on the enum Command which can be retreived by nMsg.getCommand()
        // to retreive a parameter do nMsg.getParameter( parameterName).
        // will return null if specified parameter could not be found (message was created incorrect)

        switch (nMsg.getCommand()) {
            case FIRE:
                fire(nMsg);
                break;
            case MOVE:
                move(nMsg);
                break;
            case BEGINTURN:
                beginTurn(nMsg);
                break;
            case ENDTURN:
                endTurn(nMsg);
                break;
            case INITGAME:
                initGame(nMsg);
                break;
            case SYNCUNITS:
                syncUnits(nMsg);
                break;
            case SELECTWEAPON:
                selectWeapon(nMsg);
                break;
            case SWITCHUNIT:
                switchUnit(nMsg);
                break;
            case SYNCCOLLISION:
                syncCollision(nMsg);
                break;
            default:
                Gdx.app.log("networkingUtil", "Command was not processed");
                break;
        }
    }

    /**
     * function to get the networking interfaces of the client that calls this
     * function.
     */
    public static void clientIPInfo() {
        // The following code loops through the available network interfaces
        // Keep in mind, there can be multiple interfaces per device, for example
        // one per NIC, one per active wireless and the loopback
        // In this case we only care about IPv4 address ( x.x.x.x format )
        List<String> addresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : list(interfaces)) {
                for (InetAddress address : list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        addresses.add(address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Gdx.app.log("networkingUtil", e.getMessage());
        }

        // Print the contents of our array to a string.  Yeah, should have used StringBuilder
        String ipAddress = "";
        for (String str : addresses) {
            ipAddress = ipAddress + str + "\n";
            System.out.println(ipAddress);
        }
    }

    /**
     * Fire function which retrieves the mousePos parameter from the network
     * message and call in scene fire function.
     * @param nMsg Network message which holds the data for the fire method.
     */
    private void fire(NetworkMessage nMsg) {
        try {
            String mousePosXStr = nMsg.getParameter("mousePosX");
            String mousePosYStr = nMsg.getParameter("mousePosY");

            float mousePosX = Float.parseFloat(mousePosXStr);
            float mousePosY = Float.parseFloat(mousePosYStr);

            scene.fire(mousePosX, mousePosY);
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function which is received by all connected clients to initialize the
     * game the first time. Units are being spawned based on the random
     * locations sent by the host First turn is started
     * @param nMsg Network message which holds the data for the initGame method.
     */
    private void initGame(NetworkMessage nMsg) {
        try {
            scene.spawnUnits(unitPositions);
            scene.getGame().beginTurn();
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function which is retrieved by all clients to beginTurn on the game. We
     * want to send the wind parameters to all connected clients, EXCEPT the
     * host because he has generated the wind and is already on the correct wind
     * vector.
     * @param nMsg Network message which holds the data for the beginTurn
     * method.
     */
    private void beginTurn(NetworkMessage nMsg) {
        try {
            // host has already ran this action when sending this message, so we want to apply it only on the connected clients 
            if (scene.getGame().getPlayingPlayer().getId() != host.getId()) {
                // set the wind force
                String windXStr = nMsg.getParameter("windX");
                String windYStr = nMsg.getParameter("windY");

                float windX = Float.parseFloat(windXStr);
                float windY = Float.parseFloat(windYStr);

                Vector2 windForce = new Vector2(windX, windY);

                scene.getGame().beginTurnReceive(windForce);
            }
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * On first time of call on this function, it should retrieve the randomly
     * generated spawn locations and put it in the unitPositions array. After
     * the first call all unit positions will be sent after each turn, to sync
     * the unit positions. The health of the units is also being synchronized by
     * this function.
     * @param nMsg Network message which holds the data for the syncUnits
     * method.
     */
    private void syncUnits(NetworkMessage nMsg) {
        try {
            List<Team> teams = scene.getGame().getTeams();
            int unitCount = 0;
            int unitAmount = scene.getGame().getTeam(0).getUnits().size() * scene.getGame().getTeams().size();

            for (Team team : teams) {
                for (Unit unit : team.getUnits()) {
                    String unitXStr = nMsg.getParameter("u" + unitCount + "x");
                    String unitYStr = nMsg.getParameter("u" + unitCount + "y");

                    float unitX = Float.parseFloat(unitXStr);
                    float unitY = Float.parseFloat(unitYStr);

                    Vector2 unitPosition = new Vector2(unitX, unitY);

                    // When no positions are yet added, add them to the array, else we are going to sync the current positions and the unit health
                    if (unitPositions.size() != unitAmount) {
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
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function which will retrieve the direction and trigger a move by the
     * active unit.
     * @param nMsg Network message which holds the data for the move method.
     */
    private void move(NetworkMessage nMsg) {
        try {
            // Retrieve direction
            String direction = nMsg.getParameter("direction");

            // Either move left or right depending on the parameter direction, above.
            if ("right".equals(direction)) {
                scene.getGame().getActiveTeam().getActiveUnit().jump(true);
            } else {
                scene.getGame().getActiveTeam().getActiveUnit().jump(false);
            }
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function to receive a switch between active unit
     * @param nMsg Network message which holds the data for the switchUnit
     * method.
     */
    private void switchUnit(NetworkMessage nMsg) {
        try {
            scene.getGame().getActiveTeam().setNextActiveUnit();
            scene.getGame().setActiveUnit(scene.getGame().getActiveTeam());
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * method used by networking that makes sure the active unit selects the
     * right weapon
     * @param nMsg string from the network device that contains the needed
     * information.
     */
    private void selectWeapon(NetworkMessage nMsg) {
        try {
            String weaponIndexStr = nMsg.getParameter("weaponIndex");

            int weaponIndex = Integer.parseInt(weaponIndexStr);

            scene.getGame().getActiveTeam().getActiveUnit().selectWeaponIndex(weaponIndex);
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function to synchronize the collision that was shot by the active player.
     * @param nMsg Network message which holds the data for the syncCollision
     * method.
     */
    private void syncCollision(NetworkMessage nMsg) {
        try {
            String posXStr = nMsg.getParameter("posX");
            String posYStr = nMsg.getParameter("posY");

            int posX = Integer.parseInt(posXStr);
            int posY = Integer.parseInt(posYStr);

            scene.getGame().getActiveTeam().getActiveUnit().getWeapon().getBullet().terrainCollisionReceive(posX, posY);
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }

    /**
     * Function to end a turn. When host has ended a turn receive it for all
     * clients.
     * @param nMsg Network message which holds the data for the endTurn method.
     */
    private void endTurn(NetworkMessage nMsg) {
        try {
            scene.getGame().endTurnReceive();
        } catch (InvalidParameterException ipe) {
            Gdx.app.log("networkingUtil", "An error occured while processing command", ipe);
        }
    }
}
