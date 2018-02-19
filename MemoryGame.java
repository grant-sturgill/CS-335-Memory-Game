/*
Grant Sturgill
CS335 Program 0
9.20.17

This program is a memory came with a space theme. The game will open in a separate window. The game will be timed
and errors counted. The timer stops when all matches are found. The user can quit or restart the game at any time.

Original Framework for all classes provided by Dr. Brent Seales
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MemoryGame extends JFrame implements ActionListener
{
    // Core game play objects
    private Board gameBoard;
    private FlippableCard card1, card2;

    // Labels to display game info
    private JLabel errorLabel, timerLabel;

    // Game timer: will be configured to increment timer count every second
    private int delay = 1000; //milliseconds
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            gameTime += 1;
            timerLabel.setText("Timer: " + gameTime);
        }
    };
    private Timer gameTimer = new Timer(delay, taskPerformer);
    // layout objects: Views of the board and the label area
    private JPanel boardView, labelView;

    // Record keeping counts and times
    private int clickCount = 0, gameTime = 0, errorCount = 0;
    private int pairsFound = 0;
    private int maxPairs = 0;

    public MemoryGame()
    {
        // Call the base class constructor
        super("Hubble Memory Game");

        // Allocate the interface elements
        JButton restart = new JButton("Restart");
        JButton quit = new JButton("Quit");
        timerLabel = new JLabel("Timer: 0");
        errorLabel = new JLabel("Errors: 0");

        //Implement restart functionality for restart button
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        //Implement quit functionality for quit button
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //Start timer
        gameTimer.start();
        // Allocate two major panels to hold interface
        labelView = new JPanel();  // used to hold labels
        boardView = new JPanel();  // used to hold game board

        // get the content pane, onto which everything is eventually added
        Container c = getContentPane();

        // Setup the game board with cards
        gameBoard = new Board(24, this);
        maxPairs = gameBoard.getSize() / 2;

        //Enable cards to be clicked
        FlippableCard[] cards = gameBoard.getCards();
        for(int i = 0; i < cards.length; i++){
            cards[i].addActionListener(this::actionPerformed);
        }

        // Add the game board to the board layout area
        boardView.setLayout(new GridLayout(4, 6, 10, 10));
        gameBoard.fillBoardView(boardView);


        // Add required interface elements to the "label" JPanel
        labelView.setLayout(new GridLayout(1, 4, 2, 2));
        labelView.add(quit);
        labelView.add(restart);
        labelView.add(timerLabel);
        labelView.add(errorLabel);

        // Both panels should now be individually layed out
        // Add both panels to the container
        c.add(labelView, BorderLayout.NORTH);
        c.add(boardView, BorderLayout.SOUTH);

        setSize(745, 470);
        setVisible(true);
    }

    //Handle card clicking
    public void actionPerformed(ActionEvent e)
    {
        //If pick is first card of pair, show card and make it so it can't be clicked again
        if(clickCount == 0)
        {
            //Get and store card that was clicked
            clickCount++;
            card1 = (FlippableCard)e.getSource();
            card1.showFront();
            card1.setEnabled(false);
        }
        //If second pick, compare cards and see if they match
        else
        {
            //Get and store card that was clicked
            card2 = (FlippableCard)e.getSource();
            card2.showFront();
            card2.setEnabled(false);

            //If they match, increment pairs counter and leave cards unclickable
            if(card1.customName() == card2.customName()) {
                pairsFound++;
                //If this is the last pair, stop timer
               if(pairsFound == maxPairs) {
                    gameTimer.stop();
                }
            }

            //If they do not match, increment error counter and reset cards
            if(card1.customName() != card2.customName()) {
                errorCount++;
                errorLabel.setText("Errors: " + errorCount);
                //Disable cards so third card can't be clicked, creating game-breaking error, be sure to show clicked cards
                FlippableCard[] cards = gameBoard.getCards();
                for(int i = 0; i < cards.length; i++){
                    cards[i].lock(true);
                    cards[i].setEnabled(false);
                }
                card1.lock(false);
                card2.lock(false);
                //Pause program for a half second so user can read mismatched cards
                Timer pause = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        card1.hideFront();
                        card1.setEnabled(true);
                        card2.hideFront();
                        card2.setEnabled(true);
                        for(int i = 0; i < cards.length; i++){
                            cards[i].lock(false);
                            cards[i].setEnabled(true);
                        }
                    }
                });
                //Make sure timer doesn't repeat
                pause.setRepeats(false);
                pause.start();
            }
            clickCount = 0;
        }
    }

    private void restartGame()
    {
        pairsFound = 0;
        gameTimer.restart();
        gameTime = 0;
        clickCount = 0;
        errorCount = 0;
        timerLabel.setText("Timer: 0");
        errorLabel.setText("Errors: 0");

        // Clear the boardView and have the gameBoard generate a new layout
        boardView.removeAll();
        gameBoard.resetBoard();
        gameBoard.fillBoardView(boardView);
    }

    public static void main(String args[])
    {
        MemoryGame M = new MemoryGame();
        M.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
