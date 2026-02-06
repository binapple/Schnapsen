/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;
import game.action.SchnapsenAction;
import game.board.PlayingCard;
import game.board.SchnapsenBoard;

import java.util.*;

public class Schnapsen implements Game<SchnapsenAction, SchnapsenBoard> {

    SchnapsenBoard schnapsenBoard;
    List<ActionRecord<SchnapsenAction>> actionRecords;

    public Schnapsen() {
        //TODO: Remove seed used for testing
        this (new SchnapsenBoard(new Random(0)));
    }

    /**
     * Makes a new Schnapsen instance from the passed board (not a deep copy)
     * @param schnapsenBoard board to be used as a reference
     */
    public Schnapsen(SchnapsenBoard schnapsenBoard) {
        this.schnapsenBoard = schnapsenBoard;
        this.actionRecords = new ArrayList<>();
    }

    /**
     * Constructor called by Game Engine
     * In Schnapsen we only have 2 players, that is why we call our default constructor
     *
     * @param stringBoard string of board
     * @param numberOfPlayers number of players
     */
    public Schnapsen(String stringBoard, int numberOfPlayers)
    {
        if(stringBoard != null) {
            if(!stringBoard.isEmpty()) {
                String[] params = stringBoard.split(";");

                int bummerlCount = Integer.parseInt(params[0]);
                if (params.length > 1) {
                    int seed = Integer.parseInt(params[1]);
                    this.schnapsenBoard = new SchnapsenBoard(new Random(seed), bummerlCount);
                } else {
                    //TODO remove Test seed
                    this.schnapsenBoard = new SchnapsenBoard(new Random(0), bummerlCount);
                }
                this.actionRecords = new ArrayList<>();
            } else {
                this.schnapsenBoard = new SchnapsenBoard();
                this.actionRecords = new ArrayList<>();
            }
        }
        else {
            this.schnapsenBoard = new SchnapsenBoard();
            this.actionRecords = new ArrayList<>();
        }
    }

    /**
     * This constructor is used to create a board with hidden information (information for the player only)
     * to achieve this the board is deep copied and then the information is hidden that would not be accessible for the current player
     * @param game the game with full information
     */
    public Schnapsen(Schnapsen game)
    {
        //TODO Implement an adaptable cheating version for testing
        SchnapsenBoard newBoard = new SchnapsenBoard(game.schnapsenBoard);
        newBoard.hideInformation(newBoard.getPlayerTurnId());
        this.schnapsenBoard = newBoard;
        this.actionRecords = new ArrayList<>(game.actionRecords);
    }

    @Override
    public boolean isGameOver() {
        return schnapsenBoard.isGameOver();
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getCurrentPlayer() {
        return schnapsenBoard.getPlayerTurnId();
    }

    @Override
    public double getUtilityValue(int i) {
        return schnapsenBoard.getUtilityValue(i);
    }

    @Override
    public Set<SchnapsenAction> getPossibleActions() {
        return SchnapsenAction.getPossibleActions(schnapsenBoard);
    }

    @Override
    public SchnapsenBoard getBoard() {
        return new SchnapsenBoard(this.schnapsenBoard);
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> doAction(SchnapsenAction schnapsenAction) {
        Schnapsen newBoard = new Schnapsen(this);
        schnapsenAction.doAction(newBoard.schnapsenBoard);
        newBoard.actionRecords.add(new ActionRecord<SchnapsenAction>(newBoard.getCurrentPlayer(),schnapsenAction));
        return newBoard;
    }

    @Override
    public SchnapsenAction determineNextAction() {
        return getPossibleActions().iterator().next();
    }

    @Override
    public List<ActionRecord<SchnapsenAction>> getActionRecords() {
        return actionRecords;
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> getGame(int i) {
        return new Schnapsen(this);
        //return this;
    }

    @Override
    public String toString() {
        return schnapsenBoard.toString();
    }
}
