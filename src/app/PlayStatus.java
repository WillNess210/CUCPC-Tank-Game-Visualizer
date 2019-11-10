package app;

public class PlayStatus{
    public int turn, maxTurn;
    private boolean running;
    public double prog;
    private long last;
    private long curTime;
    public PlayStatus(int maxTurn){
        this.maxTurn = maxTurn;
        this.turn = 0;
        this.running = false;
        this.prog = 0.0;
        this.last = -1;
        this.curTime = -1;
    }
    // main running
    public void tick(){
        if(running){
            this.curTime += (System.currentTimeMillis() - this.last);
            this.last = System.currentTimeMillis();
            if(this.curTime >= Constants.MS_PER_TURN){
                this.turn++;
                this.curTime = 0;
                this.prog = 0;
                // CHECK IF AT END
                if(this.isAtEnd()){
                    this.turnOff();
                    return;
                }
            }else{
                this.prog = this.curTime / ((double) Constants.MS_PER_TURN);
            }
        }
    }
    private boolean isAtEnd(){
        return this.turn >= this.maxTurn - 1;
    }
    // helpers 
    public void setTurn(int turn){
        this.turn = turn;
    }

    public boolean running(){
        return this.running;
    }

    public void toggleOnOff(){
        if(this.running){
            this.turnOff();
        }else{
            this.turnOn();
        }
    }

    public void turnOff(){
        this.running = false;
        this.prog = 0;
        this.curTime = 0;
    }
    public void turnOn(){
        if(this.isAtEnd()){
            this.turn = 0;
        }
        this.last = System.currentTimeMillis();
        this.running = true;
        this.curTime = 0;
    }
    public void ffTurn(){
        this.turnOff();
        this.turn = Math.min(this.turn + 1, this.maxTurn - 1);
    }
    public void rwTurn(){
        this.turnOff();
        this.turn = Math.max(this.turn - 1, 0);
    }
    public int getTurn(){
        return this.turn;
    }
    public double getFrac(){
        return this.prog;
    }
    public String getTurnString(){
        return "Turn: " + this.turn;
    }
    public String getTurnStringWithFrac(){
        if(this.running){
            String fracRep = (new String(this.prog + "")).substring(1);
            return "Turn: " + this.turn + fracRep;
        }
        return this.getTurnString();
    }
}