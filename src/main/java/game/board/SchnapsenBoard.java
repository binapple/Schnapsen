/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

import java.util.*;

public class SchnapsenBoard {
    public enum cardSuits {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS
    }

    private final Random random;
    private final LinkedList<PlayingCard> playingCardPile = new LinkedList<>();
    private final List<PlayingCard> player0Cards = new ArrayList<>();
    private final List<PlayingCard> player1Cards = new ArrayList<>();

    private final List<PlayingCard[]> player0Tricks = new ArrayList<>();
    private final List<PlayingCard[]> player1Tricks = new ArrayList<>();

    private int player0Score;
    private int player1Score;

    //A typical game of Schnapsen is finished when one player gets an overall score of 7 or higher known as a "Bummerl"
    //It is typical to count down from 7, therefore remove the winning points from the score
    private int player0Bummerl = 7;
    private int player1Bummerl = 7;

    //Score for marriages only gets added if the player has made any tricks
    private int player0MarriageTempScore;
    private int player1MarriageTempScore;

    private boolean talonClosed = false;
    private int talonClosingPlayerId;
    private int talonClosedEnemyScore;

    private PlayingCard trumpCard;
    private cardSuits trumpSuit;

    private PlayingCard leadingCard;

    private final int startingPlayer;
    private int playerTurnId;

    /**
     * Creating the initial Cards and preparing the board for the first round
     */
    private void boardInitialisation() {
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, "J♠", 2));
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, "Q♠", 3));
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, "K♠", 4));
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, "10♠", 10));
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, "A♠", 11));

        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, "J♡", 2));
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, "Q♡", 3));
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, "K♡", 4));
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, "10♡", 10));
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, "A♡", 11));

        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, "J♢", 2));
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, "Q♢", 3));
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, "K♢", 4));
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, "10♢", 10));
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, "A♢", 11));

        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, "J♣", 2));
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, "Q♣", 3));
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, "K♣", 4));
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, "10♣", 10));
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, "A♣", 11));

        shuffleCards();
        createTrumpCard();
        passCards(startingPlayer, 5);

    }

    /**
     * Initialize the SchnapsenBoard with a random seed
     */
    public SchnapsenBoard() {
        //TODO: Remove test seed
        this(new Random(0));
    }

    /**
     * Initialize the Schnapsen board with a random object for seed manipulation
     *
     * @param random object that manipulates the shuffling of the deck
     */
    SchnapsenBoard(Random random) {
        startingPlayer = 0;
        playerTurnId = 1 - startingPlayer;
        this.random = random;
        boardInitialisation();

    }

    /**
     * Shuffling cards based on stored random object
     */
    private void shuffleCards() {
        Collections.shuffle(playingCardPile, random);
    }

    /**
     * This method sets the trumpCard and the trump suit, if it is not set yet.
     * The card will then be added as the last card in the card pile.
     * The other cards of this suit will be updated to state they are of the trump suit
     */
    private void createTrumpCard() {
        if (trumpCard == null) {
            trumpCard = playingCardPile.get(10);
            playingCardPile.remove(trumpCard);
            playingCardPile.addLast(trumpCard);
            trumpSuit = trumpCard.getSuit();
        }


        for (PlayingCard card : playingCardPile) {
            if (card.getSuit() == trumpSuit) {
                card.setIsTrumpSuit(true);
            }
        }
    }

    /**
     * Passing cards from the pile to the players, first to the player with the given id
     *
     * @param playerId      players id that receives the card first
     * @param numberOfCards number of cards that are passed out
     */
    private void passCards(int playerId, int numberOfCards) {
        if (!talonClosed && !playingCardPile.isEmpty()) {
            for (int i = 0; i < numberOfCards; i++) {

                if (playerId == 0) {
                    player0Cards.add(playingCardPile.pop());
                    player1Cards.add(playingCardPile.pop());
                } else {
                    player1Cards.add(playingCardPile.pop());
                    player0Cards.add(playingCardPile.pop());
                }
            }
        } else throw new IllegalStateException("Can not pass cards, when talon is closed or pile is empty!");
    }

    /**
     * Player plays a card on the board, logic for who takes the trick if the played card is not the leading card
     *
     * If the playing card pile is empty or the talon was closed,
     * the following card has to have the same color of the leading card and one is obliged to take the trick if possible.
     *
     * Trick is scored and added to the tricks taken pile of the player in the end.
     *
     * @param playerId id of player playing the card
     * @param card     the card the player wants to play
     */
    private void playCard(int playerId, PlayingCard card) {
        if (playerTurnId == playerId) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }

            if (playerCards.contains(card)) {
                if (leadingCard == null) {
                    leadingCard = card;
                    playerCards.remove(leadingCard);
                } else {
                    cardSuits leadingSuit = leadingCard.getSuit();
                    int trickWinnerId = -1;
                    if (talonClosed || playingCardPile.isEmpty()) {
                        if (card.getSuit() != leadingSuit) {
                            for (PlayingCard playingCard : playerCards) {
                                if (playingCard.getSuit() == leadingSuit) {
                                    throw new IllegalArgumentException("Player has to follow leading suit");
                                }
                            }
                            if (card.isTrumpSuit()) {
                                trickWinnerId = playerId;
                            } else {
                                for(PlayingCard playingCard : playerCards) {
                                    if(playingCard.getSuit() == trumpSuit) {
                                        throw new IllegalArgumentException("Player has to play trump if they can not follow the suit");
                                    }
                                }
                                trickWinnerId = 1 - playerId;
                            }
                        } else {
                            if (card.getCardValue() > leadingCard.getCardValue()) {
                                trickWinnerId = playerId;
                            } else {
                                for (PlayingCard playingCard : playerCards) {
                                    if(playingCard.getSuit() == leadingSuit) {
                                        if(playingCard.getCardValue() > leadingCard.getCardValue()) {
                                            throw new IllegalArgumentException("Player has to take the trick if possible!");
                                        }
                                    }
                                }
                                trickWinnerId = 1 - playerId;
                            }
                        }
                    } else {
                        if (card.getSuit() != leadingSuit) {
                            if (card.isTrumpSuit()) {
                                trickWinnerId = playerId;
                            } else {
                                trickWinnerId = 1 - playerId;
                            }
                        } else {
                            if (card.getCardValue() > leadingCard.getCardValue()) {
                                trickWinnerId = playerId;
                            } else {
                                trickWinnerId = 1 - playerId;
                            }
                        }
                    }

                    if (trickWinnerId == 0) {
                        player0Tricks.add(new PlayingCard[]{card,leadingCard});
                        player0Score += card.getCardValue() + leadingCard.getCardValue();
                        if (player0MarriageTempScore != 0) {
                            player0Score += player0MarriageTempScore;
                            player0MarriageTempScore = 0;
                        }
                        if(!talonClosed) {
                            passCards(0, 1);
                        }
                    } else {
                        player1Tricks.add(new PlayingCard[]{card,leadingCard});
                        player1Score += card.getCardValue() + leadingCard.getCardValue();
                        if (player1MarriageTempScore != 0) {
                            player1Score += player1MarriageTempScore;
                            player1MarriageTempScore = 0;
                        }
                        if(!talonClosed) {
                            passCards(1,1);
                        }
                    }
                    playerCards.remove(card);
                    leadingCard = null;

                    //Winning player gets to play the leading card
                    playerTurnId = trickWinnerId;

                    //Board checks if round is over
                    if (isRoundOver()){
                        calculateBummerl();
                    }

                    return;
                }

                //player turn shifts to player that has not yet played a card
                playerTurnId = 1 - playerTurnId;

            } else throw new IllegalArgumentException("Card not in players hand!");
        } else throw new IllegalStateException("It is not the players turn!");
    }

    /**
     * Leading players can trade the Jack of the trump suit with the trump card.
     *
     * @param playerId id of the player to make the exchange
     */
    private void exchangeTrumpCard(int playerId) {

        if (playerId == playerTurnId && leadingCard == null) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }

            String jackName = switch (trumpSuit) {
                case SPADES -> "J♠";
                case HEARTS -> "J♡";
                case DIAMONDS -> "J♢";
                case CLUBS -> "J♣";
            };

            for (PlayingCard playingCard : playerCards) {
                if (playingCard.getCardName().equals(jackName)) {
                    PlayingCard cardSwitch = trumpCard;
                    trumpCard = playingCard;
                    playerCards.remove(playingCard);
                    playerCards.add(cardSwitch);
                }
            }
        } else {
            throw new IllegalStateException("Player can only swap Trump Card if they are the leading player");
        }
    }


    /**
     * Leading players are allowed to declare a marriage of the Queen and King of the same suit to earn points.
     * Trump marriages gain 40 points, the others gain 20.
     *
     * @param playerId      id of player to declare marriage
     * @param marriageCard1 first card of the marriage
     * @param marriageCard2 second card of the marriage
     */
    private void declareMarriage(int playerId, PlayingCard marriageCard1, PlayingCard marriageCard2) {
        if (playerTurnId == playerId && leadingCard == null) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }
            int tempScore = 0;
            if (playerCards.contains(marriageCard1) && playerCards.contains(marriageCard2)) {
                if (marriageCard1.getSuit() == marriageCard2.getSuit()) {
                    if (marriageCard1.getCardValue() != marriageCard2.getCardValue()) {
                        if (marriageCard1.getCardValue() == 3 || marriageCard1.getCardValue() == 4) {
                            if (marriageCard2.getCardValue() == 3 || marriageCard1.getCardValue() == 4) {
                                if (marriageCard1.isTrumpSuit()) {
                                    tempScore = 40;
                                } else {
                                    tempScore = 20;
                                }
                            }
                        }
                    } else throw new IllegalArgumentException("Marriage cards are the same card!");
                } else throw new IllegalArgumentException("Marriage cards are not the same suit");

                if (tempScore > 0) {
                    if (playerId == 0) {
                        if (player0Score != 0) {
                            player0Score += tempScore;
                        } else {
                            player0MarriageTempScore = tempScore;
                        }
                    } else {
                        if (player1Score != 0) {
                            player1Score += tempScore;
                        } else {
                            player1MarriageTempScore = tempScore;
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Player does not have the necessary cards");
            }
        } else throw new IllegalStateException("Player can only declare marriages if they are the leading player");
    }

    /**
     * Leading players can close the talon to stop the drawing of new cards and enforce the follow suit rule
     * @param playerId id of player to close the talon
     */
    private void closeTalon(int playerId) {
        if(playerTurnId == playerId && leadingCard == null) {
            talonClosed = true;
            talonClosingPlayerId = playerId;
            if(playerId == 0) {
                talonClosedEnemyScore = player1Score;
            } else {
                talonClosedEnemyScore = player0Score;
            }

        } else throw new IllegalStateException("Player can only close talon if they are the leading player");
    }

    /**
     * The Bummerl scoring is done by checking the score points of the loosing player when the round is over:
     * If the loosing player has at least 33 score points in the round the winning player will receive 1 Bummerl point,
     * if they managed to get at least 1 trick the winning player will receive 2 Bummerl points,
     * if they did not get any tricks the winning player receives 3 Bummerl points.
     *
     * If no player had 66 score points in total, the last player to win a trick wins 1 Bummerl point.
     *
     * If the player that closed the talon wins, this scoring system is the same,
     * but the score the non closing player receives during the "closing" are not counted.
     * If they loosed after closing, all points they would receive when winning are given to the non-closing player
     * and the non-closing player will get at least 2 Bummerl points no matter the amount of tricks they had.
     * Loosing means that they did not get 66 score points even tough they closed the talon.
     * The last trick scoring rule is not active if the talon was closed
     *
     *
     */
    private void calculateBummerl()
    {
        if(isRoundOver() && !isGameOver())
        {
            if(talonClosed)
            {
                if(talonClosingPlayerId == 0)
                {
                    if(player0Score >= 66)
                    {
                        if(player1Score == 0)
                        {
                            player0Bummerl = player0Bummerl-3;
                        } else if(player1Score < 33)
                        {
                            player0Bummerl = player0Bummerl-2;
                        }
                        else {
                            player0Bummerl = player0Bummerl-1;
                        }
                    }
                    else {
                        if(talonClosedEnemyScore == 0)
                        {
                            player1Bummerl = player1Bummerl-3;
                        } else {
                            player1Bummerl = player1Bummerl-2;
                        }

                    }
                }
                else
                {
                    if(player1Score >= 66){
                        if(player0Score == 0)
                        {
                            player1Bummerl = player1Bummerl-3;
                        } else if (player0Score < 33)
                        {
                            player1Bummerl = player1Bummerl-2;
                        } else  {
                            player1Bummerl = player1Bummerl-1;
                        }
                    }
                    else {
                        if(talonClosedEnemyScore == 0)
                        {
                            player0Bummerl = player0Bummerl-3;
                        }
                        else {
                            player0Bummerl = player0Bummerl-2;
                        }
                    }
                }
            } else {
                if(player1Score >= 66)
                {
                    if(player0Score == 0) {
                        player1Bummerl = player1Bummerl-3;
                    }
                    else if (player0Score < 33) {
                        player1Bummerl = player1Bummerl-2;
                    } else {
                        player1Bummerl = player1Bummerl-1;
                    }
                } else if(player0Score >= 66) {
                    if(player1Score == 0)
                    {
                        player0Bummerl = player0Bummerl-3;
                    }
                    else if(player0Score < 33) {
                        player0Bummerl = player0Bummerl-2;
                    } else {
                        player0Bummerl = player0Bummerl-1;
                    }
                } else {
                    if(playerTurnId == 0)
                    {
                        player0Bummerl = player0Bummerl-1;
                    } else {
                        player0Bummerl = player0Bummerl-1;
                    }
                }

            }
            if(isGameOver()){
                if(player0Bummerl <= 0)
                {
                    System.out.println("Player 1 won the Bummerl (game)!");
                } else {
                    System.out.println("Player 2 won the Bummerl (game)!");
                }
            }
            else {
                System.out.println("Round over! Player 1 has " + player0Bummerl + " points left on the Bummerl!");
                System.out.println("Round over! Player 2 has " + player1Bummerl + " points left on the Bummerl!");
            }
        }
    }

    /**
     * This method checks the players scores and returns true if the round is over,
     * as the total possible score without marriages is 120 it is possible that no player has 66 or more points (especially when the talon was closed),
     * therefore the player card piles are checked as well
     * @return boolean to check if round is over
     */
    private boolean isRoundOver() {
        return player1Score >= 66 || player0Score >= 66 || player0Cards.isEmpty() && player1Cards.isEmpty();
    }

    /**
     * If the overall score of one player is 0 or lower the game is over (counted down from 7)
     * @return boolean that checks if the game is over
     */
    private boolean isGameOver() {
        return player0Bummerl <= 0 || player1Bummerl <= 0;
    }




    @Override
    public String toString() {
        String leadCard = "";
        if (leadingCard != null) {
            leadCard = "Leading Card: " + leadingCard.toString() + "\n" + "--------------------\n";
        }
        String sb =
                "--------------------\n" +
                "♠♡♢♣ SCHNAPSEN ♣♢♡♠\n" +
                "--------------------\n" +
                "Player 1's Hand: " + player0Cards + " Player 1's Score: " + player0Score + " Player 1's Tricks: " + player0Tricks + "\n" +
                "--------------------\n" +
                "Remaining Cards: " + playingCardPile.size() + " Trump Card: " + trumpCard.toString() + "\n" +
                "--------------------\n" +
                leadCard +
                "Drawing Pile: " + playingCardPile + "\n" +
                "--------------------\n" +
                "Player 2's Hand: " + player1Cards + " Player 2's Score: " + player1Score + " Player 2's Tricks: " + player1Tricks + "\n";
        return sb;
    }
}
