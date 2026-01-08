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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Schnapsen implements Game<SchnapsenAction, SchnapsenBoard> {

    SchnapsenBoard schnapsenBoard;
    List<ActionRecord<SchnapsenAction>> actionRecords;

    public Schnapsen() {
        //TODO: Remove seed used for testing
        this (new SchnapsenBoard(new Random(0)));
    }

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
        this();
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
        return SchnapsenAction.getPossibleActions(schnapsenBoard,getCurrentPlayer());
    }

    @Override
    public SchnapsenBoard getBoard() {
        return schnapsenBoard;
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> doAction(SchnapsenAction schnapsenAction) {
        schnapsenAction.doAction();
        actionRecords.add(new ActionRecord<SchnapsenAction>(getCurrentPlayer(),schnapsenAction));
        return this;
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
        return this;
    }

    @Override
    public String toString() {
        return schnapsenBoard.toString();
    }
}
