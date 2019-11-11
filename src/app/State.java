package app;

import java.util.HashMap;
import java.awt.Graphics2D;

public class State{
    private HashMap<String, UnitRep> units = new HashMap<String, UnitRep>();
    public State(){
        units = new HashMap<String, UnitRep>();
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
    public void drawState(Graphics2D g2d, Board brd){
        for(UnitRep u : this.units.values()){
            u.draw(g2d, brd);
        }
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
        return nState;
    }
}