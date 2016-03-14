package wotf.classes;

/**
 * Created by wesse on 14/03/2016.
 */
public class Map {
    private double gravityModifier;
    private int waterLevel;
    private int width;
    private int height;
    private boolean[][] terrain;
    private Sprite landscapeSprite;
    private Sprite backgroundSprite;

    public Map(String filename) {
        // logic for loading
    }

    public void readMap(String filename) {
        // logic for reading map
    }

    public double getGravityModifier() {
        return gravityModifier;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean[][] getTerrain() {
        return terrain;
    }

    public void setTerrain(boolean[][] terrain) {
        this.terrain = terrain;
    }

    public Sprite getLandscapeSprite() {
        return landscapeSprite;
    }

    public Sprite getBackgroundSprite() {
        return backgroundSprite;
    }
}
