/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.Networking;

/**
 * @author  Jip boesenkool 
 * @date    01-05-'16
 * These commands are used by the class NetworkMessage to specify what action has to be taken.
 */
public enum Command {
    
    //actual enum commands
    FIRE, 
    MOVE,
    BEGINTURN,
    ENDTURN,
    INITGAME,
    SYNCUNITS,
    TERRAIN, 
    SWITCHUNIT,
    SELECTWEAPON,
    SYNCCOLLISION;
    
    /**
     * Helper method which converts a string into command enum.
     * @param str The name of the command.
     * @return Command enum if command exists, Else return null if command was not found.
     */
    public static Command convert( String str ) {
        for (Command cmd : Command.values()) {
            if ( cmd.toString().equals(str) ) {
                return cmd;
            }
        }
        return null;
    }
}
