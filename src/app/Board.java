package app;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.event.ChangeEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.*;  

public class Board extends JPanel implements Runnable{
    private static final int MS_PER_LOOP = 100;
    private int B_WIDTH;
    private int B_HEIGHT;
    private final long MIN_FRAME_LENGTH = (long)(1000/30);
    private Thread animator;
    private State[] turns;
    private State curState, sites;
    public Image mapBoard, roverImgRed, roverImgBlue, tankImgRed, tankImgBlue, siteImg, explosionImg;
    private static PlayStatus ps;
    private JPanel controlPanel, sliderPanel, turnPanel;
    
    public Board(State sites, State[] turns, int width, int height, int deploymentType){
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        this.controlPanel = new JPanel();
        this.controlPanel.setLayout(new BorderLayout());
        this.sliderPanel = new JPanel(new BorderLayout());
        this.turnPanel = new JPanel(new FlowLayout());
        this.B_WIDTH = width;
        this.B_HEIGHT = height+125;
        
        // handling states
        this.turns = turns;
        // loading map image
        this.mapBoard = (new ImageIcon(Board.class.getResource("/resources/map.png"))).getImage();
        this.mapBoard = this.mapBoard.getScaledInstance(this.B_WIDTH, this.B_HEIGHT+125, Image.SCALE_DEFAULT);
        this.roverImgBlue = (new ImageIcon(Board.class.getResource("/resources/rover_blue.png"))).getImage();
        this.roverImgRed = (new ImageIcon(Board.class.getResource("/resources/rover_red.png"))).getImage();
        this.tankImgBlue = (new ImageIcon(Board.class.getResource("/resources/tank_blue.png"))).getImage();
        this.tankImgRed = (new ImageIcon(Board.class.getResource("/resources/tank_red.png"))).getImage();
        this.siteImg = (new ImageIcon(Board.class.getResource("/resources/site.png"))).getImage();
        this.explosionImg = (new ImageIcon(Board.class.getResource("/resources/explosion.png"))).getImage();
        this.curState = turns.length > 0 ? turns[0] : null;
        ps = new PlayStatus(this.turns.length);

        this.sites = sites;
        // adding buttons
        this.addButtons();
        controlPanel.add(this.turnPanel, BorderLayout.NORTH);
        controlPanel.add(this.sliderPanel, BorderLayout.SOUTH);
        this.add(this.controlPanel, BorderLayout.SOUTH);
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
        // DRAW SITES
        if(this.sites != null){
            this.sites.drawUnits(g2d, this);
        }
        // DRAW STATE (if exists)
        if(this.curState != null){
            this.curState.drawState(g2d, this);
        }
        // DRAW TURN / WINNER INFO
        if(ps.isAtEnd()) {
        	int winner = ps.getCurrentWinner(turns);
        	g2d.setColor(ps.getWinnerColor(winner));
        	switch(winner) {
        		case -1:
        			g2d.drawString("Winner: TIE" , this.B_WIDTH/2, this.B_HEIGHT+15);
        			g2d.setColor(Color.BLACK);
                    g2d.drawString(ps.getTurnStringWithFrac(), this.B_WIDTH/2-70, this.B_HEIGHT+15);
        			break;
        		case 0:
        			g2d.drawString("Winner: BLUE (0)" , this.B_WIDTH/2, this.B_HEIGHT+15);
        			g2d.setColor(Color.BLACK);
                    g2d.drawString(ps.getTurnStringWithFrac(), this.B_WIDTH/2-70, this.B_HEIGHT+15);
        			break;
        		case 1:
        			g2d.drawString("Winner: RED (1)" , this.B_WIDTH/2, this.B_HEIGHT+15);
        			g2d.setColor(Color.BLACK);
                    g2d.drawString(ps.getTurnStringWithFrac(), this.B_WIDTH/2-70, this.B_HEIGHT+15);
        			break;
        	}
        }else {
        	g2d.setColor(Color.BLACK);
            g2d.drawString(ps.getTurnStringWithFrac(), this.B_WIDTH/2, this.B_HEIGHT+15);
        }
        
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
        	long turnStart = System.currentTimeMillis();
            // update game state
            ps.tick();
            if(ps.running()){
                this.curState = this.turns[ps.getTurn()].getTransitionState(this.turns[ps.getTurn() + 1], ps.getFrac());
            }else{
                this.curState = this.turns[ps.getTurn()];
            }
            // repaint
            repaint();
            while(System.currentTimeMillis() - turnStart < MIN_FRAME_LENGTH) {
            	// do nothing
            }
        }
    }

    // BUTTON HANDLING
    private JButton genButton(String label, int height){
        JButton but = new JButton(label);
        int width = Math.max(label.length() * Constants.BUTTON_WIDTH_PER_CHAR, Constants.MIN_BUTTON_WIDTH);
        but.setSize(width, height);
        return but;
    }
    private void addButtons(){
        // REVERSE 1 TURN
        JButton backTurnButton = genButton("<", 20);
        backTurnButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.rwTurn();
            }
        });
        this.turnPanel.add(backTurnButton);
        
        // PAUSE / PLAY
        JButton pausePlayButton = genButton("Pause/Play", 20);
        pausePlayButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.toggleOnOff();
            }
        });
        this.turnPanel.add(pausePlayButton);

        // FAST FORWARD 1 TURN
        JButton forwardTurnButton = genButton(">", 20);
        forwardTurnButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                ps.ffTurn();
            }
        });
        this.turnPanel.add(forwardTurnButton);
        
        // SLIDER
        JSlider skipper = new JSlider(JSlider.HORIZONTAL, 0, this.turns.length - 1, 0);
        skipper.setVisible(true);
        skipper.setMajorTickSpacing(10);
        skipper.setMinorTickSpacing(1);
        skipper.setPaintTicks(true);
        skipper.setPaintLabels(true);
        skipper.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		ps.setTurn(skipper.getValue());
        		ps.turnOff();
        	}
        });
        skipper.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent e) {
        		JSlider sourceSlider = (JSlider)e.getSource();
        		BasicSliderUI ui = (BasicSliderUI)sourceSlider.getUI();
        		int value = ui.valueForXPosition(e.getX());
        		skipper.setValue(value);
        	}
        });
        this.sliderPanel.add(skipper);        
    }
    public int getWidth() {
    	return this.B_WIDTH;
    }
    public int getHeight() {
    	return this.B_HEIGHT;
    }

}