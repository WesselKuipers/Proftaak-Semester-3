/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

/**
 *
 * @author chaos
 */
/*
public class GrenadePrototype extends Grenade implements ITimer, IThrowable, IExplosion{
    
    public GrenadePrototype(String nm, int pw, int rad) {
        super(nm, pw, rad);
    }
    
    @Override
    public void activate() {
        detonationTime();
        //other things?
    }

    @Override
    public void explode() {
        //TODO: AANROEPEN DESTROY METHODE VAN WESSEL
        
        // radius wordt bepaald door de radius functie 
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int radius() {
        int rad = super.getBlastRadius();
        int power = super.getPower();
        Random r = new Random();
        int newRad = r.nextInt(rad) + 1;
        int newPower = r.nextInt(power) + 1;
        return (newRad + (int) (Math.random() * newPower));
    }
    
    @Override
    public int blastControl(int distance) {
        //returns int with the power an object gets pushed backwards
        //direction is always from center of explosion through unit 
        //e.g. if unit on top, unit gets blown upwards
        //if unit on left, unit gets blown left etc. 
        
        int blastradius = super.getBlastRadius();
        int power = super.getPower();
        int force = power+blastradius;
        
        if(distance < force){
            force -= distance;
            //working with vectors, we'd need:
            //explosion coords (1), and unit coords (2) for finding angle of explosion--> Tan2(x1-x2, y1-y2)*180/PI
            //force X --> force*cos(angle)      force Y --> force*sin(angle)
            return force;
        }
        return 0;
    }

    @Override
    public void detonationTime() {
        //use timer to explode after time x
        
        explode();
    }
    
}*/
