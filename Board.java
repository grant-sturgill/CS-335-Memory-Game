import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Board
{
    // Array to hold board cards
    private FlippableCard cards[];

    // Resource loader
    private ClassLoader loader = getClass().getClassLoader();

    public Board(int size, ActionListener AL)
    {
        // Allocate and configure the game board: an array of cards
        cards = new FlippableCard[size];

        // Fill the Cards array
        int imageIdx = 1;
        for (int i = 0; i < size; i += 2) {

            // Load the front image from the resources folder
            String imgPath = "res/hub" + imageIdx + ".jpg";
            ImageIcon img = new ImageIcon(loader.getResource(imgPath));
            imageIdx++;  // get ready for the next pair of cards

            // Setup two cards at a time
            FlippableCard c1 = new FlippableCard(img);
            c1.setCustomName(imgPath);
            FlippableCard c2 = new FlippableCard(img);
            c2.setCustomName(imgPath);

            // Add them to the array
            cards[i] = c1;
            cards[i + 1] = c2;
        }

        //Randomize the card positions
        int index;
        Random random = new Random();
        FlippableCard temp = new FlippableCard();
        for (int i = cards.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = cards[index];
            cards[index] = cards[i];
            cards[i] = temp;
        }
        //Set cards so backs are facing forwards
        for(int i = 0; i < cards.length; i++)
        {
            cards[i].hideFront();
        }
    }

    public void fillBoardView(JPanel view)
    {
        for (FlippableCard c : cards) {
            view.add(c);
        }
    }

    public void resetBoard()
    {
        //Display the back of all cards and reset clickability
        for(int i = 0; i < cards.length; i++)
        {
            cards[i].setEnabled(true);
            cards[i].hideFront();
        }
        //Randomize the card positions
        int index;
        Random random = new Random();
        FlippableCard temp = new FlippableCard();
        for (int i = cards.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = cards[index];
            cards[index] = cards[i];
            cards[i] = temp;
        }
    }

    public int getSize() {return cards.length;}
    public FlippableCard[] getCards(){return cards;}
}
