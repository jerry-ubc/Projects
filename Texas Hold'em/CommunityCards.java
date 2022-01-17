import java.util.*;

public class CommunityCards extends Hand
{
    //Instance variables
    ArrayList<Card> com;
    
    //Constructor
    public CommunityCards()
    {
        com = new ArrayList<Card>();
    }
    
    /**
     * Returns card at index i
     * 
     * @param i index at which to pick card from
     * @return  Card at index i
     */ 
    public Card get(int i)
    {
        return com.get(i);
    }
    
    /**
     * Returns size of community cards
     * 
     * @return Integer size of ArrayList com
     */ 
    public int size()
    {
        return com.size();
    }
    
    /**
     * Adds a card to ArrayList com
     * 
     * @param a Card with identity a at which to add to ArrayList com
     * @return  void
     */ 
    public void draw(Card a)
    {
        com.add(a);
    }
    
    /**
     * Removes all cards in com
     * 
     * @return void
     */ 
    public void reset()
    {
        for(int i = com.size() - 1; i >= 0; i--)
        {
            com.remove(i);
        }
    }
    
    public String toString()
    {
        String comCont = "";
        for(int i = 0; i < com.size(); i++)
        {
            comCont += com.get(i);
            if(i < com.size() - 1)
            {
                comCont += ", ";
            }
        }
        return comCont;
    }
}
