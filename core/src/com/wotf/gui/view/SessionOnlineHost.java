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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import com.wotf.game.classes.Team;
import com.wotf.game.database.PlayerContext;
import com.wotf.game.database.SessionContext;
import com.wotf.game.database.SessionPlayerContext;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen that shows the sessions menu for a host
 */
public class SessionOnlineHost implements Screen {

    private List teams;
    private List players;
    private final WotFGame game;
    private Stage stage;
    private Skin skin;
    private final java.util.List<Team> teamList;
    private java.util.List<Player> playerList;
    private final GameSettings gameSettings;
    private Image map;
    private Session session;
    private final Player player;
    private Timer timer;
    private SelectBox maxPlayerBox;
    private SelectBox unitBox;
    private Table outerTable;
    private ScrollPane scroll;

    /**
     * Constructor of SessionLocal, initializes teamList and gameSetting Other
     * important points: - adds the host to the local playerlist - gets the list
     * of players from the DB and adds it to the local playerlist.
     *
     * @param game Game object associated with this session
     * @param session Session object for this session
     * @param player The player who is the host of this session
     * @throws java.rmi.RemoteException Thrown when a connection error occurs
     */
    public SessionOnlineHost(WotFGame game, Session session, Player player) throws RemoteException {
        this.game = game;
        this.session = session;
        this.player = player;
        session.setHostGui(this);
        gameSettings = new GameSettings();
        teamList = new ArrayList<>();
        playerList = new ArrayList<>();
        addPlayerToDb();
        playerList = getPlayersOfSession(session);
        gameSettings.setIsLocal(false);
    }

    /**
     * Gets all the players in a session from the DB.
     *
     * @param session current session
     * @return list of players from the DB
     */
    public java.util.List<Player> getPlayersOfSession(Session session) {
        try {
            SessionPlayerContext sp = new SessionPlayerContext();
            return sp.getPlayersFromSession(session);
        } catch (SQLException ex) {
            Gdx.app.log("SQL", ex.getMessage());
        }
        
        return null;
    }

    /**
     * Adds a player to the session in the DB.
     */
    public void addPlayerToDb() {
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
     * The Screen for the host. The host can select new values from selectboxes
     * and fire events for adding a Team.
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
        Table teamsTable = new Table();
        Table mapsTable = new Table();
        Table settingsTable = new Table();
        Table teamSelectable = new Table();
        Table playersTable = new Table();
        teamsTable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 130, 130, 160, 160)));
        mapsTable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 220, 220, 160, 160)));
        teamSelectable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 100, 100, 160, 160)));
        playersTable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 125, 125, 160, 160)));

        outerTable = new Table();
        outerTable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 130, 130, 160, 160)));

        scroll = new ScrollPane(outerTable);
        outerTable.setSkin(skin);
        scroll.setSize(500, 300);
        scroll.setPosition(500, 50);
        scroll.setScrollingDisabled(true, false);
        scroll.setForceScroll(false, true);
        scroll.setFlickScroll(true);
        scroll.setOverscroll(false, false);
        stage.addActor(scroll);

        TextField chatMessageField = new TextField("", skin);
        chatMessageField.setWidth(500);
        chatMessageField.setHeight(40);
        chatMessageField.setPosition(500, 5);
        stage.setKeyboardFocus(chatMessageField);
        stage.addActor(chatMessageField);

        TextButton sendMessage = new TextButton("Send", skin);
        sendMessage.setColor(Color.BLACK);
        sendMessage.setWidth(200);
        sendMessage.setHeight(60);
        sendMessage.setPosition(1010, 120);
        stage.addActor(sendMessage);
        sendMessage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (chatMessageField.getText().equals("")) {
                    // Username is empty, show dialog asking for the user to enter a username
                    Dialog noUsernameDialog = new Dialog("ERROR", skin);
                    noUsernameDialog.text("Message may not be empty.\nPlease enter a message.");
                    noUsernameDialog.button("Ok");
                    noUsernameDialog.show(stage);
                    return;
                }

                String message = SessionOnlineHost.this.player.getName() + ": " + chatMessageField.getText();

                try {
                    SessionOnlineHost.this.session.sendChatMessage(message);
                    chatMessageField.setText("");
                } catch (RemoteException ex) {
                    Gdx.app.log("SessionOnlineHost", ex.getMessage());
                }
            }
        });

        setHostLabel();

        setPlayerList(playersTable);

        teamSelectionLabel(teamSelectable);
        // 
        // Teams adding
        addAlpha(teamSelectable);
        addBeta(teamSelectable);
        addGamma(teamSelectable);
        //
        //
        setTitleLabel();

        setIp(settingsTable);

        setTurnTimevals(settingsTable);

        setMaxPlayersvals(settingsTable);

        setSpeedsvals(settingsTable);

        setPhysicsvals(settingsTable);

        setWeaponvals(settingsTable);

        setTimervals(settingsTable);

        setUnitsvals(settingsTable);

        settingsTable.setWidth(300);
        settingsTable.setHeight(200);
        settingsTable.setPosition(30, 110);
        stage.addActor(settingsTable);

        SelectBox chooseMap = setMaps(mapsTable);

        setTeamsToTable(teamsTable);

        startGame(chooseMap);

        exitGame();

        refreshPlayers();

    }

    /**
     * Sets the players from the DB to the GUI list.
     *
     * @param playersTable Table that holds the list of players
     */
    private void setPlayerList(Table playersTable) {
        players.setItems(playerList.toArray());
        playersTable.add(players);
        playersTable.setWidth(250);
        playersTable.setHeight(320);
        playersTable.setPosition(1020, 360);
        stage.addActor(playersTable);
    }

    /**
     * Sets the hostlabel. Here a player can see who is hosting the current
     * session.
     *
     */
    private void setHostLabel() {
        Label hostLabel = new Label(session.getHost().getIp() + " " + session.getHost().getName(), skin);
        hostLabel.setPosition(30, 30);
        stage.addActor(hostLabel);
    }

    /**
     * Sets the teamslist to a table of teams.
     *
     * @param teamsTable Table that holds the list of players
     */
    private void setTeamsToTable(Table teamsTable) {
        Label teamslabel = new Label("Teams", skin);
        teamsTable.setPosition(730, 360);
        teamsTable.add(teamslabel);
        teamsTable.row();
        teamsTable.add(teams).width(200);
        teamsTable.setWidth(260);
        teamsTable.setHeight(320);
        stage.addActor(teamsTable);
    }

    /**
     * Set the label for teamselection.
     *
     * @param teamSelectTable Table that holds the list of players
     */
    private void teamSelectionLabel(Table teamSelectTable) {
        Label selectTeamLabel = new Label("Team selection", skin);
        teamSelectTable.add(selectTeamLabel).padBottom(15);
        teamSelectTable.row();
    }

    /**
     * Adds team alpha
     *
     * @param teamSelectTable Table that holds the list of players
     */
    private void addAlpha(Table teamSelectTable) {
        TextButton btnTeamAlpha = new TextButton("Alpha", skin); // Use the initialized skin
        btnTeamAlpha.setName("Alpha");
        btnTeamAlpha.setColor(Color.BLUE);
        btnTeamAlpha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teamAlpha = new Team("Alpha", Color.BLUE);
                        teamAlpha.setColorname(teamAlpha.getColor().toString());
                        Player selectedPlayer = (Player) players.getSelected();
                        int selectedUnitCount = Integer.parseInt(unitBox.getSelected().toString());

                        java.util.List<Team> teamListCopy = new ArrayList<>(teamList);
                        for (Team fTeam : teamListCopy) {
                            if (fTeam.getPlayer().getName().equals(selectedPlayer.getName())) {
                                fTeam.setPlayer(null);

                                TextButton teamtb = (TextButton) stage.getRoot().findActor(fTeam.getName());
                                teamtb.setTouchable(Touchable.enabled);
                                teamtb.setColor(fTeam.getColor());

                                teamList.remove(fTeam);
                                gameSettings.removeTeam(fTeam);
                            }
                        }

                        teamAlpha.setPlayer(selectedPlayer);

                        addUnitsSingleTeam(selectedUnitCount, teamAlpha);

                        session.setGameSettings(gameSettings);

                        btnTeamAlpha.setTouchable(Touchable.disabled);
                        btnTeamAlpha.setColor(Color.LIGHT_GRAY);
                    } catch (RemoteException ex) {
                        Gdx.app.log("RemoteException", ex.getMessage());
                    }
                }
            }
        });

        teamSelectTable.add(btnTeamAlpha).padBottom(10).width(150).height(50);
        teamSelectTable.row();
    }

    /**
     * Adds team beta.
     *
     * @param teamSelectTable Table that holds the list of players
     */
    private void addBeta(Table teamSelectTable) {
        TextButton btnTeamBeta = new TextButton("Beta", skin); // Use the initialized skin
        btnTeamBeta.setName("Beta");
        btnTeamBeta.setColor(Color.CORAL);
        btnTeamBeta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teamBeta = new Team("Beta", Color.CORAL);
                        teamBeta.setColorname(teamBeta.getColor().toString());
                        Player selectedPlayer = (Player) players.getSelected();
                        int selectedUnitCount = Integer.parseInt(unitBox.getSelected().toString());

                        java.util.List<Team> teamListCopy = new ArrayList<>(teamList);
                        for (Team fTeam : teamListCopy) {
                            if (fTeam.getPlayer().getName().equals(selectedPlayer.getName())) {
                                fTeam.setPlayer(null);

                                TextButton teamtb = (TextButton) stage.getRoot().findActor(fTeam.getName());
                                teamtb.setTouchable(Touchable.enabled);
                                teamtb.setColor(fTeam.getColor());

                                teamList.remove(fTeam);
                                gameSettings.removeTeam(fTeam);
                            }
                        }

                        teamBeta.setPlayer(selectedPlayer);

                        addUnitsSingleTeam(selectedUnitCount, teamBeta);

                        session.setGameSettings(gameSettings);

                        btnTeamBeta.setTouchable(Touchable.disabled);
                        btnTeamBeta.setColor(Color.LIGHT_GRAY);

                        /*TextButton tbAlpha = (TextButton) stage.getRoot().findActor("Alpha");
                        tbAlpha.setTouchable(Touchable.enabled);
                        tbAlpha.setColor(Color.BLACK);*/
                    } catch (RemoteException ex) {
                        Gdx.app.log("RemoteException", ex.getMessage());
                    }
                }
            }
        });
        
        teamSelectTable.add(btnTeamBeta).padBottom(10).width(150).height(50);
        teamSelectTable.row();
    }

    /**
     * Adds team Gamma.
     *
     * @param teamSelectTable Table that holds the list of players
     */
    private void addGamma(Table teamSelectTable) {
        TextButton btnTeamGamma = new TextButton("Gamma", skin); // Use the initialized skin
        btnTeamGamma.setName("Gamma");
        btnTeamGamma.setColor(Color.GREEN);
        btnTeamGamma.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (players.getSelected() != null) {
                    try {
                        Team teamGamma = new Team("Gamma", Color.GREEN);
                        teamGamma.setColorname(teamGamma.getColor().toString());
                        Player selectedplayer = (Player) players.getSelected();
                        int selectedunitcount = Integer.parseInt(unitBox.getSelected().toString());

                        java.util.List<Team> teamlistcopy = new ArrayList<>(teamList);
                        for (Team fTeam : teamlistcopy) {
                            if (fTeam.getPlayer().getName().equals(selectedplayer.getName())) {
                                fTeam.setPlayer(null);

                                TextButton teamTb = (TextButton) stage.getRoot().findActor(fTeam.getName());
                                teamTb.setTouchable(Touchable.enabled);
                                teamTb.setColor(fTeam.getColor());

                                teamList.remove(fTeam);
                                gameSettings.removeTeam(fTeam);
                            }
                        }

                        teamGamma.setPlayer(selectedplayer);

                        addUnitsSingleTeam(selectedunitcount, teamGamma);

                        session.setGameSettings(gameSettings);

                        btnTeamGamma.setTouchable(Touchable.disabled);
                        btnTeamGamma.setColor(Color.LIGHT_GRAY);
                    } catch (RemoteException ex) {
                        Gdx.app.log("RemoteException", ex.getMessage());
                    }
                }
            }
        });
        
        teamSelectTable.add(btnTeamGamma).width(150).height(50);
        teamSelectTable.setWidth(200);
        teamSelectTable.setHeight(320);
        teamSelectTable.setPosition(500, 360);
        stage.addActor(teamSelectTable);
    }

    /**
     * Sets the title label to the name of the game.
     */
    private void setTitleLabel() {
        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);
    }

    /**
     * Set the IP adress of the host. To the settingstable.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setIp(Table settingsTable) {
        Label ipLabel = new Label("IP :", skin);
        settingsTable.add(ipLabel).width(120);
        Label ipValLabel = new Label(session.getHost().getIp(), skin);
        settingsTable.add(ipValLabel).width(180);
        settingsTable.row();
    }

    /**
     * Sets the turn times to the settingstable.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setTurnTimevals(Table settingsTable) {
        Object[] turnTimeVals = new Object[6];
        turnTimeVals[0] = "10";
        turnTimeVals[1] = "20";
        turnTimeVals[2] = "30";
        turnTimeVals[3] = "40";
        turnTimeVals[4] = "50";
        turnTimeVals[5] = "60";
        Label turntimelabel = new Label("Turn Time :", skin);
        settingsTable.add(turntimelabel).width(120);
        SelectBox turnTimeBox = new SelectBox(skin);
        turnTimeBox.setItems(turnTimeVals);
        turnTimeBox.setSelectedIndex(3);
        turnTimeBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setTurnTime(Integer.parseInt(turnTimeBox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        settingsTable.add(turnTimeBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the max player selectbox to the current value. Also make initial
     * values. Add this to the gamesettings.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setMaxPlayersvals(Table settingsTable) {
        Label playersLabel = new Label("Players :", skin);
        settingsTable.add(playersLabel).width(120);
        Object[] maxPlayerVals = new Object[4];
        maxPlayerVals[0] = "2";
        maxPlayerVals[1] = "3";
        maxPlayerVals[2] = "4";
        maxPlayerVals[3] = "5";
        maxPlayerBox = new SelectBox(skin);
        maxPlayerBox.setItems(maxPlayerVals);
        maxPlayerBox.setSelected(Integer.toString(session.getGameSettings().getMaxPlayersSession()));
        maxPlayerBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    SessionContext sc = new SessionContext();
                    // Send a new property because this is sending a session object instead of a gamesettings object
                    gameSettings.setMaxPlayersSession(Integer.valueOf(maxPlayerBox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                    // Update this session's max players value.
                    // Otherwise people will be able to connect to the session while over the max players value.
                    sc.update(session);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        settingsTable.add(maxPlayerBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the label for Speeds which isn't currently used.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setSpeedsvals(Table settingsTable) {
        Label speedsLabel = new Label("Speeds :", skin);
        settingsTable.add(speedsLabel).width(120);
        Label speedsValLabel = new Label("Marathon", skin);
        settingsTable.add(speedsValLabel).width(180);
        settingsTable.row();
    }

    /**
     * Sets the selectbox with values true and false. Also add it to the
     * gamesettings.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setPhysicsvals(Table settingsTable) {
        Object[] physicsVals = new Object[2];
        physicsVals[0] = "true";
        physicsVals[1] = "false";
        Label physicslabel = new Label("Physics :", skin);
        settingsTable.add(physicslabel).width(120);
        SelectBox physicsBox = new SelectBox(skin);
        physicsBox.setItems(physicsVals);
        
        physicsBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setPhysics(Boolean.parseBoolean(physicsBox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        settingsTable.add(physicsBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the weapon selectbox with values. Not added to the gamesettings yet.
     * It doesn't have a functional use.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setWeaponvals(Table settingsTable) {
        Object[] weaponsVals = new Object[3];
        weaponsVals[0] = "All Weapons";
        weaponsVals[1] = "Non-Explosive";
        weaponsVals[2] = "Grenades Only";
        Label weaponsLabel = new Label("Weapons :", skin);
        settingsTable.add(weaponsLabel).width(120);
        SelectBox weaponsBox = new SelectBox(skin);
        weaponsBox.setItems(weaponsVals);
        settingsTable.add(weaponsBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the MaxTime selectbox with values. Adds the selected maxtime to the
     * gamesettings list. If it is changed inform it to the clients.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setTimervals(Table settingsTable) {
        Object[] timerVals = new Object[3];
        timerVals[0] = "60";
        timerVals[1] = "30";
        timerVals[2] = "10";
        Label timerLabel = new Label("Timer :", skin);
        settingsTable.add(timerLabel).width(120);
        SelectBox timerBox = new SelectBox(skin);
        timerBox.setItems(timerVals);
        timerBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gameSettings.setMaxTime(Integer.parseInt(timerBox.getSelected().toString()));
                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        settingsTable.add(timerBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the current units with values. Adds the selected units to the
     * gamesettings list. If it is changed inform the clients.
     *
     * @param settingsTable Table that holds the list of players
     */
    private void setUnitsvals(Table settingsTable) {
        Object[] unitVals = new Object[4];
        unitVals[0] = "1";
        unitVals[1] = "2";
        unitVals[2] = "3";
        unitVals[3] = "4";
        Label unitLabel = new Label("Units :", skin);
        settingsTable.add(unitLabel).width(120);
        unitBox = new SelectBox(skin);
        unitBox.setItems(unitVals);
        unitBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int selectedUnitCount = Integer.parseInt(unitBox.getSelected().toString());
                    gameSettings.setMaxUnitCount(selectedUnitCount);
                    // For each team in the list remove all the units first and remove it from the gamesettings.
                    refreshUnitsForTeam(selectedUnitCount);

                    session.setGameSettings(gameSettings);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        settingsTable.add(unitBox).width(180);

    }

    /**
     * Sets the map selectbox with all the images in the internal /map folder
     * Adds the selected map to the gamesettings list. If it is changed inform
     * the clients.
     *
     * @param mapsTable Table that holds the list of maps
     * @return SelectBox control containing the selected map
     */
    private SelectBox setMaps(Table mapsTable) {
        java.util.List<String> mapslist = new ArrayList<>();
        FileHandle dirHandle = Gdx.files.internal("maps");
        
        for (FileHandle entry : dirHandle.list()) {
            mapslist.add(entry.toString().substring(5));
        }
        
        map = new Image(new Texture("maps/" + mapslist.get(0)));
        
        try {
            session.setGameSettings(gameSettings);
        } catch (RemoteException ex) {
            Gdx.app.log("RemoteException", ex.getMessage());
        }
        
        map.setPosition(20, 70);
        map.setWidth(400);
        map.setHeight(230);
        mapsTable.addActor(map);
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

                    mapsTable.removeActor(map);
                    map = new Image(new Texture("maps/" + mapslist.get(chooseMap.getSelectedIndex())));
                    map.setPosition(20, 70);
                    map.setWidth(400);
                    map.setHeight(230);
                    map.invalidate();
                    mapsTable.addActor(map);
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
        
        chooseMap.setWidth(400);
        chooseMap.setPosition(20, 20);
        mapsTable.addActor(chooseMap);
        mapsTable.setPosition(30, 360);
        mapsTable.setHeight(320);
        mapsTable.setWidth(440);
        stage.addActor(mapsTable);
        
        return chooseMap;
    }

    /**
     * Starts the game with the current gamesettings. Inform the clients to
     * start the game. The teamsize must be at least 2.
     *
     * @param chooseMap Control containing the selected map
     */
    private void startGame(SelectBox chooseMap) {
        TextButton start = new TextButton("Start", skin); // Use the initialized skin
        start.setColor(Color.BLACK);
        start.setWidth(200);
        start.setHeight(60);
        start.setPosition(1010, 190);
        stage.addActor(start);
        
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    timer.cancel();

                    // Updating map before launch
                    gameSettings.setMapName(chooseMap.getSelected().toString());
                    gameSettings.setMapIndex(chooseMap.getSelectedIndex());
                    session.setGameSettings(gameSettings);

                    // Updating the playerList before launch
                    playerList = getPlayersOfSession(session);
                    session.setPlayerList(playerList);

                    // Check if there are at least 2 teams otherwise return
                    if (teamList.size() < 2) {
                        return;
                    }
                    session.startGame();
                    game.setScreen(new GameEngine(game, session, player));
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
    }

    /**
     * Refreshes the players in the session each 7 seconds. From the DB
     */
    private void refreshPlayers() {
        SessionPlayerContext sc = new SessionPlayerContext();
        timer = new Timer("PlayerRefresh");
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    playerList = sc.getPlayersFromSession(session);
                    players.setItems(playerList.toArray());
                } catch (SQLException ex) {
                    Gdx.app.log("SQL", ex.getMessage());
                    timer.cancel();
                }
            }
        }, 0, 7000);
    }

    /**
     * Cancels the game and inform the clients to exit the session.
     */
    private void exitGame() {
        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(200);
        exit.setHeight(60);
        exit.setPosition(1010, 50);
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
                    Gdx.app.log("SQL", ex.getMessage());
                } catch (NoSuchObjectException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                } catch (RemoteException ex) {
                    Gdx.app.log("RemoteException", ex.getMessage());
                }
            }
        });
    }

    /**
     * Method that adds a message to the GUI
     *
     * @param message Message to add
     */
    public void chatMessage(String message) {
        outerTable.add(message).align(Align.left);
        outerTable.row();
        scroll.layout();
        scroll.scrollTo(0, 120, 0, 0);

    }

    /**
     * Make new units for each team.
     *
     * @param selectedUnitCount Amount of units to add to teams
     */
    public void refreshUnitsForTeam(int selectedUnitCount) {
        // For each team in the list remove all the units first and remove it from the gamesettings.
        for (Team teamV : teamList) {
            gameSettings.removeTeam(teamV);
            teamV.removeAllUnits();
            // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
            for (int i = 0; i < selectedUnitCount; i++) {
                teamV.addUnit(teamV.getName() + Integer.toString(i), 100);
            }
            gameSettings.addTeam(teamV);
        }
    }

    /**
     * Make units for selected team.
     *
     * @param selectedUnitCount Amount of units to add
     * @param team Team to add units to
     */
    public void addUnitsSingleTeam(int selectedUnitCount, Team team) {
        // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
        for (int i = 0; i < selectedUnitCount; i++) {
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
            Gdx.app.log("RemoteException", ex.getMessage());
        } catch (RemoteException ex) {
            Gdx.app.log("RemoteException", ex.getMessage());
        }

    }
}
