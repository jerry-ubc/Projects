import java.util.*;

public class Deck
{
    //Random object to choose random card
    private Random rand = new Random();
    int randCardIndex;
    
    //Instance fields
    private ArrayList<Card> deck = new ArrayList<Card>();
    private String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private char[] suits = {'♦', '♣', '♥', '♠'};
    private int[] numVals = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    
    //Constructor
    public Deck()
    {
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 13; j++)
            {
                deck.add(new Card(suits[i], values[j], numVals[j]));
            }
        }
    }
    
    /**
     * Returns the size of the deck
     * 
     * @Return Integer size of deck
     */ 
    public int size()
    {
        return deck.size();
    }
    
    /**
     * Returns card at index i
     * 
     * @param i index of card inside deck
     * @return  Card at index i
     */ 
    public Card get(int i)
    {
        return deck.get(i);
    }

    
    /**
     * Removes card at index i from deck
     * 
     * @param i index of deck at which to remove card
     * @return  void
     */ 
    public void remove(int i)
    {
        deck.remove(i);
    }
    
    /**
     * Returns a random card and removes it from the deck (simulating a draw)
     * 
     * @return Card at random index to "draw""
     */ 
    public Card drawRandCard()
    {
        randCardIndex = rand.nextInt(deck.size());
        Card a = deck.get(randCardIndex);
        deck.remove(randCardIndex);
        return a;
    }
    
    public String toString()
    {
        String full = "";
        for(int k = 0; k < deck.size(); k++)
        {
            full = full + deck.get(k) + "\n";
        }
        System.out.println(deck.size() + "cards");
        return full;
    }
}