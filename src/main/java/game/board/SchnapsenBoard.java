/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SchnapsenBoard {

    //These enums help in keeping the naming of suits and cards consistent
    public enum CardSuit {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS
    }

    public enum CardName {
        JackOfSpades {
            @Override
            public String toString() {
                return "JS";
            }
        },
        QueenOfSpades {
            @Override
            public String toString() {
                return "QS";
            }
        },
        KingOfSpades {
            @Override
            public String toString() {
                return "KS";
            }
        },
        TenOfSpades {
            @Override
            public String toString() {
                return "10S";
            }
        },
        AceOfSpades {
            @Override
            public String toString() {
                return "AS";
            }
        },
        JackOfHearts {
            @Override
            public String toString() {
                return "JH";
            }
        },
        QueenOfHearts {
            @Override
            public String toString() {
                return "QH";
            }
        },
        KingOfHearts {
            @Override
            public String toString() {
                return "KH";
            }
        },
        TenOfHearts {
            @Override
            public String toString() {
                return "10H";
            }
        },
        AceOfHearts {
            @Override
            public String toString() {
                return "AH";
            }
        },
        JackOfDiamonds {
            @Override
            public String toString() {
                return "JD";
            }
        },
        QueenOfDiamonds {
            @Override
            public String toString() {
                return "QD";
            }
        },
        KingOfDiamonds {
            @Override
            public String toString() {
                return "KD";
            }
        },
        TenOfDiamonds {
            @Override
            public String toString() {
                return "10D";
            }
        },
        AceOfDiamonds {
            @Override
            public String toString() {
                return "AD";
            }
        },
        JackOfClubs {
            @Override
            public String toString() {
                return "JC";
            }
        },
        QueenOfClubs {
            @Override
            public String toString() {
                return "QC";
            }
        },
        KingOfClubs {
            @Override
            public String toString() {
                return "KC";
            }
        },
        TenOfClubs {
            @Override
            public String toString() {
                return "10C";
            }
        },
        AceOfClubs {
            @Override
            public String toString() {
                return "AC";
            }
        },
        PlaceHolder {
            @Override
            public String toString() {
                return "Hidden Card";
            }
        }
    }

    //Random object that controls the shuffling of cards
    private Random random;

    //These lists track the drafting pile and the players cards
    private LinkedList<PlayingCard> playingCardPile = new LinkedList<>();
    private List<PlayingCard> player0Cards = new ArrayList<>();
    private List<PlayingCard> player1Cards = new ArrayList<>();

    //In this List the Tricks taken by the player are stored in pairs
    private List<PlayingCard[]> player0Tricks = new ArrayList<>();
    private List<PlayingCard[]> player1Tricks = new ArrayList<>();

    //This is the current round score (when on player reaches 66, the round is over)
    private int player0Score;
    private int player1Score;

    //A typical game of Schnapsen is finished when one player gets an overall score of 7 or higher known as a "Bummerl"
    //It is typical to count down from 7, therefore remove the winning points from the score
    private int player0Bummerl = 7;
    private int player1Bummerl = 7;

    //One can set the amount of Bummerl to be played, when creating a board
    //A Bummerl is given to the losing player of each round.
    //The game ends when one player has reached the maximum Bummerl amount. (Default 1)
    private int bummerlMax = 1;
    private int player0BummerlAmount;
    private int player1BummerlAmount;

    //Score for marriages only gets added if the player has made any tricks
    //These variables help in tracking if there are any marriage points not yet scored
    private int player0MarriageTempScore;
    private int player1MarriageTempScore;

    //This variable checks if a marriage was declared, therefore one would have to play one of the two marriage partners
    private PlayingCard marriageCardDeclared;

    //Keep track of all declared marriages by the players this round -> this Information is public
    private List<PlayingCard> player0Marriages = new ArrayList<>();
    private List<PlayingCard> player1Marriages = new ArrayList<>();

    //These variables help in the talon closing logic of the board
    //The id of the closing player is tracked as well as the score of the non-closing player
    //This score has an impact on the amount of points deducted of the current Bummerl, after the round is over
    private boolean talonClosed = false;
    private int talonClosingPlayerId = -1;
    private int talonClosedEnemyScore;

    //These variables help in tracking the trump card and the trump suit
    private PlayingCard trumpCard;
    private CardSuit trumpSuit;

    //Keep track of the old trump card in case of an "exchange"
    private PlayingCard oldTrumpCard;

    //When one player plays a card it is no longer in their hand, but untill the other player plays their card and the trick is resovled
    //the lead card is tracked in this variable
    private PlayingCard leadingCard;

    //These variables help in tracking whose turn it is to be starting a round and which players turn it is
    private int startingPlayer;
    private int playerTurnId;

    /**
     * Creating the playing cards and adding them to the playing card pile with the provided enums
     */
    private void createCards()
    {
        playingCardPile.add(new PlayingCard(CardSuit.SPADES, CardName.JackOfSpades, 2));
        //Adding possible marriages to the spades cards
        PlayingCard queenSpades = new PlayingCard(CardSuit.SPADES, CardName.QueenOfSpades, 3);
        PlayingCard kingSpades = new PlayingCard(CardSuit.SPADES, CardName.KingOfSpades, 4);
        queenSpades.setPossibleMarriage(kingSpades);
        kingSpades.setPossibleMarriage(queenSpades);
        playingCardPile.add(queenSpades);
        playingCardPile.add(kingSpades);
        playingCardPile.add(new PlayingCard(CardSuit.SPADES, CardName.TenOfSpades, 10));
        playingCardPile.add(new PlayingCard(CardSuit.SPADES, CardName.AceOfSpades, 11));

        playingCardPile.add(new PlayingCard(CardSuit.HEARTS, CardName.JackOfHearts, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenHearts = new PlayingCard(CardSuit.HEARTS, CardName.QueenOfHearts, 3);
        PlayingCard kingHearts = new PlayingCard(CardSuit.HEARTS, CardName.KingOfHearts, 4);
        queenHearts.setPossibleMarriage(kingHearts);
        kingHearts.setPossibleMarriage(queenHearts);
        playingCardPile.add(queenHearts);
        playingCardPile.add(kingHearts);
        playingCardPile.add(new PlayingCard(CardSuit.HEARTS, CardName.TenOfHearts, 10));
        playingCardPile.add(new PlayingCard(CardSuit.HEARTS, CardName.AceOfHearts, 11));

        playingCardPile.add(new PlayingCard(CardSuit.DIAMONDS, CardName.JackOfDiamonds, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenDiamonds = new PlayingCard(CardSuit.DIAMONDS, CardName.QueenOfDiamonds, 3);
        PlayingCard kingDiamonds = new PlayingCard(CardSuit.DIAMONDS, CardName.KingOfDiamonds, 4);
        queenDiamonds.setPossibleMarriage(kingDiamonds);
        kingDiamonds.setPossibleMarriage(queenDiamonds);
        playingCardPile.add(queenDiamonds);
        playingCardPile.add(kingDiamonds);
        playingCardPile.add(new PlayingCard(CardSuit.DIAMONDS, CardName.TenOfDiamonds, 10));
        playingCardPile.add(new PlayingCard(CardSuit.DIAMONDS, CardName.AceOfDiamonds, 11));

        playingCardPile.add(new PlayingCard(CardSuit.CLUBS, CardName.JackOfClubs, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenClubs = new PlayingCard(CardSuit.CLUBS, CardName.QueenOfClubs, 3);
        PlayingCard kingClubs = new PlayingCard(CardSuit.CLUBS, CardName.KingOfClubs, 4);
        queenClubs.setPossibleMarriage(kingClubs);
        kingClubs.setPossibleMarriage(queenClubs);
        playingCardPile.add(queenClubs);
        playingCardPile.add(kingClubs);
        playingCardPile.add(new PlayingCard(CardSuit.CLUBS, CardName.TenOfClubs, 10));
        playingCardPile.add(new PlayingCard(CardSuit.CLUBS, CardName.AceOfClubs, 11));
    }

    /**
     * preparing the board for the next round
     */
    private void roundInitialisation() {

        if(playingCardPile.isEmpty())
        {
            createCards();
        }
        shuffleCards();
        createTrumpCard();
        passCards(startingPlayer, 5);

    }

    /**
     * Initialize the SchnapsenBoard with a random seed
     */
    public SchnapsenBoard() {
        this(new Random());
    }

    /**
     * Initialize the Schnapsen board with a random object for seed manipulation
     *
     * @param random object that manipulates the shuffling of the deck
     */
    public SchnapsenBoard(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random object cannot be null");
        }
        startingPlayer = 0;
        playerTurnId = startingPlayer;
        this.random = random;
        roundInitialisation();

    }

    /**
     * Create a board based on an amount of Bummerl and a random object
     * @param random object that manipulates shuffling of the deck
     * @param bummerlMax states how many Bummerl the game will last
     */
    public SchnapsenBoard(Random random, int bummerlMax) {
        if (random == null) {
            throw new IllegalArgumentException("Random object cannot be null");
        }
        if (bummerlMax < 1) {
            throw new IllegalArgumentException("The max of Bummerl can not be lower than 1");
        }
        this.startingPlayer = 0;
        this.playerTurnId = startingPlayer;
        this.random = random;
        this.bummerlMax = bummerlMax;
        roundInitialisation();
    }

    /**
     * This constructor is used for deep copying the given Schnapsen Board
     * @param schnapsenBoard the Schnapsen Board to be deeply copied into the new one
     */
    public SchnapsenBoard(SchnapsenBoard schnapsenBoard) {
      if(schnapsenBoard == null) throw new IllegalArgumentException("board to be copied cannot be null");
        this(schnapsenBoard.player0Cards, schnapsenBoard.player1Cards, schnapsenBoard.playingCardPile, schnapsenBoard.player0Tricks,
                schnapsenBoard.player1Tricks, schnapsenBoard.startingPlayer, schnapsenBoard.playerTurnId, schnapsenBoard.random, schnapsenBoard.player0MarriageTempScore,
                schnapsenBoard.player1MarriageTempScore, schnapsenBoard.player0Bummerl, schnapsenBoard.player1Bummerl, schnapsenBoard.player0Score, schnapsenBoard.player1Score,
                schnapsenBoard.talonClosingPlayerId, schnapsenBoard.talonClosedEnemyScore, schnapsenBoard.talonClosed, schnapsenBoard.leadingCard,
                schnapsenBoard.trumpCard, schnapsenBoard.trumpSuit,schnapsenBoard.marriageCardDeclared, schnapsenBoard.bummerlMax, schnapsenBoard.player0BummerlAmount, schnapsenBoard.player1BummerlAmount,
                schnapsenBoard.player0Marriages, schnapsenBoard.player1Marriages, schnapsenBoard.oldTrumpCard);
    }


    /**
     * This constructor is used to fill in all "hidden" Information to a new deep copied Schnapsen Board, while keeping all the other boards information
     * @param otherBoard the board which information will be used by the new board
     * @param player0Cards the player 0 cards to be used by the new board
     * @param player1Cards the player 1 cards to be used by the new board
     * @param playingCardPile the playing card pile to be used by the new board
     */
    public SchnapsenBoard(SchnapsenBoard otherBoard, List<PlayingCard> player0Cards, List<PlayingCard> player1Cards, LinkedList<PlayingCard> playingCardPile) {
      if (otherBoard == null) throw new IllegalArgumentException("The passed board cannot be null");
      if (player0Cards == null) throw new IllegalArgumentException("player0Cards cannot be null");
      if (player1Cards == null) throw new IllegalArgumentException("player1Cards cannot be null");
      if (playingCardPile == null) throw new IllegalArgumentException("playingCardPile cannot be null");

      this(player0Cards, player1Cards, playingCardPile, otherBoard.player0Tricks, otherBoard.player1Tricks, otherBoard.startingPlayer, otherBoard.playerTurnId, otherBoard.random,
                otherBoard.player0MarriageTempScore, otherBoard.player1MarriageTempScore, otherBoard.player0Bummerl, otherBoard.player1Bummerl, otherBoard.player0Score, otherBoard.player1Score,
                otherBoard.talonClosingPlayerId, otherBoard.talonClosedEnemyScore, otherBoard.talonClosed, otherBoard.leadingCard, otherBoard.trumpCard, otherBoard.trumpSuit,
                otherBoard. marriageCardDeclared, otherBoard.bummerlMax, otherBoard.player0BummerlAmount, otherBoard.player1BummerlAmount, otherBoard.player0Marriages, otherBoard.player1Marriages, otherBoard.oldTrumpCard);
    }

    /**
     * Creates a deep copy of a Schnapsen board based on the inputs given, the new board also features new Cards
     */
    public SchnapsenBoard(List<PlayingCard> player0Cards,
                          List<PlayingCard> player1Cards,
                          LinkedList<PlayingCard> playingCardPile,
                          List<PlayingCard[]> player0Tricks,
                          List<PlayingCard[]> player1Tricks,
                          int startingPlayer,
                          int playerTurnId,
                          Random random,
                          int player0MarriageTempScore,
                          int player1MarriageTempScore,
                          int player0Bummerl,
                          int player1Bummerl,
                          int player0Score,
                          int player1Score,
                          int talonClosingPlayerId,
                          int talonClosedEnemyScore,
                          boolean talonClosed,
                          PlayingCard leadingCard,
                          PlayingCard trumpCard,
                          CardSuit trumpSuit,
                          PlayingCard marriageCardDeclared,
                          int bummerlMax,
                          int player0BummerlAmount,
                          int player1BummerlAmount,
                          List<PlayingCard> player0Marriages,
                          List<PlayingCard> player1Marriages,
                          PlayingCard oldTrumpCard

                          ) {

        this.createCards();

        this.player0Cards = new ArrayList<>();
        this.player1Cards = new ArrayList<>();
        this.player0Tricks = new ArrayList<>();
        this.player1Tricks = new ArrayList<>();
        List<PlayingCard> newPile = new ArrayList<>(this.playingCardPile);
        this.playingCardPile.clear();

        this.trumpCard = findCardInPile(newPile, trumpCard);
        this.trumpCard.setIsTrumpSuit(true);
        if (marriageCardDeclared != null){
            this.marriageCardDeclared = findCardInPile(newPile, marriageCardDeclared);
            this.marriageCardDeclared.setIsTrumpSuit(marriageCardDeclared.isTrumpSuit());
        }

        if(oldTrumpCard != null){
            this.oldTrumpCard = findCardInPile(newPile, oldTrumpCard);
            this.oldTrumpCard.setIsTrumpSuit(true);
        }

        if (leadingCard != null){
            this.leadingCard = findCardInPile(newPile, leadingCard);
            this.leadingCard.setIsTrumpSuit(leadingCard.isTrumpSuit());
        }

        for(PlayingCard playingCard : player0Cards)
        {
            PlayingCard foundCard = findCardInPile(newPile, playingCard);
            foundCard.setIsTrumpSuit(playingCard.isTrumpSuit());
            this.player0Cards.add(foundCard);
        }

        for(PlayingCard playingCard : player1Cards)
        {
            PlayingCard foundCard = findCardInPile(newPile, playingCard);
            foundCard.setIsTrumpSuit(playingCard.isTrumpSuit());
            this.player1Cards.add(foundCard);
        }

        for(PlayingCard playingCard : playingCardPile)
        {
            PlayingCard foundCard = findCardInPile(newPile, playingCard);
            foundCard.setIsTrumpSuit(playingCard.isTrumpSuit());
            this.playingCardPile.add(foundCard);
        }

        for(PlayingCard[] playingCardArray : player0Tricks)
        {
            PlayingCard foundCard0 = findCardInPile(newPile, playingCardArray[0]);
            PlayingCard foundCard1 = findCardInPile(newPile, playingCardArray[1]);
            foundCard1.setIsTrumpSuit(playingCardArray[0].isTrumpSuit());
            foundCard0.setIsTrumpSuit(playingCardArray[1].isTrumpSuit());
            this.player0Tricks.add(new PlayingCard[]{foundCard0, foundCard1});
        }

        for(PlayingCard[] playingCardArray : player1Tricks)
        {
            PlayingCard foundCard0 = findCardInPile(newPile, playingCardArray[0]);
            PlayingCard foundCard1 = findCardInPile(newPile, playingCardArray[1]);
            foundCard1.setIsTrumpSuit(playingCardArray[0].isTrumpSuit());
            foundCard0.setIsTrumpSuit(playingCardArray[1].isTrumpSuit());
            this.player1Tricks.add(new PlayingCard[]{foundCard0, foundCard1});
        }

        for(PlayingCard card : player0Marriages)
        {
            PlayingCard foundCard = findCardInPile(newPile, card);
            foundCard.setIsTrumpSuit(card.isTrumpSuit());
            this.player0Marriages.add(card);
        }

        for(PlayingCard card : player1Marriages)
        {
            PlayingCard foundCard = findCardInPile(newPile, card);
            foundCard.setIsTrumpSuit(card.isTrumpSuit());
            this.player1Marriages.add(foundCard);
        }



        this.startingPlayer = startingPlayer;
        this.playerTurnId = playerTurnId;
        this.random = new Random(random.nextLong());
        this.player0MarriageTempScore = player0MarriageTempScore;
        this.player1MarriageTempScore = player1MarriageTempScore;
        this.player0Bummerl = player0Bummerl;
        this.player1Bummerl = player1Bummerl;
        this.player0Score = player0Score;
        this.player1Score = player1Score;
        this.talonClosingPlayerId = talonClosingPlayerId;
        this.talonClosedEnemyScore = talonClosedEnemyScore;
        this.talonClosed = talonClosed;
        this.trumpSuit = trumpSuit;
        this.bummerlMax = bummerlMax;
        this.player0BummerlAmount = player0BummerlAmount;
        this.player1BummerlAmount = player1BummerlAmount;
    }

    /**
     * helper method to get the first card equal to the specified card from a pile
     * @param pile the playing card pile to be searched
     * @param targetCard the card to be found
     * @return PlayingCard matching the search card from the pile
     */
    private PlayingCard findCardInPile(List<PlayingCard> pile, PlayingCard targetCard){
        if (pile == null || targetCard == null) {
            return null;
        }
        if(targetCard.getCardName() == CardName.PlaceHolder) {
            return new PlayingCard(CardSuit.SPADES,CardName.PlaceHolder,0);
        }
        return pile.stream().filter(p -> p.equals(targetCard)).findFirst().orElse(null);
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
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
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
     * Player plays a card on the board, includes logic for who takes the trick if the played card is not the leading card
     * <p>
     * If the playing card pile is empty or the talon was closed,
     * the following card has to have the same color of the leading card and one is obliged to take the trick if possible.
     * These rules are enforced by throwing exceptions if broken
     * <p>
     * Trick is scored and added to the tricks taken pile of the player in the end.
     *
     * @param playerId id of player playing the card
     * @param card the card the player wants to play
     */
    public void playCard(int playerId, PlayingCard card) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
       if (playerTurnId == playerId) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }

            if (playerCards.contains(card)) {
                if (leadingCard == null) {

                    //if marriage was declared, the player has to lead with one of the two marriage partner cards
                    if(marriageCardDeclared != null) {
                        if(marriageCardDeclared.equals(card) || marriageCardDeclared.getPossibleMarriage().equals(card)) {
                            leadingCard = card;
                            playerCards.remove(card);
                            marriageCardDeclared = null;
                        } else
                        {
                            throw new IllegalArgumentException("Player has to play one of the declared marriage partners!");
                        }
                    } else {
                        //If no marriage was declared, the leading player can play any card of their choice
                        leadingCard = card;
                        playerCards.remove(leadingCard);
                    }
                } else {
                    //the non-leading player may have restrictions on which cards to play based on the status of the talon/drawing pile
                    CardSuit leadingSuit = leadingCard.getSuit();
                    int trickWinnerId = -1;
                    if (talonClosed || playingCardPile.isEmpty()) {
                        //the player has to follow the leading cards suit if possible
                        if (card.getSuit() != leadingSuit) {
                            for (PlayingCard playingCard : playerCards) {
                                if (playingCard.getSuit() == leadingSuit) {
                                    throw new IllegalArgumentException("Player has to follow leading suit");
                                }
                            }
                            //if not possible they are obliged to play a trump card
                            if (card.isTrumpSuit()) {
                                trickWinnerId = playerId;
                            } else {
                                for(PlayingCard playingCard : playerCards) {
                                    if(playingCard.getSuit() == trumpSuit) {
                                        throw new IllegalArgumentException("Player has to play trump if they can not follow the suit");
                                    }
                                }
                                //if suit is not matching and trump cards are not available the trick goes to the leading player
                                trickWinnerId = 1 - playerId;
                            }
                        } else {
                            //When following leading suit the card must take the trick if possible
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
                        //If the talon is not closed there are no restrictions on which cards can be played
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

                    //Scoring logic adds current tricks points to the round score
                    // (if marriage score was not yet added it is added after the first trick taken by the player)
                    if (trickWinnerId == 0) {
                        player0Tricks.add(new PlayingCard[]{card,leadingCard});
                        player0Score += card.getCardValue() + leadingCard.getCardValue();
                        if (player0MarriageTempScore != 0) {
                            player0Score += player0MarriageTempScore;
                            player0MarriageTempScore = 0;
                        }
                        if(!talonClosed && !playingCardPile.isEmpty()) {
                            passCards(0, 1);
                        }
                    } else {
                        player1Tricks.add(new PlayingCard[]{card,leadingCard});
                        player1Score += card.getCardValue() + leadingCard.getCardValue();
                        if (player1MarriageTempScore != 0) {
                            player1Score += player1MarriageTempScore;
                            player1MarriageTempScore = 0;
                        }
                        if(!talonClosed &&  !playingCardPile.isEmpty()) {
                            passCards(1,1);
                        }
                    }

                    //the following players card is still in their hand, we remove it and set the leading card to null
                    playerCards.remove(card);
                    leadingCard = null;

                    //The winning player gets to be the leading player in the next trick
                    playerTurnId = trickWinnerId;

                    //the Board checks if round is over, if so it calculates who gets a Bummerl
                    if (isRoundOver()){
                        calculateBummerl();
                    }

                    //everything is set for the next trick, therefore return
                    return;
                }

                //the player turn shifts to player that has not yet played a card
                playerTurnId = 1 - playerTurnId;

            } else throw new IllegalArgumentException("Card not in players hand!");
       } else throw new IllegalStateException("It is not the players turn!");
    }

    /**
     * Leading players can trade the Jack of the trump suit with the trump card, if the talon is not closed
     *
     * @param playerId id of the player to make the exchange
     */
    public void exchangeTrumpCard(int playerId) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }

        if(!talonClosed) {
            //We check if the player is allowed to make a change (only when they are leading, so the leadingCard must be null)
            if (playerId == playerTurnId && leadingCard == null) {
                List<PlayingCard> playerCards;
                if (playerId == 0) {
                    playerCards = player0Cards;
                } else {
                    playerCards = player1Cards;
                }

                //This logic checks if the player has the correct Jack in their hand
                CardName jackName = switch (trumpSuit) {
                    case SPADES -> CardName.JackOfSpades;
                    case HEARTS -> CardName.JackOfHearts;
                    case DIAMONDS -> CardName.JackOfDiamonds;
                    case CLUBS -> CardName.JackOfClubs;
                };

                PlayingCard cardSwitch = null;
                for (PlayingCard playingCard : playerCards) {
                    if (playingCard.getCardName().equals(jackName)) {
                        cardSwitch = trumpCard;
                        trumpCard = playingCard;
                        break;
                    }
                }
                if (cardSwitch != null) {
                    oldTrumpCard = cardSwitch;
                    playerCards.remove(trumpCard);
                    playerCards.add(cardSwitch);
                    playingCardPile.remove(cardSwitch);
                    playingCardPile.addLast(trumpCard);
                } else {
                    throw new IllegalStateException("Player can only swap trump if they have the Jack in the trump suit!");
                }

            } else {
                throw new IllegalStateException("Player can only swap trump card if they are the leading player");
            }
        } else {
            throw new IllegalStateException("Player can only exchange the trump card if the talon is not closed");
        }
    }


    /**
     * Leading players are allowed to declare a marriage of the Queen and King of the same suit to earn points.
     * Trump marriages gain 40 points, the others gain 20.
     * <p>
     * A common rule is that one has to play one of the two declared marriage cards after the declaration, therefore we track this card
     * @param playerId      id of player to declare marriage
     * @param marriageCard1 first card of the marriage
     * @param marriageCard2 second card of the marriage
     */
    public void declareMarriage(int playerId, PlayingCard marriageCard1, PlayingCard marriageCard2) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
        if (marriageCard1 == null || marriageCard2 == null) {
            throw new IllegalArgumentException("marriageCard cannot be null");
        }
        if (playerTurnId == playerId && leadingCard == null) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }
            int tempScore;
            if (playerCards.contains(marriageCard1) && playerCards.contains(marriageCard2)) {
                if(marriageCard1.getPossibleMarriage() != null)
                {
                    if(marriageCard1.getPossibleMarriage().equals(marriageCard2))
                    {
                        if(marriageCard1.isTrumpSuit())
                        {
                            tempScore = 40;
                        }
                        else {
                            tempScore = 20;
                        }
                    } else {
                        throw new IllegalArgumentException("Cards can not form a correct marriage");
                    }
                } else {
                    throw new IllegalArgumentException("Cards are not able to be married");
                }

                if (tempScore > 0) {
                    //marriage declared storing marriage Card for action restriction
                    marriageCardDeclared = marriageCard1;

                    //After a marriage declaration it is possible that the round is instantly over
                    //if not we add the newly shown marriage as public information in the players marriage list
                    if (playerId == 0) {
                        if (player0Score != 0) {
                            player0Score += tempScore;
                            //Board checks if round is over
                            if (isRoundOver()){
                                calculateBummerl();
                            } else {
                                //storing marriage cards as public information
                                player0Marriages.add(marriageCard1);
                                player0Marriages.add(marriageCard2);
                            }
                        } else {
                            player0MarriageTempScore = tempScore;
                        }
                    } else {
                        if (player1Score != 0) {
                            player1Score += tempScore;
                            //Board checks if round is over
                            if (isRoundOver()){
                                calculateBummerl();
                            } else {
                                //storing marriage cards as public information
                                player1Marriages.add(marriageCard1);
                                player1Marriages.add(marriageCard2);
                            }
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
    public void closeTalon(int playerId) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
        //We got to keep track of the current score of the non-closing player for the end of round Bummerl calculation
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
     * The Bummerl scoring is done by checking the score points of the losing player when the round is over:
     * If the losing player has at least 33 score points in the round the winning player will receive 1 Bummerl point,
     * if they managed to get at least 1 trick the winning player will receive 2 Bummerl points,
     * if they did not get any tricks the winning player receives 3 Bummerl points.
     * <p>
     * If no player had 66 score points in total, the last player to win a trick wins 1 Bummerl point.
     * <p>
     * If the player that closed the talon wins, this scoring system is the same,
     * but the score the non closing player receives during the "closing" are not counted.
     * If they loosed after closing, all points they would receive when winning are given to the non-closing player
     * and the non-closing player will get at least 2 Bummerl points no matter the amount of tricks they had.
     * losing means that they did not get 66 score points even tough they closed the talon.
     * The last trick scoring rule is not active if the talon was closed
     * <p>
     * <p>
     * This method also adds the Bummerl to the losing player.
     * If the losing player had not won any rounds (7 on their score), they receive a "Schneider", which counts as 2 Bummerl.
     */
    private void calculateBummerl()
    {
        if(isRoundOver() && !isGameOver())
        {
            //If the talon was closed the scoring changes accordingly
            if(talonClosed)
            {
                if(talonClosingPlayerId == 0)
                {
                    if(player0Score >= 66)
                    {
                        if(talonClosedEnemyScore == 0)
                        {
                            player0Bummerl = player0Bummerl-3;
                        } else if(talonClosedEnemyScore < 33)
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
                        if(talonClosedEnemyScore == 0)
                        {
                            player1Bummerl = player1Bummerl-3;
                        } else if (talonClosedEnemyScore < 33)
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
                    else if(player1Score < 33) {
                        player0Bummerl = player0Bummerl-2;
                    } else {
                        player0Bummerl = player0Bummerl-1;
                    }
                } else {
                    //This is the special case that no one got to score 66 points
                    //Then it is checked who won the last trick (therefore whose turn would be next to start a new trick)
                    if(playerTurnId == 0)
                    {
                        player0Bummerl = player0Bummerl-1;
                    } else {
                        player1Bummerl = player1Bummerl-1;
                    }
                }

            }

            //check who lost and gets the Bummerl if it is over already
            //If the losing player did not manage to score any points in this Bummerl they get two amounts of Bummerl = "Schneider"
            if(isBummerlOver()) {
                if (player0Bummerl <= 0) {
                    if (player1Bummerl == 7) {
                        player1BummerlAmount += 2;
                    } else {
                        player1BummerlAmount++;
                    }
                } else {
                    if (player0Bummerl == 7) {
                        player0BummerlAmount += 2;
                    } else {
                        player0BummerlAmount++;
                    }
                }
                //We reset the game board to start a fresh Bummerl from scratch
                resetBummerl();
            }


            if(!isGameOver()) {
                //If game is not over yet, the starting player shifts and a new round begins
                startingPlayer = 1 - startingPlayer;
                resetRound();
            }
        }
    }

    /**
     * This method resets the Bummerl and the corresponding scores
     */
    private void resetBummerl() {
        player0Bummerl = 7;
        player1Bummerl = 7;
    }

    /**
     * Checks if one of the players has reached the Bummerl maximum, and therefore a game over state
     * @return boolean if the game is over
     */
    public boolean isGameOver() {
        return player0BummerlAmount >= bummerlMax || player1BummerlAmount >= bummerlMax;
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
     * This method resets the round as long as the game is not over
     */
    private void resetRound() {
        if(!isGameOver())
        {
            //resetting round scores
            player0Score = 0;
            player1Score = 0;

            //resetting hands
            player0Cards.clear();
            player1Cards.clear();

            //resetting tricks
            player0Tricks.clear();
            player1Tricks.clear();

            //resetting pile
            playingCardPile.clear();

            //resetting marriage scores and card
            player0MarriageTempScore = 0;
            player1MarriageTempScore = 0;
            marriageCardDeclared = null;

            //resetting tracked marriage cards
            player0Marriages.clear();
            player1Marriages.clear();

            //resetting talon logic
            talonClosed = false;
            talonClosingPlayerId = -1;
            talonClosedEnemyScore = 0;

            //resetting trumps
            trumpCard = null;
            trumpSuit = null;
            oldTrumpCard = null;

            //resetting leading card
            leadingCard = null;

            //setting playerTurnId to starting player
            playerTurnId = startingPlayer;

            //starting new round
            roundInitialisation();
        }
    }

    /**
     * If the overall score of one player is 0 or lower the current Bummerl is over (counted down from 7)
     * @return boolean that checks if the game is over
     */
    public boolean isBummerlOver() {
        return player0Bummerl <= 0 || player1Bummerl <= 0;
    }




    @Override
    public String toString() {
        String leadCard = "";
        if (leadingCard != null) {
            leadCard = "Leading Card: " + leadingCard + "\n" + "--------------------\n";
        } else {
            leadCard = "--------------------\n";
        }

        String talonCards = "";
        if(talonClosed) {
            talonCards = "Talon closed by Player " + (talonClosingPlayerId) + ", trump suit: " + switch (trumpSuit) {case SPADES -> "(S)pades"; case HEARTS ->  "(H)earts"; case DIAMONDS ->  "(D)iamonds"; case CLUBS -> "(C)lubs";} + "\n";
        } else if (this.playingCardPile.isEmpty()) {
            talonCards = "Playing pile empty, trump suit: " + switch (trumpSuit) {case SPADES -> "(S)pades"; case HEARTS ->  "(H)earts"; case DIAMONDS ->  "(D)iamonds"; case CLUBS -> "(C)lubs";} + "\n";
        } else {
            talonCards ="Remaining Cards: " + playingCardPile.size() + " Trump Card: " + trumpCard.toString() + "\n";
        }

        String bummerlPlayer0 = "°".repeat(Math.max(0, player0BummerlAmount));

        String bummerlPlayer1 = "°".repeat(Math.max(0, player1BummerlAmount));


        String player0CardsString = "";
        String player1CardsString = "";

        if(playerTurnId == 0) {
          Set<PlayingCard> player0Set = new TreeSet<>(player0Cards);
          player0CardsString = player0Set.toString();
          player1CardsString = player1Cards.toString();
        } else {
          Set<PlayingCard> player1Set = new TreeSet<>(player1Cards);
          player1CardsString = player1Set.toString();
          player0CardsString = player0Cards.toString();
        }

        String sb =
                "-------------------\n" +
                ".... SCHNAPSEN ´´´´\n" +
                "-------------------\n" +
                "Player 0's Hand: " + player0CardsString + " Player 0's Score: " + player0Score + " Player 0's Tricks: " + player0Tricks.stream()
                        .map(Arrays::toString) // Converts each PlayingCard[] to a readable String
                        .collect(Collectors.joining(", ", "[", "]")) + "\n" +
                "--------------------\n" +
                talonCards +
                "--------------------\n" +
                leadCard +
                //"Drawing Pile: " + playingCardPile + "\n" + //-> for testing
                //"--------------------\n" +
                "Player 1's Hand: " + player1CardsString + " Player 1's Score: " + player1Score + " Player 1's Tricks: " + player1Tricks.stream()
                .map(Arrays::toString) // Converts each PlayingCard[] to a readable String
                .collect(Collectors.joining(", ", "[", "]")) + "\n" +
                "--------------------\n" +
                "Current Bummerl Score: Player 0: " + player0Bummerl + ", Player 1: " + player1Bummerl + "\n" +
                "--------------------\n" +
                "Playing up to " + bummerlMax + " Bummerl: Player 0's Bummerl: (" + player0BummerlAmount + ") " + bummerlPlayer0 + ", Player 1's Bummerl: (" + player1BummerlAmount + ") " + bummerlPlayer1;
        return sb;
    }

    /**
     * This method returns the player id who has the current turn on this board
     * @return playerId who has the turn
     */
    public int getPlayerTurnId() {
        return playerTurnId;
    }

    /**
     * This method gives a current overview of the players overall score and how good they are faring in the game in total, for the current round
     * the overall Bummerl score, as well as the amounts of Bummerl the opposing player has. To give the scores an accurate weight they are
     * compared with the most extreme values:
     * <p>
     * The most extreme value for losing the game in terms of Bummerl is getting a "Schneider", therefore 2 Bummerl when having maxBummerl -1.
     * e.g. when playing to 5 Bummerl the maximum would be 6, when getting a Schneider at 4.
     * <p>
     * When having only 1 point left on the current Bummerl and winning a 3 Bummerl point round the
     * value would result in -2. To accustom this to our utility value we add 2 to the maximum possible current Bummerl score so we look at 9.
     * <p>
     * To give an insight on the current round played we also look at the most extreme value:
     * Having a score of 65 is not yet game ending. The highest increase in one action that can happen is a marriage of 40.
     * <p>
     *
     * These scores are than added to show the current standing of a players performance in this game.
     * To accurately give each category a weight the current standing in Bummerl will be multiplied by 10, as it has the highest priority.
     * The current Bummerl's Score will be 0-9
     * and the round score would be 0.0-0.99 (by dividing through 106 = maximum possible round score + 1)
     * <p>
     * A complete example with a game going to 7 Bummerl:
     * 34.5 -> 3x.x stands for 3 Bummerl on the opposing players side (The higher this number the better)
     * -> x4.x stands for 9 - the current Bummerls points of the player, so the player has 5 points left to win this Bummerl (The higher this number the better)
     * -> xx.5 stands for the current rounds score of the player, so the player has a score of 0.5*106= 53 in this round (The higher this number the better)
     *
     * @param playerId id of the player to check the score
     * @return a double value of the players overall utility score
     */
    public double getUtilityValue(int playerId) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
        if(playerId == 0)
        {
            double bummerlScore0 = player1BummerlAmount * 10;
            double currentBummerlScore0 = (9-player0Bummerl);
            double currentRoundScore0 = player0Score/106.0d;

            return bummerlScore0 + currentBummerlScore0 + currentRoundScore0;

        }
        else {
            double bummerlScore1 = player0BummerlAmount * 10;
            double currentBummerlScore1 = (9-player1Bummerl);
            double currentRoundScore1 = player1Score/106.0d;

            return bummerlScore1 + currentBummerlScore1 + currentRoundScore1;
        }
    }

    /**
     * This method strips all information of the game that is not tied to the player.
     * This includes all the cards in the drawing pile (except the trump card)
     * and the cards in the opposing players hand.
     * The removed cards are replaced by a placeholder card
     */
    public void hideInformation(int playerId) {
        if (playerId != 0 && playerId != 1) {
            throw new IllegalArgumentException("playerId must be 0 or 1");
        }
        if (playerId == 0) {
            int player1CardNum = player1Cards.size();
            player1Cards.clear();
            for (int i = 0; i < player1CardNum; i++) {
                player1Cards.add(new PlayingCard(CardSuit.SPADES, CardName.PlaceHolder, 0));
            }
        } else {
            int player0CardNum = player0Cards.size();
            player0Cards.clear();
            for (int i = 0; i < player0CardNum; i++) {
                player0Cards.add(new PlayingCard(CardSuit.SPADES, CardName.PlaceHolder, 0));
            }
        }

        int playingCardNum = playingCardPile.size();
        //only add hidden cards and a trumpCard at the bottom of the deck if it is not empty yet
        if(playingCardNum != 0) {
            playingCardPile.clear();
            for (int i = 0; i < playingCardNum - 1; i++) {
                playingCardPile.add(new PlayingCard(CardSuit.SPADES, CardName.PlaceHolder, 0));
            }
            playingCardPile.addLast(trumpCard);
        }
    }

    /**
     * Returns the cards of player 1 if it is their turn
     * @return List of PlayingCards, empty if not the turn of player 1
     */
    public List<PlayingCard> getPlayer1Cards() {
        if(this.getPlayerTurnId()== 1)
            return Collections.unmodifiableList(player1Cards);
        else
            return Collections.emptyList();
    }

    /**
     * Returns the cards of player 0 if it is their turn
     * @return List of PlayingCards, empty if not the turn of player 0
     */
    public List<PlayingCard> getPlayer0Cards() {
        if(this.getPlayerTurnId()== 0)
            return Collections.unmodifiableList(player0Cards);
        else
            return Collections.emptyList();
    }

    /**
     * Returns the leading card
     * @return PlayingCard of the current leading card, can be null if no card is lead
     */
    public PlayingCard getLeadingCard() {
        return leadingCard;
    }

    /**
     * Returns the current rounds trumpCard
     * @return PlayingCard the present trump card of the round
     */
    public PlayingCard getTrumpCard() {
        return trumpCard;
    }

    /**
     * Information if the talon is closed in this round, therefore no more cards can be drawn and the rules are changed to:
     * Must follow suit and must take trick rules
     * @return true if talon has been close this round
     */
    public boolean isTalonClosed() {
        return talonClosed;
    }

    /**
     * Information if the playing pile is empty, therefore no more cards can be drawn and the rules are changed to:
     * Must follow suit and must take trick rules
     * @return true if pile is empty
     */
    public boolean playingCardPileIsEmpty()
    {
        return playingCardPile.isEmpty();
    }

    /**
     * Information on how many cards are left in the drawing pile
     * @return a number of cards left in drawing pile
     */
    public int playingCardsLeftInPile() {
        return playingCardPile.size();
    }

    /**
     * Information if currently a marriage card has been declared
     * @return PlayingCard of the marriage card declared, returns null if no declaration was made
     */
    public PlayingCard getMarriageCardDeclared() {
        return marriageCardDeclared;
    }

    /**
     * This returns a list of the tricks the player 0 has made in pairs of two
     * @return PlayingCard list of tricks taken by player 0
     */
    public List<PlayingCard[]> getPlayer0Tricks() {
        return player0Tricks;
    }

    /**
     * This returns a list of the tricks the player 1 has made in pairs of two
     * @return PlayingCard list of tricks taken by player 1
     */
    public List<PlayingCard[]> getPlayer1Tricks() {
        return player1Tricks;
    }

    /**
     * This returns a list of marriages the player 1 has declared
     * @return The marriages player 1 has declared in form of a List of PlayingCards
     */
    public List<PlayingCard> getPlayer1Marriages() {
        return player1Marriages;
    }

    /**
     * This returns a list of marriages the player 0 has declared
     * @return The marriages player 0 has declared in form of a List of PlayingCards
     */
    public List<PlayingCard> getPlayer0Marriages() {
        return player0Marriages;
    }

    /**
     * This returns the old PlayingCard if there has been a trump exchange
     * @return the old trump card as a PlayingCard that has been exchanged for the new one
     */
    public PlayingCard getOldTrumpCard() {
        return oldTrumpCard;
    }

    /**
     * Returns the score of the current round for player 0
     * @return integer representing current rounds score
     */
    public int getPlayer0Score() {
        return player0Score;
    }

    /**
     * Returns the score of the current round for player 1
     * @return integer representing current rounds score
     */
    public int getPlayer1Score() {
        return player1Score;
    }

    /**
     * Returns the current Bummerl score of the current round for player 0
     * @return integer representing current Bummerl score
     */
    public int getPlayer0Bummerl() {
        return player0Bummerl;
    }

    /**
     * Returns the current Bummerl score of the current round for player 1
     * @return integer representing current Bummerl score
     */
    public int getPlayer1Bummerl() {
        return player1Bummerl;
    }

    /**
     * Returns the amount of Bummerl one player needs to lose the game
     * @return integer representing the Bummerl max of this game
     */
    public int getBummerlMax() {
        return bummerlMax;
    }

    /**
     * Returns the amount of Bummerl that player 0 has lost
     * @return integer representing the amount of lost Bummerl
     */
    public int getPlayer0BummerlAmount() {
        return player0BummerlAmount;
    }

    /**
     * Returns the amount of Bummerl that player 1 has lost
     * @return integer representing the amount of lost Bummerl
     */
    public int getPlayer1BummerlAmount() {
        return player1BummerlAmount;
    }
}
