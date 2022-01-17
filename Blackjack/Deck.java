import java.util.*;

public class Deck
{
    //Instance fields
    private ArrayList<Card> deck = new ArrayList<Card>();
    private String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private char[] suits = {'♦', '♣', '♥', '♠'};
    private int[] numVals = {11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
    
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
    
    //Getter Methods
    public int size()
    {
        return deck.size();
    }
    
    public Card get(int i)
    {
        return deck.get(i);
    }
    
    //Mutators
    public void remove(int i)
    {
        deck.remove(i);
    }
    
    public String toString()
    {
        String full = "";
        for(int k = 0; k < deck.size(); k++)
        {
            full = full + deck.get(k) + "\n";
        }
        return full;
    }
}
