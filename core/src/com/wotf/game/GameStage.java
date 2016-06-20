/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.Networking.Command;
import com.wotf.game.Networking.NetworkMessage;
import com.wotf.game.Networking.NetworkUtil;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.Projectile;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.TurnLogic.TurnState;
import com.wotf.game.classes.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of Stage that contains a game session
 *
 */
public class GameStage extends Stage {

    private final Game game;
    private final GuiStage guiStage;

    private final SpriteBatch batch;
    private final SpriteBatch guiBatch;
    
    private NetworkUtil networkingUtil;
    
    private final BitmapFont font;
    
    private boolean showDebug = false;
    private Actor focusedActor; // if this is set to an actor
    // have the camera follow it automatically, otherwise set it to null

    // particle effect objects
    private ParticleEffectPool explosionEffectPool;
    private List<PooledEffect> particles;

    private ParticleEffect tempParticleEffects;
    /**
     * Constructor for GameStage that initializes everything required for the
     * game to run Generates the initial texture based on the data contained
     * within Map
     *
     * @param game Game data structure to base initial variables on (settings,
     * map, etc)
     * @param guiStage The stage upon which the gui can be drawn
     */
    public GameStage(Game game, GuiStage guiStage) {
        super(new ScreenViewport());
        this.game = game;
        this.focusedActor = null;
        this.guiStage = guiStage;

        batch = new SpriteBatch();
        guiBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.BLACK);

        particles = new ArrayList<>();
    }

    /**
     * Initial setup for the map used to add all team members to the list of
     * actors Spawns the units at random collision-free locations
     */
    public void init() {
        getCamera().update();
        
        networkingUtil = new NetworkUtil( game.getHost(), this );
        
        // Initialize the game by host, after that send it to all connected clients
        if (game.getPlayingPlayer().getId() == game.getHost().getId()) {
            
            // Send all the random spawn locations in one message
            int unitCount = 0;
            List<Vector2> randomSpawnLocations = getRandomSpawnLocations();
            NetworkMessage syncUnitsMsg = new NetworkMessage ( Command.SYNCUNITS );
            
            for (Vector2 spawnLocation : randomSpawnLocations) {
                syncUnitsMsg.addParameter("u" + unitCount + "x", Float.toString(spawnLocation.x));
                syncUnitsMsg.addParameter("u" + unitCount + "y", Float.toString(spawnLocation.y));
                unitCount++;
            }
            
            networkingUtil.sendToHost( syncUnitsMsg );

            NetworkMessage initGameMsg = new NetworkMessage( Command.INITGAME );
            networkingUtil.sendToHost( initGameMsg );
        }
        
        guiStage.updateWind();
    }

    /**
     * Iterates through every team's units and determines a random spawn location
     * 
     * Spawn location is determined by the highest available pixel above
     * a solid pixel at a randomly generated X-coordinate.
     * 
     * @param spawnLocations
     */
    public void spawnUnits(List<Vector2> spawnLocations) {       
        int i = 0;
        // Adds every unit as an actor to this stage
        for (Team team : game.getTeams()) {
            for (Unit unit : team.getUnits()) {

                // spawn a unit
                unit.spawn(spawnLocations.get(i));

                // formally adds the unit as an actor to the stage
                this.addActor(unit);
                i++;
            }
        }
    }
    
    /**
     * 
     * @return a list with random spawn locations 
     */
    public List<Vector2> getRandomSpawnLocations() {
        boolean[][] terrain = game.getMap().getTerrain(); 
        List<Vector2> spawnLocations = new ArrayList<>();
        int unitAmount = game.getTeam(0).getUnits().size() * game.getTeams().size();
        float unitWidth = game.getTeam(0).getUnit(0).getWidth();

        for (int i = unitAmount; i >= 0; i--) {
            // Generates a random X position and attempts to find the highest collision-free position
            // and spawns the unit at that position, will continue looping until a position has been found
            
            while (true) {
                int posX = MathUtils.random(0 + (int) unitWidth, (int) game.getMap().getWidth() - (int) unitWidth);
                int posY = -1;

                // loops through terrain[x ± half its width][y] to check for collision free locations
                for (int x = posX; x < posX + unitWidth; x++) {
                    for (int y = terrain[0].length - 1; y > 0; y--) {
                        if (terrain[x][y]) {
                            if (y > posY) {
                                posY = y;
                            }
                        }
                    }
                }

                // if a position has been found, we can exit the loop
                if (posY != -1) {
                    spawnLocations.add(new Vector2(posX, posY + 1));
                    break;
                }
            }
        }
        
        return spawnLocations;
    }  

    /**
     * Returns game object associated with this stage
     *
     * @return Object of type Game
     */
    public Game getGame() {
        return this.game;
    }
    
    /**
     * Returns network distribution object associated with this stage
     *
     * @return Object of type Network Distribution
     */
    public NetworkUtil getNetworkingUtil() {
        return this.networkingUtil;
    }

    /**
     * Performs a single update step using the current game Calls act() method
     * within each actor in the stage and updates the camera
     */
    @Override
    public void act() {
        super.act();
        float delta = Gdx.graphics.getDeltaTime();

        game.getTurnLogic().update(delta);
        guiStage.update();

        // if focusedActor is set to an actor, we want the camera to follow it
        // otherwise, call the update() method on camera normally
        if (focusedActor != null) {
            setCameraFocusToActor(focusedActor, false);
        } else {
            getCamera().update();
        }
    }

    /**
     * Handles all the keyDown inputs for the stage
     *
     * @param keyCode Entry from Keys enum
     * @return True if the key was handled
     */
    @Override
    public boolean keyDown(int keyCode) {

        OrthographicCamera cam = (OrthographicCamera) getCamera();

        // Always allow these controls (camera controls)
        switch (keyCode) {
            // Camera controls (position)
            case Keys.NUMPAD_2:
                cam.translate(new Vector2(0, -50f));
                focusedActor = null;
                break;
            case Keys.NUMPAD_4:
                cam.translate(new Vector2(-50f, 0));
                focusedActor = null;
                break;
            case Keys.NUMPAD_8:
                cam.translate(new Vector2(0, 50f));
                focusedActor = null;
                break;
            case Keys.NUMPAD_6:
                cam.translate(new Vector2(50f, 0));
                focusedActor = null;
                break;
            // Camera controls (zoom)
            case Keys.PLUS:
                cam.zoom -= 0.05f;
                break;
            case Keys.MINUS:
                cam.zoom += 0.05f;
                break;
            case Keys.ENTER:
                cam.zoom = 1;
                break;
        }
        
        // Only allow these controls for the current playing player
        if (game.getPlayingPlayer().getId() == game.getActiveTeam().getPlayer().getId()) {
            switch (keyCode) {
                // Unit selection
                case Keys.TAB:
                    if (game.getTurnLogic().getState() == TurnState.PLAYING) {
                        NetworkMessage switchUnitMsg = new NetworkMessage(Command.SWITCHUNIT);
                        networkingUtil.sendToHost(switchUnitMsg);
                    }
                    break;
                // Debug key for killing current unit
                case Keys.G:
                    game.getActiveTeam().getActiveUnit().decreaseHealth(100);
                    game.getTurnLogic().endTurn();
                    break;
                case Keys.F4:
                    showDebug = !showDebug;
                    break;
                case Keys.RIGHT:
                    if (game.getActiveTeam().getActiveUnit() != null) {
                        NetworkMessage moveMsg = new NetworkMessage(Command.MOVE);
                        moveMsg.addParameter("direction", "right");
                        networkingUtil.sendToHost(moveMsg);
                    }    
                    break;
                case Keys.LEFT:
                    if (game.getActiveTeam().getActiveUnit() != null) {
                        NetworkMessage moveMsg = new NetworkMessage(Command.MOVE);
                        moveMsg.addParameter("direction", "left");
                        networkingUtil.sendToHost(moveMsg);
                    }
                    break;
                case Keys.NUM_0:
                    sendSelectWeapon(0);
                    break;
                case Keys.NUM_1:
                    sendSelectWeapon(1);
                    break;
                case Keys.NUM_2:
                    sendSelectWeapon(2);
                    break;
                case Keys.NUM_3:
                    sendSelectWeapon(3);
                    break;
                case Keys.NUM_4:
                    sendSelectWeapon(4);
                    break;    
            }
        }

        clampCamera();
        cam.update();

        return super.keyDown(keyCode);
    }
    
    /**
     * Function to send the change of a weapon selection to network.
     * @param weaponIndex index of the weapon
     */
    private void sendSelectWeapon(int weaponIndex) {
        NetworkMessage selectWeaponMsg = new NetworkMessage(Command.SELECTWEAPON);
        selectWeaponMsg.addParameter("weaponIndex", Integer.toString(weaponIndex));
        networkingUtil.sendToHost(selectWeaponMsg);
    }

    /**
     * Click event for the stage Handles the logic for using the currently
     * selected item
     *
     * @param screenX X-coordinate of the cursor
     * @param screenY Y-coordinate of the cursor
     * @param pointer The pointer of the event
     * @param button The keycode of the button passed through
     * @return Whether the input was processed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 rel = getCamera().unproject(new Vector3(screenX, screenY, 0));
        
        // Check if the playing player is allowed to do actions
        if (game.getPlayingPlayer().getId() == game.getActiveTeam().getPlayer().getId()) {
            
            // Check if turn state is playing
            if (game.getTurnLogic().getState() == TurnState.PLAYING) {
                if (button == Input.Buttons.LEFT) {
                    //create fire message to send to host
                    NetworkMessage fireMsg = new NetworkMessage( Command.FIRE );
                    fireMsg.addParameter( "mousePosX", Float.toString(  rel.x ));
                    fireMsg.addParameter( "mousePosY", Float.toString(  rel.y ));
                    
                    //send message to host
                    networkingUtil.sendToHost( fireMsg );

                } else if (button == Input.Buttons.RIGHT) {
                    explode((int) rel.x, (int) rel.y, 30, 0, false);
                }
            }
        }
        return true;
    }

    /**
     * Draws the background and foreground textures and calls the draw() method
     * in each actor
     */
    @Override
    public void draw() {
        batch.setProjectionMatrix(getCamera().combined);
        batch.begin();

        Texture backgroundTexture = game.getMap().getBackgroundTexture();
        Texture terrainTexture = game.getMap().getLandscapeTexture();

        // draws background and foreground
        batch.draw(backgroundTexture, 0, 0, 0, 0,
                backgroundTexture.getWidth(), backgroundTexture.getHeight(),
                1f, 1f, 0, 0, 0,
                backgroundTexture.getWidth(), backgroundTexture.getHeight(),
                false, true);
        batch.draw(terrainTexture, 0, 0, 0, 0,
                terrainTexture.getWidth(), terrainTexture.getHeight(),
                1f, 1f, 0, 0, 0,
                terrainTexture.getWidth(), terrainTexture.getHeight(),
                false, true);

        // draws all particle effects
        for (int i = particles.size() - 1; i >= 0; i--) {
            PooledEffect effect = particles.get(i);
            effect.update(Gdx.graphics.getDeltaTime());
            effect.draw(batch, Gdx.graphics.getDeltaTime());

            if (effect.isComplete()) {
                effect.free();
                particles.remove(i);
                
                //effect.reset();
            }
        }
        batch.end();

        super.draw();

        if (showDebug) {
            // guiBatch is primarily used to display text and miscellaneous graphics
            guiBatch.begin();

            font.draw(guiBatch, "Debug variables:", 0, this.getHeight());
            font.draw(guiBatch, "Actors amount: " + this.getActors().size, 0, this.getHeight() - 20);

            if (this.game.getActiveTeam().getActiveUnit() != null) {
                font.draw(guiBatch,
                        String.format("Active actor: %s XY[%f, %f]",
                                this.game.getActiveTeam().getActiveUnit().getName(),
                                this.game.getActiveTeam().getActiveUnit().getX(),
                                this.game.getActiveTeam().getActiveUnit().getY()),
                        0,
                        this.getHeight() - 40);
            }

            font.draw(guiBatch, String.format("Mouse position: screen [%d, %d], viewport %s", Gdx.input.getX(), game.getMap().getHeight() - Gdx.input.getY(), getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0))), 0, this.getHeight() - 60);
            font.draw(guiBatch, String.format("Camera coords [%s], zoom %f", getCamera().position.toString(), ((OrthographicCamera) getCamera()).zoom), 0, this.getHeight() - 80);
            font.draw(guiBatch, "Turn Time remaining: " + (game.getGameSettings().getTurnTime() - (int) game.getTurnLogic().getElapsedTime()), 0, this.getHeight() - 100);
            font.draw(guiBatch, "Time remaining: " + (game.getGameSettings().getMaxTime() - (int) game.getTurnLogic().getTotalTime()), 0, this.getHeight() - 120);
            font.draw(guiBatch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 0, this.getHeight() - 140);

            if (game.getTurnLogic().getState() == TurnState.GAMEOVER) {
                font.draw(guiBatch, "GAME OVER", this.getWidth() / 2, this.getHeight() / 2);
            }

            font.draw(guiBatch, "Wind: " + game.getMap().getWind().toString(), 0, this.getHeight() - 160);

            guiBatch.end();
        }
    }

    /**
     * Calculates an explosion at position X and Y using specified radius Sets
     * all solid pixels that were detected within the radius to false and
     * updates the terrain accordingly Iterates through all units that got hit
     *
     * @param x X-position of the explosion
     * @param y Y-position of the explosion (0 = bottom)
     * @param radius Length of the radius in pixels
     * @param damage Amount of damage the explosion does should it collide with a Unit
     * @param cluster Determines whether or not the explosion is a cluster explosion
     */
    public void explode(int x, int y, int radius, int damage, boolean cluster) {
        // calls radius destruction method in game map
        // which will update the boolean[][] and the terrain texture
        game.getMap().destroyRadius(x, y, radius);

        unitCollisionExplosion(x, radius, y, damage);

//        Gdx.app.postRunnable(() -> {
//            // adds effect to the list of effects to draw
//            PooledEffect effect = explosionEffectPool.obtain();
//            effect.setPosition(x, y);
//            effect.scaleEffect(radius/100);
//            effect.start();
//            particles.add(effect);
//            if (cluster) {
//               fireCluster(x, y);
//            }
//        });
        //game.getActiveTeam().getActiveUnit().getWeapon().getBullet().remove();
        // End the turn after unit has fired
        //game.endTurn();
    }

    /**
     * Spawns a cluster of projectiles
     * @param x x-coordinate of start position
     * @param y y-coordinate of start position
     */
    private void fireCluster(int x, int y) {
        Sprite partSprite = new Sprite(new Texture(Gdx.files.internal("part.png")));

        x = x - 3;
        for (int i = 0; i <= 4; i++) {
            Vector2 mousePos = new Vector2(x, y + 2);
            Vector2 position = new Vector2(x + i * 2, y);
            Projectile part = new Projectile(partSprite, tempParticleEffects);

            //fire: fire from, fire towards, power, wind, gravity, radius, damage
            part.fire(position, mousePos, 5, Vector2.Zero, 9.8, 10, 16);
            part.updateShot();
            addActor(part);
        }
    }

    /**
     * @param pEff sets the temporarily particle effect
     */
    public void setParticle(ParticleEffect pEff) {
        tempParticleEffects = pEff;

        explosionEffectPool = new ParticleEffectPool(tempParticleEffects, 1, 5);
        //tempParticleEffects.setEmittersCleanUpBlendFunction(false);  
    }

    private void unitCollisionExplosion(int x, int radius, int y, int damage) {
        List<Unit> collidedUnits = new ArrayList<>();
        
        for (int xPos = x - radius; xPos <= x + radius; xPos++) {
            for (int yPos = y - radius; yPos <= y + radius; yPos++) {
                // scan square area around radius to determine which pixels are within the radius
                if (Math.pow((double) xPos - x, 2.0d) + Math.pow((double) yPos - y, 2.0d) < radius * radius) {
                    // Iterate through every team and unit
                    // and add it to the list of collided Units if its bounding box contains explosion Xs and Ys
                    for (Team t : game.getTeams()) {
                        for (Unit u : t.getUnits()) {
                            if (!collidedUnits.contains(u)) {
                                if (u.getBounds().contains(xPos, yPos)) {
                                    collidedUnits.add(u);
                                    System.out.println("Collided with unit: " + u.getName());
                                }
                            }
                        }
                    }
                }
            }
        }

        // if any units have taken damage, we want to update their health and team healthbars
        if (!collidedUnits.isEmpty()) {
            
            // Iterates through all of the collided units and decreases their health
            // based on the damage caused by the explosion
            for (Unit u : collidedUnits) {
                u.decreaseHealth(damage);
            }
            
            guiStage.updateHealthBars();
        }
    }

    /**
     * Checks if a pixel at a given coordinate collides with a unit or not
     * @param x X-coordinate in world map
     * @param y Y-coordinate in world map
     * @return True if pixel if a collision was detected, false if not
     */
    public boolean isCollidedWithUnit(int x, int y) {

        for (Team t : game.getTeams()) {
            for (Unit u : t.getUnits()) {
                if (u.getBounds().contains(x, y)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Centers the camera on a specific Actor object
     *
     * @param actor Actor to center the camera on
     * @param keepFollowing If set to true the camera will continue following
     * the actor until a manual camera action gets called
     */
    public void setCameraFocusToActor(Actor actor, boolean keepFollowing) {
        OrthographicCamera cam = (OrthographicCamera) this.getCamera();

        // Sets the camera's X position based on the center of the specified actor
        cam.position.x = actor.getX() + actor.getWidth() / 2;
        //if(followVertically) { cam.position.y = actor.getY() + actor.getHeight() / 2; }
        clampCamera();
        cam.update();

        if (keepFollowing) {
            focusedActor = actor;
        }
    }

    /**
     * @author Jip Boesenkool Function which handles the use case to fire a
     * bullet, Get's called by mouseClick event. Trigger MUST BE inside game
     * stage, else mouse Click won't get the right coordinates.
     * @param screenX Unprojected mouse position.x
     * @param screenY Unprojected mouse position.y
     */
    public void fire( float screenX, float screenY ) {
        // get wind force from map
        Vector2 wind = game.getMap().getWind();

        // get gravity force from map (Const atm)
        double gravity = game.getMap().getGravityModifier();

        // get mouseposition to determine what direction the bullet must fly
        Vector2 mousePos = new Vector2(screenX, screenY);

        // trigger the fire method in unit which holds the gun
        (game.getActiveTeam().getActiveUnit()).fire(
                mousePos,
                wind,
                gravity
        );

        // make camera follow bullet
        focusedActor = (Actor)( game.getActiveTeam().getActiveUnit().getWeapon().getBullet() );
    }

    /**
     * Constraints the camera's projection to the bounds of the stage
     */
    private void clampCamera() {
        // Restrains the camera from leaving the bounds of the map
        OrthographicCamera cam = (OrthographicCamera) getCamera();

        if (cam.zoom > 1f) {
            cam.zoom = 1;
        }
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, game.getMap().getWidth() / cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, game.getMap().getWidth() - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, game.getMap().getHeight() - effectiveViewportHeight / 2f);
    }
}
