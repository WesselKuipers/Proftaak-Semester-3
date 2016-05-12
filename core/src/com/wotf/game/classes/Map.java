package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/**
 * Map contains data regarding the map the session is using for its terrain
 */
public class Map {

    private static final double GravityModifier = 9.8;
    private int waterLevel;
    private int width;
    private int height;
    private boolean[][] terrain;
    private Texture landscapeTexture;
    private Texture backgroundTexture;
    private Pixmap pixmap;

    private Vector2 windForce;

    /**
     * Constructor of Map Loads the image associated with the specified filepath
     * into a boolean[][] for each pixel that isn't part of the background
     * (black pixels)
     *
     * @param filename Filepath
     */
    public Map(String filename) {
        Pixmap.setBlending(Pixmap.Blending.None);
        
        // Generates and initializes background and foreground textures
        generateBackgroundTexture();
        initializeTerrainTexture(filename);
    }

    /**
     * Constructor without any graphics Made for the unit testing.
     */
    public Map() {
        width = 1920;
        height = 720;
        landscapeTexture = null;
    }
    
    /**
     * Attempts to read an image file and generates a terrain texture
     * based on the pixel information of this image.
     * 
     * Black pixels (#000000) are considered transparent.
     * @param filename Name of the image to read
     */
    private void initializeTerrainTexture(String filename) {
        // reads the image with this filename and stores it in a pixmap
        Pixmap inPixmap = new Pixmap(Gdx.files.internal(filename));
        
        width = inPixmap.getWidth();
        height = inPixmap.getHeight();

        // new pixmap that will contain the processed output of inPixmap
        Pixmap outPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // initializes the terrain array based on the width and height of the image
        terrain = new boolean[width][height];

        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[0].length; y++) {
                int flippedY = height - y - 1;

                // 255 is the value of black
                if (inPixmap.getPixel(x, y) != 255) {
                    // This pixel isn't black, so we mark it as solid
                    terrain[x][flippedY] = true;
                    outPixmap.drawPixel(x, flippedY, inPixmap.getPixel(x, y));
                } else {
                    // Since this pixel is black, we want to make it transparent
                    outPixmap.drawPixel(x, flippedY, Color.CLEAR.toIntBits());
                }
            }
        }

        // sets the landscape texture based on outPixmap
        landscapeTexture = new Texture(outPixmap);
        inPixmap.dispose();
    }
    
    /**
     * Generates a flat coloured background texture
     */
    private void generateBackgroundTexture() {
        Pixmap bgPixmap = new Pixmap(this.getWidth(), this.getHeight(), Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.PURPLE);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
    }
    
    /**
     * Updates the Terrain boolean[][] based on a given radius and coordinates 
     * <br>
     * Calls {@link updateTerrain()} after updating the terrain array
     * 
     * @param x X-origin of terrain destruction
     * @param y Y-origin of terrain destruction
     * @param radius Radius from the given X and Y coordinates to destroy
     */
    public void destroyRadius(int x, int y, int radius) {
        for (int xPos = x - radius; xPos <= x + radius; xPos++) {
            for (int yPos = y - radius; yPos <= y + radius; yPos++) {
                // scan square area around radius to determine which pixels to destroy
                if (Math.pow(xPos - x, 2) + Math.pow(yPos - y, 2) < radius * radius) {

                    // Check if the position is in bounds of the array, if not, skip this iteration
                    if (!(xPos >= 0 && yPos >= 0 && xPos < terrain.length && yPos < terrain[0].length)) {
                        continue;
                    }

                    // if the pixel at (xPos, yPos) is solid, set it to false
                    if (terrain[xPos][yPos]) {
                        terrain[xPos][yPos] = false;
                    }
                }
            }
        }
        
        updateTerrain();
    }
    
    /**
     * Updates the terrain texture based on the terrain array
     */
    public void updateTerrain() {
        pixmap = new Pixmap(terrain.length, terrain[0].length, Pixmap.Format.RGBA8888);

        if (!landscapeTexture.getTextureData().isPrepared()) {
            landscapeTexture.getTextureData().prepare();
        }

        Pixmap oldPixmap = landscapeTexture.getTextureData().consumePixmap();

        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[1].length; y++) {
                if (terrain[x][y]) {
                    pixmap.drawPixel(x, y, oldPixmap.getPixel(x, y));
                }
            }
        }

        landscapeTexture = new Texture(pixmap);
        oldPixmap.dispose();
    }

    /**
     * Returns the gravity value of this map
     *
     * @return Gravity value used for falling objects
     */
    public double getGravityModifier() {
        return GravityModifier;
    }

    /**
     * Returns the height of the water of the map
     *
     * @return Integer representing the water level in pixels
     */
    public int getWaterLevel() {
        return waterLevel;
    }

    /**
     * Sets the height of the water of the map
     *
     * @param waterLevel Integer representing the water level in pixels can't be
     * higher than the map or lower than 0
     */
    public void setWaterLevel(int waterLevel) {
        if (waterLevel < 0 || waterLevel > height) {
            return;
        }
        this.waterLevel = waterLevel;
    }

    /**
     * Returns the width of the map
     *
     * @return Integer representing the width of the map in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the map
     *
     * @param width Integer representing the width of the map in pixels, can't
     * be lower than 0
     */
    public void setWidth(int width) {
        if (width < 0) {
            return;
        }
        this.width = width;
    }

    /**
     * Returns the height of the map
     *
     * @return Integer representing the height of the map in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the map
     *
     * @param height Integer representing the height of the map in pixels, can't
     * be lower than 0
     */
    public void setHeight(int height) {
        if (height < 0) {
            return;
        }
        this.height = height;
    }

    /**
     * Returns a boolean[][] representing the pixels of the game world
     * Position[x][y] represents whether or not a pixel at given location is
     * solid
     *
     * @return Boolean[][] representing the terrain
     */
    public boolean[][] getTerrain() {
        return terrain;
    }

    /**
     * Sets the terrain boolean[][]
     *
     * @param terrain Boolean[][] to set
     */
    public void setTerrain(boolean[][] terrain) {
        this.terrain = terrain;
    }

    /**
     * Returns a Texture object that's equal to the original bitmap used when
     * initializing this Map instance
     *
     * @return Texture object containing the original foreground landscape
     */
    public Texture getLandscapeTexture() {
        return landscapeTexture;
    }

    /**
     * Returns a Texture object that's equal to the original bitmap used when
     * initializing this Map instance
     *
     * @return Texture instance containting the background
     */
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    /**
     * Returns a Rectangle object based on the size of the map
     *
     * @return Rectangle object representing the bounds of the map
     */
    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    /**
     * Function that calculates a random wind, should be called every turn.
     */
    public void calculateWind() {
        Random random = new Random();

        int range = 20;

        float x = random.nextInt(range) - range / 2;
        float y = 0;

        this.windForce = new Vector2(x, y);
        System.out.println(windForce.toString());
    }

    /**
     * Function that returns the current wind force.
     *
     * @return Vector2 wind force.
     */
    public Vector2 getWind() {
        return this.windForce;
    }
    
    /**
     * Checks if a pixel at a given coordinate is set to solid or not
     *
     * @param x X-coordinate in world map
     * @param y Y-coordinate in world map
     * @return True if pixel is solid, false if not
     */
    public boolean isPixelSolid(int x, int y) {       
        if (!(x >= 0 && y >= 0 && x < terrain.length && y < terrain[0].length)) {
            // Out of bounds
            return false;
        }
        
        return terrain[x][y];
    }
}
