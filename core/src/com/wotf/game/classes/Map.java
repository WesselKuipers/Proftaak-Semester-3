package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Map {
    private double gravityModifier = 9.8;
    private int waterLevel;
    private int width;
    private int height;
    private boolean[][] terrain;
    private Texture landscapeTexture;
    private Texture backgroundTexture;

    public Map(String filename) {
        // logic for loading
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

    public double getGravityModifier() {
        return gravityModifier;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean[][] getTerrain() {
        return terrain;
    }

    public void setTerrain(boolean[][] terrain) {
        this.terrain = terrain;
    }

    public Texture getLandscapeTexture() {
        return landscapeTexture;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }
}
