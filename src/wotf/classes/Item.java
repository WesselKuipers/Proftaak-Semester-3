package wotf.classes;

/**
 * Created by wesse on 14/03/2016.
 */
public class Item {
    private String name;
    private int power;
    private int blastRadius;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
    }

    public void activate() {
        // logic for item activation
    }
}
