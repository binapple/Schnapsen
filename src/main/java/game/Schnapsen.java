/*
 * Copyright (c) 2025 Bina Philipp C.
 * Licensed under the GNU GPL v3.0.
 * Part of the project: Schnapsen
 */

package game;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;
import game.action.SchnapsenAction;
import game.board.SchnapsenBoard;

import java.util.*;

public class Schnapsen implements Game<SchnapsenAction, SchnapsenBoard> {

    SchnapsenBoard schnapsenBoard;
    List<ActionRecord<SchnapsenAction>> actionRecords;

    public Schnapsen() {
        this (new SchnapsenBoard(new Random()));
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
     * This constructor checks if the -b option of the Strategy Game Engine's command was used
     * If so we use the first number to set to how many Bummerl the game will be played
     * The second number split by ; will be used to seed the random object
     * <p>
     * It is possible to only pass one number which states the amount of Bummerl, the seed will then be randomized
     *
     * @param stringBoard string passed by the engine when using -b option
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
                    this.schnapsenBoard = new SchnapsenBoard(new Random(), bummerlCount);
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
     * This constructor is used to create a new game based on a given game.
     * It can be done either with or without hidden information (information for the player only)
     * to achieve this the board is deep copied and then the information that would not be accessible for the current player can be hidden
     * @param game the game with full information
     * @param hideInformation a boolean which decides if the information should be hidden
     */
    public Schnapsen(Schnapsen game, boolean hideInformation)
    {
        SchnapsenBoard newBoard = new SchnapsenBoard(game.schnapsenBoard);
        if(hideInformation) {
            newBoard.hideInformation(newBoard.getPlayerTurnId());
        }
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
        return schnapsenBoard;
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> doAction(SchnapsenAction schnapsenAction) {
        Schnapsen newBoard = new Schnapsen(this, false);
        int doingPlayer = newBoard.getCurrentPlayer();
        schnapsenAction.doAction(newBoard.schnapsenBoard);
        newBoard.actionRecords.add(new ActionRecord<SchnapsenAction>(doingPlayer,schnapsenAction));
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
        return new Schnapsen(this, true);
    }

    @Override
    public String toString() {
        return schnapsenBoard.toString();
    }
}
