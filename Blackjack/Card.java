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
    
    
    //Getter methods
    public char getSuit()
    {
        return suit;
    }
    public String getValue()
    {
        return value;
    }
    public int getNumVal()
    {
        return numVal;
    }
    
    public String toString()
    {
        return value + suit;
    }
}
