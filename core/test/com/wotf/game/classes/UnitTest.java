/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import HeadlessRunner.GdxTestRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.wotf.game.classes.Items.Bazooka;
import com.wotf.game.classes.Items.Item;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Remco
 */
@RunWith(GdxTestRunner.class)
public class UnitTest {

    private Team team;
    private Unit unit;
    private Item bazooka;

    @Before
    public void initItem() {
        team = new Team("Alpha", Color.BLUE);
        unit = new Unit("Unit1", 100, team, new Vector2(40, 80));
        bazooka = new Bazooka();
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", unit);
    }

    // Uses the GameStage.. Cannot be tested.
    @Test
    public void testselectWeapon() {
        // Select the given Item.
        unit.selectWeapon(bazooka);
        // Test if the selectedweapon is the same as the one we created.
        assertEquals("Bazooka", unit.getWeapon().getName());
    }

    @Test
    public void testdestroyWeapon() {
        // Remove the current weapon of the figure.
        unit.destroyWeapon();
        // Test if the weapon indeed is null now.
        assertNull(unit.getWeapon());
    }

    @Test
    public void testFire() {
        // Select the bazooka
        // unit.selectWeapon(bazooka);
        // It shouldn't have a weapon AFTER firing.
        // It uses the gamestage. Cannot be tested..
        unit.fire(new Vector2(20, 20), new Vector2(10, 10), 4);
        // Now it shouldn't have a weapon.
        assertNull(unit.getWeapon());
    }

    @Test
    public void testgetHealth() {
        /**
         * @return health of the unit
         */
        assertEquals(100, unit.getHealth());
    }

    @Test
    public void testincreaseHealth() {
        /**
         * Increase the health of the unit
         *
         * @param amount to increase health
         */
        unit.increaseHealth(20);
        // Test if the unit now has 100 + 20 health.
        assertEquals(120, unit.getHealth());
    }

    @Test
    public void testdecreaseHealth() {
        /**
         * Decrease the health of the unit
         *
         * @param amount to decrease health
         */
        unit.decreaseHealth(40);
        // Test if the unit now has 100 - 40 which should be 60.
        assertEquals(60, unit.getHealth());
    }

    @Test
    public void testgetSprite() {
        /**
         * @return the sprite of the unit
         */
        // The sprite has a width of 17. Test if this is true
        assertEquals(17, unit.getSprite().getTexture().getWidth());
    }

    @Test
    public void testsetSprite() {
        /**
         * Set a new sprite.
         */
        Sprite sprite = new Sprite();
        unit.setSprite(sprite);
        // The sprite is now set to something with a value. This means it shouldn't be null anymore.
        assertNotNull(unit.getSprite());
    }

    @Test
    public void testgetPosition() {
        /**
         * Gets the position of the figure on the map.
         */
        // Check if the vector is correctly set.
        Vector2 position = new Vector2(40, 80);
        assertEquals(position, unit.getPosition());
    }

    @Test
    public void testgetName() {
        /**
         * Returns the name associated with this unit
         *
         * @return String containing the name of this unit
         */
        assertEquals("Unit1", unit.getName());
    }

    @Test
    public void testgetBounds() {
        /**
         * Returns a rectangle representing the bounds of unit
         *
         * @return Rectangle based on X, Y, Width and Height of unit
         */
        // At this moment the sprite has 0,0,17,17 as bounds.
        assertEquals(new Rectangle(0, 0, 17, 17), unit.getBounds());
    }

    // This Test WILL fail. There is an internal method called "PositionChanged()" 
    // This method changes values of a sprite, but a sprite is a graphical object which is not used in the testcases. This doesn't work.
    @Test
    public void testSpawn() {
        /**
         * Spawns a unit at the specified location
         *
         * @param position Position to spawn the unit at
         */
        Vector2 newposition = new Vector2(100, 80);
        unit.spawn(newposition);
        // Check if the position of the unit is changed now.
        assertEquals(newposition, unit.getPosition());
    }

    // The function uses a GameStage..
    @Test
    public void testJump() {
        /**
         * In jump() we set the following - setAngle with the next position -
         * setAcceleration with the gravity - setVelocity with the force
         *
         * Then the act calls the updateJump().
         */
        Vector2 afterjump = new Vector2(120, 80);
        // MoveRight is set to True by default.
        // This means the nextX will be the position of X + 20 when the unit lands.
        // unit.jump();
        // The earlier created spawn was on the position 100, 80. So with the +20 of the jump it should be 120.
        assertEquals(afterjump, unit.getPosition());
    }

    @Test
    public void testsetPosition() {
        /**
         * Sets the new position.
         */
        Vector2 newposition = new Vector2(160, 80);
        unit.setPosition(new Vector2(160, 80));
        // Test if the position is correct.
        assertEquals(newposition, unit.getPosition());
    }

    @Test
    public void testgetWeapon() {
        /**
         * Function to get the current weapon of the unit, needed to get bullet
         * in gamescene.
         *
         * @return return activeWeapon.
         */
        assertEquals("Bazooka", unit.getWeapon().getName());
    }
    
//    @Test
//    public void testgetFrame(){
//        // Changes the units facing direction if not facing the direction it should face.
//        // unit.getFrame(1);
//    }
}
