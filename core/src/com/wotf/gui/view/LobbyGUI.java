package com.wotf.gui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.Lobby;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Session;
import com.wotf.game.database.PlayerContext;
import com.wotf.game.database.SessionContext;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Screen that shows the lobby GUI
 */
public class LobbyGUI implements Screen {

    private final WotFGame game;
    private Stage stage;
    private Skin skin;
    private List sessions;
    private final Lobby lobby;
    private final SessionContext sessionContext;
    private final PlayerContext playerContext;

    /**
     * Creates a new instance of the LobbyGUI based on the game.
     *
     * @param game so we can switch the screen of the 'current' game
     * @param player
     * @throws java.sql.SQLException
     */
    public LobbyGUI(WotFGame game, Player player) throws SQLException {
        this.game = game;
        lobby = new Lobby();
        sessionContext = new SessionContext();
        playerContext = new PlayerContext();

        // Getting session out of database and sets it in lobby
        for (Session session : sessionContext.getAll()) {
            lobby.addSession(session);
        }
    }

    /**
     * Create a NinePatch Texture based on the internal file name.
     *
     * @param fname the location/name of the internal file
     * @return NinePatch texture with the given bounds which is an internal
     * file.
     */
    private NinePatch getNinePatch(String fname) {
        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 150, 150, 200, 200);
    }

    /**
     * First makes a new stage for the LobbyGUI screen. Fill in the default skin
     * file which is the uiskin.json. Create the Scene2d objects on the screen
     * with the given position. There is a table around each section. There is a
     * list of players which shows the ping of the players. There is a list of
     * sessions which shows all the sessions There are buttons to create a
     * session, join a session and to exit the LobbyGUI screen.
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        sessions = new List(skin);
        List players = new List(skin);
        Table sessionstable = new Table();
        Table playerstable = new Table();

        sessionstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"))));
        playerstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"))));
        String[] playerlist = null;
        try {
            playerlist = new String[playerContext.getAll().size()];
            int i = 0;

            for (Player player : playerContext.getAll()) {
                playerlist[i] = player.getName();
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LobbyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);

        Label sessionslabel = new Label("Sessions", skin);
        sessionstable.add(sessionslabel).padRight(20);
        sessionstable.row();
        sessions.setItems(lobby.getSessions());
        sessionstable.add(sessions).minWidth(250).height(200);
        sessionstable.setPosition(30, 260);
        sessionstable.setWidth(300);
        sessionstable.setHeight(400);
        stage.addActor(sessionstable);

        Label playerslabel = new Label("Players", skin);
        playerstable.add(playerslabel).padRight(20);
        playerstable.row();
        players.setItems(playerlist);
        playerstable.add(players).minWidth(250).height(200);
        playerstable.setPosition(500, 260);
        playerstable.setWidth(300);
        playerstable.setHeight(400);
        stage.addActor(playerstable);

        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(950, 30);
        stage.addActor(exit);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        TextButton join = new TextButton("Join", skin); // Use the initialized skin
        join.setColor(Color.BLACK);
        join.setWidth(300);
        join.setHeight(60);
        join.setPosition(30, 30);
        stage.addActor(join);

        TextButton makesession = new TextButton("Make Session", skin); // Use the initialized skin
        makesession.setColor(Color.BLACK);
        makesession.setWidth(300);
        makesession.setHeight(60);
        makesession.setPosition(350, 30);
        stage.addActor(makesession);

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
