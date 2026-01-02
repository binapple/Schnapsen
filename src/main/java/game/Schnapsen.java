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

import java.util.List;
import java.util.Set;

public class Schnapsen implements Game<SchnapsenAction, SchnapsenBoard> {


    @Override
    public boolean isGameOver() {
        return false;
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
        return 0;
    }

    @Override
    public int getCurrentPlayer() {
        return 0;
    }

    @Override
    public double getUtilityValue(int i) {
        return 0;
    }

    @Override
    public Set<SchnapsenAction> getPossibleActions() {
        return Set.of();
    }

    @Override
    public SchnapsenBoard getBoard() {
        return null;
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> doAction(SchnapsenAction schnapsenAction) {
        return null;
    }

    @Override
    public SchnapsenAction determineNextAction() {
        return null;
    }

    @Override
    public List<ActionRecord<SchnapsenAction>> getActionRecords() {
        return List.of();
    }

    @Override
    public boolean isCanonical() {
        return false;
    }

    @Override
    public Game<SchnapsenAction, SchnapsenBoard> getGame(int i) {
        return null;
    }
}
