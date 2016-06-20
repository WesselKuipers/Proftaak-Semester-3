package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import com.wotf.game.classes.SessionManager;
import com.wotf.game.classes.Team;
import com.wotf.game.database.PlayerContext;
import com.wotf.game.database.SessionPlayerContext;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen that shows the sessions menu for a connected player
 */
public class SessionOnlinePlayer implements Screen {

    private final List teams;
    private List players;
    private final WotFGame game;
    private final Stage stage;
    private final Skin skin;
    private final java.util.List<Team> teamList;
    private final GameSettings gameSettings;
    private Image map;
    private Session session;
    private final SessionManager manager;
    private java.util.List<String> mapslist;
    private java.util.List<Player> playerList;
    private final SelectBox turnTimeBox;
    private final SelectBox physicsbox;
    private final SelectBox timerbox;
    private SelectBox chooseMap;
    private SelectBox unitBox;
    private Table mapsTable;
    private final java.util.List<Texture> mapTextures;
    private final Player player;
    private Timer timer;
    private SelectBox maxplayerbox;
    private int switchScreenCheck;
    private int startGame;
    private boolean updateUnit;
    private boolean refreshUnit;
    private Table outerTable;
    private ScrollPane scroll;

    /**
     * Constructor of SessionLocal, initializes teamList and gameSetting
     * Connects to the parameter session in the SessionManager class.
     * Initializes global tables. Makes a list of Textures where all internal
     * maps are saved Update the playerlist of the current session
     *
     * @param game current
     * @param session from host
     */
    public SessionOnlinePlayer(WotFGame game, Session session, Player player) throws RemoteException {
        switchScreenCheck = 0;
        startGame = 0;
        this.player = player;
        this.game = game;
        gameSettings = new GameSettings();
        teamList = new ArrayList<>();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        //System.out.println(Gdx.files.internal("maps"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        teams = new List(skin);
        timerbox = new SelectBox(skin);
        turnTimeBox = new SelectBox(skin);
        physicsbox = new SelectBox(skin);
        mapTextures = new ArrayList<>();
        playerList = new ArrayList<>();
        setMapsList();
        // This manager will be used to connect to the registry of the host.
        manager = new SessionManager(session, this);
        addPlayerToDB();
        playerList = getPlayersOfSession(session);
        updateUnit = false;
        refreshUnit = false;
    }

    /**
     * Get all the players for a given session.
     *
     * @param session
     * @return
     */
    public ArrayList<Player> getPlayersOfSession(Session session) {
        try {
            SessionPlayerContext sp = new SessionPlayerContext();
            return sp.getPlayersFromSession(session);
        } catch (SQLException ex) {
            Gdx.app.log("SQL", ex.getMessage());
        }
        return null;
    }

    /**
     * Adds a single player to the session to the DB.
     *
     */
    public void addPlayerToDB() {
        SessionPlayerContext sp = new SessionPlayerContext();
        sp.insert(player, manager.getSession());
    }

    /**
     * Sets a list of the maps which are located in the internal /maps folder.
     *
     */
    public void setMapsList() {
        mapslist = new ArrayList<>();
        FileHandle dirHandle = Gdx.files.internal("maps");
        for (FileHandle entry : dirHandle.list()) {
            mapslist.add(entry.toString().substring(5));
            mapTextures.add(new Texture(Gdx.files.internal("maps/" + entry.toString().substring(5))));
        }
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
     * The screen for the client(player). This screen is based on what the
     * SessionManager receives from the host. The selectboxes etc. are updated
     * when the host changes a value/selectbox.
     *
     */
    @Override
    public void show() {
        // If we pick the new session from SessionManager, we got all the current gamesettings ready in this object.
        session = manager.getSession();

        players = new List(skin);

        // Alle teams en labels hiervoor.
        Table teamstable = new Table();
        mapsTable = new Table();
        Table settingstable = new Table();
        Table teamselecttable = new Table();
        Table playerstable = new Table();
        teamstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 130, 130, 160, 160)));
        mapsTable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 220, 220, 160, 160)));
        teamselecttable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 100, 100, 160, 160)));
        playerstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"), 125, 125, 160, 160)));

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
                
                String message = SessionOnlinePlayer.this.player.getName() + ": " + chatMessageField.getText();
                SessionOnlinePlayer.this.manager.sendMessage(message);
                chatMessageField.setText("");
            }
        });

        setPlayerList(playerstable);

        addTeamsTable(teamselecttable);

        setTitelLabel();

        setIp(settingstable);

        setTurnTimevals(settingstable);

        setMaxPlayervals(settingstable);

        setSpeedvals(settingstable);

        setPhysicsvals(settingstable);

        setWeaponvals(settingstable);

        setTimervals(settingstable);

        setUnitvals(settingstable);

        settingstable.setWidth(300);
        settingstable.setHeight(200);
        settingstable.setPosition(30, 110);
        stage.addActor(settingstable);

        setMaps();

        setTeamsToTable(teamstable);

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
     * Sets the teamslist to a table of teams. Adds new TextButtons for each
     * team.
     *
     * @param teamstable Table that holds the list of teams
     */
    private void addTeamsTable(Table teamSelectTable) {
        Label selectTeamLabel = new Label("Team selection", skin);
        teamSelectTable.add(selectTeamLabel).padBottom(15);
        teamSelectTable.row();
        TextButton teamalpha = new TextButton("Alpha", skin); // Use the initialized skin
        teamalpha.setName("Alpha");
        teamalpha.setColor(Color.BLUE);

        teamSelectTable.add(teamalpha).padBottom(10).width(150).height(50);
        teamSelectTable.row();
        TextButton teamBeta = new TextButton("Beta", skin); // Use the initialized skin
        teamBeta.setName("Beta");
        teamBeta.setColor(Color.CORAL);

        teamSelectTable.add(teamBeta).padBottom(10).width(150).height(50);
        teamSelectTable.row();
        TextButton teamGamma = new TextButton("Gamma", skin); // Use the initialized skin
        teamGamma.setName("Gamma");
        teamGamma.setColor(Color.GREEN);

        teamSelectTable.add(teamGamma).width(150).height(50);
        teamSelectTable.setWidth(200);
        teamSelectTable.setHeight(320);
        teamSelectTable.setPosition(500, 360);
        stage.addActor(teamSelectTable);
    }

    /**
     * Sets the title label to the name of the game.
     */
    private void setTitelLabel() {
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
        Label turnTimeLabel = new Label("Turn Time :", skin);
        settingsTable.add(turnTimeLabel).width(120);
        turnTimeBox.setItems(turnTimeVals);
        String turntimestr = Integer.toString(session.getGameSettings().getTurnTime());
        turnTimeBox.setSelected(turntimestr);
        turnTimeBox.setTouchable(Touchable.disabled);

        settingsTable.add(turnTimeBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the max player selectbox to the current value. Also make initial
     * values. Add this to the gamesettings.
     *
     * @param settingstable Table that holds the list of players
     */
    private void setMaxPlayervals(Table settingstable) {
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
        maxplayerbox.setTouchable(Touchable.disabled);
        settingstable.add(maxplayerbox).width(180);
        settingstable.row();
    }

    /**
     * Sets the label for Speeds which isn't currently used.
     *
     * @param settingstable Table that holds the list of players
     */
    private void setSpeedvals(Table settingstable) {
        Label speedslabel = new Label("Speeds :", skin);
        settingstable.add(speedslabel).width(120);
        Label speedsvallabel = new Label("Marathon", skin);
        settingstable.add(speedsvallabel).width(180);
        settingstable.row();
    }

    /**
     * Sets the selectbox with values true and false. Also add it to the
     * gamesettings.
     *
     * @param settingstable Table that holds the list of players
     */
    private void setPhysicsvals(Table settingstable) {
        Object[] physicsvals = new Object[2];
        physicsvals[0] = "true";
        physicsvals[1] = "false";
        Label physicslabel = new Label("Physics :", skin);
        settingstable.add(physicslabel).width(120);
        physicsbox.setItems(physicsvals);
        String physicsstr = Boolean.toString(session.getGameSettings().getPhysics());
        physicsbox.setSelected(physicsstr);
        physicsbox.setTouchable(Touchable.disabled);

        settingstable.add(physicsbox).width(180);
        settingstable.row();
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
        weaponsBox.setTouchable(Touchable.disabled);
        settingsTable.add(weaponsBox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the MaxTime selectbox with values. Adds the selected maxtime to the
     * gamesettings list.
     *
     * @param settingsTable Table that holds the list of players
     */ 
    private void setTimervals(Table settingsTable) {
        Object[] timervals = new Object[3];
        timervals[0] = "60";
        timervals[1] = "30";
        timervals[2] = "10";
        Label timerlabel = new Label("Timer :", skin);
        settingsTable.add(timerlabel).width(120);
        timerbox.setItems(timervals);
        String timerstr = Integer.toString(session.getGameSettings().getMaxTime() / 60);
        timerbox.setSelected(timerstr);
        timerbox.setTouchable(Touchable.disabled);
        settingsTable.add(timerbox).width(180);
        settingsTable.row();
    }

    /**
     * Sets the current units with values. Adds the selected units to the
     * gamesettings list.
     *
     * @param settingstable Table that holds the list of players
     */
    private void setUnitvals(Table settingstable) {
        Object[] unitvals = new Object[4];
        unitvals[0] = "1";
        unitvals[1] = "2";
        unitvals[2] = "3";
        unitvals[3] = "4";
        Label unitlabel = new Label("Units :", skin);
        settingstable.add(unitlabel).width(120);
        unitBox = new SelectBox(skin);
        unitBox.setItems(unitvals);
        String unitstr = Integer.toString(session.getGameSettings().getMaxUnitCount());
        unitBox.setSelected(unitstr);
        unitBox.setTouchable(Touchable.disabled);
        settingstable.add(unitBox).width(180);
    }

    /**
     * Sets the map to the current map. Get the map index from the list of maps
     * internal.
     *
     * @param mapstable Table that holds the list of players
     */
    private void setMaps() {
        map = new Image(new Texture("maps/" + mapslist.get(session.getGameSettings().getMapIndex())));
        map.setPosition(20, 70);
        map.setWidth(400);
        map.setHeight(230);
        mapsTable.addActor(map);
        chooseMap = new SelectBox(skin);
        chooseMap.setItems(mapslist.toArray());
        chooseMap.setSelected(session.getGameSettings().getMapName());
        chooseMap.setTouchable(Touchable.disabled);
        chooseMap.setWidth(400);
        chooseMap.setPosition(20, 20);
        mapsTable.addActor(chooseMap);
        mapsTable.setPosition(30, 360);
        mapsTable.setHeight(320);
        mapsTable.setWidth(440);
        stage.addActor(mapsTable);
    }

    /**
     * Sets the teamslist to a table of teams.
     *
     * @param teamsTable Table that holds the list of players
     */
    private void setTeamsToTable(Table teamsTable) {
        Label teamsLabel = new Label("Teams", skin);
        teamsTable.setPosition(730, 360);
        teamsTable.add(teamsLabel);
        teamsTable.row();
        teamList.clear();
        teamList.addAll(session.getGameSettings().getTeams());
        
        // For the color of the boxes and if it is touchable.
        for (Team teamV : teamList) {
            TextButton teamTb = (TextButton) stage.getRoot().findActor(teamV.getName());
            teamTb.setTouchable(Touchable.disabled);
            teamTb.setColor(Color.LIGHT_GRAY);
        }
        
        teams.setItems(teamList.toArray());
        teamsTable.add(teams).width(200);
        teamsTable.setWidth(260);
        teamsTable.setHeight(320);
        stage.addActor(teamsTable);
    }

    /**
     * When clicked the client exits the session, cancels the timer to refresh
     * the players, removes the registry it is connected to, remove the player
     * from the current session from db, sets the screen back to the lobby.
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
                try {
                    manager.removeRegistry();
                    timer.cancel();
                    game.setScreen(new LobbyGUI(game, player));
                    SessionPlayerContext part = new SessionPlayerContext();
                    part.deletePlayerFromSession(player, session);
                } catch (SQLException ex) {
                    Gdx.app.log("SQL", ex.getMessage());
                }
            }
        });
    }

    /**
     * Refresh the players in the session each 7 seconds. From the DB
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
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        if (switchScreenCheck != 0) {
            try {
                game.setScreen(new LobbyGUI(game, player));
            } catch (SQLException ex) {
                Gdx.app.log("SQL", ex.getMessage());
            }
        }

        if (startGame != 0) {
            game.setScreen(new GameEngine(game, session, player));
        }

        if (updateUnit) {
            int selectedUnitCount = Integer.parseInt(unitBox.getSelected().toString());
            for (Team teamv : session.getGameSettings().getTeams()) {
                addUnitsSingleTeam(selectedUnitCount, teamv);
            }
            updateUnit = false;
        }

        if (refreshUnit) {
            int selectedUnitCount = Integer.parseInt(unitBox.getSelected().toString());
            
            // For each team in the list remove all the units first and remove it from the gamesettings.
            for (Team teamv : session.getGameSettings().getTeams()) {
                teamv.removeAllUnits();
                
                // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
                for (int i = 0; i < selectedUnitCount; i++) {
                    teamv.addUnit(teamv.getName() + Integer.toString(i), 100);
                }
            }
            refreshUnit = false;
        }
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
        timer.cancel();

        PlayerContext pc = new PlayerContext();
        pc.delete(player);

        manager.removeRegistry();

        SessionPlayerContext part = new SessionPlayerContext();
        part.deletePlayerFromSession(player, session);
    }

    /**
     * The player should not be removed from the db because it only quitted to
     * the lobby. If it quits to the lobby we execute the dispose above.
     *
     */
    public void disposeWithoutPlayer() {
        timer.cancel();

        manager.removeRegistry();

        SessionPlayerContext part = new SessionPlayerContext();
        part.deletePlayerFromSession(player, session);
    }

    /**
     * Updates the teamlist for the client to the actual state on the host.
     *
     * @param managersession a copy of the host session object.
     */
    public void updateTeamList(Session managersession) {
        session = managersession;
        if (session.getGameSettings().getTeams() != null) {
            // Set all the teambuttons to touchable first again.
            for (Team teamv : teamList) {
                TextButton teamTb = (TextButton) stage.getRoot().findActor(teamv.getName());
                teamTb.setColor(Color.valueOf(teamv.getColorname()));
            }

            teamList.clear();

            // For the color of the boxes and if it is touchable.
            for (Team teamv : session.getGameSettings().getTeams()) {
                teamList.add(teamv);
                gameSettings.addTeam(teamv);
                TextButton teamtb = (TextButton) stage.getRoot().findActor(teamv.getName());
                teamtb.setTouchable(Touchable.disabled);
                teamtb.setColor(Color.LIGHT_GRAY);
                teamv.setColor(Color.valueOf(teamv.getColorname()));
                teamv.intializeForClient();
            }
            teams.setItems(teamList.toArray());
            updateUnit = true;
        }
    }

    /**
     * Updates the Selectboxes for the client to the actual state on the host.
     *
     * @param managerSession a copy of the host session object.
     */
    public void updateSelectedItems(Session managerSession) {
        if (managerSession.getGameSettings() != null) {
            // MaxTime selected

            String maxtime = Integer.toString(managerSession.getGameSettings().getMaxTime() / 60);
            timerbox.setSelected(maxtime);
            // TurnTime selected
            String turntime = Integer.toString(managerSession.getGameSettings().getTurnTime());
            turnTimeBox.setSelected(turntime);
            // Physics selected
            String physics = Boolean.toString(managerSession.getGameSettings().getPhysics());
            physicsbox.setSelected(physics);
            // Maxplayers selected
            String maxplayers = Integer.toString(managerSession.getGameSettings().getMaxPlayersSession());
            maxplayerbox.setSelected(maxplayers);
            // MaxUnitCount selected
            String maxunitcount = Integer.toString(managerSession.getGameSettings().getMaxUnitCount());
            unitBox.setSelected(maxunitcount);
            refreshUnit = true;
        }
    }

    /**
     * Updates the Selectbox for the map and changes the image of the map to the
     * Currently selected map.
     *
     * @param managerSession a copy of the host session object.
     */
    public void updateSelectedMap(Session managerSession) {
        if (managerSession.getGameSettings() != null) {
            chooseMap.setSelected(managerSession.getGameSettings().getMapName());
            mapsTable.removeActor(map);
            Texture mapName = mapTextures.get(managerSession.getGameSettings().getMapIndex());
            map = new Image(mapName);
            map.setPosition(20, 70);
            map.setWidth(400);
            map.setHeight(230);
            map.invalidate();
            mapsTable.addActor(map);
        }
    }

    /**
     * Set the variable switchscreencheck to 1. This variable is used in the
     * render, so that the game can change screen as soon as this value is 1.
     * This is checked inside the render.
     *
     */
    public void backToLobby() {
        // Solution could be to change a variable which will be checked and then change screen.
        switchScreenCheck = 1;
    }

    /**
     * Change variable startGame to 1 if this method is called. This will be
     * checked in the render. For a change from 0 to 1. Updates the playerlist
     * before launch.
     */
    public void startGame() {
        startGame = 1;
        // Updating the playerList before lauch
        playerList = getPlayersOfSession(session);
        session.setPlayerList(playerList);
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
     * Create units for a single team based on how many units there should be
     * created.
     *
     * @param selectedUnitCount amount of units to create
     * @param team team to add units to
     */
    public void addUnitsSingleTeam(int selectedUnitCount, Team team) {
        // The new units to the team. The name of the unit is the teamname + the number of the variable 'i'.
        for (int i = 0; i < selectedUnitCount; i++) {
            team.addUnit(team.getName() + Integer.toString(i), 100);
        }
    }
}
