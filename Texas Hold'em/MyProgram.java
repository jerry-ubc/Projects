import java.util.*;

public class MyProgram
{
    public static void main(String[] args)
    {
        Deck deck;
        String playerName;
        Hand cpu = new Hand();
        Hand player = new Hand();
        Hand comCards = new CommunityCards();
        boolean playerDealer = true;
        Balance playerBal = new Balance(1000);
        Balance cpuBal = new Balance(1000);
        int turn = 0;
        int pot = 0;
        int totalTurns = 1;
        int smallBlind = 20;
        boolean cpuBluff = false;
        boolean cpuSlowRoll = false;
        int raiseNum;
        Hand cBestHand = new Hand();
        Hand pBestHand = new Hand();
        int pMoveInt = 0;
        int cMoveInt = 0;
        int pMove;
        System.out.print("What is your name? ");
        Scanner sc = new Scanner(System.in);
        playerName = sc.nextLine();
        System.out.println(playerName + ", welcome to TEXAS HOLD'EM!");
        System.out.println("You will play against our bot \"Alex\"");
        System.out.println("You may enter the number by which you wish to raise by, \n0 to check, or -1 to fold");
        Scanner readPlayerMove = new Scanner(System.in);
        Scanner sc2;
        String keepPlaying = "y";
        int str = evaluate(cpu);
        while((keepPlaying.equals("y") || keepPlaying.equals("Y")) && playerBal.balance() > 0)
        {
            if(totalTurns % 5 == 0)
            {
                smallBlind *= 2;
                System.out.println("The blinds are now doubled.");
            }
            
            playerDealer = !playerDealer;
            turn++;
            printBreak();
            
            if(playerDealer == false)
            {
                totalTurns++;
                turn = 0;
                comCards.reset();
                player.reset();
                cpu.reset();
                pBestHand.reset();
                cBestHand.reset();
                deck = new Deck();
                player.draw(deck.drawRandCard());
                player.draw(deck.drawRandCard());
                cpu.draw(deck.drawRandCard());
                cpu.draw(deck.drawRandCard());
                pBestHand.draw(player.get(0));
                pBestHand.draw(player.get(1));
                cBestHand.draw(cpu.get(0));
                cBestHand.draw(cpu.get(1));
                System.out.println("Shuffling deck...");
                System.out.println("The blinds are at: " + smallBlind + "/" + 2*smallBlind);
                if(evaluate(cpu) == 5)
                {
                    cpuSlowRoll = cpuHalfTrue();
                }
                else if(evaluate(cpu) == 1)
                {
                    if(cpuHalfTrue() == true)
                    {
                        cpuBluff = true;
                        str = 5;
                    }
                }
                System.out.println("You're in the small blind; Alex is in the big blind.");
                playerBal.pay(smallBlind);
                cpuBal.pay(smallBlind * 2);
                pot += 3 * smallBlind;
                printPot(pot);
                printBreak();
                //PREFLOP
                System.out.println("Alex has $" + cpuBal);
                System.out.println("You have $" + playerBal.balance());
                System.out.println("Your cards: " + player);
                System.out.print("Enter your move: $");
                pMove = readPlayerMove.nextInt();
                while(pMoveInt > playerBal.balance())
                {
                    System.out.println("Please enter a number smaller than " + playerBal);
                    System.out.print("Enter your move: $");
                    pMoveInt = readPlayerMove.nextInt();
                }
                pMoveInt = pMove;
                printBreak();
                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                cpuBal.pay(cMoveInt);
                raiseNum = cMoveInt - pMoveInt;
                while(pMoveInt != cMoveInt)
                {
                    if(pMoveInt == -1)
                    {
                        System.out.println("Player folds.");
                        break;
                    }
                    if(cMoveInt > pMoveInt)
                    {
                        System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                        printBreak();
                        System.out.println("You have $" + playerBal.balance());
                        System.out.print("Enter your move (" + raiseNum + " to call): $");
                        
                        pMoveInt += readPlayerMove.nextInt();
                        if(pMoveInt == cMoveInt)
                        {
                            System.out.println("You call.");
                            break;
                        }
                        
                        if(pMoveInt == -1)
                        {
                            System.out.println("Player folds.");
                            break;
                        }
                        else if(pMoveInt < raiseNum)
                        {
                            System.out.println("Please enter a number larger than " + (raiseNum - 1));
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        else if(pMoveInt >= raiseNum)
                        {
                            int saveCMove = cMoveInt;
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            if(cMoveInt == -1)
                            {
                                pot += saveCMove;
                                cpuBal.pay(saveCMove - raiseNum - 1);
                            }
                        }
                    }
                    while(pMoveInt > playerBal.balance())
                    {
                        System.out.println("Please enter a number smaller than " + playerBal);
                        System.out.print("Enter your move: $");
                        pMoveInt = readPlayerMove.nextInt();
                    }
                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                    if(cMoveInt == -1)
                    {
                        pMoveInt = cMoveInt + 1;
                        System.out.println("Alex folds.");
                        break;
                    }
                }
                if(pMoveInt >= 0 && cMoveInt >= 0)
                {
                    playerBal.pay(pMoveInt);
                    pot += cMoveInt + pMoveInt;
                    cMoveInt = 0;
                    pMoveInt = 0;
                    printPot(pot);
                    System.out.println("You have $" + playerBal.balance());
                    printBreak();
                    
                    
                    //FLOP
                    for(int i = 0; i < 3; i++)
                    {
                        comCards.draw(deck.drawRandCard());
                        pBestHand.draw(comCards.get(i));
                        cBestHand.draw(comCards.get(i));
                        turn++;
                    }
                    System.out.println("Here comes the flop: ");
                    System.out.println(comCards);
                    str = evaluate(cBestHand);
                    
                    System.out.println("Alex has $" + cpuBal);
                    System.out.println("You have $" + playerBal.balance());
                    System.out.println("Your cards: " + player);
                    System.out.print("Enter your move: $");
                    pMoveInt = readPlayerMove.nextInt();
                    printBreak();
                    while(pMoveInt > playerBal.balance())
                    {
                        System.out.println("Please enter a number smaller than " + playerBal);
                        System.out.print("Enter your move: $");
                        pMoveInt = readPlayerMove.nextInt();
                    }
                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                    cpuBal.pay(cMoveInt);
                    raiseNum = cMoveInt - pMoveInt;
                    while(pMoveInt != cMoveInt)
                    {
                        if(pMoveInt == -1)
                        {
                            System.out.println("Player folds.");
                            break;
                        }
                        if(cMoveInt > pMoveInt)
                        {
                            System.out.println("CPU raises you to $" + (cMoveInt - pMoveInt));
                            printBreak();
                            System.out.println("You have $" + playerBal.balance());
                            System.out.print("Enter your move (" + raiseNum + " to call): $");
                            
                            pMoveInt += readPlayerMove.nextInt();
                            if(pMoveInt == cMoveInt)
                            {
                                System.out.println("You call.");
                                break;
                            }
                            
                            if(pMoveInt == -1)
                            {
                                System.out.println("Player folds.");
                                break;
                            }
                            else if(pMoveInt < raiseNum)
                            {
                                System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            else if(pMoveInt >= raiseNum)
                            {
                                int saveCMove = cMoveInt;
                                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                if(cMoveInt == -1)
                                {
                                    pot += saveCMove;
                                    cpuBal.pay(saveCMove - raiseNum - 1);
                                }
                            }
                        }
                        while(pMoveInt > playerBal.balance())
                        {
                            System.out.println("Please enter a number smaller than " + playerBal);
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                        if(cMoveInt == -1)
                        {
                            pMoveInt = cMoveInt + 1;
                            System.out.println("Alex folds.");
                            break;
                        }
                    }
                    if(pMoveInt >= 0 && cMoveInt >= 0)
                    {
                        playerBal.pay(pMoveInt);
                        pot += cMoveInt + pMoveInt;
                        cMoveInt = 0;
                        pMoveInt = 0;
                        printPot(pot);
                        System.out.println("You have $" + playerBal.balance());
                        printBreak();
                        
                        //TURN
                        comCards.draw(deck.drawRandCard());
                        cBestHand.draw(comCards.get(3));
                        pBestHand.draw(comCards.get(3));
                        System.out.println("Here comes the turn.");
                        System.out.println(comCards);
                        turn++;
                        
                        System.out.println("Alex has $" + cpuBal);
                        System.out.println("You have $" + playerBal.balance());
                        System.out.println("Your cards: " + player);
                        System.out.print("Enter your move: $");
                        pMoveInt = readPlayerMove.nextInt();
                        while(pMoveInt > playerBal.balance())
                        {
                            System.out.println("Please enter a number smaller than " + playerBal);
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        
                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                        cpuBal.pay(cMoveInt);
                        raiseNum = cMoveInt - pMoveInt;
                        while(pMoveInt != cMoveInt)
                        {
                            if(pMoveInt == -1)
                            {
                                System.out.println("Player folds.");
                                break;
                            }
                            if(cMoveInt > pMoveInt)
                            {
                                System.out.println("CPU raises you to $" + (cMoveInt - pMoveInt));
                                printBreak();
                                System.out.println("You have $" + playerBal.balance());
                                System.out.print("Enter your move (" + raiseNum + " to call): $");
                                
                                pMoveInt += readPlayerMove.nextInt();
                                if(pMoveInt == cMoveInt)
                                {
                                    System.out.println("You call.");
                                    break;
                                }
                                
                                if(pMoveInt == -1)
                                {
                                    System.out.println("Player folds.");
                                    break;
                                }
                                else if(pMoveInt < raiseNum)
                                {
                                    System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                    System.out.print("Enter your move: $");
                                    pMoveInt = readPlayerMove.nextInt();
                                }
                                else if(pMoveInt >= raiseNum)
                                {
                                    int saveCMove = cMoveInt;
                                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                    if(cMoveInt == -1)
                                    {
                                        pot += saveCMove;
                                        cpuBal.pay(saveCMove - raiseNum - 1);
                                    }
                                }
                            }
                            while(pMoveInt > playerBal.balance())
                            {
                                System.out.println("Please enter a number smaller than " + playerBal);
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            if(cMoveInt == -1)
                            {
                                pMoveInt = cMoveInt + 1;
                                System.out.println("Alex folds.");
                                break;
                            }
                        }
                        if(pMoveInt >= 0 && cMoveInt >= 0)
                        {
                            playerBal.pay(pMoveInt);
                            pot += cMoveInt + pMoveInt;
                            cMoveInt = 0;
                            pMoveInt = 0;
                            printPot(pot);
                            System.out.println("You have $" + playerBal.balance());
                            printBreak();
                            //RIVER
                            comCards.draw(deck.drawRandCard());
                            cBestHand.draw(comCards.get(4));
                            pBestHand.draw(comCards.get(4));
                            System.out.println("Here comes the river.");
                            System.out.println(comCards);
                            turn++;
                            System.out.println("You have $" + playerBal.balance());
                            System.out.println("Your cards: " + player);
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                            while(pMoveInt > playerBal.balance())
                            {
                                System.out.println("Please enter a number smaller than " + playerBal);
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            cpuBal.pay(cMoveInt);
                            raiseNum = cMoveInt - pMoveInt;
                            
                            while(pMoveInt != cMoveInt)
                            {
                                if(pMoveInt == -1)
                                {
                                    System.out.println("Player folds.");
                                    break;
                                }
                                if(cMoveInt > pMoveInt)
                                {
                                    System.out.println("CPU raises you to $" + (cMoveInt - pMoveInt));
                                    printBreak();
                                    System.out.println("You have $" + playerBal.balance());
                                    System.out.print("Enter your move (" + raiseNum + " to call): $");
                                    
                                    pMoveInt += readPlayerMove.nextInt();
                                    if(pMoveInt == cMoveInt)
                                    {
                                        System.out.println("You call.");
                                        break;
                                    }
                                    
                                    if(pMoveInt == -1)
                                    {
                                        System.out.println("Player folds.");
                                        break;
                                    }
                                    else if(pMoveInt < raiseNum)
                                    {
                                        System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                        System.out.print("Enter your move: $");
                                        pMoveInt = readPlayerMove.nextInt();
                                    }
                                    else if(pMoveInt >= raiseNum)
                                    {
                                        int saveCMove = cMoveInt;
                                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                        if(cMoveInt == -1)
                                        {
                                            pot += saveCMove;
                                            cpuBal.pay(saveCMove - raiseNum - 1);
                                        }
                                    }
                                }
                                while(pMoveInt > playerBal.balance())
                                {
                                    System.out.println("Please enter a number smaller than " + playerBal);
                                    System.out.print("Enter your move: $");
                                    pMoveInt = readPlayerMove.nextInt();
                                }
                                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                if(cMoveInt == -1)
                                {
                                    pMoveInt = cMoveInt + 1;
                                    System.out.println("Alex folds.");
                                    break;
                                }
                            }
                            if(pMoveInt >= 0 && cMoveInt >= 0)
                            {
                                pot += cMoveInt + pMoveInt;
                                cMoveInt = 0;
                                pMoveInt = 0;
                                printPot(pot);
                                printBreak();
                                //SHOWDOWN
                                System.out.println("Showdown time!");
                                System.out.println("Your cards:      " + player);
                                System.out.println("Community cards: " + comCards);
                                System.out.println("Alex's cards:    " + cpu);
                                if(pBestHand.getHandValue().compareTo(cBestHand.getHandValue()) == 0)
                                {
                                    System.out.println("Tie! The pot is split back evenly.");
                                    playerBal.wins(pot/2);
                                    cpuBal.wins(pot/2);
                                    pot = 0;
                                }
                                else if(pBestHand.getHandValue().compareTo(cBestHand.getHandValue()) > 0)
                                {
                                    System.out.println("You win!");
                                    playerBal.wins(pot);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                                else
                                {
                                    System.out.println("Alex wins!");
                                    cpuBal.wins(pot);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                            }
                            else
                            {
                                if(pMoveInt < 0)
                                {
                                    cpuBal.wins(pot + cMoveInt);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                                else
                                {
                                    cpuBal.pay(1);
                                    playerBal.wins(pot + pMoveInt);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                            }
                        }
                        else
                        {
                            if(pMoveInt < 0)
                            {
                                cpuBal.wins(pot + cMoveInt);
                                pot = 0;
                                System.out.println("Your new balance:   $" + playerBal);
                                System.out.println("Alex's new balance: $" + cpuBal);
                            }
                            else
                            {
                                cpuBal.pay(1);
                                playerBal.wins(pot + pMoveInt);
                                pot = 0;
                                System.out.println("Your new balance:   $" + playerBal);
                                System.out.println("Alex's new balance: $" + cpuBal);
                            }
                        }
                    }
                    else
                    {
                        if(pMoveInt < 0)
                        {
                            cpuBal.wins(pot + cMoveInt);
                            pot = 0;
                            System.out.println("Your new balance:   $" + playerBal);
                            System.out.println("Alex's new balance: $" + cpuBal);
                            playerDealer = true;
                        }
                        else
                        {
                            cpuBal.pay(1);
                            playerBal.wins(pot + pMoveInt);
                            pot = 0;
                            System.out.println("Your new balance:   $" + playerBal);
                            System.out.println("Alex's new balance: $" + cpuBal);
                        }
                    }
                 }
                else
                {
                    if(pMoveInt < 0)
                    {
                        cpuBal.wins(pot + cMoveInt);
                        pot = 0;
                        System.out.println("Your new balance:   $" + playerBal);
                        System.out.println("Alex's new balance: $" + cpuBal);
                    }
                    else if(cMoveInt < 0)
                    {
                        cpuBal.pay(1);
                        playerBal.wins(pot + pMoveInt);
                        pot = 0;
                        System.out.println("Your new balance:   $" + playerBal);
                        System.out.println("Alex's new balance: $" + cpuBal);
                    }
                }
            }
            //IF ALEX IS THE DEALER
            else if(playerDealer == true)
            {
                totalTurns++;
                turn = 0;
                deck = new Deck();
                System.out.println("Shuffling deck...");
                System.out.println("The blinds are at: " + smallBlind + "/" + 2*smallBlind);
                comCards.reset();
                player.reset();
                cpu.reset();
                pBestHand.reset();
                cBestHand.reset();
                player.draw(deck.drawRandCard());
                player.draw(deck.drawRandCard());
                cpu.draw(deck.drawRandCard());
                cpu.draw(deck.drawRandCard());
                pBestHand.draw(player.get(0));
                pBestHand.draw(player.get(1));
                cBestHand.draw(cpu.get(0));
                cBestHand.draw(cpu.get(1));
                if(evaluate(cpu) == 5)
                {
                    cpuSlowRoll = cpuHalfTrue();
                }
                else if(evaluate(cpu) == 1)
                {
                    if(cpuHalfTrue() == true)
                    {
                        cpuBluff = true;
                        str = 5;
                    }
                }
                //PREFLOP
                System.out.println("Your cards: " + player);
                System.out.println("You're in the big blind; Alex is in the small blind");
                playerBal.pay(smallBlind * 2);
                cpuBal.pay(smallBlind);
                pot += 3 * smallBlind;
                printPot(pot);
                printBreak();
                System.out.println("You have $" + playerBal);
                System.out.println("Alex has $" + cpuBal);
                pMoveInt = 0;
                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                raiseNum = cMoveInt - pMoveInt;
                if(cMoveInt == 0)
                {
                    System.out.println("Alex checks.");
                }
                else
                {
                    System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                }
                System.out.println("Your cards: " + player);
                System.out.print("Enter your move: $");
                pMove = readPlayerMove.nextInt();
                pMoveInt = pMove;
                
                while(pMoveInt < cMoveInt)
                {
                    if(pMoveInt == -1)
                    {
                        System.out.println("You fold.");
                        break;
                    }
                    System.out.println("Please enter a number larger than " + (raiseNum - 1));
                    System.out.print("Enter your move: $");
                    pMoveInt = readPlayerMove.nextInt();
                }
                printBreak();
                cpuBal.pay(cMoveInt);
                raiseNum = cMoveInt - pMoveInt;
                while(pMoveInt != cMoveInt)
                {
                    if(pMoveInt == -1)
                    {
                        System.out.println("Player folds.");
                        break;
                    }
                    if(cMoveInt > pMoveInt)
                    {
                        System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                        printBreak();
                        System.out.println("You have $" + playerBal.balance());
                        System.out.print("Enter your move (" + raiseNum + " to call): $");
                        pMoveInt += readPlayerMove.nextInt();
                        if(pMoveInt == cMoveInt)
                        {
                            System.out.println("You call.");
                            break;
                        }
                        if(pMoveInt == -1)
                        {
                            System.out.println("Player folds.");
                            break;
                        }
                        else if(pMoveInt < raiseNum)
                        {
                            System.out.println("Please enter a number larger than " + (raiseNum - 1));
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        else if(pMoveInt >= raiseNum)
                        {
                            int saveCMove = cMoveInt;
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            if(cMoveInt == -1)
                            {
                                pot += saveCMove;
                                cpuBal.pay(saveCMove - raiseNum - 1);
                            }
                        }
                        while(pMoveInt > playerBal.balance())
                        {
                            System.out.println("Please enter a number smaller than " + playerBal);
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                        if(cMoveInt == -1)
                        {
                            pMoveInt = cMoveInt + 1;
                            System.out.println("Alex folds.");
                            break;
                        }
                    }
                    while(pMoveInt > playerBal.balance())
                    {
                        System.out.println("Please enter a number smaller than " + playerBal);
                        System.out.print("Enter your move: $");
                        pMoveInt = readPlayerMove.nextInt();
                    }
                    pot += cMoveInt;
                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                    if(cMoveInt == -1)
                    {
                        pMoveInt = cMoveInt + 1;
                        System.out.println("Alex folds.");
                        break;
                    }
                    
                }
                if(pMoveInt >= 0 && cMoveInt >= 0)
                {
                    playerBal.pay(pMoveInt);
                    pot += cMoveInt + pMoveInt;
                    cMoveInt = 0;
                    pMoveInt = 0;
                    printPot(pot);
                    System.out.println("You have $" + playerBal.balance());
                    printBreak();
                    
                    //FLOP
                    for(int i = 0; i < 3; i++)
                    {
                        comCards.draw(deck.drawRandCard());
                        cBestHand.draw(comCards.get(i));
                        pBestHand.draw(comCards.get(i));
                        turn++;
                    }
                    System.out.println("Here comes the flop.");
                    System.out.println(comCards);
                    
                    
                    printPot(pot);
                    printBreak();
                    System.out.println("You have $" + playerBal);
                    System.out.println("Alex has $" + cpuBal);
                    pMoveInt = 0;
                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                    raiseNum = cMoveInt - pMoveInt;
                    if(cMoveInt == 0)
                    {
                        System.out.println("Alex checks.");
                    }
                    else
                    {
                        System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                    }
                    System.out.println("Your cards: " + player);
                    System.out.print("Enter your move: $");
                    pMove = readPlayerMove.nextInt();
                    pMoveInt = pMove;
                    
                    while(pMoveInt < cMoveInt)
                    {
                        if(pMoveInt == -1)
                        {
                            System.out.println("You fold.");
                            break;
                        }
                        System.out.println("Please enter a number larger than " + (raiseNum - 1));
                        System.out.print("Enter your move: $");
                        pMoveInt = readPlayerMove.nextInt();
                    }
                    printBreak();
                    cpuBal.pay(cMoveInt);
                    raiseNum = cMoveInt - pMoveInt;
                    while(pMoveInt != cMoveInt)
                    {
                        if(pMoveInt == -1)
                        {
                            System.out.println("Player folds.");
                            break;
                        }
                        if(cMoveInt > pMoveInt)
                        {
                            System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                            printBreak();
                            System.out.println("You have $" + playerBal.balance());
                            System.out.print("Enter your move (" + raiseNum + " to call): $");
                            pMoveInt += readPlayerMove.nextInt();
                            if(pMoveInt == cMoveInt)
                            {
                                System.out.println("You call.");
                                break;
                            }
                            if(pMoveInt == -1)
                            {
                                System.out.println("Player folds.");
                                break;
                            }
                            else if(pMoveInt < raiseNum)
                            {
                                System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            else if(pMoveInt >= raiseNum)
                            {
                                int saveCMove = cMoveInt;
                                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                if(cMoveInt == -1)
                                {
                                    pot += saveCMove;
                                    cpuBal.pay(saveCMove - raiseNum - 1);
                                }
                            }
                            while(pMoveInt > playerBal.balance())
                            {
                                System.out.println("Please enter a number smaller than " + playerBal);
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            if(cMoveInt == -1)
                            {
                                pMoveInt = cMoveInt + 1;
                                System.out.println("Alex folds.");
                                break;
                            }
                        }
                        while(pMoveInt > playerBal.balance())
                        {
                            System.out.println("Please enter a number smaller than " + playerBal);
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        pot += cMoveInt;
                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                        if(cMoveInt == -1)
                        {
                            pMoveInt = cMoveInt + 1;
                            System.out.println("Alex folds.");
                            break;
                        }
                        
                    }
                    if(pMoveInt >= 0 && cMoveInt >= 0)
                    {
                        playerBal.pay(pMoveInt);
                        pot += cMoveInt + pMoveInt;
                        cMoveInt = 0;
                        pMoveInt = 0;
                        printPot(pot);
                        System.out.println("You have $" + playerBal.balance());
                        printBreak();
                    
                        //TURN
                        comCards.draw(deck.drawRandCard());
                        pBestHand.draw(comCards.get(3));
                        cBestHand.draw(comCards.get(3));
                        System.out.println("Here comes the turn.");
                        System.out.println(comCards);
                        
                        
                        printPot(pot);
                        printBreak();
                        System.out.println("You have $" + playerBal);
                        System.out.println("Alex has $" + cpuBal);
                        pMoveInt = 0;
                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                        raiseNum = cMoveInt - pMoveInt;
                        if(cMoveInt == 0)
                        {
                            System.out.println("Alex checks.");
                        }
                        else
                        {
                            System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                        }
                        System.out.println("Your cards: " + player);
                        System.out.print("Enter your move: $");
                        pMove = readPlayerMove.nextInt();
                        pMoveInt = pMove;
                        
                        while(pMoveInt < cMoveInt)
                        {
                            if(pMoveInt == -1)
                            {
                                System.out.println("You fold.");
                                break;
                            }
                            System.out.println("Please enter a number larger than " + (raiseNum - 1));
                            System.out.print("Enter your move: $");
                            pMoveInt = readPlayerMove.nextInt();
                        }
                        printBreak();
                        cpuBal.pay(cMoveInt);
                        raiseNum = cMoveInt - pMoveInt;
                        while(pMoveInt != cMoveInt)
                        {
                            if(pMoveInt == -1)
                            {
                                System.out.println("Player folds.");
                                break;
                            }
                            if(cMoveInt > pMoveInt)
                            {
                                System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                                printBreak();
                                System.out.println("You have $" + playerBal.balance());
                                System.out.print("Enter your move (" + raiseNum + " to call): $");
                                pMoveInt += readPlayerMove.nextInt();
                                if(pMoveInt == cMoveInt)
                                {
                                    System.out.println("You call.");
                                    break;
                                }
                                if(pMoveInt == -1)
                                {
                                    System.out.println("Player folds.");
                                    break;
                                }
                                else if(pMoveInt < raiseNum)
                                {
                                    System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                    System.out.print("Enter your move: $");
                                    pMoveInt = readPlayerMove.nextInt();
                                }
                                else if(pMoveInt >= raiseNum)
                                {
                                    int saveCMove = cMoveInt;
                                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                    if(cMoveInt == -1)
                                    {
                                        pot += saveCMove;
                                        cpuBal.pay(saveCMove - raiseNum - 1);
                                    }
                                }
                                while(pMoveInt > playerBal.balance())
                                {
                                    System.out.println("Please enter a number smaller than " + playerBal);
                                    System.out.print("Enter your move: $");
                                    pMoveInt = readPlayerMove.nextInt();
                                }
                                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                if(cMoveInt == -1)
                                {
                                    pMoveInt = cMoveInt + 1;
                                    System.out.println("Alex folds.");
                                    break;
                                }
                            }
                            while(pMoveInt > playerBal.balance())
                            {
                                System.out.println("Please enter a number smaller than " + playerBal);
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            pot += cMoveInt;
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            if(cMoveInt == -1)
                            {
                                pMoveInt = cMoveInt + 1;
                                System.out.println("Alex folds.");
                                break;
                            }
                            
                        }
                        if(pMoveInt >= 0 && cMoveInt >= 0)
                        {
                            playerBal.pay(pMoveInt);
                            pot += cMoveInt + pMoveInt;
                            cMoveInt = 0;
                            pMoveInt = 0;
                            printPot(pot);
                            System.out.println("You have $" + playerBal.balance());
                            printBreak();
                            
                            //RIVER
                            comCards.draw(deck.drawRandCard());
                            pBestHand.draw(comCards.get(4));
                            cBestHand.draw(comCards.get(4));
                            System.out.println("Here comes the river.");
                            System.out.println(comCards);
                            
                            
                            printPot(pot);
                            printBreak();
                            System.out.println("You have $" + playerBal);
                            System.out.println("Alex has $" + cpuBal);
                            pMoveInt = 0;
                            cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                            raiseNum = cMoveInt - pMoveInt;
                            if(cMoveInt == 0)
                            {
                                System.out.println("Alex checks.");
                            }
                            else
                            {
                                System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                            }
                            System.out.println("Your cards: " + player);
                            System.out.print("Enter your move: $");
                            pMove = readPlayerMove.nextInt();
                            pMoveInt = pMove;
                            
                            while(pMoveInt < cMoveInt)
                            {
                                if(pMoveInt == -1)
                                {
                                    System.out.println("You fold.");
                                    break;
                                }
                                System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                System.out.print("Enter your move: $");
                                pMoveInt = readPlayerMove.nextInt();
                            }
                            printBreak();
                            cpuBal.pay(cMoveInt);
                            raiseNum = cMoveInt - pMoveInt;
                            while(pMoveInt != cMoveInt)
                            {
                                if(pMoveInt == -1)
                                {
                                    System.out.println("Player folds.");
                                    break;
                                }
                                if(cMoveInt > pMoveInt)
                                {
                                    System.out.println("Alex raises you to $" + (cMoveInt - pMoveInt));
                                    printBreak();
                                    System.out.println("You have $" + playerBal.balance());
                                    System.out.print("Enter your move (" + raiseNum + " to call): $");
                                    pMoveInt += readPlayerMove.nextInt();
                                    if(pMoveInt == cMoveInt)
                                    {
                                        System.out.println("You call.");
                                        break;
                                    }
                                    if(pMoveInt == -1)
                                    {
                                        System.out.println("Player folds.");
                                        break;
                                    }
                                    else if(pMoveInt < raiseNum)
                                    {
                                        System.out.println("Please enter a number larger than " + (raiseNum - 1));
                                        System.out.print("Enter your move: $");
                                        pMoveInt = readPlayerMove.nextInt();
                                    }
                                    else if(pMoveInt >= raiseNum)
                                    {
                                        int saveCMove = cMoveInt;
                                        cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                        if(cMoveInt == -1)
                                        {
                                            pot += saveCMove;
                                            cpuBal.pay(saveCMove - raiseNum - 1);
                                        }
                                    }
                                    while(pMoveInt > playerBal.balance())
                                    {
                                        System.out.println("Please enter a number smaller than " + playerBal);
                                        System.out.print("Enter your move: $");
                                        pMoveInt = readPlayerMove.nextInt();
                                    }
                                    cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                    if(cMoveInt == -1)
                                    {
                                        pMoveInt = cMoveInt + 1;
                                        System.out.println("Alex folds.");
                                        break;
                                    }
                                }
                                while(pMoveInt > playerBal.balance())
                                {
                                    System.out.println("Please enter a number smaller than " + playerBal);
                                    System.out.print("Enter your move: $");
                                    pMoveInt = readPlayerMove.nextInt();
                                }
                                pot += cMoveInt;
                                cMoveInt = cpuMove(evaluate(cBestHand), pMoveInt, pot, cpuSlowRoll, turn, playerBal.balance(), cpuBal.balance());
                                if(cMoveInt == -1)
                                {
                                    pMoveInt = cMoveInt + 1;
                                    System.out.println("Alex folds.");
                                    break;
                                }
                            }
                            if(pMoveInt >= 0 && cMoveInt >= 0)
                            {
                                pot += cMoveInt + pMoveInt;
                                cMoveInt = 0;
                                pMoveInt = 0;
                                printPot(pot);
                                printBreak();
                                //SHOWDOWN
                                System.out.println("Showdown time!");
                                System.out.println("Your cards:      " + player);
                                System.out.println("Community cards: " + comCards);
                                System.out.println("Alex's cards:    " + cpu);
                                if(pBestHand.getHandValue().compareTo(cBestHand.getHandValue()) == 0)
                                {
                                    System.out.println("Tie! The pot is split back evenly.");
                                    playerBal.wins(pot/2);
                                    cpuBal.wins(pot/2);
                                    pot = 0;
                                }
                                else if(pBestHand.getHandValue().compareTo(cBestHand.getHandValue()) > 0)
                                {
                                    System.out.println("You win!");
                                    playerBal.wins(pot);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                                else
                                {
                                    System.out.println("Alex wins!");
                                    cpuBal.wins(pot);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                            }
                            else
                            {
                                if(pMoveInt < 0)
                                {
                                    cpuBal.wins(pot + cMoveInt);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                                else if(cMoveInt < 0)
                                {
                                    cpuBal.pay(1);
                                    playerBal.wins(pot + pMoveInt);
                                    pot = 0;
                                    System.out.println("Your new balance:   $" + playerBal);
                                    System.out.println("Alex's new balance: $" + cpuBal);
                                }
                            }
                        }
                        else
                        {
                            if(pMoveInt < 0)
                            {
                                cpuBal.wins(pot + cMoveInt);
                                pot = 0;
                                System.out.println("Your new balance:   $" + playerBal);
                                System.out.println("Alex's new balance: $" + cpuBal);
                            }
                            else if(cMoveInt < 0)
                            {
                                playerBal.wins(pot + pMoveInt);
                                pot = 0;
                                System.out.println("Your new balance:   $" + playerBal);
                                System.out.println("Alex's new balance: $" + cpuBal);
                            }
                        }
                    }
                    else
                    {
                        if(pMoveInt < 0)
                        {
                            cpuBal.wins(pot + cMoveInt);
                            pot = 0;
                            System.out.println("Your new balance:   $" + playerBal);
                            System.out.println("Alex's new balance: $" + cpuBal);
                        }
                        else if(cMoveInt < 0)
                        {
                            playerBal.wins(pot + pMoveInt);
                            pot = 0;
                            System.out.println("Your new balance:   $" + playerBal);
                            System.out.println("Alex's new balance: $" + cpuBal);
                        }
                    }
                }
                else
                {
                
                
                    if(pMoveInt < 0)
                    {
                        cpuBal.wins(pot + cMoveInt);
                        pot = 0;
                        System.out.println("Your new balance:   $" + playerBal);
                        System.out.println("Alex's new balance: $" + cpuBal);
                    }
                    else if(cMoveInt < 0)
                    {
                        playerBal.wins(pot + pMoveInt);
                        pot = 0;
                        System.out.println("Your new balance:   $" + playerBal);
                        System.out.println("Alex's new balance: $" + cpuBal);
                    }
                }
            }
        if(playerBal.balance() < 0)
        {
            System.out.println("You are out of chips, please reset the program to start again.");
            break;
        }
        else if(cpuBal.balance() < 0)
        {
            System.out.println("Alex is out of chips, congratulations; you win!");
            break;
        }
        System.out.println("Stay for another round? (type \"y\" or \"n\")");
        sc2 = new Scanner(System.in);
        keepPlaying = sc2.nextLine();
        }
        System.out.println("See you next time!");
    }
    
    
    /**
     * Prints a dashed line to make the game look more user friendly
     * 
     * @return void
     */
    private static void printBreak()
    {
        System.out.println("-------------");
    }
    
    
    /**
     * Takes in a hand and evaluates the strength of it from a scale of 1-5
     * 
     * @param a The Hand that is up for assessment
     * 
     * @return  Integer 1-5 representing strength of Hand a
     */ 
    private static int evaluate(Hand a)
    {
        String handValue = a.getHandValue();
        int strength = -1;
        if(a.size() == 2)
        {
            if(handValue.substring(0,1).equals("b"))
            {
                //7 pair or lower
                if(handValue.compareTo("bf") < 0)
                {
                    return 3;
                }
                //10 pair or lower
                else if(handValue.compareTo("bi") < 0)
                {
                    return 4;
                }
                else
                {
                    return 5;
                }
            }
            //8 7 or lower
            if(handValue.compareTo("agf") <= 0)
            {
                return 1;
            }
            //10 9 or lower
            else if(handValue.compareTo("aih") <= 0)
            {
                return 2;
            }
            //Q 8 or lower
            else if(handValue.compareTo("akg") <= 0)
            {
                return 3;
            }
            //A J or lower
            else if(handValue.compareTo("amj") <= 0)
            {
                return 4;
            }
            //A K or lower
            else if(handValue.compareTo("aml") <= 0)
            {
                return 5;
            }
        }
        else if(a.size() > 2)
        {
            //Straight or greater
            if(handValue.compareTo("e") >= 0)
            {
                return 5;
            }
            //3 of a kind OR at least 55 44 two pair
            else if(handValue.substring(0,1).equals("d") || handValue.compareTo("cdc") > 0)
            {
                return 4;
            }
            //Any other 2 pair OR at least a 6 pair
            else if(handValue.substring(0,1).equals("c") || handValue.compareTo("be") >= 0)
            {
                return 3;
            }
            //At least a Queen high
            else if(handValue.substring(0,1).equals("b") || handValue.compareTo("ak") >= 0)
            {
                return 2;
            }
            //Any other high card
            else
            {
                return 1;
            }
        }
        
        return strength;
    }
    
    
    /**
     * Determines Alex's move based on an algorithm considering multiple parameters
     * 
     * @param str         The strength of Alex's hand determined by another method (between 1-5)
     * @param playerMove  The integer of the player's bet ("check" if 0)
     * @param pot         The integer size of the pot
     * @param cpuSlowRoll Whether Alex has decided to play the slow roll strategy
     * @param turn        The integer turn the current game is in
     * @param pBal        The integer player's balance
     * @param cBal        Alex's integer balance
     * 
     * @return            Integer of Alex's move ("check" if 0)
     */
    private static int cpuMove(int str, int playerMove, int pot, boolean cpuSlowRoll, int turn, int pBal, int cBal)
    {
        str = 1;
        if(cpuSlowRoll == true)
        {
            if(playerMove == 0 && turn <= 3)
            {
                return 0;
            }
            else if(playerMove == 0 && turn < 5)
            {
                if(pot > 150)
                {
                    return 50;
                }
                else
                {
                    return pot;
                }
            }
            else
            {
                return playerMove;
            }
        }
        //If strong hand
        if(str == 5)
        {
            //If Player checks, return with a strong move
            if(playerMove == 0)
            {
                if(turn == 5)
                {
                    if(pBal > 150)
                    {
                        if(cBal > 150)
                        {
                            return 100;
                        }
                        else
                        {
                            System.out.println("Alex moves all in!");
                            return cBal;
                        }
                    }
                    else
                    {
                        System.out.println("Alex moves all in!");
                        return pBal;
                    }
                }
                if(cBal > 100)
                {
                    if(pBal > 50)
                    {
                        return 50;
                    }
                    else
                    {
                        System.out.println("Alex moves all in!");
                        return pBal;
                    }
                }
                else
                {
                    return cBal;
                }
            }
            else if((playerMove < 70) && (pBal > playerMove + 50) && (cBal > playerMove + 50))
            {
                return playerMove + 50;
            }
            else
            {
                return playerMove;
            }
        }
        else if(str == 4)
        {
            if(turn < 3)
            {
                if(playerMove == 0)
                {
                    return 50;
                }
                else if(playerMove < 100 && playerMove > 0)
                {
                    return playerMove;
                }
                else if(playerMove >= 100)
                {
                    return -1;
                }
            }
            else
            {
                if(playerMove == 0)
                {
                    return 50;
                }
                else if(playerMove < 160 && playerMove > 0)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
        }
        else if(str == 3)
        {
            if(turn < 3)
            {
                if(playerMove == -1)
                {
                    return 0;
                }
                else if(playerMove == 0)
                {
                    return 50;
                }
                else if(playerMove < 80)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                if(playerMove < 140)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
        }
        else if(str == 2)
        {
            if(turn <= 1)
            {
                if(playerMove == -1)
                {
                    return 0;
                }
                else if(playerMove == 0)
                {
                    System.out.println("Alex checks.");
                    return 0;
                }
                else if(playerMove == 50)
                {
                    return playerMove;
                }
                else if(playerMove < 40)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                if(playerMove == 0)
                {
                    System.out.println("Alex checks.");
                    return 0;
                }
                if(playerMove < 60)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
        }
        else if(str == 1)
        {
            if(turn <= 1)
            {
                if(playerMove == -1)
                {
                    return 0;
                }
                else if(playerMove == 0)
                {
                    return 50;
                }
                else if(playerMove == 50)
                {
                    return 50;
                }
                else if(playerMove < 30)
                {
                    return playerMove;
                }
            }
            else
            {
                if(playerMove <= 50)
                {
                    return playerMove;
                }
                else
                {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    /**
     * Returns true 50% of the time, used to determine if Alex wishes to 
     * bluff or slow roll
     * 
     * @return true or false 50/50 chances
     */
    private static boolean cpuHalfTrue()
    {
        if((int)(Math.random() + 0.5) < 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Prints the size of the pot to the screen
     * 
     * @param pot The variable storing the pot size
     * @return void
     */
    private static void printPot(int pot)
    {
        System.out.println("The pot is sitting at $" + pot);
    }
}
