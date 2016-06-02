package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

public class CharacterActor extends Widget {
    private float elapsedTime = 0;    
    
    private final Sound idleSound;
    private final Sound moveSound;
    private final Sound clickedSound;
    
    private       Animation currentAnim;
    private final Animation idleAnim;
    private final Animation moveAnim;
    private final Animation clickedAnim;
    
    private final Point startingPosition;
    private final Point currentPosition;
    private final Point destinationPosition;   
    
    private final TextureAtlas everything;
    private final TextureRegion cow;
    
    CharacterActor(String type, Point start, Point destination) {   
        everything = new TextureAtlas(Gdx.files.internal("everything.atlas"));        
        
        idleSound = Gdx.audio.newSound(Gdx.files.internal(type + "idle.ogg"));
        moveSound = Gdx.audio.newSound(Gdx.files.internal(type + "move.ogg"));
        clickedSound = Gdx.audio.newSound(Gdx.files.internal(type + "clicked.ogg"));                                     
        
        idleAnim = new Animation(1 / 4f, everything.findRegions("cowidle"), PlayMode.LOOP);
        moveAnim = new Animation(1 / 4f, everything.findRegions("cowmove"), PlayMode.LOOP);
        clickedAnim = new Animation(1 / 4f, everything.findRegions("cowclicked"), PlayMode.LOOP);
        currentAnim = idleAnim; // idle == green, clicked == blue, move == red
                
        startingPosition = start;
        currentPosition = start;
        destinationPosition = destination;
        
        cow = everything.findRegion("cow");  
        setDimensions(cow.getRegionWidth(), cow.getRegionHeight());
        setPosition(start.x, start.y);
    }
    
    @Override
    public void act(float delta)
    {
        Array<Action> actions = getActions();
        if (actions.size > 0) {
                if (getStage() != null && getStage().getActionsRequestRendering()) Gdx.graphics.requestRendering();
                for (int i = 0; i < actions.size; i++) {
                        Action action = actions.get(i);
                        if (action.act(delta) && i < actions.size) {
                                Action current = actions.get(i);
                                int actionIndex = current == action ? i : actions.indexOf(action, true);
                                if (actionIndex != -1) {
                                        actions.removeIndex(actionIndex);
                                        action.setActor(null);
                                        i--;
                                }
                        }
                }
        }        
        
        elapsedTime += delta;
    }    
    
    public final void setDimensions(float x, float y) {
        this.setWidth(x);
        this.setHeight(y);
    }
    
    @Override
    public final void setPosition(float x, float y) {
        this.setX(x * 100 + x * 2);
        this.setY(y * 100 + y * 2);
    }
    
    public void setAnimation(int num) {
        if (num == 0) {
            System.out.println("current to idle");
            currentAnim = idleAnim;
        }
        
        if (num == 1) {
            System.out.println("current to move");            
            currentAnim = moveAnim;
        }
        
        if (num == 2) {
            System.out.println("current to clicked");            
            currentAnim = clickedAnim;
        }
    }
    
    public void setSound(int num) {
        if (num == 0) {
            idleSound.play();
        }
        
        if (num == 1) {
            moveSound.play();
        }
        
        if (num == 2) {
            clickedSound.play();
        }
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentAnim.getKeyFrame(elapsedTime), getX(), getY());                         
    }        
}