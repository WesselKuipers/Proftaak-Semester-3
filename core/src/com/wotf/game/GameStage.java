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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.TurnLogic.TurnState;
import com.wotf.game.classes.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of Stage that contains a game session
 *
 * @author Wessel
 */
public class GameStage extends Stage {

    //private WotFGame game;
    private final Game game;

    private final SpriteBatch batch;
    private final SpriteBatch guiBatch;
    private final BitmapFont font;

    private Texture terrainTexture;
    private final Texture backgroundTexture;
    private Pixmap pixmap;

    private Actor focusedActor; // if this is set to an actor
                                // have the camera follow it automatically, otherwise set it to null

    
    /**
     * Constructor for GameStage that initializes everything required for the game to run
     * Generates the initial texture based on the data contained within Map
     * @param game Game data structure to base initial variables on (settings, map, etc)
     */
    public GameStage(Game game) {
        super(new ScreenViewport());
        this.game = game;
        this.focusedActor = null;

        batch = new SpriteBatch();
        guiBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.BLACK);

        Pixmap bgPixmap = new Pixmap(this.game.getMap().getWidth(), this.game.getMap().getHeight(), Pixmap.Format.RGBA8888);
        System.out.println(game.getMap().getWidth() + "" + game.getMap().getHeight() + "");
        bgPixmap.setColor(Color.PURPLE);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        terrainTexture = game.getMap().getLandscapeTexture();
    }

    /**
     * Initial setup for the map used to add all team members to the list of
     * actors Spawns the units at random collision-free locations
     */
    public void init() {
        boolean[][] terrain = game.getMap().getTerrain();

        // Adds every unit as an actor to this stage
        for (Team team : game.getTeams()) {
            for (Unit unit : team.getUnits()) {

                // Spawns a unit in a random location (X axis)
                Vector2 ranLocation = new Vector2(MathUtils.random(0, game.getMap().getWidth() - unit.getWidth()), 80);
                unit.spawn(ranLocation);

                // Generates a random X position and attempts to find the highest collision-free position
                // and spawns the unit at that position, will continue looping until a position has been found
                boolean spawned = false;
                int posX = 0;
                int posY = 0;

                while (!spawned) {
                    posX = MathUtils.random(0 + (int) unit.getWidth(), (int) game.getMap().getWidth() - (int) unit.getWidth());
                    posY = -1;

                    // loops through terrain[x ± half its width][y] to check for collision free locations
                    for (int x = posX; x < posX + unit.getWidth(); x++) {
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
                        spawned = true;
                        break;
                    }
                }

                // since [posX][posY] is solid, we want to spawn the unit one pixel higher
                unit.spawn(new Vector2(posX, posY + 1));

                // formally adds the unit as an actor to the stage
                this.addActor(unit);
            }
        }

        getCamera().update();
        game.beginTurn();
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
     * Performs a single update step using the current game Calls act() method
     * within each actor in the stage and updates the camera
     */
    @Override
    public void act() {
        super.act();
        float delta = Gdx.graphics.getDeltaTime();

        game.getTurnLogic().update(delta);

        if (game.getTurnLogic().getElapsedTime() >= game.getGameSettings().getTurnTime()) {
            game.endTurn();
            game.beginTurn();
        }

        if(game.getTurnLogic().getTurnState() == TurnState.WITHDRAW) {
            if(game.getTurnLogic().getElapsedTime() >= game.getGameSettings().getWithdrawTime()) {
                game.beginTurn();
            }
        }
	
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

            // Unit selection
            case Keys.TAB:
                int selectedPlayerIndex = 0;
                int i = 0;

                Team activeTeam = game.getActiveTeam();

                for (Unit u : activeTeam.getUnits()) {
                    if (u == this.getKeyboardFocus()) {
                        selectedPlayerIndex = i + 1;
                    }
                    i++;
                }

                selectedPlayerIndex = (selectedPlayerIndex >= activeTeam.getUnits().size()) ? 0 : selectedPlayerIndex;

                for (Actor actor : this.getActors()) {
                    if (activeTeam.getUnit(selectedPlayerIndex) == actor) {
                        this.setKeyboardFocus(actor);
                        setCameraFocusToActor(actor, true);
                    }
                }
                break;

            // Debug key for killing current unit
            case Keys.G:
                game.getActiveTeam().getActiveUnit().decreaseHealth(100);
                game.endTurn();
                break;
        }

        clampCamera();
        cam.update();

        return super.keyDown(keyCode);
    }

    /**
     * Click event for the stage
     * Handles the logic for using the currently selected item
     * @param screenX X-coordinate of the cursor
     * @param screenY Y-coordinate of the cursor
     * @param pointer The pointer of the event
     * @param button The keycode of the button passed through
     * @return Whether the input was processed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 rel = getCamera().unproject(new Vector3(screenX, screenY, 0));
        
        if (game.getTurnLogic().getTurnState() == TurnState.PLAYING) {
            System.out.println(String.format("Touchdown event (%d, %d) button %d", screenX, screenY, button));
            System.out.println(String.format("Relative Touchdown event (%f, %f) button %d", rel.x, rel.y, button));

            if (button == Input.Buttons.LEFT) {
                System.out.println("Firing bullet");
                bulletLogic((int) rel.x, (int) rel.y);
            } else if (button == Input.Buttons.RIGHT) {
                explode((int) rel.x, (int) rel.y, 30, 0);
            }
            
            game.endTurn();
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

        batch.end();

        super.draw();
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
        font.draw(guiBatch, "Time remaining: " + (game.getGameSettings().getTurnTime() - (int) game.getTurnLogic().getElapsedTime()), 0, this.getHeight() - 100);
        font.draw(guiBatch, String.format("FPS: %d", Gdx.graphics.getFramesPerSecond()), 0, this.getHeight() - 120);
        if (game.getTurnLogic().getTurnState() == TurnState.GAMEOVER) {
            game.endTurn();
            game.getTurnLogic().gameOverState();
            font.draw(guiBatch, "GAME OVER", this.getWidth() / 2, this.getHeight() / 2);
        }
        
        
        guiBatch.end();
    }

    /**
     * Updates the terrain texture based on the terrain array in game map
     */
    public void updateTerrain() {
        boolean terrain[][] = game.getMap().getTerrain();

        pixmap = new Pixmap(terrain.length, terrain[0].length, Pixmap.Format.RGBA8888);

        if (!terrainTexture.getTextureData().isPrepared()) {
            terrainTexture.getTextureData().prepare();
        }

        Pixmap oldPixmap = terrainTexture.getTextureData().consumePixmap();

        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[1].length; y++) {
                if (terrain[x][y]) {
                    pixmap.drawPixel(x, y, oldPixmap.getPixel(x, y));
                }
            }
        }

        terrainTexture = new Texture(pixmap);
        oldPixmap.dispose();
    }

    /**
     * Calculates an explosion at position X and Y using specified radius
     * Sets all solid pixels that were detected within the radius to false and
     * updates the terrain accordingly Iterates through all units that got hit
     *
     * @param x X-position of the explosion
     * @param y Y-position of the explosion (0 = bottom)
     * @param radius Length of the radius in pixels
     * @param damage Amount of damage the explosion does should it collide with a Unit
     */
    public void explode(int x, int y, int radius, int damage ) {
        boolean[][] terrain = game.getMap().getTerrain();
        List<Unit> collidedUnits = new ArrayList<>();

        for (int xPos = x - radius; xPos <= x + radius; xPos++) {
            for (int yPos = y - radius; yPos <= y + radius; yPos++) {
                // scan square area around radius to determine which pixels to destroy
                if (Math.pow(xPos - x, 2) + Math.pow(yPos - y, 2) < radius * radius) {
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

                    // Check if the position is in bounds of the array, if not, skip this iteration
                    if (!(xPos >= 0 && yPos >= 0 && xPos < terrain.length && yPos < terrain[0].length)) {
                        continue;
                    }

                    // if the pixel at (xPos, yPos) is solid, set it to false
                    if (terrain[xPos][yPos]) {
                        // Additional checks can be done here
                        terrain[xPos][yPos] = false;
                    }
                }
            }
        }

        // TODO: Pass through correct/proper damage value?
        // Iterates through all of the collided units and decreases their health
        // based on the damage caused by the explosion
        for (Unit u : collidedUnits) {
            u.decreaseHealth( damage );
        }

        game.getMap().setTerrain(terrain);
        updateTerrain();
    }

    /**
     * Checks if a pixel at a given coordinate is set to solid or not
     *
     * @param x X-coordinate in world map
     * @param y Y-coordinate in world map
     * @return True if pixel is solid, false if not
     */
    public boolean isPixelSolid(int x, int y) {
        boolean[][] terrain = this.game.getMap().getTerrain();
        if (!(x >= 0 && y >= 0 && x < terrain.length && y < terrain[0].length)) {
            // Out of bounds
            return false;
        }
        return terrain[x][y];
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
     * @author Jip Boesenkool Function which handles the logic to fire a bullet,
     * Get's called by mouseClick event. Trigger MUST BE inside game stage, else
     * mouse Click wont get the right coordinates.
     * @param screenX Unprojected mouse position.x
     * @param screenY Unprojected mouse position.y
     */
    private void bulletLogic(int screenX, int screenY) {

        // TODO: get wind from turnLogic, Generate wind each turn.
        // Jip Boesenkool - 29-030'16
        Vector2 wind = new Vector2(0f, 0f);

        double gravity = game.getMap().getGravityModifier();

        //System.out.println( (game.getActiveTeam().getActiveUnit()).getName() );
        
        Vector2 mousePos = new Vector2(screenX, screenY);
        (game.getActiveTeam().getActiveUnit()).fire(
                mousePos,
                wind,
                gravity
        );
        
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