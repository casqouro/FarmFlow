package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FarmFlow extends ApplicationAdapter {
        SpriteBatch batch;
        Stage stage;
        Camera camera;
        Table table;
        GridBoard board;
        TextureRegion background;
        
        TextureAtlas everything; // TEMP
        TextureRegion highlight1;
        TextureRegion highlight2;
        TextureRegion highlight3;
    
	@Override
	public void create () {
            batch = new SpriteBatch();                
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);
            camera = new OrthographicCamera(510, 510);
            camera.position.set(camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f, 0);
            camera.update();    
            
            everything = new TextureAtlas(Gdx.files.internal("everything.atlas")); // TEMP
                        
            try {
                board = new GridBoard(1);
            } catch (IOException ex) {
                Logger.getLogger(FarmFlow.class.getName()).log(Level.SEVERE, null, ex);
            }                        
            
            background = board.getBackground();
                        
            table = new Table();
            table.addActor(board);
            stage.addActor(table);          
	}

	@Override
	public void render () {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);                        
            batch.setProjectionMatrix(camera.combined);
            
            stage.act();
            stage.draw();            

            batch.begin();
            //batch.draw(background, 0, 0);
            
            // TEMP
            for (int a = 0; a < 25; a++) {
                int location = board.locations[a/5][a%5];
                if (location != 0) {
                    batch.draw(everything.findRegion("highlight" + location), (a/5) * 100 + 4, (a%5) * 100 + 4);  
                    System.out.println((a/5) * 100);
                }
            }
            
            batch.end();                       
	}       
}