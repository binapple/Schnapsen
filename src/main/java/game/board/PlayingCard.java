/*
 * Copyright (c) 2026 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

import game.action.SchnapsenAction;

import java.util.Objects;

public class PlayingCard {
    private SchnapsenBoard.cardSuits suit;
    private SchnapsenBoard.cardNames cardName;
    private int cardValue;
    private PlayingCard possibleMarriage;
    //private String cardPicture;
    private boolean isTrumpSuit = false;

    public PlayingCard(SchnapsenBoard.cardSuits suit, SchnapsenBoard.cardNames cardName, int cardValue) {
        this.suit = suit;
        this.cardName = cardName;
        this.cardValue = cardValue;
    }

    public boolean isTrumpSuit() {
        return isTrumpSuit;
    }

    public void setIsTrumpSuit(boolean trumpSuit) {
        isTrumpSuit = trumpSuit;
    }

    public SchnapsenBoard.cardSuits getSuit() {
        return suit;
    }

    public SchnapsenBoard.cardNames getCardName() {
        return cardName;
    }

    public int getCardValue() {
        return cardValue;
    }

    public void setPossibleMarriage(PlayingCard possibleMarriage) {
        this.possibleMarriage = possibleMarriage;
    }

    public PlayingCard getPossibleMarriage() {
        return possibleMarriage;
    }

    @Override
    public String toString() {
        return cardName.toString();
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
            PlayingCard other = (PlayingCard) obj;
            return suit == other.suit &&
                            Objects.equals(cardName, other.cardName) &&
                            Objects.equals(cardValue, other.cardValue);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, cardName, cardValue);
    }
}