package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Map {
    private double gravityModifier = 9.8;
    private int waterLevel;
    private int width;
    private int height;
    private boolean[][] terrain;
    private Texture landscapeTexture;
    private Texture backgroundTexture;

    /**
     * Constructor of Map
     * Loads the image associated with the specified filepath into
     * a boolean[][] for each pixel that isn't part of the background (black pixels)
     * @param filename Filepath
     */
    public Map(String filename) {
        Pixmap.setBlending(Pixmap.Blending.None);
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        
        Pixmap out = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        terrain = new boolean[width][height];
        
        pixmap.setColor(Color.CLEAR);
        
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                int flippedY = height - y - 1;
                
                // 255 is the value of black
                if (pixmap.getPixel(x, y) != 255) {
                    // This pixel isn't black, so we mark it as solid
                    terrain[x][flippedY] = true;
                    out.drawPixel(x, flippedY, pixmap.getPixel(x, y));
                } else {
                    // Since this pixel is black, we want to make it transparent
                    out.drawPixel(x, flippedY, Color.CLEAR.toIntBits());//Color.RED.toIntBits());
                }
            }
        }
        
        landscapeTexture = new Texture(out);
        pixmap.dispose();
    }

    public void readMap(String filename) {
        // logic for reading map
        
    }

    /**
     * Returns the gravity value of this map
     * @return Gravity value used for falling objects
     */
    public double getGravityModifier() {
        return gravityModifier;
    }

    /**
     * Returns the height of the water of the map
     * @return Integer representing the water level in pixels
     */
    public int getWaterLevel() {
        return waterLevel;
    }

    /**
     * Sets the height of the water of the map
     * @param waterLevel Integer representing the water level in pixels
     *                   can't be higher than the map or lower than 0
     */
    public void setWaterLevel(int waterLevel) {
        if(waterLevel < 0 || waterLevel > height) { return; }
        this.waterLevel = waterLevel;
    }

    /**
     * Returns the width of the map
     * @return Integer representing the width of the map in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the map
     * @param width Integer representing the width of the map in pixels, can't be lower than 0
     */
    public void setWidth(int width) {
        if(width < 0) { return; }
        this.width = width;
    }

    /**
     * Returns the height of the map
     * @return Integer representing the height of the map in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the map
     * @param height Integer representing the height of the map in pixels, can't be lower than 0
     */
    public void setHeight(int height) {
        if(height < 0) { return; }
        this.height = height;
    }

    /**
     * Returns a boolean[][] representing the pixels of the game world
     * Position[x][y] represents whether or not a pixel at given location is solid
     * @return Boolean[][] representing the terrain
     */
    public boolean[][] getTerrain() {
        return terrain;
    }

    /**
     * Sets the terrain boolean[][]
     * @param terrain Boolean[][] to set
     */
    public void setTerrain(boolean[][] terrain) {
        this.terrain = terrain;
    }

    /**
     * Returns a Texture object that's equal to the original bitmap used when initializing this Map instance
     * @return Texture object containing the original foreground landscape
     */
    public Texture getLandscapeTexture() {
        return landscapeTexture;
    }

    /**
     * Returns a Texture object that's equal to the original bitmap used when initializing this Map instance
     * @return Texture instance containting the background
     */
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
    
    /**
     * Returns a Rectangle object based on the size of the map
     * @return Rectangle object representing the bounds of the map
     */
    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }
}
