/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.extra;

/**
 *
 * @author Gebruiker
 */
public final class Pathfinder {

    private static String absolutePath;

    /**
     * Function which gets the filepath of the .jar file on every platform.
     *
     * @return string which points to the path of the .jar file.
     */
    private String getExecutionPath() {
        absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.replaceAll("%20", " ");
        return absolutePath;
    }

    /**
     *
     * @return the current project's path.
     */
    public static String getRelativePath() {
        absolutePath = System.getProperty("user.dir");
        if (absolutePath.contains("assets")) {
            return absolutePath + "/";
        } else {
            return absolutePath + "/assets/";
        }
    }
}
