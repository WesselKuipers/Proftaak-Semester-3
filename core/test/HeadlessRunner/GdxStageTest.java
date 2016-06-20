/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HeadlessRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.extra.Pathfinder;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Items.Bazooka;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.Unit;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Gebruiker
 */
@RunWith(GdxTestRunner.class)
public class GdxStageTest {

    @Test
    public void initStage() {
        //GameStage stage = new GameStage(); 
        Team team = new Team("Alpha", Color.BLACK);
        team.addUnit("UnitTeam", 50);
        
        Item bazooka = new Bazooka();
        team.getUnit(0).selectWeapon(bazooka);
    }
}
