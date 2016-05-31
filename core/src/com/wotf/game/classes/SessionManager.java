/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.wotf.gui.view.ISessionSettings;
import com.wotf.gui.view.SessionOnlineHost;
import com.wotf.gui.view.SessionOnlinePlayer;
import fontyspublisher.IRemotePropertyListener;
import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To connect a client to a session there must be a SessionManager instance.
 * This class makes it possible to connect to a specific registry/session based on the hostIP.
 * 
 * @author Remco
 */
public class SessionManager extends UnicastRemoteObject implements IRemotePropertyListener {

    private Registry registry;
    private Session session;
    private ISessionSettings regsettings;
    private GameSettings gamesettings;
    private SessionOnlinePlayer GUI;

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
     * @param GUI which will be shown for the client
     * @throws RemoteException 
     */
    public SessionManager(Session session, SessionOnlinePlayer GUI) throws RemoteException {
        this();
        this.session = session;
        this.GUI = GUI;
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
            regsettings = (ISessionSettings) registry.lookup("SessionSettings");
            gamesettings = (GameSettings) regsettings.getGameSettings();
            regsettings.subscribeRemoteListener(this, "sessionsettingsprop");
            regsettings.subscribeRemoteListener(this, "cancelgameprop");
            regsettings.subscribeRemoteListener(this, "startgameprop"); 
            regsettings.subscribeRemoteListener(this, "chatmessageprop"); 
            // Hou er rekening mee dat de inform van hieruit niet doorgevoerd wordt.
            // Dit is puur om de gamesettings te zetten. De Session klasse kan alleen de inform afgeven.
            session.setGameSettings(gamesettings);
            //regsettings.subscribeRemoteListener(this, "startgameprop");
        } catch (RemoteException ex) {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            registry = null;
        } catch (NotBoundException ex) {
            Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
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
     * Set the registry for a client back to null. This means the registry is removed.
     * Also unsubscribe from the properties it was subscribed to.
     * 
     */
    public void removeRegistry(){
        try {
            registry = null;
            regsettings.unsubscribeRemoteListener(this, "sessionsettingsprop");
            regsettings.unsubscribeRemoteListener(this, "cancelgameprop");
            regsettings.unsubscribeRemoteListener(this, "startgameprop");   
            regsettings.unsubscribeRemoteListener(this, "chatmessageprop");  
        } catch (RemoteException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
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
            GUI.updateTeamList(session);

            // Update the selected items SelectBoxes.
            GUI.updateSelectedItems(session);

            // Update the map in the image.
            GUI.updateSelectedMap(session);
        }
        if (evt.getPropertyName().equals("cancelgameprop")){
            // Clean the defaults for a player who leaves the session. Like delete him from a session
            // The default should not delete the player from the list of players because he is going back to the lobby.
            GUI.disposeWithoutPlayer();

            // Go back to the LobbyGUI for a player.
            // This will set a value for a variable which will be checked constantly inside the render method.
            GUI.backToLobby();
        }
        
        if (evt.getPropertyName().equals("startgameprop")){
            GUI.startGame();
        }
        if (evt.getPropertyName().equals("chatmessageprop")){
            GUI.chatMessage((String) evt.getNewValue());
        }

    }
}
