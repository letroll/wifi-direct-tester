package edu.rit.se.crashavoidance.game.tictactoe;

/**
 * Created by letroll on 04/09/16.
 */

public enum TicTacToeChoise {
    PIERRE,
    FEUILLE,
    CISEAU;

    public static TicTacToeChoise fromString(final String text){
        if(text!=null) {
            for (final TicTacToeChoise val : values()) {
                if (val.name().toLowerCase().equals(text.toLowerCase())) {
                    return val;
                }
            }
        }
        return PIERRE;
    }
}
