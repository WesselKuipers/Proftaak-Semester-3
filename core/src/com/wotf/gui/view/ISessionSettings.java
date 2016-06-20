package com.wotf.gui.view;

import com.wotf.game.classes.GameSettings;
import fontyspublisher.IRemotePublisherForListener;
import java.rmi.RemoteException;

/**
 * Interface used to communicate with the host of a session and obtain its settings
 */
public interface ISessionSettings extends IRemotePublisherForListener{
    
    /**
     * @return The settions associated with the game of this session
     * @throws RemoteException thrown when there is a connection error
     */
    GameSettings getGameSettings() throws RemoteException;
    
    /**
     * Sends a message to the host which gets relayed to all clients
     * @param message Message to send
     * @throws RemoteException thrown when there is a connection error
     */
    void sendChatMessage(String message) throws RemoteException;
}
