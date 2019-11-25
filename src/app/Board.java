package app;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.event.ChangeEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.GridLayout;
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
    private int deploymentType;
    private final long MIN_FRAME_LENGTH = (long)(1000/30);
    private Thread animator;
    private State[] turns;
    private State curState, sites;
    public Image mapBoard, roverImgRed, roverImgBlue, tankImgRed, tankImgBlue, siteImg, explosionImg;
    private static PlayStatus ps;
    private JPanel controlPanel, sliderPanel, turnPanel;
    
    public Board(State sites, State[] turns, int mapWidth, int mapHeight, int deploymentType){
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        this.B_WIDTH = mapWidth;
        this.B_HEIGHT = mapHeight;
        this.deploymentType = deploymentType;
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT+125));
        
        this.controlPanel = new JPanel();
        this.controlPanel.setLayout(new BorderLayout());
        this.sliderPanel = new JPanel(new BorderLayout());
        this.turnPanel = new JPanel(new FlowLayout());
        
        // handling states
        this.turns = turns;
        
        // loading map image
        this.mapBoard = (new ImageIcon(Board.class.getResource("/resources/map.png"))).getImage();
        this.mapBoard = this.mapBoard.getScaledInstance(this.B_WIDTH, this.B_HEIGHT, Image.SCALE_DEFAULT);
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
        // DRAW DEPLOYMENT ZONES
        g2d.setStroke(new BasicStroke(3));
        if (this.deploymentType == 1) {
        	g2d.setColor(Color.BLUE);
        	int[] px1 = new int[]{0, 305, (int)(this.B_WIDTH/2)-229, 305, 0};
            int[] py1 = new int[]{0, 0, (int)this.B_HEIGHT/2, this.B_HEIGHT, this.B_HEIGHT};
        	g2d.drawPolygon(px1, py1, 5);
        	g2d.setColor(Color.RED);
        	int[] px2 = new int[]{this.B_WIDTH, this.B_WIDTH-305, (int)(this.B_WIDTH/2)+229, this.B_WIDTH-305, this.B_WIDTH};
            int[] py2 = new int[]{0, 0, (int)this.B_HEIGHT/2, this.B_HEIGHT, this.B_HEIGHT};
        	g2d.drawPolygon(px2, py2, 5);
        }
        else if (this.deploymentType == 2) {
        	g2d.setColor(Color.BLUE);
        	g2d.draw(new Rectangle2D.Double(0, 0, this.B_WIDTH, (int) this.B_HEIGHT/2 - 305));
        	g2d.setColor(Color.RED);
        	g2d.draw(new Rectangle2D.Double(0, (int) this.B_HEIGHT/2+305, this.B_WIDTH, (int) this.B_HEIGHT/2 - 305));
        }
        else if (this.deploymentType == 3) {
        	Ellipse2D center = new Ellipse2D.Double((int)this.B_WIDTH/2-229, (int)this.B_HEIGHT/2-229, 457, 457);
        	g2d.setColor(Color.BLUE);
        	Rectangle part11 = new Rectangle(0, 0, (int)this.B_WIDTH/2, (int)this.B_HEIGHT/2);
    		Area deployArea1 = new Area(part11);
    		deployArea1.subtract(new Area(center));
    		g2d.draw(deployArea1);
    		
    		g2d.setColor(Color.RED);
    		Rectangle part12 = new Rectangle((int)this.B_WIDTH/2, (int)this.B_HEIGHT/2, (int)this.B_WIDTH/2, (int)this.B_HEIGHT/2);
    		Area deployArea2 = new Area(part12);
    		deployArea2.subtract(new Area(center));
    		g2d.draw(deployArea2);
        }
        else if (this.deploymentType == 4) {
    		g2d.setColor(Color.BLUE);
			g2d.draw(new Rectangle(0, 0, (int)(this.B_WIDTH/2)-305, this.B_HEIGHT));
			g2d.setColor(Color.RED);
			g2d.draw(new Rectangle((int)this.B_WIDTH/2+305, 0, (int)(this.B_WIDTH/2)-305, this.B_HEIGHT));
    	}
        else if (this.deploymentType == 5) {
        	g2d.setColor(Color.BLUE);
        	int[] px1 = new int[]{0, this.B_WIDTH, this.B_WIDTH, (int)(this.B_WIDTH/2), 0};
            int[] py1 = new int[]{0, 0, 152, (int)(this.B_HEIGHT/2)-229, 152};
    		g2d.draw(new Area(new Polygon(px1, py1, 5)));
    		g2d.setColor(Color.RED);
    		int[] px2 = new int[]{0, this.B_WIDTH, this.B_WIDTH, (int)(this.B_WIDTH/2), 0};
            int[] py2 = new int[]{this.B_HEIGHT, this.B_HEIGHT, this.B_HEIGHT-152, (int)(this.B_HEIGHT/2)+229, this.B_HEIGHT-152};
    		g2d.draw(new Area(new Polygon(px2, py2, 5)));
    		
        }
        else if (this.deploymentType == 6) {
        	g2d.setColor(Color.BLUE);
        	int[] px1 = new int[]{0, this.B_WIDTH-305, 0};
            int[] py1 = new int[]{305, this.B_HEIGHT, this.B_HEIGHT};
            g2d.draw(new Area(new Polygon(px1, py1, 3)));
    		g2d.setColor(Color.RED);
    		int[] px2 = new int[]{305, this.B_WIDTH, this.B_WIDTH};
            int[] py2 = new int[]{0, 0, this.B_HEIGHT-305};
            g2d.draw(new Area(new Polygon(px2, py2, 3)));
        }
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
        			g2d.drawString("Winner: BLUE (0)" , this.B_WIDTH/2, 515);
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
    int getBWidth() {
    	return this.B_WIDTH;
    }
    int getBHeight() {
    	return this.B_HEIGHT;
    }
}