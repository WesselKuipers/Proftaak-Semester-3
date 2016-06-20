package com.extra;

/**
 *
 * @author Gebruiker
 */
public final class Pathfinder {

    private static String absolutePath;

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
