package wotf.classes;

import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Game {
    private Player host;
    private List<Player> players;
    private List<Team> teams;

    private Vector2 wind;
    private int elapsedTime;
    private int turnTime;
    private int maxTime;
    private int withdrawTime;
    private boolean fallingDamage;
    private boolean suddenDeath;
    private Team activeTeam;

    public Game(List<Player> players, List<Team> teams, Vector2 wind, int elapsedTime, int turnTime, int withdrawTime, boolean fallingDamage, boolean suddenDeath, Team activeTeam) {
        this.host = players.get(0);
        this.players = players;
        this.teams = teams;
        this.wind = wind;
        this.elapsedTime = elapsedTime;
        this.turnTime = turnTime;
        this.withdrawTime = withdrawTime;
        this.fallingDamage = fallingDamage;
        this.suddenDeath = suddenDeath;
        this.activeTeam = activeTeam;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getPlayer(String ip) {
        return players
                .stream()
                .filter(x -> x.getIp().equals(ip))
                .findFirst()
                .get();
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Player getHost() {
        return host;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public Team getTeam(int index) {
        return teams.get(index);
    }

    public Vector2 getWind() {
        return wind;
    }

    public void setWind(Vector2 wind) {
        this.wind = wind;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getWithdrawTime() {
        return withdrawTime;
    }

    public boolean isFallingDamage() {
        return fallingDamage;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public Team getActiveTeam() {
        return activeTeam;
    }

    public void setActiveTeam(Team activeTeam) {
        this.activeTeam = activeTeam;
    }

    // TODO: Function for handling new/next turn
}
