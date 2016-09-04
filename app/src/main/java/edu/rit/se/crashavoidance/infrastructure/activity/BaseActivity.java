package edu.rit.se.crashavoidance.infrastructure.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import edu.rit.se.crashavoidance.AndroidApplication;
import edu.rit.se.crashavoidance.R;
import edu.rit.se.crashavoidance.infrastructure.di.component.ActivityComponent;
import edu.rit.se.crashavoidance.infrastructure.di.component.ApplicationComponent;
import edu.rit.se.crashavoidance.infrastructure.di.component.DaggerActivityComponent;
import edu.rit.se.crashavoidance.infrastructure.di.module.ActivityModule;

/**
 * Created by letroll on 04/09/16.
 */

public class BaseActivity extends AppCompatActivity {

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
        return ((AndroidApplication)getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    /**
     * Replaces a Fragment in the 'fragment_container'
     * @param fragment Fragment to add
     */
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
}
