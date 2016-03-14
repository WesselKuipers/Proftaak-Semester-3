package wotf.classes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Session {

    private Player host;
    private List<Player> players;
    private List<Team> teams;

    private int maxTurns;
    private int maxTime;
    private int withdrawTime;
    private boolean fallingDamage;
    private boolean suddenDeath;

    public Session(Player host) {
        this.host = host;
        players = new ArrayList<>();
        teams = new ArrayList<>();

        maxTurns = 30;
        maxTime = 30*60;
        withdrawTime = 3;
        fallingDamage = true;
        suddenDeath = true;
    }

    public Player getHost() {
        return host;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(int withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    public boolean isFallingDamage() {
        return fallingDamage;
    }

    public void setFallingDamage(boolean fallingDamage) {
        this.fallingDamage = fallingDamage;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public void addTeam(String name, Color color) {
        teams.add(new Team(name, color));
    }

    public void removeTeam(Team t) {
        teams.remove(t);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: logic for kicking players
    }


    public void startGame() {
        // TODO: handle code for creating game object
    }
}
