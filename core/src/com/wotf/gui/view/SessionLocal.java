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
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Team;
import java.util.ArrayList;

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

    /**
     * Constructor of SessionLocal, initializes teamList and gameSetting
     *
     * @param game
     */
    public SessionLocal(WotFGame game) {
        this.game = game;
        gameSettings = new GameSettings();
        teamList = new ArrayList<>();
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
        Gdx.input.setInputProcessor(stage); // Make the stage consume events
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        teams = new List(skin);

        // All teams and labels
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
        TextButton teamalpha = new TextButton("Alpha", skin); // Use the initialized skin
        teamalpha.setColor(Color.BLUE);
        teamalpha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teamalpha = new Team("Alpha", Color.BLUE);
                teamalpha.addUnit(teamalpha.getName(), 100);

                teamList.add(teamalpha);
                gameSettings.addTeam(teamalpha);
                teams.clear();
                teams.setItems(teamList.toArray());
                teams.invalidateHierarchy();
            }
        });

        teamselecttable.add(teamalpha).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton teambeta = new TextButton("Beta", skin); // Use the initialized skin
        teambeta.setColor(Color.CORAL);
        teambeta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teambeta = new Team("Beta", Color.CORAL);
                teambeta.addUnit(teambeta.getName(), 100);

                teamList.add(teambeta);
                gameSettings.addTeam(teambeta);
                teams.setItems(teamList.toArray());
            }
        });
        teamselecttable.add(teambeta).padBottom(10).width(150).height(50);
        teamselecttable.row();
        TextButton teamgamma = new TextButton("Gamma", skin); // Use the initialized skin
        teamgamma.setColor(Color.GREEN);
        teamgamma.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Team teamgamma = new Team("Gamma", Color.GREEN);
                teamgamma.addUnit(teamgamma.getName(), 100);

                teamList.add(teamgamma);
                gameSettings.addTeam(teamgamma);
                teams.setItems(teamList.toArray());
            }
        });
        teamselecttable.add(teamgamma).width(150).height(50);
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

        String[] turntimevals = new String[6];
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
        settingstable.add(turntimebox).width(180);
        settingstable.row();

        Label playerslabel = new Label("Players :", skin);
        settingstable.add(playerslabel).width(120);
        Label playersvallabel = new Label("2/10", skin);
        settingstable.add(playersvallabel).width(180);
        settingstable.row();

        Label speedslabel = new Label("Speeds :", skin);
        settingstable.add(speedslabel).width(120);
        Label speedsvallabel = new Label("Marathon", skin);
        settingstable.add(speedsvallabel).width(180);
        settingstable.row();

        String[] physicsvals = new String[2];
        physicsvals[0] = "True";
        physicsvals[1] = "False";
        Label physicslabel = new Label("Physics :", skin);
        settingstable.add(physicslabel).width(120);
        SelectBox physicsbox = new SelectBox(skin);
        physicsbox.setItems(physicsvals);
        settingstable.add(physicsbox).width(180);
        settingstable.row();

        String[] weaponsvals = new String[3];
        weaponsvals[0] = "All Weapons";
        weaponsvals[1] = "Non-Explosive";
        weaponsvals[2] = "Grenades Only";
        Label weaponslabel = new Label("Weapons :", skin);
        settingstable.add(weaponslabel).width(120);
        SelectBox weaponsbox = new SelectBox(skin);
        weaponsbox.setItems(weaponsvals);
        settingstable.add(weaponsbox).width(180);
        settingstable.row();

        String[] timervals = new String[3];
        timervals[0] = "60";
        timervals[1] = "30";
        timervals[2] = "10";
        Label timerlabel = new Label("Timer :", skin);
        settingstable.add(timerlabel).width(120);
        SelectBox timerbox = new SelectBox(skin);
        timerbox.setItems(timervals);
        settingstable.add(timerbox).width(180);

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

                // Create the map
                Map map = new Map(chooseMap.getSelected().toString());
                game.setScreen(new GameEngine(game, gameSettings, map));
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

}
