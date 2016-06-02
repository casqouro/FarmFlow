package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    
	@Override
	public void create () {
            batch = new SpriteBatch();                
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);
            camera = new OrthographicCamera(510, 510);
            camera.position.set(camera.viewportWidth * 0.5f, camera.viewportHeight * 0.5f, 0);
            camera.update();            
                        
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

            batch.begin();
            batch.draw(background, 0, 0);
            batch.end();
            
            stage.act();
            stage.draw();            
	}       
}