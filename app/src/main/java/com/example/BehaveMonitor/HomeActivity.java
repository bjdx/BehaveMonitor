//------------------------------------------------------------------------------
// Copyright (c) 2015 Barney Dennis & Gareth Lewis.
//------------------------------------------------------------------------------

package com.example.BehaveMonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.example.BehaveMonitor.fragments.FolderFragment;
import com.example.BehaveMonitor.fragments.NavigationDrawerFragment;
import com.example.BehaveMonitor.fragments.ObservationFragment;
import com.example.BehaveMonitor.fragments.SessionFragment;
import com.example.BehaveMonitor.fragments.SessionHistoryFragment;

import java.io.File;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to store the fragment currently being displayed. Defaults to -1 to show the initial fragment.
     */
    private int fragmentDisplayed = -1;

    private File activeFolder = null;
    private Observation activeObservation = null;
    private Session activeSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FileHandler.setRootDirectory(this);
        DBHelper db = DBHelper.getInstance(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        FileHandler.checkFoldersExist(this);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("activeFolderString");
            String tString = savedInstanceState.getString("activeObservationString");
            if (path != null) activeFolder = new File(path);
            if (tString != null) activeObservation = new Observation(tString);
        }

        if (activeFolder == null) {
            activeFolder = new File(FileHandler.getSessionsDirectory(), db.getFolder());
        }

        if (activeObservation == null) {
            String observationString = db.getObservation();
            if (observationString != null) {
                activeObservation = new Observation(observationString);
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tString = bundle.getString("activeObservationString");
            if (tString != null) activeObservation = new Observation(tString);
        }

//        Drawable drawable = getResources().getDrawable(R.drawable.ic_action_discard_black);
//        drawable.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);
//
//        drawable = getResources().getDrawable(R.drawable.ic_action_new_black);
//        drawable.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);
//
//        drawable = getResources().getDrawable(R.drawable.ic_action_edit);
//        drawable.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);
//
//        drawable = getResources().getDrawable(R.drawable.ic_action_save);
//        drawable.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);

        int redirect = getIntent().getIntExtra("redirect", 0);
        if (redirect != 0) {
            displayFragment(redirect);
        }
    }

    /**
     * @return The name of the active folder, or "" if no folder has been selected.
     */
    public String getFolderName() {
        DBHelper db = DBHelper.getInstance(this);
        String folder = db.getFolder();
        return folder == null ? "" : folder;
    }

    /**
     * @return The name of the active observation, or "" if no observation has been selected.
     */
    public String getObservationName() {
        DBHelper db = DBHelper.getInstance(this);
        Observation observation = new Observation(db.getObservation());

        return "null;".equals(observation.toString()) ? "" : observation.name;
    }

    public void setActiveFolder(File file) {
        activeFolder = file;
    }

    public void setActiveTmp(Observation tmp) {
        activeObservation = tmp;
    }

    public void setActiveSession(Session session) {
        activeSession = session;
    }

    public Session getActiveSession(){
        return activeSession;
    }

    public Session makeSession(String name, String loc) {
        activeSession = new Session(name, loc, activeFolder.getAbsolutePath());
        activeSession.setObservation(activeObservation);
        return activeSession;
    }

    public String getActivePath() {
        return activeFolder.getAbsolutePath();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (fragmentDisplayed != position) {
            displayFragment(position);
        }
    }

    public void displayFragment(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = new SessionFragment();
                break;
            case 1:
                fragment = new FolderFragment();
                break;
            case 2:
                fragment = new ObservationFragment();
                break;
            case 3:
                fragment = new SessionHistoryFragment();
            default:
                break;
        }

        if (fragment == null) {
            Log.e("Behave", "Error displaying fragment");
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        fragmentDisplayed = position;
        if  (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setItemChecked(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentDisplayed != 0) {
            displayFragment(0);
        } else {
            super.onBackPressed();
        }
    }
}
