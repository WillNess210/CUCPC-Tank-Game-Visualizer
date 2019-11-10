package app;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;

public class UnitRep{
    private int owner, id, type, x, y, param1;
    public UnitRep(int owner, int id, int type, int x, int y, int param1){
        this.owner = owner;
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.param1 = param1;
    }
    public int getType(){
        return this.type;
    }
    public void draw(Graphics2D g2d, Board brd){
        Image img = null;
        if(this.type == 0){
            img = this.owner == 0 ? brd.roverImgBlue : brd.roverImgRed;
        }
        if(img == null){
            System.out.println("Error: could not find image for unit of type " + this.getType());
            return;
        }
        // DRAWING UNIT w/ ID ABOVE IT
        int imgW = img.getWidth(null);
        int imgH = img.getHeight(null);
        g2d.drawImage(img, this.x - imgW/2, this.y - imgH/2, brd);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(this.id + "", this.x - 5, this.y - imgH/2 - 5);
        if(this.type == 0){
            g2d.setColor(Color.WHITE);
            g2d.drawString(this.param1 + "", this.x - 5, this.y - 1);
        }
    }
    public int getID(){
        return this.id;
    }
    public boolean is(UnitRep u){
        return u.is(this.id);
    }
    public boolean is(int nid){
        return this.id == nid;
    }
    public UnitRep getTransitionUnit(UnitRep next, double fracCompleted){
        int nx = (int) (this.x + ((next.x - this.x) * fracCompleted));
        int ny = (int) (this.y + ((next.y - this.y) * fracCompleted));;
        int np1 = (int) (this.param1 + ((next.param1 - this.param1) * fracCompleted));
        return new UnitRep(this.owner, this.id, this.type, nx, ny, np1);
    }
}