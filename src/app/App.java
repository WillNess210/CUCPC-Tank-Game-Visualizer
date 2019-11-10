package app;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class App extends JFrame {
    
    public App() {
        State[] turns = new State[5];
        // TURN 1
        turns[0] = new State();
        turns[0].addUnit(0, 0, 0, 100, 250, 0);
        turns[0].addUnit(1, 1, 0, 900, 250, 0);
        // TURN 2
        turns[1] = new State();
        turns[1].addUnit(0, 0, 0, 200, 250, 100);
        turns[1].addUnit(1, 1, 0, 900, 150, 0);
        turns[1].addUnit(0, 2, 0, 500, 250, 0);
        // TURN 3
        turns[2] = new State();
        turns[2].addUnit(0, 0, 0, 250, 250, 200);
        turns[2].addUnit(1, 1, 0, 900, 250, 0);
        turns[2].addUnit(0, 2, 0, 500, 350, 0);
        // TURN 4
        turns[3] = new State();
        turns[3].addUnit(0, 0, 0, 400, 250, 300);
        turns[3].addUnit(1, 1, 0, 900, 350, 0);
        // TURN 5
        turns[4] = new State();
        turns[4].addUnit(0, 0, 0, 500, 250, 1000);
        turns[4].addUnit(1, 1, 0, 900, 250, 0);
        initUI(turns);
    }

    private void initUI(State[] turns) {

        add(new Board(turns));

        setResizable(false);
        pack();

        setTitle("Application");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            App ex = new App();
            ex.setVisible(true);
        });
    }
}