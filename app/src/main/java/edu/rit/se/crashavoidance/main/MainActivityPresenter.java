package edu.rit.se.crashavoidance.main;

import javax.inject.Inject;

import edu.rit.se.crashavoidance.infrastructure.di.annotation.PerActivity;

/**
 * Created by letroll on 04/09/16.
 */

@PerActivity
public class MainActivityPresenter {

    private CommunicationView communicationView;

    @Inject
    public MainActivityPresenter() {
    }

    public void setView(final CommunicationView communicationView) {
        this.communicationView = communicationView;
    }
}
