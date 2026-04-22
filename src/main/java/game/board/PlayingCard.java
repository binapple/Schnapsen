/*
 * Copyright (c) 2026 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

import java.util.Objects;

public class PlayingCard implements Comparable<PlayingCard> {
    private final SchnapsenBoard.CardSuit suit;
    private final SchnapsenBoard.CardName cardName;
    private final int cardValue;
    private PlayingCard possibleMarriage;
    private boolean isTrumpSuit = false;

    public PlayingCard(SchnapsenBoard.CardSuit suit, SchnapsenBoard.CardName cardName, int cardValue) {
        if (suit == null) {
            throw new IllegalArgumentException("Suit cannot be null");
        }
        if (cardName == null) {
            throw new IllegalArgumentException("Card name cannot be null");
        }
        if (cardValue < 0) {
            throw new IllegalArgumentException("Card value cannot be negative");
        }
        this.suit = suit;
        this.cardName = cardName;
        this.cardValue = cardValue;
    }

    public boolean isTrumpSuit() {
        return isTrumpSuit;
    }

    public void setIsTrumpSuit(boolean trumpSuit) {
        this.isTrumpSuit = trumpSuit;
    }

    public SchnapsenBoard.CardSuit getSuit() {
        return suit;
    }

    public SchnapsenBoard.CardName getCardName() {
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

  @Override
  public int compareTo(PlayingCard o) {
      if(o!=null) {
        //Order cards based on their value
        if (o.getSuit() == suit) {
         return Integer.compare(o.getCardValue(), cardValue);
        } else {

          //Sort Trump cards first
          if(o.isTrumpSuit())
            return 1;
          }

          if(this.isTrumpSuit) {
            return -1;
          }

          //sort based on suit order
          return suit.compareTo(o.getSuit());

        }
      //Fallback compare toString
      return this.toString().compareTo(o.toString());
  }
}