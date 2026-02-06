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

    public enum cardSuits {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS
    }

    public enum cardNames {
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

    private Random random;
    private LinkedList<PlayingCard> playingCardPile = new LinkedList<>();
    private List<PlayingCard> player0Cards = new ArrayList<>();
    private List<PlayingCard> player1Cards = new ArrayList<>();

    private List<PlayingCard[]> player0Tricks = new ArrayList<>();
    private List<PlayingCard[]> player1Tricks = new ArrayList<>();

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
    private int player0MarriageTempScore;
    private int player1MarriageTempScore;
    private PlayingCard marriageCardDeclared;

    private boolean talonClosed = false;
    private int talonClosingPlayerId = -1;
    private int talonClosedEnemyScore;

    private PlayingCard trumpCard;
    private cardSuits trumpSuit;

    private PlayingCard leadingCard;

    private int startingPlayer;
    private int playerTurnId;

    /**
     * Creating the playing cards and adding them to the playing card pile
     */
    private void createCards()
    {
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, cardNames.JackOfSpades, 2));
        //Adding possible marriages to the spades cards
        PlayingCard queenSpades = new PlayingCard(cardSuits.SPADES, cardNames.QueenOfSpades, 3);
        PlayingCard kingSpades = new PlayingCard(cardSuits.SPADES, cardNames.KingOfSpades, 4);
        queenSpades.setPossibleMarriage(kingSpades);
        kingSpades.setPossibleMarriage(queenSpades);
        playingCardPile.add(queenSpades);
        playingCardPile.add(kingSpades);
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, cardNames.TenOfSpades, 10));
        playingCardPile.add(new PlayingCard(cardSuits.SPADES, cardNames.AceOfSpades, 11));

        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, cardNames.JackOfHearts, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenHearts = new PlayingCard(cardSuits.HEARTS, cardNames.QueenOfHearts, 3);
        PlayingCard kingHearts = new PlayingCard(cardSuits.HEARTS, cardNames.KingOfHearts, 4);
        queenHearts.setPossibleMarriage(kingHearts);
        kingHearts.setPossibleMarriage(queenHearts);
        playingCardPile.add(queenHearts);
        playingCardPile.add(kingHearts);
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, cardNames.TenOfHearts, 10));
        playingCardPile.add(new PlayingCard(cardSuits.HEARTS, cardNames.AceOfHearts, 11));

        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, cardNames.JackOfDiamonds, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenDiamonds = new PlayingCard(cardSuits.DIAMONDS, cardNames.QueenOfDiamonds, 3);
        PlayingCard kingDiamonds = new PlayingCard(cardSuits.DIAMONDS, cardNames.KingOfDiamonds, 4);
        queenDiamonds.setPossibleMarriage(kingDiamonds);
        kingDiamonds.setPossibleMarriage(queenDiamonds);
        playingCardPile.add(queenDiamonds);
        playingCardPile.add(kingDiamonds);
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, cardNames.TenOfDiamonds, 10));
        playingCardPile.add(new PlayingCard(cardSuits.DIAMONDS, cardNames.AceOfDiamonds, 11));

        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, cardNames.JackOfClubs, 2));
        //Adding possible marriages to the hearts cards
        PlayingCard queenClubs = new PlayingCard(cardSuits.CLUBS, cardNames.QueenOfClubs, 3);
        PlayingCard kingClubs = new PlayingCard(cardSuits.CLUBS, cardNames.KingOfClubs, 4);
        queenClubs.setPossibleMarriage(kingClubs);
        kingClubs.setPossibleMarriage(queenClubs);
        playingCardPile.add(queenClubs);
        playingCardPile.add(kingClubs);
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, cardNames.TenOfClubs, 10));
        playingCardPile.add(new PlayingCard(cardSuits.CLUBS, cardNames.AceOfClubs, 11));
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
        //TODO: Remove test seed
        this(new Random(0));
    }

    /**
     * Initialize the Schnapsen board with a random object for seed manipulation
     *
     * @param random object that manipulates the shuffling of the deck
     */
    public SchnapsenBoard(Random random) {
        startingPlayer = 0;
        playerTurnId = startingPlayer;
        this.random = random;
        roundInitialisation();

    }

    /**
     * Create a board based on an amount of bummerl and a random object
     * @param random object that manipulates shuffling of the deck
     * @param bummerlMax states how many bummers the game will last
     */
    public SchnapsenBoard(Random random, int bummerlMax) {
        this.startingPlayer = 0;
        this.playerTurnId = startingPlayer;
        this.random = random;
        this.bummerlMax = bummerlMax;
        roundInitialisation();
    }

    public SchnapsenBoard(SchnapsenBoard schnapsenBoard) {
        this(schnapsenBoard.player0Cards, schnapsenBoard.player1Cards, schnapsenBoard.playingCardPile, schnapsenBoard.player0Tricks,
                schnapsenBoard.player1Tricks, schnapsenBoard.startingPlayer, schnapsenBoard.playerTurnId, schnapsenBoard.random, schnapsenBoard.player0MarriageTempScore,
                schnapsenBoard.player1MarriageTempScore, schnapsenBoard.player0Bummerl, schnapsenBoard.player1Bummerl, schnapsenBoard.player0Score, schnapsenBoard.player1Score,
                schnapsenBoard.talonClosingPlayerId, schnapsenBoard.talonClosedEnemyScore, schnapsenBoard.talonClosed, schnapsenBoard.leadingCard,
                schnapsenBoard.trumpCard, schnapsenBoard.trumpSuit,schnapsenBoard.marriageCardDeclared, schnapsenBoard.bummerlMax, schnapsenBoard.player0BummerlAmount, schnapsenBoard.player1BummerlAmount);
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
                          cardSuits trumpSuit,
                          PlayingCard marriageCardDeclared,
                          int bummerlMax,
                          int player0BummerlAmount,
                          int player1BummerlAmount

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

        this.startingPlayer = startingPlayer;
        this.playerTurnId = playerTurnId;
        this.random = new Random(random.nextLong());
        //TODO adaptable cheating version for testing
        //this.random = deepCopyRandom(random);
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

    private PlayingCard findCardInPile(List<PlayingCard> pile, PlayingCard targetCard){
        if(targetCard.getCardName() == cardNames.PlaceHolder) {
            return new PlayingCard(cardSuits.SPADES,cardNames.PlaceHolder,0);
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
     * <p>
     * If the playing card pile is empty or the talon was closed,
     * the following card has to have the same color of the leading card and one is obliged to take the trick if possible.
     * <p>
     * Trick is scored and added to the tricks taken pile of the player in the end.
     *
     * @param playerId id of player playing the card
     * @param card     the card the player wants to play
     */
    public void playCard(int playerId, PlayingCard card) {
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
                        leadingCard = card;
                        playerCards.remove(leadingCard);
                    }
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
    public void exchangeTrumpCard(int playerId) {

        if (playerId == playerTurnId && leadingCard == null) {
            List<PlayingCard> playerCards;
            if (playerId == 0) {
                playerCards = player0Cards;
            } else {
                playerCards = player1Cards;
            }

            cardNames jackName = switch (trumpSuit) {
                case SPADES -> cardNames.JackOfSpades;
                case HEARTS -> cardNames.JackOfHearts;
                case DIAMONDS -> cardNames.JackOfDiamonds;
                case CLUBS -> cardNames.JackOfClubs;
            };

            PlayingCard cardSwitch = null;
            for (PlayingCard playingCard : playerCards) {
                if (playingCard.getCardName().equals(jackName)) {
                    cardSwitch = trumpCard;
                    trumpCard = playingCard;
                    break;
                }
            }
            if(cardSwitch != null) {
                playerCards.remove(trumpCard);
                playerCards.add(cardSwitch);
                playingCardPile.remove(cardSwitch);
                playingCardPile.addLast(trumpCard);
            }
            
        } else {
            throw new IllegalStateException("Player can only swap Trump Card if they are the leading player");
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

                    if (playerId == 0) {
                        if (player0Score != 0) {
                            player0Score += tempScore;
                            //Board checks if round is over
                            if (isRoundOver()){
                                calculateBummerl();
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
     * <p>
     * If no player had 66 score points in total, the last player to win a trick wins 1 Bummerl point.
     * <p>
     * If the player that closed the talon wins, this scoring system is the same,
     * but the score the non closing player receives during the "closing" are not counted.
     * If they loosed after closing, all points they would receive when winning are given to the non-closing player
     * and the non-closing player will get at least 2 Bummerl points no matter the amount of tricks they had.
     * Loosing means that they did not get 66 score points even tough they closed the talon.
     * The last trick scoring rule is not active if the talon was closed
     * <p>
     * <p>
     * This method also adds the Bummerl to the loosing player.
     * If the loosing player had not won any rounds (7 on their score), they receive a "Schneider", which counts as 2 Bummerl.
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
                    if(playerTurnId == 0)
                    {
                        player0Bummerl = player0Bummerl-1;
                    } else {
                        player1Bummerl = player1Bummerl-1;
                    }
                }

            }

            //check who lost and gets the Bummerl
            if(isBummerlOver()) {
                if (player0Bummerl <= 0) {
                    // System.out.println("Player 1 won the Bummerl (game)!");
                    if (player1Bummerl == 7) {
                        player1BummerlAmount += 2;
                    } else {
                        player1BummerlAmount++;
                    }
                } else {
                    //   System.out.println("Player 2 won the Bummerl (game)!");
                    if (player0Bummerl == 7) {
                        player0BummerlAmount += 2;
                    } else {
                        player0BummerlAmount++;
                    }
                }
                resetBummerl();
            }


            if(!isGameOver()) {
               // System.out.println("Round over! Player 1 has " + player0Bummerl + " points left on the Bummerl!");
               // System.out.println("Round over! Player 2 has " + player1Bummerl + " points left on the Bummerl!");

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

            //resetting talon logic
            talonClosed = false;
            talonClosingPlayerId = -1;
            talonClosedEnemyScore = 0;

            //resetting trumps
            trumpCard = null;
            trumpSuit = null;

            //resetting leading card
            leadingCard = null;

            //setting playerTurnId to starting player
            playerTurnId = startingPlayer;

            //starting new round
            roundInitialisation();
        }
    }

    /**
     * If the overall score of one player is 0 or lower the game is over (counted down from 7)
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
            talonCards = "Talon closed by Player " + (talonClosingPlayerId+1) + ", trump suit: " + switch (trumpSuit) {case SPADES -> "(S)pades"; case HEARTS ->  "(H)earts"; case DIAMONDS ->  "(D)iamonds"; case CLUBS -> "(C)lubs";} + "\n";
        } else if (this.playingCardPile.isEmpty()) {
            talonCards = "Playing pile empty, trump suit: " + switch (trumpSuit) {case SPADES -> "(S)pades"; case HEARTS ->  "(H)earts"; case DIAMONDS ->  "(D)iamonds"; case CLUBS -> "(C)lubs";} + "\n";
        } else {
            talonCards ="Remaining Cards: " + playingCardPile.size() + " Trump Card: " + trumpCard.toString() + "\n";
        }

        String bummerlPlayer0 = "°".repeat(Math.max(0, player0BummerlAmount));

        String bummerlPlayer1 = "°".repeat(Math.max(0, player1BummerlAmount));

        String sb =
                "-------------------\n" +
                ".... SCHNAPSEN ´´´´\n" +
                "-------------------\n" +
                "Player 1's Hand: " + player0Cards + " Player 1's Score: " + player0Score + " Player 1's Tricks: " + player0Tricks.stream()
                        .map(Arrays::toString) // Converts each PlayingCard[] to a readable String
                        .collect(Collectors.joining(", ", "[", "]")) + "\n" +
                "--------------------\n" +
                talonCards +
                "--------------------\n" +
                leadCard +
                //"Drawing Pile: " + playingCardPile + "\n" + //-> for testing
                //"--------------------\n" +
                "Player 2's Hand: " + player1Cards + " Player 2's Score: " + player1Score + " Player 2's Tricks: " + player1Tricks.stream()
                .map(Arrays::toString) // Converts each PlayingCard[] to a readable String
                .collect(Collectors.joining(", ", "[", "]")) + "\n" +
                "--------------------\n" +
                "Current Bummerl Score: Player 1: " + player0Bummerl + ", Player 2: " + player1Bummerl + "\n" +
                "--------------------\n" +
                "Playing up to " + bummerlMax + " Bummerl: Player 1's Bummerl: (" + player0BummerlAmount + ") " + bummerlPlayer0 + ", Player 2's Bummerl: (" + player1BummerlAmount + ") " + bummerlPlayer1;
        return sb;
    }

    public int getPlayerTurnId() {
        return playerTurnId;
    }

    /**
     * This method gives a current overview of the players score, for the current round
     * and the overall Bummerl score, as well as the amounts of Bummerl the opposing player has. To give the scores an accurate weight they are
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
     * and the cards in the opposing players hand
     */
    public void hideInformation(int playerId) {
        if (playerId == 0) {
            int player1CardNum = player1Cards.size();
            player1Cards.clear();
            for (int i = 0; i < player1CardNum; i++) {
                player1Cards.add(new PlayingCard(cardSuits.SPADES, cardNames.PlaceHolder, 0));
            }
        } else {
            int player0CardNum = player0Cards.size();
            player0Cards.clear();
            for (int i = 0; i < player0CardNum; i++) {
                player0Cards.add(new PlayingCard(cardSuits.SPADES, cardNames.PlaceHolder, 0));
            }
        }

        int playingCardNum = playingCardPile.size();
        //only add hidden cards and a trumpCard at the bottom of the deck if it is not empty yet
        if(playingCardNum != 0) {
            playingCardPile.clear();
            for (int i = 0; i < playingCardNum - 1; i++) {
                playingCardPile.add(new PlayingCard(cardSuits.SPADES, cardNames.PlaceHolder, 0));
            }
            playingCardPile.addLast(trumpCard);
        }
    }

    /**
     * Used to create a deep copy of a random object using Serializable
     * @param original random object to be copied
     * @return deep copy of random object
     */
    private static Random deepCopyRandom(Random original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(original);
            oos.flush();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return (Random) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to clone Random Object", e);
        }
    }

    public List<PlayingCard> getPlayer1Cards() {
        if(this.getPlayerTurnId()== 1)
            return player1Cards;
        else
            return new ArrayList<>();
    }

    public List<PlayingCard> getPlayer0Cards() {
        if(this.getPlayerTurnId()== 0)
            return player0Cards;
        else
            return new ArrayList<>();
    }

    public PlayingCard getLeadingCard() {
        return leadingCard;
    }

    public PlayingCard getTrumpCard() {
        return trumpCard;
    }

    public boolean isTalonClosed() {
        return talonClosed;
    }

    public boolean playingCardPileIsEmpty()
    {
        return playingCardPile.isEmpty();
    }

    public PlayingCard getMarriageCardDeclared() {
        return marriageCardDeclared;
    }

    public List<PlayingCard[]> getPlayer0Tricks() {
        return player0Tricks;
    }

    public List<PlayingCard[]> getPlayer1Tricks() {
        return player1Tricks;
    }
}
