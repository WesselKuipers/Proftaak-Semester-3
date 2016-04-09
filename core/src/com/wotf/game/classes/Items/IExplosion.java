/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

/**
 * Adds an explosion implemntation to an item
 */
public interface IExplosion {
    
    /**
    * trigger the explosion method of the object
    */
    public void explode();
    
    /**
    * size of the explosion
    * @return the radius of the explosion
    */
    public int radius();
    
    /**
    * Method to cotnrol the blast and the objects affected by this explosion
    * @param distance distance of object to the explosion centre
    * @return the force an object is affected by
    */
    public int blastControl(int distance);
}
