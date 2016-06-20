/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.Networking;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


/**
 * @author Jip Boesenkool
 * Message to be sended between clients.
 * Message should always be of the following syntax: cmd:commandname,parName:parValue;
 * The specified action should always be the first on a line and is specified by the keyword cmd.
 * : specifies that the value is following
 * , splits the parameters
 * ; end a line so multiple commands can be send in 1 go.
 */
public class NetworkMessage {
    
    private Command cmd;
    private final HashMap<String, String> parameters;
    
    /**
     * Constructor to create a message action should be known when creating a message and CANNOT be altered.
     * @param cmd   Specifies the action the data in the message was ment for.
     */
    public NetworkMessage( Command cmd ){    
        this.cmd = cmd;
        parameters = new LinkedHashMap<>();
    }
    
    /**
     * Converts an existing message into hashmap ready to be processed.
     * @param message   Message to be converted to hashmap.
     */
    public NetworkMessage( String message ){
        parameters = new LinkedHashMap<>();
        
        if( !parseString( message ) ){
            throw new IllegalArgumentException("NetworkMessage syntax error, Please check the construction of the message.");
        }
    }    
    
    /**
     * get command.
     * @return command
     */
    public Command getCommand(){
        return cmd;
    }
    
    /**
     * Get the value of specified parameter name;
     * @param parameterName The name of the parameter
     * @return The value as string or null if the value was not found.
     */
    public String getParameter( String parameterName ){
        String value = parameters.get( parameterName );
        if(value == null){
            throw new InvalidParameterException("Value of " + parameterName + " was not found.");
        }
        
        return value;
    }
    
    /**
     * Adds parameter to the hashMap.
     * @param parameterName name of the parameter specified in the function so it can be checked and parsed easily.
     * @param ParameterValue The value of the parameter converted to string.
     */
    public void addParameter( String parameterName, String ParameterValue ){
        parameters.put( parameterName, ParameterValue );
    }
    
    /**
     * Deconstruct the message into hashmap.
     * @param message message to be converted.
     * @return True if message is deconstructed correctly, false otherwise.
     */
    private boolean parseString( String message ){

        //split string by seperator ,
        String[] splitSeperator = message.split(",");
        
        //handle command 
        if( !splitCommand( splitSeperator[0] ) ){
            return false;
        }
        
        //handle parameters, except for last parameter
        for(int index = 1; index < splitSeperator.length; index++){
            String[] splitPar = splitSeperator[index].split(":");
            
            if( splitPar.length == 2 ){
                parameters.put( splitPar[0], splitPar[1] );
            }
            else{
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Split a string array into a command.
     * @param cmdStr  String which specifies the command in format.
     * @return True if command was valid, False if syntax is wrong or command was not found in enum.
     */
    private boolean splitCommand( String cmdStr ){
        String[] splitCmd = cmdStr.split(":");
        
        if( splitCmd.length == 2 ){            
            //check if command exists
            if( splitCmd[0].equals("cmd") && Command.convert( splitCmd[1] ) != null){
                cmd = Command.valueOf( splitCmd[1] );
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Formats the data so it can be send as a message to other clients.
     * @return String in correct syntax.
     */
    @Override
    public String toString(){
        
        String message = "cmd:" + cmd.toString();
        for( Entry<String, String> entry : parameters.entrySet() ) {
            String key = entry.getKey();
            String value = entry.getValue();
            message += "," + key + ":" + value ;
        }

        //add end of message
        message += ";";
        
        return message;
    }
}
