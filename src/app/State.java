package app;

import java.util.HashMap;
import java.awt.Graphics2D;
import java.awt.Color;

public class State{
    private HashMap<String, UnitRep> units = new HashMap<String, UnitRep>();
    private int[] scores;
    public State(){
        units = new HashMap<String, UnitRep>();
        scores = new int[2];
        scores[0] = -1;
        scores[1] = -1;
    }
    public void setScore(int id, int coins){
        this.scores[id] = coins;
    }
    public int getScore(int id){
        return this.scores[id];
    }
    public void addUnit(String lineFromLog){
        String[] splitLine = lineFromLog.split(" ");
        int[] splitCast = new int[splitLine.length];
        for(int i = 0; i < splitCast.length; i++){
            splitCast[i] = Integer.parseInt(splitLine[i]);
        }
        this.addUnit(splitCast[0], splitCast[1], splitCast[2], splitCast[3], splitCast[4], splitCast[5]);
    }
    public void addUnit(int owner, int id, int type, int x, int y, int param1){
        this.addUnit(new UnitRep(owner, id, type, x, y, param1));
    }
    public void addUnit(UnitRep u){
        units.put(u.getHashMapID(), u);
    }
    public void drawUnits(Graphics2D g2d, Board brd){
        for(UnitRep u : this.units.values()){
            u.draw(g2d, brd);
        }
    }
    public void drawState(Graphics2D g2d, Board brd){
        this.drawUnits(g2d, brd);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Blue Score:   " + this.scores[0], 5, 515);
        g2d.drawString("Red Score:    " + this.scores[1], 800, 515);
    }
    public UnitRep getUnitRep(UnitRep u){
        return this.getUnitRep(u.getHashMapID());
    }
    public UnitRep getUnitRep(String hashMapID){
        return this.units.get(hashMapID);
    }
    public State getTransitionState(State next, double fracComplete){
        State nState = new State();
        for(UnitRep u : this.units.values()){
            UnitRep partner = next.getUnitRep(u);
            if(partner != null){
                UnitRep trans = u.getTransitionUnit(partner, fracComplete);
                nState.addUnit(trans);
            }
        }
        // mid-scores
        nState.setScore(0, (int)(this.getScore(0) + (next.getScore(0) - this.getScore(0)) * fracComplete));
        nState.setScore(1, (int)(this.getScore(1) + (next.getScore(1) - this.getScore(1)) * fracComplete));
        return nState;
    }
}