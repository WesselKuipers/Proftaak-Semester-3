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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.classes.Game;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.classes.Projectile;
import com.wotf.game.classes.Team;
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
    private Game game;
    private World world;
    private Box2DDebugRenderer b2dr;

    private SpriteBatch batch;
    private SpriteBatch guiBatch;
    private BitmapFont font;

    private Texture terrainTexture;
    private Texture backgroundTexture;
    private Pixmap pixmap;
    private Body floorBody;
    //private OrthographicCamera camera;
    private float accumulator = 0f;
    private Unit activeUnit;

    public final float PIXELS_TO_METERS = 100;
    private final float TIME_STEP = 1 / 300f;
    private Actor focusedActor; // if this is set to an actor
                                // have the camera follow it automatically, otherwise set it to null

    public GameStage(Game game) {
        super(new ScreenViewport());
        this.game = game;
        this.world = new World(new Vector2(0, -10f), true);
        this.b2dr = new Box2DDebugRenderer();
        this.focusedActor = null;

        batch = new SpriteBatch();
        guiBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        
        //floor
        createGroundFloor(world);

        Pixmap bgPixmap = new Pixmap(this.game.getMap().getWidth(), this.game.getMap().getHeight(), Pixmap.Format.RGBA8888);
        System.out.println(game.getMap().getWidth() + "" + game.getMap().getHeight() + "");
        bgPixmap.setColor(Color.PURPLE);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        
        terrainTexture = game.getMap().getLandscapeTexture();
        //updateTerrain();
    }

    /**
     * Initial setup for the map used to add all team members to the list of actors
     * Spawns the units at random locations
     */
    public void init() {
        // Adds every unit as an actor to this stage

        int count = 1;
        for (Team team : game.getTeams()) {
            for (Unit unit : team.getUnits()) {
                // Retrieves the currently attached sprite of this unit,
                // adds a color tint to it and assigns it back to the unit
                //Sprite unitSprite = unit.getSprite();
                //unitSprite.setColor(team.getColor());
                //unit.setSprite(unitSprite);

                // TODO: Check if spot is actually free/usable
                // Spawns a unit in a random location (X axis)
                Vector2 ranLocation = new Vector2(MathUtils.random(0, game.getMap().getWidth() - unit.getWidth()), 80);
                unit.spawn(ranLocation);
                unit.setWorld(world);
                if (count == 1) {
                    unit.defineBody(world);
                    //camera.position.set(unit.getPosition().x, unit.getPosition().y, 0);
                    //camera.update();
                    activeUnit = unit;
                    Item weapon = WEAPONS_ARMORY.get(1);
                    activeUnit.selectWeapon( weapon );
                }
                this.addActor(unit);
                count++;
            }
        }
        
        getCamera().update();
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public void act() {
        super.act();
        float delta = Gdx.graphics.getDeltaTime();

        game.getTurnLogic().update(delta);
        if (game.getTurnLogic().getElapsedTime() >= game.getGameSettings().getTurnTime()) {
            game.endTurn();
         }
	
        if(focusedActor != null) {
            setCameraFocusToActor(focusedActor, false);
        } else {
            getCamera().update();
        }
        
        if (activeUnit.b2body != null) {
            activeUnit.setPosition(activeUnit.b2body.getPosition().x * 95f, activeUnit.b2body.getPosition().y  * 50f);
            activeUnit.sprite.setPosition(activeUnit.b2body.getPosition().x * 95f, activeUnit.b2body.getPosition().y * 50f);
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
            && (activeUnit.getState() == Unit.State.STANDING || activeUnit.getState() == Unit.State.RUNNING)
            && activeUnit.b2body.getLinearVelocity().x <= 2) {
            // TODO: Refactor using a speed or velocity field in activeUnit
            // and move all unit controls to either GameStage or Unit
            // so that they can all be accessed from the same place
            activeUnit.b2body.setLinearVelocity(0.5f, 0f);
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)
            && (activeUnit.getState() == Unit.State.STANDING || activeUnit.getState() == Unit.State.RUNNING)
            && activeUnit.b2body.getLinearVelocity().x >= -2) {
            activeUnit.b2body.setLinearVelocity(-0.5f,0f);
        }

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

    }
    
    @Override
    public boolean keyDown(int keyCode) {

        OrthographicCamera cam = (OrthographicCamera) getCamera();

        // Temporary camera controls
        switch (keyCode) {
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
            case Keys.PLUS:
                cam.zoom -= 0.05f;
                break;
            case Keys.MINUS:
                cam.zoom += 0.05f;
                break;
            case Keys.ENTER:
                cam.zoom = 1;
                break;
            case Keys.TAB:
                int selectedPlayerIndex = 0;
                int i = 0;
                
                Team activeTeam = game.getActiveTeam();
                
                for (Unit u : activeTeam.getUnits()) {
                    if(u == this.getKeyboardFocus()) {
                        selectedPlayerIndex = i + 1;
                    }
                    i++;
                }
                
                selectedPlayerIndex = (selectedPlayerIndex >= activeTeam.getUnits().size()) ? 0 : selectedPlayerIndex;
                
                for (Actor a : this.getActors()) {
                    if(activeTeam.getUnit(selectedPlayerIndex) == a) {
                        this.setKeyboardFocus(a);
                        setCameraFocusToActor(a, true);
                    }
                }
                break;
        }

        clampCamera();
        cam.update();
        return super.keyDown(keyCode);
    }
    
    /**
    * @Author  Jip Boesenkool
    * Function which handles the bullets on button click.
    * @return
    */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 rel = getCamera().unproject( new Vector3(screenX, screenY, 0) );
        System.out.println(String.format("Touchdown event (%d, %d) button %d", screenX, screenY, button));
        System.out.println(String.format("Relative Touchdown event (%f, %f) button %d", rel.x, rel.y, button));
        
        if(button == Input.Buttons.LEFT) {
            System.out.println("Firing bullet");
            bulletLogic((int)rel.x, (int)rel.y);
        } else if (button == Input.Buttons.RIGHT) {
            explode((int) rel.x, (int) rel.y, 30);
        }
        
        return true;
    }
    
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
        world.step(1f / 60f, 6, 2);
        //batch.setProjectionMatrix(camera.combined);
        b2dr.render(world, batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0));

        guiBatch.begin();

        font.draw(guiBatch, "Debug variables:", 0, this.getHeight());
        font.draw(guiBatch, "Actors amount: " + this.getActors().size, 0, this.getHeight() - 20);
        font.draw(guiBatch,
                String.format("Active actor: %s XY[%f, %f]",
                        this.getKeyboardFocus().getName(),
                        this.getKeyboardFocus().getX(),
                        this.getKeyboardFocus().getY()),
                0,
                this.getHeight() - 40);
        font.draw(guiBatch, String.format("Mouse position: screen [%d, %d], viewport %s", Gdx.input.getX(), game.getMap().getHeight() - Gdx.input.getY(), getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0))), 0, this.getHeight() - 60);
        font.draw(guiBatch, String.format("Camera coords [%s], zoom %f", getCamera().position.toString(), ((OrthographicCamera) getCamera()).zoom), 0, this.getHeight() - 80);
        font.draw(guiBatch, "Time remaining: " + (game.getGameSettings().getTurnTime() - (int)game.getTurnLogic().getElapsedTime()), 0, this.getHeight() - 100);

        font.draw(guiBatch, String.format("Active body: %s XY[%f, %f]",
                this.getKeyboardFocus().getName(),
                activeUnit.b2body.getPosition().x,
                activeUnit.b2body.getPosition().y),
                0,
                this.getHeight() - 120);
        guiBatch.end();
    }

    /**
     * Updates the terrain texture based on the terrain array in game map
     */
    public void updateTerrain() {
        boolean terrain[][] = game.getMap().getTerrain();
        
        pixmap = new Pixmap(terrain.length, terrain[0].length, Pixmap.Format.RGBA8888);

        if(!terrainTexture.getTextureData().isPrepared()) {
            terrainTexture.getTextureData().prepare();
        }
        
        Pixmap oldPixmap = terrainTexture.getTextureData().consumePixmap();
        
        for (int x = 0; x < terrain.length; x++) {
            for (int y = 0; y < terrain[1].length; y++) {
                if (terrain[x][y]) {
                    pixmap.drawPixel(x, y, oldPixmap.getPixel(x,y));
                }
            }
        }
        
        terrainTexture = new Texture(pixmap);
        oldPixmap.dispose();
    }

    /**
     * Calculates an explosion at position X and Y using specified radius
     * Sets all solid pixels that were detected within the radius to false and updates the terrain accordingly
     * Iterates through all units that got hit (currently unused)
     * @param x X-position of the explosion
     * @param y Y-position of the explosion (0 = bottom)
     * @param radius Length of the radius in pixels
     */
    public void explode(int x, int y, int radius) {
        boolean[][] terrain = game.getMap().getTerrain();
        List<Unit> collidedUnits = new ArrayList<>();
        
        for (int xPos = x - radius; xPos <= x + radius; xPos++) {
            for (int yPos = y - radius; yPos <= y + radius; yPos++) {
                // scan square area around radius to determine which pixels to destroy
                if (Math.pow(xPos - x, 2) + Math.pow(yPos - y, 2) < radius * radius) {
                    // Iterate through every team and unit
                    // and add it to the list of collided Units if its bounding box contains explosion Xs and Ys
                    for(Team t : game.getTeams()) {
                        for(Unit u : t.getUnits()) {
                            if(!collidedUnits.contains(u)) {
                                if(u.getBounds().contains(xPos, yPos)) {
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
                        // TODO: Spawn cool explosion effect here
                    }
                }
            }
        }
        
        // TODO: Iterate through collided units and call its damage/hit method

        game.getMap().setTerrain(terrain);
        updateTerrain();
    }

    public World getWorld() {
        return world;
    }
    
    /**
     * Checks if a pixel at a given coordinate is set to solid or not
     * @param x X-coordinate in world map
     * @param y Y-coordinate in world map
     * @return True if pixel is solid, false if not
     */
    public boolean isPixelSolid(int x, int y) {
        return this.game.getMap().getTerrain()[x][y];
    }
    
    /**
     * Centers the camera on a specific Actor object
     * @param actor Actor to center the camera on
     * @param keepFollowing If set to true the camera will continue following
     *                      the actor until a manual camera action gets called
     */
    public void setCameraFocusToActor(Actor actor, boolean keepFollowing) {
        OrthographicCamera cam = (OrthographicCamera) this.getCamera();
        
        cam.position.x = actor.getX() + actor.getWidth() / 2;
        //if(followVertically) { cam.position.y = actor.getY() + actor.getHeight() / 2; }
        clampCamera();
        cam.update();
        
        if(keepFollowing) {
            focusedActor = actor;
        }
    }

    public void createGroundFloor(World world) {
        BodyDef floorDef = new BodyDef();
        floorDef.type = BodyDef.BodyType.StaticBody;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight() / PIXELS_TO_METERS
                / PIXELS_TO_METERS;
        floorDef.position.set(10f, 0.84f);

        FixtureDef FloorFixDeff = new FixtureDef();
        EdgeShape FloorEdgeShape = new EdgeShape();

        FloorEdgeShape.set(-w / 2, -h / 2, w / 2, -h / 2);
        FloorFixDeff.shape = FloorEdgeShape;
        floorBody = world.createBody(floorDef);
        floorBody.createFixture(FloorFixDeff);
        FloorEdgeShape.dispose();
    }

    /**
    * @author Jip Boesenkool
    * Function which handles the logic to fire a bullet, Get's called by mouseClick event.
    * Trigger MUST BE inside game stage, else mouse Click wont get the right coordinates.
    * @param screenX Unprojected mouse position.x
    * @param screenY Unprojected mouse position.y
    */
    private void bulletLogic(int screenX, int screenY) {
        // TODO: Fix this! data should be from data structure not from actor.
        // Jip Boesenkool - 29-030'16
        Vector2 unitPosition = new Vector2(
           this.activeUnit.getX(),
           this.activeUnit.getY()
        );
         // TODO: Get correct force from weapon
        // Jip Boesenkool - 29-030'16
        float force = 20f;
        // TODO: get wind from turnLogic, Generate wind each turn.
        // Jip Boesenkool - 29-030'16
        Vector2 wind = new Vector2(0f, 0f);
        double gravity = game.getMap().getGravityModifier();
        //spawn bullet and add to scene           
        Projectile bullet = new Projectile(new Sprite(new Texture(Gdx.files.internal("BulletBill.png"))));
        bullet.updateShot();
        this.addActor(bullet);
    }
    
    private void clampCamera() {
        // Retrains the camera from leaving the bounds of the map
        // Uncomment this if you want an unrestrained camera
        OrthographicCamera cam = (OrthographicCamera) getCamera();
        
        if (cam.zoom > 1f) cam.zoom = 1;
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, game.getMap().getWidth() / cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, game.getMap().getWidth() - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, game.getMap().getHeight() - effectiveViewportHeight / 2f);
    }
     
     
}
