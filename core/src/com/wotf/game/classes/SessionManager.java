/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.wotf.gui.view.ISessionSettings;
import com.wotf.gui.view.SessionOnlinePlayer;
import fontyspublisher.IRemotePropertyListener;
import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * To connect a client to a session there must be a SessionManager instance.
 * This class makes it possible to connect to a specific registry/session based on the hostIP.
 * 
 * @author Remco
 */
public class SessionManager extends UnicastRemoteObject implements IRemotePropertyListener {

    private Registry registry;
    private Session session;
    private ISessionSettings regSettings;
    private GameSettings gamesettings;
    private SessionOnlinePlayer gui;

    /**
     * Private empty constructor for initializing UnicastRemoteObject.
     *
     * @throws RemoteException
     */
    private SessionManager() throws RemoteException {
    }

    /**
     * Initialize attributes, an empty constructor for the unicastremoteobject,
     * connect to the registry of the given session (host) 
     * 
     * @param session where the client will connect to
     * @param gui which will be shown for the client
     * @throws RemoteException 
     */
    public SessionManager(Session session, SessionOnlinePlayer gui) throws RemoteException {
        this();
        this.session = session;
        this.gui = gui;
        getSessionRegistry();
    }

    /**
     * Connect to the given registry of the session(host)
     * Also subscribe to the remote properties like :
     * Cancelgame
     * Startgame
     * Session gamesettings property. Will be called for the client if any gamesetting changes.
     * Also set the gamesettings for the client for one time. We need to know the current settings of the host.
     * 
     */
    public void getSessionRegistry() {
        // REGISTER AREA
        try {
            registry = LocateRegistry.getRegistry(session.getHost().getIp(), 5555);
            regSettings = (ISessionSettings) registry.lookup("SessionSettings");
            gamesettings = (GameSettings) regSettings.getGameSettings();
            regSettings.subscribeRemoteListener(this, "sessionsettingsprop");
            regSettings.subscribeRemoteListener(this, "cancelgameprop");
            regSettings.subscribeRemoteListener(this, "startgameprop"); 
            regSettings.subscribeRemoteListener(this, "chatmessageprop"); 
            // Hou er rekening mee dat de inform van hieruit niet doorgevoerd wordt.
            // Dit is puur om de gamesettings te zetten. De Session klasse kan alleen de inform afgeven.
            session.setGameSettings(gamesettings);
            //regsettings.subscribeRemoteListener(this, "startgameprop");
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            registry = null;
            Gdx.app.log("SessionManager", ex.getMessage());
        } catch (NotBoundException ex) {
            Gdx.app.log("SessionManager", ex.getMessage());
        }
    }

    /**
     * 
     * @return the current session with the current gameSettings.
     */
    public Session getSession() {
        return session;
    }
    
    /**
     * Sends message to host which gets relayed to other players
     * @param message Message to send
     */
    public void sendMessage(String message) {
        try {
            regSettings.sendChatMessage(message);
            
        } catch (RemoteException ex) {
            Gdx.app.log("SessionManager", ex.getMessage());
        }
    }
    
    /**
     * Set the registry for a client back to null. This means the registry is removed.
     * Also unsubscribe from the properties it was subscribed to.
     * 
     */
    public void removeRegistry(){
        try {
            registry = null;
            regSettings.unsubscribeRemoteListener(this, "sessionsettingsprop");
            regSettings.unsubscribeRemoteListener(this, "cancelgameprop");
            regSettings.unsubscribeRemoteListener(this, "startgameprop");   
            regSettings.unsubscribeRemoteListener(this, "chatmessageprop");  
        } catch (RemoteException ex) {
            Gdx.app.log("SessionManager", ex.getMessage());
        }
    }

    /**
     * If one of the subscribed properties changes it will run this method.
     * Check which property changed and then run the needed methods.
     * 
     * If it is the sessionsettingsprop this means there are new settings 
     * -> set the new settings for the client
     * -> update the list of teams
     * -> update the selectboxes to the new settings
     * -> update the image/selectbox map to the map of the gamesettings.
     * 
     * If it is the cancelgameprop the game will quit for the clients.
     * -> call a method from the GUI which will change a variable which will be constantly checked in the render method.
     * 
     * If it is the startgameprop the game will start for the clients.
     * -> call a method from the GUI which will change a variable which will be constantly checked in the render method.
     * @param evt
     * @throws RemoteException 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        if (evt.getPropertyName().equals("sessionsettingsprop")) {
            gamesettings = (GameSettings) evt.getNewValue();
            session.setGameSettings(gamesettings);

            // Update the teams
            gui.updateTeamList(session);

            // Update the selected items SelectBoxes.
            gui.updateSelectedItems(session);

            // Update the map in the image.
            gui.updateSelectedMap(session);
        }
        if (evt.getPropertyName().equals("cancelgameprop")){
            // Clean the defaults for a player who leaves the session. Like delete him from a session
            // The default should not delete the player from the list of players because he is going back to the lobby.
            gui.disposeWithoutPlayer();

            // Go back to the LobbyGUI for a player.
            // This will set a value for a variable which will be checked constantly inside the render method.
            gui.backToLobby();
        }
        
        if (evt.getPropertyName().equals("startgameprop")) {
            gui.startGame();
        }
        
        if (evt.getPropertyName().equals("chatmessageprop")) {
            // chat message received, add it to the gui
            gui.chatMessage((String) evt.getNewValue());
        }

    }
}
