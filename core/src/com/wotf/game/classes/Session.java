package com.wotf.game.classes;

import com.wotf.gui.view.ISessionSettings;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Session contains the data structure containing the list of teams, players and
 * gameSettings. Also the initalizitation of a registry of a sessionhost happens
 * in this class.
 */
public class Session extends UnicastRemoteObject implements ISessionSettings {

    private GameSettings gameSettings;
    private Player host;
    private List<Player> players;
    private transient Registry registry;
    private transient RemotePublisher publisher;
    private String roomName;
    private int id;

    /**
     * Private empty constructor for initializing UnicastRemoteObject.
     *
     * @throws RemoteException
     */
    private Session() throws RemoteException {
    }

    ;
    
    /**
     * Initializes a session using the information of the hosting player
     *
     * @param host the player who hosts the game
     * @param roomName the name of the room which will be displayed in the lobby
     * @throws RemoteException
     */
    public Session(Player host, String roomName) throws RemoteException {
        this();
        this.gameSettings = new GameSettings();
        this.host = host;
        this.players = new ArrayList<>();
        this.roomName = roomName;
        publisher = new RemotePublisher();
        publisher.registerProperty("sessionsettingsprop");
        publisher.registerProperty("cancelgameprop");
        publisher.registerProperty("startgameprop");
    }

    /**
     * Constructor without any graphics Made for the unit testing.
     *
     */
    public Session(Player host, String roomName, boolean any) throws RemoteException {
        this();
        this.gameSettings = new GameSettings(true);
        this.host = host;
        this.players = new ArrayList<>();
        this.roomName = roomName;
    }

    /**
     * Create the new registry for the session on a specific IP and port.
     *
     * @throws RemoteException
     */
    public void createNewRegistry() throws RemoteException {
        registry = LocateRegistry.createRegistry(5555);
        registry.rebind("SessionSettings", this);
    }

    /**
     * Unexport the unicastobject this removes the session/registry
     *
     * @throws NoSuchObjectException
     */
    public void removeRegistry() throws NoSuchObjectException {
        UnicastRemoteObject.unexportObject(registry, true);
    }

    /**
     * Cancels the session for all the clients. Push message to all the
     * listeners. Set the variable to 1. Each SessionOnlinePlayer will continue
     * to check if this variable is changed.
     *
     * @throws RemoteException
     */
    public void cancelSessionForClients() throws RemoteException {
        publisher.inform("cancelgameprop", 0, 1);
    }

    /**
     * Will be used for each player that connects to a host. A player needs to
     * know the initial GameSettings to see the correct map/settings.
     *
     * @return initial GameSettings
     */
    @Override
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Set the GameSettings for this session. Also updates the old GameSettings
     * to the new GameSettings to the clients.
     *
     * @param settings the new gamesettings
     * @throws RemoteException
     */
    public void setGameSettings(GameSettings settings) throws RemoteException {
        GameSettings oldsettings = this.gameSettings;
        this.gameSettings = settings;
        publisher.inform("sessionsettingsprop", oldsettings, this.gameSettings);
    }

    /**
     * @return The host of this session
     */
    public Player getHost() {
        return host;
    }

    /**
     * @return List of players that joined this session
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a player to the list of players
     *
     * @param p Player to add
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Removes a player from the list of players Will kick the player from the
     * lobby afterwards
     *
     * @param p Player to remove
     */
    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: logic for kicking players
    }

    /**
     * Sets the list of players for a Session.
     *
     * @param playerList
     */
    public void setPlayerList(ArrayList<Player> playerList) {
        this.players = playerList;
    }

    /**
     * Get the name of the room.
     *
     * @return roomName which will be displayed in the lobby
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Get the id of the session. This sessionid will be the id saved in the DB
     *
     * @return id as DB id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id which is the same as in the db.
     *
     * @param id as DB id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Starts the game for all the clients. Push message to all the listeners.
     * Set the variable to 1. Each SessionOnlinePlayer will continue to check if
     * this variable is changed.
     */
    public void startGame() throws RemoteException {
        publisher.inform("startgameprop", 0, 1);
    }

    /**
     * Subscribe to a RemotePublisher. From now on each update gives a
     * propertychange to the client.
     *
     * @param listener
     * @param property
     * @throws RemoteException
     */
    @Override
    public void subscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.subscribeRemoteListener(listener, property);
    }

    /**
     * Unsubscribe from a RemotePublisher. From now on there won't be any
     * updates from the remotepublisher to the client.
     *
     * @param listener
     * @param property
     * @throws RemoteException
     */
    @Override
    public void unsubscribeRemoteListener(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.unsubscribeRemoteListener(listener, property);
    }

    /**
     *
     * @return String which shows the hostname+roomname and if the session is
     * full or not.
     */
    @Override
    public String toString() {
        if (this.host == null || this.gameSettings == null) {
            return "Remove session from DB";
        }
        return host.getName() + " " + this.roomName + "     " + this.getPlayers().size() + "/" + this.gameSettings.getMaxPlayersSession();
    }

}
