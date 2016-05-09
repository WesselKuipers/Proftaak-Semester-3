/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.wotf.game.classes.GameSettings;
import fontyspublisher.IRemotePublisherForListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Remco
 */
public interface ISessionSettings extends IRemotePublisherForListener{
    GameSettings getGameSettings() throws RemoteException;
}
