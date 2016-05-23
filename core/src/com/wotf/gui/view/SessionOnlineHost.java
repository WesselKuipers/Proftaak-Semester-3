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
import com.wotf.game.WotFGame;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import com.wotf.game.classes.SessionManager;
import com.wotf.game.classes.Team;
import com.wotf.game.database.PlayerContext;
import com.wotf.game.database.SessionContext;
import com.wotf.game.database.SessionPlayerContext;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen that shows the local sessions menu
 *
 * @author Gebruiker
 */
public class SessionOnlineHost implements Screen {

    private List teams;
    private List players;
    private final WotFGame game;
    private Stage stage;
    private Skin skin;
    private final ArrayList<Team> teamList;
    private ArrayList<Player> playerList;
    private final GameSettings gameSettings;
    private Image map1;
    private Session session;
    private Registry registry;
    private ISessionSettings regsettings;
    private SessionManager manager;
    private Player player;
    private Timer timer;
    private SelectBox maxplayerbox;
    private SelectBox unitbox;

    /**
     * Constructor of SessionLocal, initializes teamList and gameSetting
     *
     * @param game
     * @param session
     */
    public SessionOnlineHost(WotFGame game, Session session, Player player) throws RemoteException {
        this.game = game;
        this.session = session;
        this.player = player;
        gameSettings = new GameSettings();
        teamList = new ArrayList<>();
        playerList = new ArrayList<>();
        addPlayerToDB();
        playerList = getPlayersOfSession(session);
    }

    public ArrayList<Player> getPlayersOfSession(Session session) {
        try {
            SessionPlayerContext sp = new SessionPlayerContext();
            return sp.getPlayersFromSession(session);
        } catch (SQLException ex) {
            Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addPlayerToDB() {
        SessionPlayerContext sp = new SessionPlayerContext();
        sp.insert(player, session);
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
        players = new List(skin);

        // Alle teams en labels hiervoor.
        Table teamstable = new Table();
        Table mapstable = new Table();
        Table settingstable = new Table();
        Table teamselecttable = new Table();
        Table playerstable = new Table();
        teamstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 130, 130, 160, 160)));
        mapstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 220, 220, 160, 160)));
        teamselecttable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 100, 100, 160, 160)));
        playerstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 125, 125, 160, 160)));

        Label hostlabel = new Label(session.getHost().getIp() + " " + session.getHost().getName(), skin);
        hostlabel.setPosition(30, 30);
        stage.addActor(hostlabel);

        players.setItems(playerList.toArray());
        playerstable.add(players);
        playerstable.setWidth(250);
        playerstable.setHeight(320);
        playerstable.setPosition(1020, 360);
        stage.addActor(playerstable);

        Label selectteamlabel = new Label("Team selection", skin);
        teamselecttable.add(selectteamlabel).padBottom(15);
        teamselecttable.row();
        TextButton btnteamalpha = new TextButton("Alpha", skin); // Use the initialized skin
        btnteamalpha.setName("Alpha");
        btnteamalpha.setColor(Color.BLUE);
        btnteamalpha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teamalpha = new Team("Alpha", Color.BLUE);
                        teamalpha.setColorname(teamalpha.getColor().toString());
                        Player selectedplayer = (Player) players.getSelected();
                        int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());

                        ArrayList<Team> teamlistcopy = new ArrayList<>(teamList);
                        for (Team fteam : teamlistcopy) {
                            if (fteam.getPlayer().getName().equals(selectedplayer.getName())) {
                                fteam.setPlayer(null);

                                TextButton teamtb = (TextButton) stage.getRoot().findActor(fteam.getName());
                                teamtb.setTouchable(Touchable.enabled);
                                teamtb.setColor(fteam.getColor());

                                teamList.remove(fteam);
                                gameSettings.removeTeam(fteam);
                            }
                        }

                        teamalpha.setPlayer(selectedplayer);

                        addUnitsSingleTeam(selectedunitcount, teamalpha);
                        
                        session.setGameSettings(gameSettings);

                        btnteamalpha.setTouchable(Touchable.disabled);
                        btnteamalpha.setColor(Color.LIGHT_GRAY);
                    } catch (RemoteException ex) {
                        Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        teamselecttable.add(btnteamalpha).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton btnteambeta = new TextButton("Beta", skin); // Use the initialized skin
        btnteambeta.setName("Beta");
        btnteambeta.setColor(Color.CORAL);
        btnteambeta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teambeta = new Team("Beta", Color.CORAL);
                        teambeta.setColorname(teambeta.getColor().toString());
                        Player selectedplayer = (Player) players.getSelected();
                        int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());

                        ArrayList<Team> teamlistcopy = new ArrayList<>(teamList);
                        for (Team fteam : teamlistcopy) {
                            if (fteam.getPlayer().getName().equals(selectedplayer.getName())) {
                                fteam.setPlayer(null);

                                TextButton teamtb = (TextButton) stage.getRoot().findActor(fteam.getName());
                                teamtb.setTouchable(Touchable.enabled);
                                teamtb.setColor(fteam.getColor());

                                teamList.remove(fteam);
                                gameSettings.removeTeam(fteam);
                            }
                        }

                        teambeta.setPlayer(selectedplayer);

                        addUnitsSingleTeam(selectedunitcount, teambeta);

                        session.setGameSettings(gameSettings);

                        btnteambeta.setTouchable(Touchable.disabled);
                        btnteambeta.setColor(Color.LIGHT_GRAY);

                        /*TextButton tbAlpha = (TextButton) stage.getRoot().findActor("Alpha");
                        tbAlpha.setTouchable(Touchable.enabled);
                        tbAlpha.setColor(Color.BLACK);*/
                    } catch (RemoteException ex) {
                        Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        teamselecttable.add(btnteambeta).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton btnteamgamma = new TextButton("Gamma", skin); // Use the initialized skin
        btnteamgamma.setName("Gamma");
        btnteamgamma.setColor(Color.GREEN);
        btnteamgamma.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teamgamma = new Team("Gamma", Color.GREEN);
                        teamgamma.setColorname(teamgamma.getColor().toString());
                        Player selectedplayer = (Player) players.getSelected();
                        int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());

                        ArrayList<Team> teamlistcopy = new ArrayList<>(teamList);
                        for (Team fteam : teamlistcopy) {
                            if (fteam.getPlayer().getName().equals(selectedplayer.getName())) {
                                fteam.setPlayer(null);

                                TextButton teamtb = (TextButton) stage.getRoot().findActor(fteam.getName());
                                teamtb.setTouchable(Touchable.enabled);
                                teamtb.setColor(fteam.getColor());

                                teamList.remove(fteam);
                                gameSettings.removeTeam(fteam);
                            }
                        }

                        teamgamma.setPlayer(selectedplayer);

                        addUnitsSingleTeam(selectedunitcount, teamgamma);

                        session.setGameSettings(gameSettings);

                        btnteamgamma.setTouchable(Touchable.disabled);
                        btnteamgamma.setColor(Color.LIGHT_GRAY);
                    } catch (RemoteException ex) {
                        Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
        Label ipvallabel = new Label(session.getHost().getIp(), skin);
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
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setTurnTime(Integer.parseInt(turntimebox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        settingstable.add(turntimebox).width(180);
        settingstable.row();

        Label playerslabel = new Label("Players :", skin);
        settingstable.add(playerslabel).width(120);
        Object[] maxplayervals = new Object[4];
        maxplayervals[0] = "2";
        maxplayervals[1] = "3";
        maxplayervals[2] = "4";
        maxplayervals[3] = "5";
        maxplayerbox = new SelectBox(skin);
        maxplayerbox.setItems(maxplayervals);
        maxplayerbox.setSelected(Integer.toString(session.getGameSettings().getMaxPlayersSession()));
        maxplayerbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    SessionContext sc = new SessionContext();
                    // Send a new property because this is sending a session object instead of a gamesettings object
                    gameSettings.setMaxPlayersSession(Integer.valueOf(maxplayerbox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                    // Update this session's max players value. 
                    // Otherwise people will be able to connect to the session while over the max players value.
                    sc.update(session);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        settingstable.add(maxplayerbox).width(180);
        settingstable.row();

        Label speedslabel = new Label("Speeds :", skin);
        settingstable.add(speedslabel).width(120);
        Label speedsvallabel = new Label("Marathon", skin);
        settingstable.add(speedsvallabel).width(180);
        settingstable.row();

        Object[] physicsvals = new Object[2];
        physicsvals[0] = "true";
        physicsvals[1] = "false";
        Label physicslabel = new Label("Physics :", skin);
        settingstable.add(physicslabel).width(120);
        SelectBox physicsbox = new SelectBox(skin);
        physicsbox.setItems(physicsvals);
        physicsbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setPhysics(Boolean.parseBoolean(physicsbox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                try {
                    gameSettings.setMaxTime(Integer.parseInt(timerbox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                try {
                    int selectedunitcount = Integer.parseInt(unitbox.getSelected().toString());
                    gameSettings.setMaxUnitCount(selectedunitcount);
                    // For each team in the list remove all the units first and remove it from the gamesettings.
                    refreshUnitsForTeam(selectedunitcount);

                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        try {
            session.setGameSettings(gameSettings);
        } catch (RemoteException ex) {
            Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
        }
        map1.setPosition(20, 70);
        map1.setWidth(400);
        map1.setHeight(230);
        mapstable.addActor(map1);
        SelectBox chooseMap = new SelectBox(skin);
        chooseMap.setItems(mapslist.toArray());
        
        gameSettings.setMapIndex(chooseMap.getSelectedIndex());
        gameSettings.setMapName(chooseMap.getSelected().toString());
        
        chooseMap.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setMapName(chooseMap.getSelected().toString());
                    gameSettings.setMapIndex(chooseMap.getSelectedIndex());
                    session.setGameSettings(gameSettings);

                    mapstable.removeActor(map1);
                    map1 = new Image(new Texture(mapslist.get(chooseMap.getSelectedIndex())));
                    map1.setPosition(20, 70);
                    map1.setWidth(400);
                    map1.setHeight(230);
                    map1.invalidate();
                    mapstable.addActor(map1);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        teamstable.setWidth(260);
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
                try {
                    timer.cancel();

                    // Updating map before lauch
                    gameSettings.setMapName(chooseMap.getSelected().toString());
                    gameSettings.setMapIndex(chooseMap.getSelectedIndex());
                    session.setGameSettings(gameSettings);
                    
                    // Updating the playerList before lauch
                    playerList = getPlayersOfSession(session);
                    session.setPlayerList(playerList);

                    // Check if there are at least 2 teams otherwise return
                    if (teamList.size() < 2) {
                        return;
                    }
                    session.startGame();
                    game.setScreen(new GameEngine(game, session, player));
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                SessionContext sc = new SessionContext();
                SessionPlayerContext part = new SessionPlayerContext();
                try {
                    timer.cancel();
                    // Make the clients know the session is stopping. Push an update.
                    session.cancelSessionForClients();
                    // If it gets to here remove the session from the DB.
                    session.removeRegistry();
                    // Remove the session_participant from the db.
                    part.deleteSessionAndPlayers(session);
                    // Remove the session from the db.
                    sc.delete(session);
                    session = null;
                    game.setScreen(new LobbyGUI(game, player));
                } catch (SQLException ex) {
                    Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchObjectException ex) {
                    Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RemoteException ex) {
                    Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        SessionPlayerContext sc = new SessionPlayerContext();
        timer = new Timer("PlayerRefresh");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    playerList = sc.getPlayersFromSession(session);
                    players.setItems(playerList.toArray());
                } catch (SQLException ex) {
                    Logger.getLogger(SessionOnlinePlayer.class.getName()).log(Level.SEVERE, null, ex);
                    timer.cancel();
                }
            }
        }, 0, 7000);

    }

    public void refreshUnitsForTeam(int selectedunitcount) {
        // For each team in the list remove all the units first and remove it from the gamesettings.
        for (Team teamv : teamList) {
            gameSettings.removeTeam(teamv);
            teamv.removeAllUnits();
            // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
            for (int i = 0; i < selectedunitcount; i++) {
                teamv.addUnit(teamv.getName() + Integer.toString(i), 100);
            }
            gameSettings.addTeam(teamv);
        }
    }

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
        try {
            timer.cancel();

            PlayerContext pc = new PlayerContext();
            pc.delete(player);

            SessionPlayerContext part = new SessionPlayerContext();
            part.deleteSessionAndPlayers(session);

            SessionContext sc = new SessionContext();
            sc.delete(session);

            session.cancelSessionForClients();
            session.removeRegistry();
            session = null;
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(SessionOnlineHost.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
