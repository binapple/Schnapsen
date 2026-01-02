/*
 * Copyright (c) 2026 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

public class PlayingCard {
    private SchnapsenBoard.cardSuits suit;
    private String cardName;
    private int cardValue;
    //private String cardPicture;
    private boolean isTrumpSuit = false;

    public PlayingCard(SchnapsenBoard.cardSuits suit, String cardName, int cardValue) {
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

    public String getCardName() {
        return cardName;
    }

    public int getCardValue() {
        return cardValue;
    }

    @Override
    public String toString() {
        return cardName;
    }
}
