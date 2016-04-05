/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class UnitTest {
    private Team team;
    private Unit unit;
    private Unit unitwithvector;
    
    @Before
    public void initItem() {
        team = new Team("Alpha", Color.BLUE);
        unit = new Unit("Unit1", 100, team); 
        Vector2 position = new Vector2(10,10);
        unitwithvector = new Unit("Unit2", 120, team, position);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", unit);
    }
    
    @Test
    public void testdefineBody(){
        
    }
}
