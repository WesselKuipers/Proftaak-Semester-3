/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Gebruiker
 */
public class BazookaTest {

    private Bazooka bazooka;

    /*@Before
    public void initItem() {
        bazooka = new Bazooka("Bazooka", 20, 30, 100, null, null);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", bazooka);
    }

    @Test
    public void testRadius() {
        // The radius picks a random radius between 1 and the given radius.
        // The radius picks a random power value between 1 and the given power.
        // In this case I'm going to test if it is returning different values between the given values.
        for (int i = 0; i < 20; i++) {
            // The return value is always smaller than the given radius + power. Check if this is true
            if (!(bazooka.radius() < 50)) {
                fail("The value is above 50 which means there is something wrong..");
            }
        }

    }

    @Test
    public void testBlastControl() {
        // Test if the force actually is force - distance
        // The power is set to 20. And now I will set the distance to 10.
        // Now the earlier created blastradius of 30 + the 20 power is 50. 50 is the force.
        // Force minus distance (50-10) = 40. So this should be the result
        assertEquals(40, bazooka.blastControl(10));
        // Test if the force actually is 0 when the force is smaller than the distance.
        // In this example the force will be 50 again and the distance is 60. If the distance is bigger than the force, it will return 0.
        assertEquals(0, bazooka.blastControl(60));

    }*/

}
