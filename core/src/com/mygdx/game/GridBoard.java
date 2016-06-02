package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;

public class GridBoard extends WidgetGroup {
    private final TextureAtlas everything;    
    private final TextureRegion background;
    private final int gridSize = 5;
    
    GridBoard(int level) throws IOException {
        FileHandle levels = Gdx.files.internal("levels.txt");
        BufferedReader br = new BufferedReader(levels.reader());       
        String line = br.readLine();
        line = br.readLine();
        String[] token;
        
        do {
            token = line.split(" ");
        }
        while (Integer.valueOf(token[0]) != level); 
        
        everything = new TextureAtlas(Gdx.files.internal("everything.atlas"));
                
        int sentinel = Integer.valueOf(token[2]);
        do {
            Point start = new Point(Integer.valueOf(token[4]), Integer.valueOf(token[5]));
            Point end = new Point(Integer.valueOf(token[6]), Integer.valueOf(token[7]));
            CharacterActor actor = new CharacterActor(token[3], start, end);
            createActor(actor);
            sentinel--;
        } while (sentinel > 0);  
        
        background = new TextureRegion(everything.findRegion(token[1]));        
    }
    
    private void createActor(CharacterActor actor) {
        this.addActor(actor);
        actor.addListener(new CharacterClickListener());
    }
    
    public TextureRegion getBackground() {
        return background;
    }
    
    private class CharacterClickListener extends ClickListener {         
        private int xOrigin = 0;
        private int yOrigin = 0;
        
        @Override        
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();           
            actor.setAnimation(2);    
            actor.setSound(2);  
            
            xOrigin = (int) (event.getStageX() / 100);
            yOrigin = (int) (event.getStageY() / 100);
            
            return true; // will receive all touchDragged + touchUp until receiving touchUp
        }
        
        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);               
            //actor.setSound(1);
        }
        
        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);  
            actor.setSound(0);
            
            int xDestination = (int) (event.getStageX() / 100);
            int yDestination = (int) (event.getStageY() / 100);
            
            int diff = Math.abs(xOrigin - xDestination) + Math.abs(yOrigin - yDestination);
            
            int xNew = xDestination * 100 + xDestination * 2;
            int yNew = yDestination * 100 + yDestination * 2;
            
            actor.addAction(moveTo(xNew, yNew, diff));
        }
    }    
}