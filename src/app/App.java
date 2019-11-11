package app;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;

public class App extends JFrame {
    
    public App(String filename) {
        initUIFromFile(filename);
    }

    private void initUI(State sites, State[] turns) {

        add(new Board(sites, turns));

        setResizable(false);
        pack();

        setTitle("Application");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }    

    private void initUIFromFile(String filename){
        File replayFile = new File(filename);
        if(!replayFile.exists()){
            System.out.println(("File not found. Input file: " + filename));
            return;
        }
        State sites = new State();
        State[] turns = null;
        // STARTING LOADING PROCESS
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            int winner = Integer.parseInt(reader.readLine());
            String[] scoreLine = reader.readLine().split(" ");
            int score_0 = Integer.parseInt(scoreLine[0]);
            int score_1 = Integer.parseInt(scoreLine[1]);
            int numSites = Integer.parseInt(reader.readLine());
            for(int i = 0; i < numSites; i++){
                String[] locLine = reader.readLine().split(" ");
                sites.addUnit(-1, i, 2, Integer.parseInt(locLine[0]), Integer.parseInt(locLine[1]), -1);
            }
            int numTurns = Integer.parseInt(reader.readLine());
            turns = new State[numTurns];
            for(int i = 0; i < numTurns; i++){
                turns[i] = new State();
                String[] scoreStrings = reader.readLine().split(" ");
                turns[i].setScore(0, Integer.parseInt(scoreStrings[0]));
                turns[i].setScore(1, Integer.parseInt(scoreStrings[1]));
                int numUnitsInTurn = Integer.parseInt(reader.readLine());
                for(int j = 0; j < numUnitsInTurn; j++){
                    String unitLine = reader.readLine();
                    turns[i].addUnit(unitLine);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        // STARTING VIS
        if(turns != null){
            initUI(sites, turns);
        }
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            if(args.length != 1){
                System.out.println("Error: needs 1 parameter of filename");
                return;
            }
            String filename = args[0];
            System.out.println("Loading file: " + filename);
            App ex = new App(filename);
            ex.setVisible(true);
        });
    }
}