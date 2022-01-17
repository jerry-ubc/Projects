import java.util.*;

public class Hand
{
    //Initiate Random object in order to choose a random card
    private Random rand = new Random();
    
    //Instance fields
    private int randCardIndex;
    private int sum = 0;
    private ArrayList<Card> hand;
    String value = "";
    
    HashMap<String, String> handVals = new HashMap<String, String>();
    
    //Constructor
    public Hand()
    {
        hand = new ArrayList<Card>();
        handVals.put("14", "m");
        handVals.put("1", "m");
        handVals.put("2", "a");
        handVals.put("3", "b");
        handVals.put("4", "c");
        handVals.put("5", "d");
        handVals.put("6", "e");
        handVals.put("7", "f");
        handVals.put("8", "g");
        handVals.put("9", "h");
        handVals.put("10", "i");
        handVals.put("11", "j");
        handVals.put("12", "k");
        handVals.put("13", "l");
    }
    
    /**
     * Returns card at index i
     * 
     * @param i index at which to return Card
     * @return  Card at index i
     */ 
    public Card get(int i)
    {
        return hand.get(i);
    }
    
    /**
     * Returns size of hand
     * 
     * @return Integer size of hand
     */ 
    public int size()
    {
        return hand.size();
    }
    
    /**
     * Removes all cards in hand
     * 
     * @return void
     */ 
    public void reset()
    {
        for(int i = hand.size() - 1; i >= 0; i--)
        {
            hand.remove(i);
        }
    }
    
    /**
     * Adds a card to hand
     * 
     * @param a Card at which to add to hand
     * @return  void
     */
    public void draw(Card a)
    {
        hand.add(a);
    }
    
    /**
     * Returns string value of hand used to compare strengths
     * 
     * @return String value of hand used
     */ 
    public String getHandValue()
    {
        //Where each index represents card values in ascending order:
        //dist[0] = number of aces, dist[1] = number of 2s, etc.
        int[] dist = new int[13];
        int[] suitDist = new int[4];
        int tempInt = 0;
        char[] suitVals = {'♦', '♣', '♥', '♠'};
        boolean onePair = false;
        boolean twoPair = false;
        boolean threeOfAKind = false;
        String threeOfAKindValue = "";
        String twoPairValue = "";
        String onePairValue = "";
        for(int i = 0; i < hand.size(); i++)
        {
            dist[hand.get(i).getNumVal() - 1]++;
            if(hand.get(i).getSuit() == '♦')
            {
                suitDist[0]++;
            }
            else if(hand.get(i).getSuit() == '♣')
            {
                suitDist[1]++;
            }
            else if(hand.get(i).getSuit() == '♥')
            {
                suitDist[2]++;
            }
            else
            {
                suitDist[3]++;
            }
        }
        String value = "";
        String temporaryString = "";
        if(hand.size() == 2)
        {
            int first = hand.get(0).getNumVal();
            int second = hand.get(1).getNumVal();
            for(int i = 0; i < dist.length; i++)
            {
                if(dist[i] == 2)
                {
                    return "b" + handVals.get("" + first);
                }
                else
                {
                    return "a" + handVals.get("" + Math.max(first, second)) + handVals.get("" + Math.min(first, second));
                }
            }
        }
        else if(hand.size() > 2)
        {
            //FOUR OF A KIND CHECKER
            for(int i = 0; i < dist.length; i++)
            {
                if(dist[i] == 4)
                {
                    value += "h";
                }
                if(value != "" && value.substring(0,1).equals("h"))
                {
                    for(int j = 0; j < dist.length; j++)
                    {
                        if(dist[j] == 1)
                        {
                            return "h" + handVals.get("" + (j+1));
                        }
                        if(temporaryString != "")
                        {
                            j = dist.length;
                        }
                    }
                }
            }
            
            //FLUSH CHECKER
            tempInt = hand.get(0).getNumVal();
            for(int i = 0; i < suitDist.length; i++)
            {
                if(suitDist[i] >= 5)
                {
                    value = "f";
                    if(value.length() > 0 && value.substring(0,1).equals("f"))
                    {
                        for(int j = 0; j < hand.size(); j++)
                        {
                            if(hand.get(j).getNumVal() == 1 && hand.get(j).getSuit() == suitVals[i])
                            {
                                return "fm";
                            }
                            if(tempInt <= hand.get(j).getNumVal() && hand.get(j).getSuit() == suitVals[i])
                            {
                                //Stores high card in tempInt that is part of the flush
                                tempInt = hand.get(j).getNumVal();
                            }
                        }
                        return "f" + handVals.get("" + tempInt);
                    }
                }
            
            }
            
            //STRAIGHT CHECKER
            for(int i = 0; i < dist.length - 5; i++)
            {
                if(dist[i] == 1 && dist[i+1] == 1 && dist[i+2] == 1 && dist[i+3] == 1 && dist[i+4] == 1)
                {
                    return "e" + handVals.get("" + (i+5));
                }
            }
            
            //FULL HOUSE/ 3 OF A KIND/ 2 PAIR/ 1 PAIR CHECKER
            for(int i = 0; i < dist.length; i++)
            {
                if(dist[i] == 3)
                {
                    threeOfAKind = true;
                    threeOfAKindValue = handVals.get("" + (i+1));
                }
                if(dist[i] == 2)
                {
                    onePair = true;
                    onePairValue = handVals.get("" + (i+1));
                }
                if(dist[i] == 2 && onePair == true)
                {
                    twoPair = true;
                    twoPairValue = handVals.get("" + (i+1));
                }
            }
            if(threeOfAKind == true && onePair == true)
            {
                return "g" + threeOfAKindValue + onePairValue;
            }
            else if(threeOfAKind == true && onePair == false)
            {
                int[] rest = {0, 0};
                
                for(int i = 0; i < hand.size(); i++)
                {
                    if(hand.get(i).getNumVal() >= rest[0] && !(("" + hand.get(i).getNumVal()).equals(threeOfAKindValue)))
                    {
                        rest[0] = hand.get(i).getNumVal();
                    }
                    else if(hand.get(i).getNumVal() == 1 && !threeOfAKindValue.equals("1"))
                    {
                        rest[0] = 14;
                    }
                    else if(hand.get(i).getNumVal() <= rest[0] && hand.get(i).getNumVal() >= rest[1] && !(("" + hand.get(i).getNumVal()).equals(threeOfAKindValue)))
                    {
                        rest[1] = hand.get(i).getNumVal();
                    }
                }
                return "d" + threeOfAKindValue + handVals.get("" + rest[0]) + handVals.get("" + rest[1]);
            }
            else if(onePair == true && threeOfAKind == false)
            {
                int[] rest = {0, 0, 0};
                int curNum = 0;
                for(int i = 0; i < hand.size(); i++)
                {
                    curNum = hand.get(i).getNumVal();
                    if(curNum >= rest[0] && !(handVals.get("" + curNum).equals(onePairValue)))
                    {
                        rest[2] = rest[1];
                        rest[1] = rest[0];
                        rest[0] = curNum;
                    }
                    if(curNum == 1 && !onePairValue.equals("1"))
                    {
                        rest[2] = rest[1];
                        rest[1] = rest[0];
                        rest[0] = 14;
                    }
                    else if(curNum < rest[0] && curNum >= rest[1] && !(handVals.get("" + curNum).equals(onePairValue)))
                    {
                        rest[2] = rest[1];
                        rest[1] = curNum;
                    }
                    else if(curNum < rest[1] && curNum >= rest[2] && !(handVals.get("" + curNum).equals(onePairValue)))
                    {
                        
                        rest[2] = curNum;
                    }
                    
                }
                return "b" + onePairValue + handVals.get("" + rest[0]) + handVals.get("" + rest[1]) + handVals.get("" + rest[2]);
            }
            else
            {
                int[] rest = {0, 0, 0, 0, 0};
                int curMax = 0;
                for(int i = 0; i < hand.size(); i++)
                {
                    curMax = hand.get(i).getNumVal();
                    if(curMax == 1)
                    {
                        rest[4] = rest[3];
                        rest[3] = rest[2];
                        rest[2] = rest[1];
                        rest[1] = rest[0];
                        rest[0] = 14;
                    }
                    else if(curMax > rest[0] && !(("" + curMax).equals(onePairValue)))
                    {
                        rest[4] = rest[3];
                        rest[3] = rest[2];
                        rest[2] = rest[1];
                        rest[1] = rest[0];
                        rest[0] = hand.get(i).getNumVal();
                    }
                    else if(curMax <= rest[0] && curMax >= rest[1])
                    {
                        rest[4] = rest[3];
                        rest[3] = rest[2];
                        rest[2] = rest[1];
                        rest[1] = curMax;
                    }
                    else if(curMax <= rest[1] && curMax >= rest[2])
                    {
                        rest[4] = rest[3];
                        rest[3] = rest[2];
                        rest[2] = curMax;
                    }
                    else if(curMax <= rest[2] && curMax >= rest[3])
                    {
                        rest[4] = rest[3];
                        rest[3] = curMax;
                    }
                    else
                    {
                        rest[4] = curMax;
                    }
                    
                }
                return "a" + handVals.get("" + rest[0]) + handVals.get("" + rest[1]) + handVals.get("" + rest[2]) + handVals.get("" + rest[3]) + handVals.get("" + rest[4]);
            }
        }
        return value;
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
