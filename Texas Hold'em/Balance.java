public class Balance
{
    private int balance;
    public Balance(int balance)
    {
        this.balance = balance;
    }
    
    /**
     * Returns the balance of the player
     * 
     * @return Integer balance of player
     */ 
    public int balance()
    {
        return balance;
    }
    
    /**
     * Removes the bet of the player
     * 
     * @param price the bet that the player has made
     * @return      void
     */ 
    public void pay(int price)
    {
        balance -= price;
    }
    
    /**
     * Adds the pot to the winner's balance
     * 
     * @param pot The size of the player's winnings
     * @return    void
     */ 
    public void wins(int pot)
    {
        balance += pot;
    }
    
    public String toString()
    {
        return "" + balance;
    }
}