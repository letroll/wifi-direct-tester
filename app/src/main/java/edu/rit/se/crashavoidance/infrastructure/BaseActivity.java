package edu.rit.se.crashavoidance.infrastructure;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.rit.se.crashavoidance.AndroidApplication;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.availableService.AvailableServicesFragment;
import edu.rit.se.crashavoidance.chat.ChatFragment;
import edu.rit.se.crashavoidance.game.tictactoe.TicTacToeFragment;
import edu.rit.se.crashavoidance.infrastructure.di.component.ActivityComponent;
import edu.rit.se.crashavoidance.infrastructure.di.component.ApplicationComponent;
import edu.rit.se.crashavoidance.infrastructure.di.component.DaggerActivityComponent;
import edu.rit.se.crashavoidance.infrastructure.di.module.ActivityModule;
import edu.rit.se.crashavoidance.main.MainFragment;

/**
 * Created by letroll on 04/09/16.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();

    protected ActivityComponent activityComponent;

    @Override
    public void onCreate(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initializeInjector();
    }

    protected void initializeInjector() {
        this.activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public void replaceMainContainerWithFragment(final Fragment fragment, final String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    protected void showFragmentInMainContainer(final FragmentType fragmentType) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentType.getTag());
        if (fragment == null) {
            fragment = getFragment(fragmentType);
        }
        replaceMainContainerWithFragment(fragment, fragmentType.getTag());
        Log.i(TAG, "Switching to " + fragmentType.getTag());
    }

    private Fragment getFragment(final FragmentType fragmentType){
        switch (fragmentType) {
            case MAIN:
                return MainFragment.newInstance();
            case AVAILABLE_SERVICES:
                return AvailableServicesFragment.newInstance();
            case TICTACTOE:
                return TicTacToeFragment.newInstance();
            case CHAT:
            default:
                return ChatFragment.newInstance();
        }
    }
}
