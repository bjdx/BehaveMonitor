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
import com.example.BehaveMonitor.fragments.HelpFragment;
import com.example.BehaveMonitor.fragments.LicensingFragment;
import com.example.BehaveMonitor.fragments.NavigationDrawerFragment;
import com.example.BehaveMonitor.fragments.SessionFragment;
import com.example.BehaveMonitor.fragments.SessionHistoryFragment;
import com.example.BehaveMonitor.fragments.SettingsFragment;
import com.example.BehaveMonitor.fragments.TemplateFragment;

import java.io.File;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the fragment currently being displayed. Defaults to -1 to show the initial fragment.
     */
    private int fragmentDisplayed = -1;

    /**
     * Used to store the amount of items in the top list of the navigation drawer.
     */
    private int amountDrawerItems;

    private File activeFolder = null;
    private Template activeTemplate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FileHandler.setRootDirectory(this);
        DBHelper db = DBHelper.getInstance(this);

        // Set up the drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        FileHandler.checkFoldersExist(this);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString("activeFolderString");
            String tString = savedInstanceState.getString("activeTemplateString");
            if (path != null) activeFolder = new File(path);
            if (tString != null) activeTemplate = new Template(tString);
        }

        if (activeFolder == null) {
            activeFolder = new File(FileHandler.getSessionsDirectory(), db.getFolder());
        }

        if (activeTemplate == null) {
            String templateString = db.getTemplate();
            if (templateString != null) {
                activeTemplate = new Template(templateString);
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tString = bundle.getString("activeTemplateString");
            if (tString != null) activeTemplate = new Template(tString);
        }

        int redirect = getIntent().getIntExtra("redirect", 0);
        if (redirect != 0) {
            displayFragment(redirect);
        }

        // This code (and the code at the end of the file) is required to debug the layouts with the hierarchy viewer. It can cause a crash however so should only be uncommented when desired.
//        if (BuildConfig.DEBUG) {
//            ViewServer.get(this).addWindow(this);
//        }
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
     * @return The name of the active template, or "" if no template has been selected.
     */
    public String getTemplateName() {
        DBHelper db = DBHelper.getInstance(this);
        Template template = new Template(db.getTemplate());

        return "null;".equals(template.toString()) ? "" : template.name;
    }

    /**
     * Sets the number of items in the nav drawer top list
     * @param amountDrawerItems the number of items in the nav drawer top list
     */
    public void setAmountDrawerItems(int amountDrawerItems) {
        this.amountDrawerItems = amountDrawerItems;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (fragmentDisplayed != position) {
            displayFragment(position);
        }
    }

    private void displayFragment(int position) {
        Fragment fragment = amountDrawerItems > position ? getMainFragment(position) : getBottomFragment(position - amountDrawerItems);
        if (fragment == null) {
            Log.e("Behave", "Error displaying fragment");
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        fragmentDisplayed = position;
        if (mNavigationDrawerFragment != null) {
            if (amountDrawerItems > position) {
                mNavigationDrawerFragment.setItemChecked(position);
            } else {
                mNavigationDrawerFragment.setBottomItemChecked(position - amountDrawerItems);
            }
        }
    }

    private Fragment getMainFragment(int position) {
        switch(position) {
            case 0:
                return new SessionFragment();
            case 1:
                return new FolderFragment();
            case 2:
                return new TemplateFragment();
            case 3:
                return new SessionHistoryFragment();
            default:
                return null;
        }
    }

    private Fragment getBottomFragment(int position) {
        switch(position) {
            case 0:
                return new SettingsFragment();
            case 1:
                return new HelpFragment();
            case 2:
                return new LicensingFragment();
            default:
                return null;
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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (BuildConfig.DEBUG) {
//            ViewServer.get(this).removeWindow(this);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (BuildConfig.DEBUG) {
//            ViewServer.get(this).setFocusedWindow(this);
//        }
//    }
}
