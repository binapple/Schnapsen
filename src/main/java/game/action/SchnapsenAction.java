/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.action;

import game.board.PlayingCard;
import game.board.SchnapsenBoard;

import java.util.*;

public class SchnapsenAction implements Comparable<SchnapsenAction> {

    //This variable is used for distinguishing between the two actions of closing the talon or exchangeing the trump card
    private Boolean talonOrExchange = null;

    //Tracks the players id
    private int playerId;

    //Tracks the card involved in the action
    private PlayingCard playCard;

    //Tracks the cards that are part of the marriage action
    private PlayingCard marriage1;
    private PlayingCard marriage2;

    //States the action in a readable manner
    private String actionMessage;

    public static Set<SchnapsenAction> getPossibleActions(SchnapsenBoard board) {
        Set<SchnapsenAction> possibleActions = new TreeSet<>();
        int playerId = board.getPlayerTurnId();

        //return empty set if game is over (as stated by the engine)
        if(board.isGameOver())
        {
            return possibleActions;
        }

        List<PlayingCard> possibleMarriageCards = new ArrayList<>();
        List<PlayingCard> playerCards;
        PlayingCard leadingCard = board.getLeadingCard();

        if (playerId == 0) {
            playerCards = board.getPlayer0Cards();
        } else {
            playerCards = board.getPlayer1Cards();
        }


        //checking if leading player
        if (leadingCard == null) {

            //checking if marriage was already declared, if so player may only play one of the marriage partners
            PlayingCard marriageCard  = board.getMarriageCardDeclared();
            if (marriageCard != null) {
                if(playerCards.contains(marriageCard)) {
                    if(playerCards.contains(marriageCard.getPossibleMarriage())) {
                        possibleActions.add(new SchnapsenAction(playerId, marriageCard, "Play " + marriageCard.getCardName()));
                        possibleActions.add(new SchnapsenAction(playerId, marriageCard.getPossibleMarriage(), "Play " + marriageCard.getPossibleMarriage().getCardName()));
                    }
                    else {
                        throw new IllegalStateException("Marriage cards not found after declaring marriage");
                    }
                } else {
                    throw new IllegalStateException("Marriage card not found after declaring marriage");
                }
            } else {


                for (PlayingCard card : playerCards) {
                    //if player is leading they may exchange trumps if they have the correct Jack
                    if (!board.isTalonClosed() && !board.playingCardPileIsEmpty()) {
                        if (card.isTrumpSuit() && card.getCardValue() == 2) {
                            //exchange trump card Action
                            possibleActions.add(new SchnapsenAction(playerId, true, "Exchange " + card.getCardName() + " with trump card"));
                        }
                    }

                    //keep track of possible Marriage cards for the player
                    if (card.getPossibleMarriage() != null) {
                        possibleMarriageCards.add(card.getPossibleMarriage());
                    }

                    //if both cards are present we can add the marriage action
                    if (possibleMarriageCards.contains(card)) {
                        //marriage Action
                        possibleActions.add(new SchnapsenAction(playerId, card, card.getPossibleMarriage(), "Marriage of " + card.getCardName() + " + " + card.getPossibleMarriage().getCardName()));
                        possibleMarriageCards.remove(card);
                    }

                    //as leading player any card can be played as an action
                    possibleActions.add(new SchnapsenAction(playerId, card, "Play " + card.getCardName()));
                }
                //close talon Action only if not closed already and pile is not empty yet
                if(!board.isTalonClosed() &&  !board.playingCardPileIsEmpty()) {
                    possibleActions.add(new SchnapsenAction(playerId, false, "Close the talon"));
                }
            }
        } else {
            //when following player the available actions change, if the talon is closed or the draw pile is empty
            if (board.isTalonClosed() || board.playingCardPileIsEmpty()) {

                //keep track of trump cards and same suit cards
                List<PlayingCard> trumps = new ArrayList<>();
                List<PlayingCard> suits = new ArrayList<>();

                //We check if there are any cards that match the suit of the leading card or the trump suit
                for(PlayingCard card : playerCards) {

                    //1. Rule: suit following and trick taking rule, these actions can be added here already
                    if (card.getSuit() == leadingCard.getSuit()) {
                        suits.add(card);
                        if (card.getCardValue() > leadingCard.getCardValue()) {
                            possibleActions.add(new SchnapsenAction(playerId, card, "Play " + card.getCardName()));
                        }
                    }

                    if(card.isTrumpSuit()) {
                        trumps.add(card);
                    }
                }

                //2. Rule: suit following rule, if no card was bigger than the one played, we still have to follow suit
                if (possibleActions.isEmpty()) {
                    for(PlayingCard suitCard : suits) {
                        possibleActions.add(new SchnapsenAction(playerId, suitCard, "Play " + suitCard.getCardName()));
                    };
                }

                //3. Rule: trick taking rule if having trump card, if no card matched the suit we must play any trump card to take the trick
                if (possibleActions.isEmpty()) {
                    for(PlayingCard trumpCard : trumps) {
                        possibleActions.add(new SchnapsenAction(playerId, trumpCard, "Play " + trumpCard.getCardName()));
                    }
                }

                //4. Rule: if no rule is taking place, every card can be played as a following card
                if (possibleActions.isEmpty()) {
                    for(PlayingCard card: playerCards) {
                        possibleActions.add(new SchnapsenAction(playerId, card, "Play " + card.getCardName()));
                    }
                }
            } else {
                //talon is not closed and pile is not empty, every card can be played
                for(PlayingCard card: playerCards) {
                    possibleActions.add(new SchnapsenAction(playerId, card, "Play " + card.getCardName()));
                }
            }


        }

        return possibleActions;
    }

    /**
     * Constructor for exchange and talon closing actions
     * @param playerId id of player who wants to take the action
     * @param talonOrExchange boolean true if exchanging trump action, false if closing talon action
     * @param actionMessage The action's message that is displayed by the engine
     */
    public SchnapsenAction(int playerId, Boolean talonOrExchange, String actionMessage) {
        this.playerId = playerId;
        this.talonOrExchange = talonOrExchange;
        this.actionMessage = actionMessage;
    }

    /**
     * Constructor for playing card actions
     * @param playerId id of player who wants to take the action
     * @param playingCard PlayingCard to be played on the board
     * @param actionMessage The action's message that is displayed by the engine
     */
    public SchnapsenAction(int playerId, PlayingCard playingCard, String actionMessage) {
        this.playerId = playerId;
        this.playCard = playingCard;
        this.actionMessage = actionMessage;
    }

    /**
     * Constructor for playing card actions
     * @param playerId id of player who wants to take the action
     * @param marriage1 one of the PlayingCards of the marriage pair
     * @param marriage2 the other one of the PlayingCards of the marriage pair
     * @param actionMessage The action's message that is displayed by the engine
     */
    public SchnapsenAction(int playerId, PlayingCard marriage1, PlayingCard marriage2, String actionMessage) {
        this.playerId = playerId;
        this.marriage1 = marriage1;
        this.marriage2 = marriage2;
        this.actionMessage = actionMessage;
    }

    /**
     * This method applies the action to the passed SchnapsenBoard
     * @param doBoard the board on which the action should take place
     */
    public void doAction(SchnapsenBoard doBoard) {

        //This action is a "playCard" action
        if (playCard != null) {
            doBoard.playCard(playerId, playCard);
            return;
        }

        //This action is a "declareMarriage" action
        if (marriage1 != null) {
            doBoard.declareMarriage(playerId, marriage1, marriage2);
            return;
        }

        //This action is either an "exchangeTrump" or a "closeTalon" action
        if (talonOrExchange != null) {
            if (talonOrExchange) {
                doBoard.exchangeTrumpCard(playerId);
            } else {
                doBoard.closeTalon(playerId);
            }
        }
    }

    @Override
    public String toString() {
        return actionMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            SchnapsenAction other = (SchnapsenAction) obj;
            return //schnapsenBoard.equals(other.schnapsenBoard) &&
                    playerId == other.playerId &&
                    Objects.equals(talonOrExchange, other.talonOrExchange) &&
                    Objects.equals(playCard, other.playCard) &&
                    Objects.equals(marriage1, other.marriage1) &&
                    Objects.equals(marriage2, other.marriage2);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, talonOrExchange, playCard, marriage1, marriage2);
    }

    /**
     * Ordering Actions to first have play card actions in descending values, prioritizing Trump cards,
     * then showing marriages, trump swaps and lastly close talon actions
     * @param o action to be ordered
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(SchnapsenAction o) {

        int myType = getOrderPriority();
        int otherType = o.getOrderPriority();

        //If the actions are not of the same priority then the comparison can be returned instantly
        if (myType != otherType) {
            return Integer.compare(myType, otherType);
        }

        PlayingCard otherPlayingCard = o.playCard;
        PlayingCard myPlayingCard = this.playCard;

        if(myPlayingCard != null && otherPlayingCard != null) {

            //priority for trump cards
            if(myPlayingCard.isTrumpSuit() !=  otherPlayingCard.isTrumpSuit())
            {
                if(myPlayingCard.isTrumpSuit())
                {
                    return -1;
                }
                else {
                    return 1;
                }
            }

            //order by suit
            if(myPlayingCard.getSuit() != otherPlayingCard.getSuit())
            {
                return myPlayingCard.getSuit().compareTo(otherPlayingCard.getSuit());
            }

            //descending order of values
            return Integer.compare(otherPlayingCard.getCardValue(), myPlayingCard.getCardValue());

        }

        //Compares marriages to each other prioritising marriages of the trump suit
        if(this.marriage1 != null && o.marriage1 != null) {
            boolean myMarriageTrump = this.marriage1.isTrumpSuit();
            boolean otherMarriageTrump = o.marriage1.isTrumpSuit();
            if (myMarriageTrump != otherMarriageTrump) {
                if(myMarriageTrump) {
                    return -1;
                } else
                {
                    return 1;
                }
            }

            return this.marriage1.getSuit().compareTo(o.marriage1.getSuit());
        }

        // Fallback sort by string
        return this.toString().compareTo(o.toString());
    }

    /**
     * Helper to determine if action has higher ordering priority, lower is higher
     * @return integer with ordering number
     */
    private int getOrderPriority() {
            if (playCard != null) return 0;       // Play Card
            if (marriage1 != null) return 1;      // Marriage
            if (talonOrExchange != null) {
                if (talonOrExchange) return 2;    // Exchange Trump
                return 3;                         // Close Talon
            }
            return 4; // Fallback
    }
}
