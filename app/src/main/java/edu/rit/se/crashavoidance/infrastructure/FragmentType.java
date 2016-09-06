package edu.rit.se.crashavoidance.infrastructure;

import edu.rit.se.crashavoidance.availableService.AvailableServicesFragment;
import edu.rit.se.crashavoidance.chat.ChatFragment;
import edu.rit.se.crashavoidance.game.tictactoe.TicTacToeFragment;
import edu.rit.se.crashavoidance.main.MainFragment;

/**
 * wifi-direct-tester
 * edu.rit.se.crashavoidance.infrastructure
 * Created by jquievreux on 06/09/2016.
 */
public enum FragmentType {
    MAIN(MainFragment.TAG),
    AVAILABLE_SERVICES(AvailableServicesFragment.TAG),
    CHAT(ChatFragment.TAG),
    TICTACTOE(TicTacToeFragment.TAG),;

    private final String tag;

    FragmentType(final String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
