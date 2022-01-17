public class Card
{
    //Instance variables
    private char suit;
    private String value;
    private int numVal;
    
    
    //Constructor
    public Card(char suit, String value, int numVal)
    {
        this.suit = suit;
        this.value = value;
        this.numVal = numVal;
    }
    
    
    /**
     * Returns the suit of the card
     * 
     * @return Suit of the card
     */ 
    public char getSuit()
    {
        return suit;
    }
    
    /**
     * Returns the string value of the card
     * 
     * @return String value of the card
     */ 
    public String getValue()
    {
        return value;
    }
    
    /**
     * Returns the integer value of the card
     * 
     * @return Integer value of the card
     */ 
    public int getNumVal()
    {
        return numVal;
    }
    
    public String toString()
    {
        return value + suit;
    }
}
