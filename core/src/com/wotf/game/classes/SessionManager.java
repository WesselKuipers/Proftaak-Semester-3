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
 *
 * @author Remco
 */
public class SessionManager extends UnicastRemoteObject implements IRemotePropertyListener {

    private Registry registry;
    private Session session;
    private ISessionSettings regsettings;
    private GameSettings gamesettings;
    private SessionOnlinePlayer GUI;

    public SessionManager() throws RemoteException {
    }

    public SessionManager(Session session, SessionOnlinePlayer GUI) throws RemoteException {
        this();
        this.session = session;
        this.GUI = GUI;
        getSessionRegistry();
    }

    public void getSessionRegistry() {
        // REGISTER AREA
        try {
            registry = LocateRegistry.getRegistry(session.getHost().getIp(), 5555);
            regsettings = (ISessionSettings) registry.lookup("SessionSettings");
            gamesettings = (GameSettings) regsettings.getGameSettings();
            regsettings.subscribeRemoteListener(this, "sessionsettingsprop");
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

    public Session getSession() {
        return session;
    }
    
    public void removeRegistry(){
        registry = null;
    }

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
        // Possible logic for making the GUI start the game from here with the right settings..
        // or just a copy of the Game object if this IS SERIALIZABLE?
        /*if (evt.getPropertyName().equals("startgameprop")) {
            //GUI.dispose();
        }*/
    }

}
