/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.action;

import game.board.PlayingCard;
import game.board.SchnapsenBoard;

import java.util.*;

public class SchnapsenAction {

    private Boolean talonOrExchange = null;
    private int playerId;
    private PlayingCard playCard;
    private PlayingCard marriage1;
    private PlayingCard marriage2;
    private SchnapsenBoard schnapsenBoard;
    private String actionMessage;

    public static Set<SchnapsenAction> getPossibleActions(SchnapsenBoard board, int playerId) {
        Set<SchnapsenAction> possibleActions = new HashSet<>();
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
                        possibleActions.add(new SchnapsenAction(board, playerId, marriageCard, "Play " + marriageCard.getCardName()));
                        possibleActions.add(new SchnapsenAction(board, playerId, marriageCard.getPossibleMarriage(), "Play " + marriageCard.getPossibleMarriage().getCardName()));
                    }
                    else {
                        throw new IllegalStateException("Marriage cards not found after declaring marriage");
                    }
                } else {
                    throw new IllegalStateException("Marriage card not found after declaring marriage");
                }
            } else {
                for (PlayingCard card : playerCards) {
                    if (!board.isTalonClosed() && !board.playingCardPileIsEmpty()) {
                        if (card.isTrumpSuit() && card.getCardValue() == 2) {
                            //exchange trump card Action
                            possibleActions.add(new SchnapsenAction(board, playerId, true, "Exchange " + card.getCardName() + " with trump card"));
                        }
                        //close talon Action only if not closed already
                        possibleActions.add(new SchnapsenAction(board, playerId, false, "Close the talon"));
                    }

                    if (card.getPossibleMarriage() != null) {
                        possibleMarriageCards.add(card.getPossibleMarriage());
                    }

                    if (possibleMarriageCards.contains(card)) {
                        //marriage Action
                        possibleActions.add(new SchnapsenAction(board, playerId, card, card.getPossibleMarriage(), "Marriage of " + card.getCardName() + " + " + card.getPossibleMarriage().getCardName()));
                        possibleMarriageCards.remove(card);
                    }

                    //play card Action
                    possibleActions.add(new SchnapsenAction(board, playerId, card, "Play " + card.getCardName()));
                }
            }
        } else {
            //when following player
            if (board.isTalonClosed() || board.playingCardPileIsEmpty()) {

                //keep track of trump cards and same suit cards
                List<PlayingCard> trumps = new ArrayList<>();
                List<PlayingCard> suits = new ArrayList<>();

                for(PlayingCard card : playerCards) {
                    //suit following and trick taking rule
                    if (card.getSuit() == leadingCard.getSuit()) {
                        suits.add(card);
                        if (card.getCardValue() > leadingCard.getCardValue()) {
                            possibleActions.add(new SchnapsenAction(board, playerId, card, "Play " + card.getCardName()));
                        }
                    }

                    if(card.isTrumpSuit()) {
                        trumps.add(card);
                    }
                }

                //suit following rule
                if (possibleActions.isEmpty()) {
                    for(PlayingCard suitCard : suits) {
                        possibleActions.add(new SchnapsenAction(board, playerId, suitCard, "Play " + suitCard.getCardName()));
                    };
                }

                //trick taking rule if having trump card
                if (possibleActions.isEmpty()) {
                    for(PlayingCard trumpCard : trumps) {
                        possibleActions.add(new SchnapsenAction(board, playerId, trumpCard, "Play " + trumpCard.getCardName()));
                    }
                }

                //if no rule is taking place, every card can be played as a following card
                if (possibleActions.isEmpty()) {
                    for(PlayingCard card: playerCards) {
                        possibleActions.add(new SchnapsenAction(board, playerId, card, "Play " + card.getCardName()));
                    }
                }
            } else {
                //talon is not closed and pile is not empty, every card can be played
                for(PlayingCard card: playerCards) {
                    possibleActions.add(new SchnapsenAction(board, playerId, card, "Play " + card.getCardName()));
                }
            }


        }

        return possibleActions;
    }

    public SchnapsenAction(SchnapsenBoard board, int playerId, Boolean talonOrExchange, String actionMessage) {
        this.schnapsenBoard = board;
        this.playerId = playerId;
        this.talonOrExchange = talonOrExchange;
        this.actionMessage = actionMessage;
    }

    public SchnapsenAction(SchnapsenBoard board, int playerId, PlayingCard playingCard, String actionMessage) {
        this.schnapsenBoard = board;
        this.playerId = playerId;
        this.playCard = playingCard;
        this.actionMessage = actionMessage;
    }

    public SchnapsenAction(SchnapsenBoard board, int playerId, PlayingCard marriage1, PlayingCard marriage2, String actionMessage) {
        this.schnapsenBoard = board;
        this.playerId = playerId;
        this.marriage1 = marriage1;
        this.marriage2 = marriage2;
        this.actionMessage = actionMessage;
    }

    public void doAction() {
        if (playCard != null) {
            schnapsenBoard.playCard(playerId, playCard);
            return;
        }

        if (marriage1 != null) {
            schnapsenBoard.declareMarriage(playerId, marriage1, marriage2);
            return;
        }

        if (talonOrExchange != null) {
            if (talonOrExchange) {
                schnapsenBoard.exchangeTrumpCard(playerId);
            } else {
                schnapsenBoard.closeTalon(playerId);
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
            return schnapsenBoard.equals(other.schnapsenBoard) &&
                    playerId == other.playerId &&
                    Objects.equals(talonOrExchange, other.talonOrExchange) &&
                    Objects.equals(playCard, other.playCard) &&
                    Objects.equals(marriage1, other.marriage1) &&
                    Objects.equals(marriage2, other.marriage2);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(schnapsenBoard, playerId, talonOrExchange, playCard, marriage1, marriage2);
    }
}
