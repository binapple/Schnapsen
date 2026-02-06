/*
 * Copyright (c) 2026 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game.board;

import game.action.SchnapsenAction;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SchnapsenBoardTest {


    @Test
    void givenNothing_creatingNewBoard_InitializesGame()
    {
        SchnapsenBoard board = new SchnapsenBoard();

        assertEquals(5, board.getPlayer0Cards().size());
        assertEquals(0, board.getPlayer1Cards().size()); //we are not allowed to look into the cards of the other player

        assertNotNull(board.getTrumpCard());
        assertFalse(board.isTalonClosed());
        assertFalse(board.isGameOver());
        assertNull(board.getLeadingCard());
        assertNull(board.getMarriageCardDeclared());
        assertFalse(board.playingCardPileIsEmpty());

        assertEquals(0, board.getPlayerTurnId());
    }

    @Test
    void givenBoardsWithSeeds_testingToString() {
        SchnapsenBoard board = new SchnapsenBoard(new Random(0));
        System.out.println(board.toString());
        SchnapsenBoard board1 = new SchnapsenBoard(new Random(1));
        System.out.println(board1.toString());
        SchnapsenBoard board2 = new SchnapsenBoard(new Random(2));
        System.out.println(board2.toString());
        SchnapsenBoard board3 = new SchnapsenBoard(new Random(3));
        System.out.println(board3.toString());
        SchnapsenBoard board4 = new SchnapsenBoard(new Random(4));
        System.out.println(board4.toString());
        SchnapsenBoard board5 = new SchnapsenBoard(new Random(5));
        System.out.println(board5.toString());
        SchnapsenBoard board6 = new SchnapsenBoard(new Random(6));
        System.out.println(board6.toString());
        SchnapsenBoard board7 = new SchnapsenBoard(new Random(7));
        System.out.println(board7.toString());
        SchnapsenBoard board8 = new SchnapsenBoard(new Random(8));
        System.out.println(board8.toString());
        SchnapsenBoard board9 = new SchnapsenBoard(new Random(9));
        System.out.println(board9.toString());
        SchnapsenBoard board10 = new SchnapsenBoard(new Random(10));
        System.out.println(board10.toString());

    }

    @Test
    void givenIntitializedBoard_wrongPlayerPlaysCard_throwsIllegalStateException() {
        SchnapsenBoard board = new SchnapsenBoard();

        PlayingCard card = board.getPlayer0Cards().get(0);

        assertThrows(IllegalStateException.class, () -> {
            board.playCard(1, card);
        });
    }

    @Test
    void givenInitializedBoard_legalPlayAdvancesTurnOrder() {
        SchnapsenBoard board = new SchnapsenBoard();

        int startingPlayer = board.getPlayerTurnId();
        PlayingCard card = board.getPlayer0Cards().get(0);

        board.playCard(startingPlayer, card);

        assertEquals(card, board.getLeadingCard());
        assertEquals(1, board.getPlayerTurnId());
    }

    @Test
    void givenInitializedBoard_leadingAndFollowingResultsInFinishedTrick() {
        SchnapsenBoard board = new SchnapsenBoard();

        PlayingCard lead = board.getPlayer0Cards().get(0);
        board.playCard(0, lead);

        PlayingCard follow = board.getPlayer1Cards().get(0);
        board.playCard(1, follow);

        assertNull(board.getLeadingCard());
    }

    @Test
    void givenInitilizedBoard_afterPlayingTrick_cardsArePassedCorrectly() {
        SchnapsenBoard board = new SchnapsenBoard();

        PlayingCard lead = board.getPlayer0Cards().get(0);
        board.playCard(0, lead);

        PlayingCard follow = board.getPlayer1Cards().get(0);
        board.playCard(1, follow);

        //check who won
        if(board.getPlayerTurnId() == 0)
        {
            assertEquals(5,  board.getPlayer0Cards().size());
        }
        else
        {
            assertEquals(5,  board.getPlayer1Cards().size());
        }
    }

    @Test
    void givenInitilizedBoardWithSeed_afterClosingTalonAndPlayingLegalTrick_cardsArePassedCorrectly() {
        SchnapsenBoard board = new SchnapsenBoard(new Random(0));

        PlayingCard lead = board.getPlayer0Cards().get(0);
        board.closeTalon(0);
        board.playCard(0, lead);

        PlayingCard follow = board.getPlayer1Cards().get(4); //follow with legal Card
        board.playCard(1, follow);

        //check who won
        if(board.getPlayerTurnId() == 0)
        {
            assertEquals(4,  board.getPlayer0Cards().size());
        }
        else
        {
            assertEquals(4,  board.getPlayer1Cards().size());
        }
    }

    @Test
    void givenInitializedBoardWithSeed_declaringMarriage_resultsInMarriageCardBeingDeclared()
    {
        SchnapsenBoard board = new SchnapsenBoard(new Random(1));

        PlayingCard marriageCard1 = board.getPlayer0Cards().get(1);
        PlayingCard marriageCard2 = board.getPlayer0Cards().get(4);

        board.declareMarriage(board.getPlayerTurnId(), marriageCard1, marriageCard2);
        assertEquals(board.getMarriageCardDeclared(),marriageCard1);
    }

    @Test
    void givenInitializedBoardWithSeed_exchangeingTrumpCard_resultsInTrumpCardBeingExchanged()
    {
        SchnapsenBoard board = new SchnapsenBoard(new Random(0));

        PlayingCard trumpCard = board.getTrumpCard();
        PlayingCard playerCard = board.getPlayer0Cards().get(1);
        board.exchangeTrumpCard(0);
        assertEquals(board.getTrumpCard(),playerCard);
        assertEquals(board.getPlayer0Cards().get(4),trumpCard);
    }

    @Test
    void givenBoardWithHiddenInformation_showsHiddenCardsInToString() {
        SchnapsenBoard board = new SchnapsenBoard(new Random(0));
        board.hideInformation(board.getPlayerTurnId());
        System.out.println(board.toString());
    }

}