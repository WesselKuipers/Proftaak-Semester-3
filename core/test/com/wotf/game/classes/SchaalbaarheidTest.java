/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.junit.Test;

/**
 *
 * @author Remco
 */
public class SchaalbaarheidTest {

    @Test
    public void test1000RMI() throws RemoteException {
        for (int i = 0; i < 1000; i++) {
            Registry registry = LocateRegistry.createRegistry(9999 + i);
            System.out.println(9999 + i + " - " + registry.toString());
        }
    }
}
