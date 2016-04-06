/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class ProjectileTest {
    private Projectile projectile;
    
    @Before
    public void initProjectile() {
        Vector2 unitposition = new Vector2(20, 80);
        Vector2 wind = new Vector2(0,0);
        //projectile = new Projectile();
    }
    
    @Test
    public void testInit() {
        
    }
}
