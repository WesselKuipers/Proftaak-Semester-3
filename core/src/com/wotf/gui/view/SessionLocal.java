/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import com.wotf.game.classes.Team;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen that shows the local sessions menu
 *
 * @author Gebruiker
 */
public class SessionLocal implements Screen {

    private List teams;
    private final WotFGame game;
    private Stage stage;
    private Skin skin;
    private final ArrayList<Team> teamList;
    private final GameSettings gameSettings;
    private Image map1;
    private SelectBox unitbox;
    private Session sessionLocal;
    private Player defaultPlayer;

    /**
     * Constructor of SessionLocal, initializes teamList and gameSetting
     *
     * @param game
     */
    public SessionLocal(WotFGame game) {
        this.game = game;
        gameSettings = new GameSettings();
        teamList = new ArrayList<>();
        defaultPlayer = new Player(getLocalhost(), "defaultPlayer");
        defaultPlayer.setId(0);
    }

    /**
     * Returns a NinePatch object based on the given image
     *
     * @param fname Filename of the image
     * @param left Left edge of NinePatch
     * @param right Right edge of NinePatch
     * @param bottom Bottom edge of NinePatch
     * @param top Top edge of NinePatch
     * @return Resulting NinePatch
     */
    private NinePatch getNinePatch(String fname, int left, int right, int bottom, int top) {

        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        // get the variables for each corner, because there can be a difference for each different table.
        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), left, right, bottom, top);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        //System.out.println(Gdx.files.internal("maps"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        teams = new List(skin);

        // Alle teams en labels hiervoor.
        Table teamstable = new Table();
        Table mapstable = new Table();
        Table settingstable = new Table();
        Table teamselecttable = new Table();
        teamstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 150, 150, 160, 160)));
        mapstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 220, 220, 160, 160)));
        teamselecttable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 100, 100, 160, 160)));

        Label selectteamlabel = new Label("Team selection", skin);
        teamselecttable.add(selectteamlabel).padBottom(15);
        teamselecttable.row();
        TextButton btnteamalpha = new TextButton("Alpha", skin); // Use the initialized skin
        btnteamalpha.setColor(Color.BLUE);
        btnteamalpha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teamalpha = new Team("Alpha", Color.BLUE);
                teamalpha.setPlayer(defaultPlayer);
                teamalpha.setColorname(teamalpha.getColor().toString());

                int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());
                addUnitsSingleTeam(selectedunitcount, teamalpha);

                btnteamalpha.setTouchable(Touchable.disabled);
                btnteamalpha.setColor(Color.LIGHT_GRAY);
            }
        });

        teamselecttable.add(btnteamalpha).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton btnteambeta = new TextButton("Beta", skin); // Use the initialized skin
        btnteambeta.setColor(Color.CORAL);
        btnteambeta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teambeta = new Team("Beta", Color.CORAL);
                teambeta.setPlayer(defaultPlayer);
                teambeta.setColorname(teambeta.getColor().toString());

                int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());
                addUnitsSingleTeam(selectedunitcount, teambeta);

                btnteambeta.setTouchable(Touchable.disabled);
                btnteambeta.setColor(Color.LIGHT_GRAY);
            }
        });
        teamselecttable.add(btnteambeta).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton btnteamgamma = new TextButton("Gamma", skin); // Use the initialized skin
        btnteamgamma.setColor(Color.GREEN);
        btnteamgamma.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teamgamma = new Team("Gamma", Color.GREEN);
                teamgamma.setPlayer(defaultPlayer);
                teamgamma.setColorname(teamgamma.getColor().toString());

                int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());
                addUnitsSingleTeam(selectedunitcount, teamgamma);

                btnteamgamma.setTouchable(Touchable.disabled);
                btnteamgamma.setColor(Color.LIGHT_GRAY);
            }
        });
        teamselecttable.add(btnteamgamma).width(150).height(50);
        teamselecttable.setWidth(200);
        teamselecttable.setHeight(320);
        teamselecttable.setPosition(500, 360);
        stage.addActor(teamselecttable);

        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);

        Label iplabel = new Label("IP :", skin);
        settingstable.add(iplabel).width(120);
        Label ipvallabel = new Label("127.0.0.1", skin);
        settingstable.add(ipvallabel).width(180);
        settingstable.row();

        Object[] turntimevals = new Object[6];
        turntimevals[0] = "10";
        turntimevals[1] = "20";
        turntimevals[2] = "30";
        turntimevals[3] = "40";
        turntimevals[4] = "50";
        turntimevals[5] = "60";
        Label turntimelabel = new Label("Turn Time :", skin);
        settingstable.add(turntimelabel).width(120);
        SelectBox turntimebox = new SelectBox(skin);
        turntimebox.setItems(turntimevals);
        turntimebox.setSelectedIndex(3);
        turntimebox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                gameSettings.setTurnTime(Integer.parseInt(turntimebox.getSelected().toString()));
            }
        });
        settingstable.add(turntimebox).width(180);
        settingstable.row();

        Object[] physicsvals = new Object[2];
        physicsvals[0] = "True";
        physicsvals[1] = "False";
        Label physicslabel = new Label("Physics :", skin);
        settingstable.add(physicslabel).width(120);
        SelectBox physicsbox = new SelectBox(skin);
        physicsbox.setItems(physicsvals);
        physicsbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSettings.setPhysics(Boolean.parseBoolean(physicsbox.getSelected().toString()));
            }
        });
        settingstable.add(physicsbox).width(180);
        settingstable.row();

        Object[] weaponsvals = new Object[3];
        weaponsvals[0] = "All Weapons";
        weaponsvals[1] = "Non-Explosive";
        weaponsvals[2] = "Grenades Only";
        Label weaponslabel = new Label("Weapons :", skin);
        settingstable.add(weaponslabel).width(120);
        SelectBox weaponsbox = new SelectBox(skin);
        weaponsbox.setItems(weaponsvals);
        settingstable.add(weaponsbox).width(180);
        settingstable.row();

        Object[] timervals = new Object[3];
        timervals[0] = "60";
        timervals[1] = "30";
        timervals[2] = "10";
        Label timerlabel = new Label("Timer :", skin);
        settingstable.add(timerlabel).width(120);
        SelectBox timerbox = new SelectBox(skin);
        timerbox.setItems(timervals);
        timerbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameSettings.setMaxTime(Integer.parseInt(timerbox.getSelected().toString()));
            }
        });
        settingstable.add(timerbox).width(180);
        settingstable.row();

        Object[] unitvals = new Object[4];
        unitvals[0] = "1";
        unitvals[1] = "2";
        unitvals[2] = "3";
        unitvals[3] = "4";
        Label unitlabel = new Label("Units :", skin);
        settingstable.add(unitlabel).width(120);
        unitbox = new SelectBox(skin);
        unitbox.setItems(unitvals);
        unitbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());
                gameSettings.setMaxUnitCount(selectedunitcount);
                refreshUnitsForTeam(selectedunitcount);

            }
        });
        settingstable.add(unitbox).width(180);

        settingstable.setWidth(300);
        settingstable.setHeight(200);
        settingstable.setPosition(30, 110);
        stage.addActor(settingstable);

        ArrayList<String> mapslist = new ArrayList<>();
        FileHandle dirHandle = Gdx.files.internal("maps");
        for (FileHandle entry : dirHandle.list()) {
            mapslist.add(entry.toString());
        }

        map1 = new Image(new Texture(mapslist.get(0)));
        map1.setPosition(20, 70);
        map1.setWidth(400);
        map1.setHeight(230);
        mapstable.addActor(map1);
        SelectBox chooseMap = new SelectBox(skin);
        chooseMap.setItems(mapslist.toArray());
        chooseMap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapstable.removeActor(map1);
                map1 = new Image(new Texture(mapslist.get(chooseMap.getSelectedIndex())));
                map1.setPosition(20, 70);
                map1.setWidth(400);
                map1.setHeight(230);
                map1.invalidate();
                mapstable.addActor(map1);
                // Table image is not updating for some reason. Does it need to be redrawn? Or the Stage?
            }
        });
        chooseMap.setWidth(400);
        chooseMap.setPosition(20, 20);
        mapstable.addActor(chooseMap);
        mapstable.setPosition(30, 360);
        mapstable.setHeight(320);
        mapstable.setWidth(440);
        stage.addActor(mapstable);

        Label teamslabel = new Label("Teams", skin);
        teamstable.setPosition(730, 360);
        teamstable.add(teamslabel);
        teamstable.row();
        teamstable.add(teams).width(200);
        teamstable.setWidth(300);
        teamstable.setHeight(320);
        stage.addActor(teamstable);

        TextButton start = new TextButton("Start", skin); // Use the initialized skin
        start.setColor(Color.BLACK);
        start.setWidth(300);
        start.setHeight(60);
        start.setPosition(590, 180);
        stage.addActor(start);
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (teamList.size() < 2) {
                    return;
                }

                // Selected MaxTime to an integer.
                gameSettings.setMaxTime(Integer.parseInt(timerbox.getSelected().toString()));

                // Selected TurnTime to an integer.
                gameSettings.setTurnTime(Integer.parseInt(turntimebox.getSelected().toString()));

                gameSettings.setIsLocal(true);
                // Create the map
                Map map = new Map(chooseMap.getSelected().toString().substring(5));

                try {
                    sessionLocal = new Session(defaultPlayer, "localHost", gameSettings);
                    sessionLocal.addPlayer(defaultPlayer);
                } catch (RemoteException ex) {
                    Gdx.app.log("SessionLocal", ex.getMessage());
                }

                game.setScreen(new GameEngine(game, gameSettings, map, sessionLocal, defaultPlayer));
            }
        });

        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(590, 110);
        stage.addActor(exit);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

    }
    
    /**
     * Make new units for each team.
     * @param selectedunitcount
     */
    public void refreshUnitsForTeam(int selectedunitcount) {
        // For each team in the list remove all the units first and remove it from the gamesettings.
        for (Team teamV : teamList) {
            gameSettings.removeTeam(teamV);
            teamV.removeAllUnits();
            
            // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
            for (int i = 0; i < selectedunitcount; i++) {
                teamV.addUnit(teamV.getName() + Integer.toString(i), 100);
            }
            
            gameSettings.addTeam(teamV);
        }
    }
    
    /**
     * Create units for a single team based on how many units there should be created.
     * 
     * @param selectedunitcount amount of units to create
     * @param team selected
     */
    public void addUnitsSingleTeam(int selectedunitcount, Team team) {
        // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
        for (int i = 0; i < selectedunitcount; i++) {
            team.addUnit(team.getName() + Integer.toString(i), 100);
        }
        teamList.add(team);
        gameSettings.addTeam(team);
        teams.setItems(teamList.toArray());
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor((float) 122 / 255, (float) 122 / 255, (float) 122 / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /**
     * Called when the {@link Application} is resized. This can happen at any
     * point during a non-paused state but will never happen before a call to
     * {@link #create()}.
     *
     * @param width the new width in pixels
     * @param height the new height in pixels
     */
    @Override
    public void resize(int width, int height) {
        // Passes the new width and height to the viewport
        stage.getViewport().update(width, height);
    }

    /**
     * Called when the {@link Application} is paused, usually when it's not
     * active or visible on screen. An Application is also paused before it is
     * destroyed.
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the {@link Application} is resumed from a paused state,
     * usually when it regains focus.
     */
    @Override
    public void resume() {
    }

    /**
     * Called when this screen is no longer the current screen for a
     * {@link Game}.
     */
    @Override
    public void hide() {
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
    }

    private String getLocalhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Gdx.app.log("Session", ex.getMessage());
        }
        return null;
    }
}
