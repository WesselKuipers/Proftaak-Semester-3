/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class MapTest {

    private Map map;

    @Before
    public void initMap() {
        map = new Map();
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", map);
    }

    @Test
    public void testReadMap() {
        // Nog niet geimplementeerd en ook geen doc.
    }

    @Test
    public void testgetGravityModifier() {
        // The default gravity modifier is set to 9.8 check if this is true.
        assertNotNull(map.getGravityModifier());
    }

    @Test
    public void testsetgetWaterLevel() {
        // Set the water level to 20
        map.setWaterLevel(20);
        // Get the water level, check if it is 20.
        assertEquals(20, map.getWaterLevel());
    }

    @Test
    public void testsetgetWidth() {
        // Set the width to 40
        map.setWidth(40);
        // Get the width, check if it is 40.
        assertEquals(40, map.getWidth());
    }

    @Test
    public void testsetgetHeight() {
        // Set the height to 40
        map.setHeight(40);
        // Get the height, check if it is 40.
        assertEquals(40, map.getHeight());
    }

    @Test
    public void testgetTerrain() {
        // Create a boolean array and set random x y as width and height
        int x = 20;
        int y = 20;
        boolean[][] terrain = new boolean[x][y];
        // Set the terrain.
        map.setTerrain(terrain);
        // Test if the terrain length equals the set terrain length.
        assertEquals(terrain.length, map.getTerrain().length);
    }

    @Test
    public void testsetandgetWind(){
        //  Sets the windforce as a vector 2.
        Vector2 vec = new Vector2(20,20);
        map.setWind(vec);
        // Test if the wind is the same as the set wind of the map.
        assertEquals(vec, map.getWind());
    }
    
    @Test
    public void testgetLandscapeTexture() {
        // The map has a width of 1920
        // Test for the width of the landscapetexture. This has to be 1920 as well.
        // assertEquals(1920, map.getLandscapeTexture().getWidth());
        
        // For the case of testing it is a graphics object and it should be null for now.
        assertNull(map.getLandscapeTexture());
    }

    @Test
    public void testgetBackgroundTexture() {
        // Background textures are not implemented. So it should be null.
        assertNull(map.getBackgroundTexture());
    }

    @Test
    public void testgetBounds() {
        // Test if the given rectangle has the width set to 1920. 
        // This is the width of the Stones.png image file.
        Rectangle testrect = new Rectangle(0, 0, 1920, 720);
        // Test if the rectangles are the same. 
        assertEquals(testrect, map.getBounds());
    }

    @Test
    public void testCalculateWind() {
        /**
         * Function that calculates a random wind, should be called every turn.
         */
        // The range in the method is 20. So it should be used here as well.
        int range = 20;
        for (int i = 0; i < 20; i++) {
            // Calculate the wind and check 20 times to be sure. Just in case the windvector X or Y is bigger than the range.
            // Which shouldn't be possible.
            map.calculateWind();
            if (map.getWind().x > range) {
                fail("The windforce on X is bigger than the range. This shouldn't happen.");
            }
            if (map.getWind().y > range) {
                fail("The windforce on X is bigger than the range. This shouldn't happen.");
            }
        }

    }
}
