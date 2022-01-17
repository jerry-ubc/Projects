import java.util.*;

public class MyProgram extends Hand
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to BLACKJACK...");
        Scanner s = new Scanner(System.in);
        System.out.println("Press enter to draw hands");
        String move = s.nextLine();
        
        
        //Initiate hands
        Hand dealerHand = new Hand();
        Hand playerHand = new Hand();
        playerHand.draw();
        
        System.out.println("Player: " + playerHand + " (" + playerHand.sum() + ")" + "\nDealer: " + dealerHand + ", --" + " (" + dealerHand.sum() + ")");
        
        while(playerHand.getDeckSize() > 7)
        {
            //Player turn
            while(playerHand.sum() <= 21)
            {
                System.out.print("Hit or Stand? ");
                move = s.nextLine();
                if(move.equals("hit") || move.equals("Hit"))
                {
                    playerHand.draw();
                    System.out.println("Player: " + playerHand + " (" + playerHand.sum() + ")");
                }
                else if(move.equals("stand") || move.equals("Stand"))
                {
                    break;
                }
                else
                {
                    System.out.println("Please try again");
                }
            }
            if(playerHand.sum() > 21)
            {
                System.out.println("You BUSTED");
            }
            while(dealerHand.sum() < 17)
            {
                System.out.print("Press enter for dealer's turn");
                String entered = s.nextLine();
                dealerHand.draw();
                System.out.println("Dealer HITS");
                System.out.println("Dealer: " + dealerHand + " (" + dealerHand.sum() + ")");
            }
            if(dealerHand.sum() > 21)
            {
                System.out.println("Dealer BUSTS");
            }
            
            //Win conditions
            if(playerHand.sum() > 21 || (21 - dealerHand.sum() < 21 - playerHand.sum() && 21 - dealerHand.sum() >= 0))
            {
                System.out.println("Dealer WINS! ~~");
            }
            else if(playerHand.sum() == dealerHand.sum() && playerHand.sum() <= 21)
            {
                System.out.println("Player PUSHES ~~");
            }
            else
            {
                System.out.println("Player WINS! ~~");
            }
            System.out.println("RESETTING HANDS...");
            
            System.out.print("Play again (y/n)? ");
            String playAgain = s.nextLine();
            if(playAgain.equals("n") || playAgain.equals("N"))
            {
                break;
            }
            
            //Check if there are enough cards to play another round
            //17 is maximum possible number of cards in a game of BlackJack
            if(playerHand.getDeckSize() <= 17)
            {
                System.out.println("Cards have run out... \nRestart the program to use a new deck");
                break;
            }
            
            
            //Reset hands
            playerHand.reset();
            playerHand.draw();
            dealerHand.reset();
            
            System.out.println("Player: " + playerHand + " (" + playerHand.sum() + ")");
            System.out.println("Dealer: " + dealerHand + ", -- (" + dealerHand.sum() + ")");
        }
        System.out.println("Thanks for playing BLACKJACK!...");
    }
}