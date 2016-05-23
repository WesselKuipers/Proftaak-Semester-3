/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.utils.Align;
import com.wotf.game.classes.Game;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.TurnLogic;
import java.util.HashMap;

/**
 * Contains all the GUI elements of GameStage
 */
public final class GuiStage extends Stage {
    private final SpriteBatch batch;
    
    private final Skin skin;
    private final Game game;
    
    private Label totalTimeLabel;
    private Label turnTimeLabel;
    
    private HashMap<Team, Slider> healthBars;
    private HashMap<Team, Label> healthBarLabels;
    
    private Texture leftWindTexture;
    private Texture rightWindTexture;
    
    private TextureRegion leftWindRegion;
    private TextureRegion rightWindRegion;
    
    private Image windContainer;
    
    private Image weaponSelector;
    private Image weaponHotbar;
       
    private boolean gameOver = false;
    private boolean turnStarting = false;
    
    /**
     * Primary constructor of GuiStage which initializes all GUI components
     * @param game Game object to base the GUI on
     */
    public GuiStage(Game game) {
        super();
        
        batch = new SpriteBatch();
        
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.game = game;
        
        initializeTurnTimers();
        initializeTeamHealthBars();
        initializeWindIndicator();
        initializeWeaponSelection();
    }
    
    public void initializeTurnTimers() {
        // container for the total played time
        Texture totalTimeContainerTexture = new Texture(Gdx.files.internal("GUI/totalTimeContainer.png"));
        
        Image totalTimeContainer = new Image(totalTimeContainerTexture);
        totalTimeContainer.setTouchable(Touchable.disabled);
        totalTimeContainer.setPosition(15, this.getHeight() - totalTimeContainer.getHeight() - 10);
        
        // label containing the total time played
        totalTimeLabel = new Label("00:00", skin);
        totalTimeLabel.setPosition(totalTimeContainer.getX() + totalTimeContainer.getWidth() / 2 - 1, totalTimeContainer.getY() + totalTimeContainer.getHeight() / 2, Align.center);
        totalTimeLabel.setTouchable(Touchable.disabled);
        totalTimeLabel.setColor(Color.GRAY);
        
        // container for the remaining turn time
        Texture turnTimeContainerTexture = new Texture(Gdx.files.internal("GUI/timeContainer.png"));
        
        Image turnTimeContainer = new Image(turnTimeContainerTexture);
        turnTimeContainer.setTouchable(Touchable.disabled);
        turnTimeContainer.setPosition(15, this.getHeight() - turnTimeContainer.getHeight() - 30);
        
        // label containing the currently remaining turn time
        LabelStyle labelStyle = new LabelStyle(new BitmapFont(Gdx.files.internal("GUI/timeFont.fnt")), Color.WHITE);
        
        turnTimeLabel = new Label("30", labelStyle);
        turnTimeLabel.setPosition(turnTimeContainer.getX() + turnTimeContainer.getWidth() / 2 - 1, turnTimeContainer.getY() + turnTimeContainer.getHeight() / 2, Align.center);
        turnTimeLabel.setTouchable(Touchable.disabled);
        turnTimeLabel.setColor(Color.WHITE);
        
        this.addActor(totalTimeContainer);
        this.addActor(totalTimeLabel);
        
        this.addActor(turnTimeContainer);
        this.addActor(turnTimeLabel);
    }
    
    /**
     * Initializes the wind indicator on the GUI
     */
    public void initializeWindIndicator() {
        // sets up textures for the wind container
        windContainer = new Image(new Texture(Gdx.files.internal("GUI/windContainer.png")));
        windContainer.setPosition((int) (this.getWidth() / 2 - windContainer.getWidth() / 2), this.getHeight() - 30);
        
        leftWindTexture = new Texture(Gdx.files.internal("GUI/leftWindArrow.png"));
        leftWindTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
        rightWindTexture = new Texture(Gdx.files.internal("GUI/rightWindArrow.png"));
        rightWindTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        
        leftWindRegion = new TextureRegion(leftWindTexture);
        rightWindRegion = new TextureRegion(rightWindTexture);
        
        this.addActor(windContainer);
    }
    
    public void initializeTeamHealthBars() {
        healthBars = new HashMap<>();
        healthBarLabels = new HashMap<>();
        
        int y = game.getMap().getHeight() - 120;
        
        SliderStyle style = skin.get("default-horizontal-no-knob", SliderStyle.class);
        style.background.setMinHeight(15f);
        
        LabelStyle healthBarLabelStyle = new LabelStyle();
        healthBarLabelStyle.font = skin.getFont("default-font");
        
        for (Team t : game.getTeams()) {
            int totalHealth = t.getUnits().stream().mapToInt(o -> o.getHealth()).sum();
            
            Slider healthBar = new Slider(0, totalHealth, 1, false, style);
            
            healthBar.setColor(t.getColor());
            healthBar.setPosition(70, y);
            healthBar.setWidth(totalHealth / t.getUnits().size());
            
            LabelStyle labelStyle = new LabelStyle(healthBarLabelStyle);
            labelStyle.fontColor = t.getColor();
            
            Label healthBarLabel = new Label(t.getName(), labelStyle);
            healthBarLabel.setPosition(healthBar.getX() - 10 - healthBarLabel.getWidth(), healthBar.getY() - 3);

            this.addActor(healthBar);
            this.addActor(healthBarLabel);
            
            healthBars.put(t, healthBar);
            healthBarLabels.put(t, healthBarLabel);
            
            y -= 20;
        }
    }
    
    public void initializeWeaponSelection() {
       //weapon hotbar setup
        Texture weaponHotbarTexture = new Texture(Gdx.files.internal("GUI/itemBar.png"));
    
        weaponHotbar = new Image(weaponHotbarTexture);
        weaponHotbar.setTouchable(Touchable.disabled);
        weaponHotbar.setPosition(this.getWidth() - weaponHotbar.getWidth()-1, this.getHeight() - weaponHotbar.getHeight()-1);
        
        //weapon selector setup
        Texture weaponSelectorTexture = new Texture(Gdx.files.internal("GUI/selectedItem.png"));
    
        weaponSelector = new Image(weaponSelectorTexture);
        weaponSelector.setTouchable(Touchable.disabled);
        weaponSelector.setPosition(this.getWidth()-weaponHotbar.getWidth()-1,this.getHeight() - weaponSelector.getHeight());
        
        //add item icons to hotbar
         int weaponNR = 0;
       for(Item i : WEAPONS_ARMORY){
           Texture ProjectileTexture = i.getProjectileTexture();
           Image ProjectileImage = new Image(ProjectileTexture);
           ProjectileImage.setSize(25, 25);
           ProjectileImage.setPosition((this.getWidth()-weaponHotbar.getWidth()-1)+(40*weaponNR)+11, this.getHeight() - weaponHotbar.getHeight()-1+5);
           weaponNR++;
                      
           this.addActor(ProjectileImage);
       }
        
        //adding actors
        this.addActor(weaponHotbar);
        this.addActor(weaponSelector);
        
    }

    public void update() {
        // update the turn and total time
        updateTime();
        
        //weapon selector update mechanism
        int indexWeapon = 0;
        if(game.getTurnState()){
            indexWeapon = WEAPONS_ARMORY.indexOf(game.getActiveTeam().getActiveUnit().getWeapon());
        }
        UpdateSelectedWeaponHotbar(indexWeapon);
        
        Label healthBarLabel = healthBarLabels.get(game.getActiveTeam());
        
        // flashes active team to white and back to its original colour
        if (game.getTurnLogic().getElapsedTime() % 2 == 1) {           
            healthBarLabel.getStyle().fontColor = Color.WHITE;
        } else {
            healthBarLabel.getStyle().fontColor = game.getActiveTeam().getColor();
        }
        
        // if the turnstate is set to game over and the gameOver state hasn't been set
        if (game.getTurnLogic().getState() == TurnLogic.TurnState.GAMEOVER && !gameOver) {
            // show the game over message and set the gui status to game over
            showGameOverMessage();
            gameOver = true;
        }
        
        // when the turnstate gets set to withdraw, it means a new turn is starting soon
        if (game.getTurnLogic().getState() == TurnLogic.TurnState.WITHDRAW) {
            turnStarting = true;
        }
        
        // at the beginning of a turn, we want to update the wind indicator to show the new wind
        if (turnStarting && game.getTurnLogic().getState() == TurnLogic.TurnState.PLAYING) {
            turnStarting = false;
            updateWind();
        }
        
        // scrolls the wind indicators to indicate movement in this direction
        leftWindRegion.scroll(0.05f, 0);
        rightWindRegion.scroll(-0.05f, 0);
    }
    
    public void UpdateSelectedWeaponHotbar(int weaponNR){
       weaponSelector.setPosition((this.getWidth()-weaponHotbar.getWidth()-1)+(40*weaponNR),this.getHeight() - weaponSelector.getHeight());
    }
    
    /**
     * Sets the remaining time of this turn and the total time on the GUI
     */
    public void updateTime() {
        int totalTime = (int) game.getTurnLogic().getTotalTime();
        int elapsedTime = (int) game.getTurnLogic().getElapsedTime();
        
        Integer turnTime = game.getGameSettings().getTurnTime() - elapsedTime;
        turnTimeLabel.setText(String.format("%02d", turnTime));
        
        totalTimeLabel.setText(String.format("%02d:%02d", totalTime / 60, totalTime % 60));
    }
    
    /**
     * Shows the game over message on the GUI stage
     */
    public void showGameOverMessage() {
        Label gameOverLabel = new Label("Game over", skin);
        gameOverLabel.setPosition(this.getWidth() / 2, this.getHeight() / 2);
        
        this.addActor(gameOverLabel);
    }
    
    /**
     * Updates the health bars of every team
     */
    public void updateHealthBars() {
        for (int i = 0; i < game.getTeams().size(); i++) {
            Team t = game.getTeam(i);
            Slider healthBar = healthBars.get(t);
            Label label = healthBarLabels.get(t);
            
            int totalHealth = t.getUnits().stream().mapToInt(o -> o.getHealth()).sum();
            
            if (totalHealth == 0) {
                label.setVisible(false);
                healthBar.setVisible(false);
                return;
            }
            
            // if the health bar needs to be updated
            if (totalHealth / t.getUnits().size() != healthBar.getWidth()) {
                // set the new width
                healthBar.setWidth(totalHealth / t.getUnits().size());
            }
        }
    }
    
    /**
     * Updates the width of the wind arrows on the wind indicator
     * based on the current wind
     */
    public void updateWind() {
        Vector2 wind = game.getMap().getWind();
        int maxWidth = 86; // width of each side of the wind container in pixels
        int width = Math.round(maxWidth * (Math.abs(wind.x) / 10));
                
        // if wind is 0, we want to hide both wind arrows
        if (wind.x == 0) {
            leftWindRegion.setRegion(0, 0, 0, 0);
            rightWindRegion.setRegion(0, 0, 0, 0);
        } else if (wind.x < 0) { // if wind is negative, set width of left arrow
                                 // and hide the right arrow                     
            rightWindRegion.setRegion(0, 0, 0, 0);
            leftWindRegion.setRegion(0, 0, width, leftWindTexture.getHeight());
        } else {
            leftWindRegion.setRegion(0, 0, 0, 0);
            rightWindRegion.setRegion(0, 0, width, rightWindTexture.getHeight());
        }
    }

    /**
     * Calls draw method on all actors and draws the wind indicator
     */
    @Override
    public void draw() {
        super.draw();
        
        batch.begin();
        
        if (leftWindRegion.getRegionWidth() > 0) {
            batch.draw(leftWindRegion, (int) (windContainer.getX() + windContainer.getWidth() / 2 - leftWindRegion.getRegionWidth() - 1), windContainer.getY() + 2);
        }
        
        if (rightWindRegion.getRegionWidth() > 0) {
            batch.draw(rightWindRegion, (int) (windContainer.getX() + windContainer.getWidth() / 2 + 2), windContainer.getY() + 2);
        }
        
        batch.end();
    }
}