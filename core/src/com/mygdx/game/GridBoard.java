package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;

public class GridBoard extends WidgetGroup {
    private final TextureAtlas everything;    
    private final TextureRegion background;
    private final int gridSize = 5;    
    private final Array<Point> pathPoints;
    private SequenceAction sequence;
    public int[][] locations;
    
    GridBoard(int level) throws IOException {
        everything = new TextureAtlas(Gdx.files.internal("everything.atlas"));
        pathPoints = new Array<>();
        locations = new int[5][5];
        
        FileHandle levels = Gdx.files.internal("levels.txt");
        BufferedReader br = new BufferedReader(levels.reader());       
        String line = br.readLine();
        line = br.readLine();
        String[] token;
        
        do {
            token = line.split(" ");
        }
        while (Integer.valueOf(token[0]) != level); 
                        
        int sentinel = Integer.valueOf(token[2]);
        int attribute = 3;
        do {
            Point start = new Point(Integer.valueOf(token[attribute + 1]), Integer.valueOf(token[attribute + 1]));
            Point end = new Point(Integer.valueOf(token[attribute + 2]), Integer.valueOf(token[attribute + 3]));
            CharacterActor actor = new CharacterActor(token[attribute], start, end);
            createActor(actor);
            sentinel--;
            attribute += 5;
        } while (sentinel > 0);  
        
        background = new TextureRegion(everything.findRegion(token[1]));
        sequence = new SequenceAction();
    }
    
    private void createActor(CharacterActor actor) {
        this.addActor(actor);
        actor.addListener(new CharacterClickListener());
    }
    
    public TextureRegion getBackground() {
        return background;
    }
    
    private class CharacterClickListener extends ClickListener {         
        // declaring here is a waste, as EACH listener will have ITS OWN set
        // once it's working, move them out
        private int xOrigin = 0;
        private int yOrigin = 0;
        
        @Override        
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();           
            actor.setAnimation(2);    
            actor.setSound(2);    
            xOrigin = (int) (event.getStageX() / 100);
            yOrigin = (int) (event.getStageY() / 100);            
            pathPoints.add(new Point(xOrigin, yOrigin));
            locations[xOrigin][yOrigin] = 1; // fine for static testing, but not nearly correct overall
            return true; // will receive all touchDragged + touchUp until receiving touchUp
        }              
        
        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);                         
            trackPath(event.getStageX(), event.getStageY());
        }
         
        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);  
            actor.setSound(0);    
                        
            buildPath();
            System.out.println(sequence.getActions().size);
            actor.addAction(sequence);
            pathPoints.removeRange(0, pathPoints.size - 1);      
        }             
    }    
    
    // incorrect but functional - (0,0) -> (0,3) is two actions instead of one...
    // that's why always using a duration of "1" works fine, when it shouldn't
    private void buildPath() {
        if (pathPoints.size > 1) {
            Point segmentStart = pathPoints.get(0);
            Point segmentEnd = pathPoints.get(1); 
            Point segmentNext = pathPoints.get(1);
            Point lastPoint = pathPoints.get(pathPoints.size - 1);
            int traverse = 1;
            int direction = calculateDirection(segmentStart, segmentNext);
            
            do {                
                while (calculateDirection(segmentStart, segmentNext) == direction) {
                    traverse++;
                    if (traverse < pathPoints.size) {
                        segmentEnd = segmentNext;
                        segmentNext = pathPoints.get(traverse);
                    } else {
                        direction = 5;
                        segmentEnd = segmentNext; 
                    }
                }
                                
                sequence.addAction(moveTo(segmentEnd.x * 100 + segmentEnd.x * 2, segmentEnd.y * 100 + segmentEnd.y * 2, 1));                               
                segmentStart = segmentEnd; 
                direction = calculateDirection(segmentStart, segmentNext);
            } while (!segmentEnd.equals(lastPoint));                                         
            
            // sequence is emptied when all queued actions are completed
            sequence.addAction(run(new Runnable() {
                @Override
                public void run() {                    
                    sequence.reset();                    
                }                
            }));          
        }
    }
        
    private void trackPath(float x, float y) {        
        Point current = pathPoints.get(pathPoints.size - 1);
        Point next = new Point((int) (x / 100), (int) (y / 100));
                
        // Adds successive adjacent points when advancing
        if (!current.equals(next) && isAdjacent(current, next) && locations[next.x][next.y] == 0) {          
            pathPoints.add(next);                
            locations[next.x][next.y] = 1;            
            return;
        }
        
        // Removes points when retreating
        if (pathPoints.size >= 2) {
            Point previous = pathPoints.get(pathPoints.size - 2);
            
            if (next.equals(previous)) {
                pathPoints.removeIndex(pathPoints.size - 1);
                locations[current.x][current.y] = 0;
            }
        }
    }
    
    private int calculateDuration(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
    
    // Check if slope is horizontal or vertical
    private int calculateDirection(Point a, Point b) {
        if (Math.abs(a.x - b.x) == 0 && Math.abs(a.y - b.y) == 1) {           
            return 1;
        }
        
        if (Math.abs(a.x - b.x) == 1 && Math.abs(a.y - b.y) == 0) {           
            return 0;
        }        
        
        return 5;
    }
        
    // Check adjacency with exclusive x=0&y=1 OR x=1&y=0
    private boolean isAdjacent(Point a, Point b) {
        int x = Math.abs(a.x - b.x);
        int y = Math.abs(a.y - b.y);
        
        if (x == 0 && y == 1) {
            return true;
        }
        
        if (x == 1 && y == 0) {
            return true;
        }
        
        return false;
    }
    
    // Makes debugging easier
    private void printPath() {
        for (int a = 0; a < pathPoints.size; a++) {
            System.out.println(pathPoints.get(a).toString());
        }
        System.out.println();
    }
}

/*
    private class CharacterClickListener extends ClickListener {         
        // declaring here is a waste, as EACH listener will have ITS OWN set
        // once it's working, move them out
        private int xOrigin = 0;
        private int yOrigin = 0;
        
        @Override        
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();           
            actor.setAnimation(2);    
            actor.setSound(2);    
            //sequence.setActor(actor);            
            
            xOrigin = (int) (event.getStageX() / 100);
            yOrigin = (int) (event.getStageY() / 100);            
            pathPoints.add(new Point(xOrigin, yOrigin));
            segmentStart = pathPoints.get(0); // track for initial action sequence point
            segmentLength = 1;
            locations[xOrigin][yOrigin] = 1; // fine for static testing, but not nearly correct overall
            return true; // will receive all touchDragged + touchUp until receiving touchUp
        }              
        
        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);                         
            trackPath(event.getStageX(), event.getStageY());
        }
 
        If Advancing:
•	Marks the tile according to character
        
	If Retreating:
•	Marks the previous tile as empty
•	Removes the previous tile from the tile path tracking data-structure
•	De-highlights the tile
        
        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            CharacterActor actor = (CharacterActor) event.getListenerActor();
            actor.setAnimation(1);  
            actor.setSound(0);                 
                                  
            int duration = Math.abs(segmentStart.x - segmentEnd.x) + Math.abs(segmentStart.y - segmentEnd.y);            
            sequence.addAction(moveTo(segmentEnd.x * 100 + segmentEnd.x * 2, segmentEnd.y * 100 + segmentEnd.y * 2, duration));            

            // sequence is emptied when all queued actions are completed
            sequence.addAction(run(new Runnable() {
                @Override
                public void run() {
                    sequence.reset();                    
                    System.out.println(sequence.getActions().size);
                }
                
            }));            
            
            actor.addAction(sequence);
            pathPoints.removeRange(0, pathPoints.size - 1);      
        }
        
	If Advancing:
•	Instructs actor to play ANIMATION-MOVE
•	Instructs actor to play SOUND-MOVE
	If Retreating:
•	Actor moved immediately
•	Instructs actor to play CLICKED-MOVE       
    }    
*/     