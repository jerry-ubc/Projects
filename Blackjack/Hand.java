import java.util.*;

public class Hand extends Deck
{
    //The deck in play
    private static Deck deck = new Deck();
    
    //Initiate Random object in order to choose a random card
    private Random rand = new Random();
    
    //Instance fields
    private int sum = 0;
    private int randCardIndex = rand.nextInt(deck.size());
    private ArrayList<Card> hand = new ArrayList<Card>();
    
    
    //Constructor
    public Hand()
    {
        draw();
    }
    
    //Getter Methods
    public static int getDeckSize()
    {
        return deck.size();
    }
    
    public int sum()
    {
        int sum = 0;
        int curVal = 0;
        boolean ace = false;
        for(int i = 0; i < hand.size(); i++)
        {
            curVal = hand.get(i).getNumVal();
            if(curVal == 11)
            {
                ace = true;
            }
            sum += curVal;
        }
        if(ace == true && sum > 21)
        {
            sum -= 10;
        }
        return sum;
    }
    
    //Unique Methods
    public void draw()
    {
        /*
        Add card at random index from deck to hand, remove card at
        same index from deck, reset randCardIndex to new index
        */
        randCardIndex = rand.nextInt(deck.size());
        hand.add(deck.get(randCardIndex));
        deck.remove(randCardIndex);
    }
    
    public void reset()
    {
        for(int i = hand.size() - 1; i >= 0; i--)
        {
            hand.remove(i);
        }
        draw();
    }
    
    public String toString()
    {
        String handCont = "";
        for(int i = 0; i < hand.size(); i++)
        {
            handCont += hand.get(i);
            if(i < hand.size() - 1)
            {
                handCont += ", ";
            }
        }
        return handCont;
    }
}
