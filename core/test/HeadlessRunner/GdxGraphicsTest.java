/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeadlessRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.extra.Pathfinder;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Gebruiker
 */
@RunWith(GdxTestRunner.class)
public class GdxGraphicsTest {

    @Test
    public void testMaps() {
        ArrayList<Texture> maptextures = new ArrayList<>();
        ArrayList<String> mapslist = new ArrayList<>();
        FileHandle dirHandle = Gdx.files.internal("../core/assets/maps");
        for (FileHandle entry : dirHandle.list()) {
            mapslist.add(entry.toString());
            maptextures.add(new Texture(entry));
        }
        assertNotNull(dirHandle);
        assertEquals(5, mapslist.size());

    }

    @Test
    public void testBullet() {
        FileHandle handle = Gdx.files.internal("../core/assets/badlogic.jpg");
        assertNotNull(handle);
    }

    @Test
    public void createTexture() {
        int width = 32;
        int height = 32;
        Pixmap pixmap = createProceduralPixmap(width, height);
        Texture texture = new Texture(pixmap);
        assertNotNull(texture);
    }

    @Test
    public void createSprite() {
        int width = 32;
        int height = 32;
        Pixmap pixmap = createProceduralPixmap(width, height);
        Texture texture = new Texture(pixmap);
        Sprite sprite = new Sprite(texture);
        assertNotNull(sprite);
    }

    @Test
    public void testPathfinder() {
        System.out.println(Pathfinder.getRelativePath());
        String workingDir = System.getProperty("user.dir");
        FileHandle handle = Gdx.files.absolute(Pathfinder.getRelativePath() + "badlogic.jpg");
        assertNotNull(handle);
    }

    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
// Fill square with red color at 50% opacity
        pixmap.setColor(1, 0, 0, .5f);
        pixmap.fill();
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }
}
