package app;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.*;  

public class Board extends JPanel implements Runnable{
    private static final int MS_PER_LOOP = 100;
    private final int B_WIDTH = 1000, B_HEIGHT = 500+100;

    private Thread animator;
    private State[] turns;
    private State curState;
    public Image mapBoard, roverImgRed, roverImgBlue;

    private static PlayStatus ps;

    public Board(State[] turns){
        setLayout(null);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        // handling states
        this.turns = turns;
        // loading map image
        this.mapBoard = (new ImageIcon("src/resources/map.png")).getImage();
        this.roverImgBlue = (new ImageIcon("src/resources/rover_blue.png")).getImage();
        this.roverImgRed = (new ImageIcon("src/resources/rover_red.png")).getImage();
        this.curState = turns.length > 0 ? turns[0] : null;
        ps = new PlayStatus(this.turns.length);

        // adding buttons
        this.addButtons();
    }
    @Override
    public void paintComponent(Graphics g){
        // g2d
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // rh
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        // draw
        // DRAW BACKGROUND
        g2d.drawImage(this.mapBoard, 0, 0, this);
        // DRAW STATE (if exists)
        if(this.curState != null){
            this.curState.drawState(g2d, this);
        }
        // DRAW TURN INFO
        g2d.setColor(Color.BLACK);
        g2d.drawString(ps.getTurnStringWithFrac(), 500, 550);
    }
    @Override
    public void addNotify() { 
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }
    @Override
    public void run(){
        while(true){
            // update game state
            ps.tick();
            if(ps.running()){
                this.curState = this.turns[ps.getTurn()].getTransitionState(this.turns[ps.getTurn() + 1], ps.getFrac());
            }else{
                this.curState = this.turns[ps.getTurn()];
            }
            /*if(running){
                curTime += (System.currentTimeMillis() - lastLoop);
                lastLoop = System.currentTimeMillis();
            }else{
                curTime = ((int)(curTime / Constants.MS_PER_TURN)) * Constants.MS_PER_TURN;
                this.curFrac = -1.0;
            }
            if(curTime == 0){ 
                this.curState = this.turns.length > 0 ? this.turns[0] : null;
                this.curFrac = 0.0;
            }else{
                int formingIntoIndex = (((int) curTime) / Constants.MS_PER_TURN) + 1;
                if(formingIntoIndex < this.turns.length){
                    double fracComplete = running ? (curTime % Constants.MS_PER_TURN)/((double)Constants.MS_PER_TURN) : 0;
                    this.curFrac = fracComplete;
                    this.curState = running ? this.turns[formingIntoIndex - 1].getTransitionState(this.turns[formingIntoIndex], fracComplete) : this.turns[formingIntoIndex - 1];
                }else{
                    this.curState = this.turns[this.turns.length - 1];
                    curTime = Constants.MS_PER_TURN * this.turns.length;
                }
            } */
            // repaint
            repaint();
        }
    }

    // BUTTON HANDLING
    private JButton genButton(String label, int cx, int y, int height){
        JButton but = new JButton(label);
        int width = Math.max(label.length() * Constants.BUTTON_WIDTH_PER_CHAR, Constants.MIN_BUTTON_WIDTH);
        but.setLocation(cx - width/2, y);
        but.setSize(width, height);
        return but;
    }
    private void addButtons(){
        // PAUSE / PLAY
        JButton pausePlayButton = genButton("Pause/Play", 500, 505, 20);
        pausePlayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.toggleOnOff();
            }
        });
        this.add(pausePlayButton);

        // FAST FORWARD 1 TURN
        JButton forwardTurnButton = genButton(">", pausePlayButton.getX() + pausePlayButton.getWidth() + Constants.PX_BTWN_BUTTONS + Constants.MIN_BUTTON_WIDTH/2, 505, 20);
        forwardTurnButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.ffTurn();
            }
        });
        this.add(forwardTurnButton);

        // REVERSE 1 TURN
        JButton backTurnButton = genButton("<", pausePlayButton.getX() - Constants.PX_BTWN_BUTTONS - Constants.MIN_BUTTON_WIDTH/2, 505, 20);
        backTurnButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.rwTurn();
            }
        });
        this.add(backTurnButton);
    }

}