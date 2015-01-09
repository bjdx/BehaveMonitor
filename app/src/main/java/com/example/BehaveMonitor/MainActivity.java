package com.example.BehaveMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

	// declare properties
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
    
    private File activeFolder;
    private Template activeTemplate;
    private Session activeSession;


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        if(activeFolder!=null) savedInstanceState.putString("activeFolderString", activeFolder.getAbsolutePath());
        if(activeTemplate!=null) savedInstanceState.putString("activeTemplateString", activeTemplate.toString());
        if(activeSession!=null) savedInstanceState.putString("activeSessionName", activeSession.name);
        if(activeSession!=null) savedInstanceState.putString("activeSessionLoc", activeSession.location);

        //May want to add more for the session name and location.
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// for proper titles
		mTitle = getTitle();
		
		//Creates the Nav drawer
		setupNavDrawer();
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        checkFoldersExist();
        
        if (savedInstanceState == null) {
            // on first time display view for first nav item
        	selectItem(0);
        } else {
            Log.d("I AM BEING CALLED LOOK AT ME","OOOOOOOOOOOOOOOOOOOOO");
            String path = savedInstanceState.getString("activeFolderString");
            String tString = savedInstanceState.getString("activeTemplateString");
            String sName = savedInstanceState.getString("activeSessionName");
            String sLoc = savedInstanceState.getString("activeSessionLoc");
            if(path!=null) activeFolder = new File(path);
            if(tString!=null) activeTemplate = new Template(tString);
        }
	}


    //Setup navigation drawer
	public void setupNavDrawer() {
		mDrawerTitle = getTitle();
		
		// initialize properties
		mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // list the drawer items
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[3];
        
        //White when not set, green when set, red when not available. Use collection  icon
        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_collection, "Folder");
        //Create New Folder Pop Up (plus folder icon) - Remove folder icon (bin icon) - List of available folders drop down
        
            
        //template name label and edittext, drop down with behaviours, Add behaviour, edit behaviour, delete behaviour.
        //pop ups for new behaviours.
        //dropdown with old templates with + button on the side. Use copy icon
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_copy, "Template");
        
        
        //Use label icon
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_action_labels, "Session");
        
        //Change start session to end session once begun
        //drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_share, "Start Session");
        //
        
        
        // Pass the folderData to our ListView adapter
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        
        // Set the adapter for the list view
        mDrawerList.setAdapter(adapter);
        
        // set the item click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        
        

        
        // for app icon control for nav drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {
        	
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	
	//Checks the required folders have been created.
	public void checkFoldersExist() {
		//If template folder doesnt exist make it
		String dirPath = getFilesDir().getAbsolutePath() + File.separator + "Templates";
		File projDir = new File(dirPath);
		if (!projDir.exists()) projDir.mkdirs();
		
		//If session folder doesn't exist make it
		dirPath = getFilesDir().getAbsolutePath() + File.separator + "Session Folders";
		projDir = new File(dirPath);
		if (!projDir.exists()) projDir.mkdirs();
		
		//If default session folder doesn't exist make it
		dirPath = getFilesDir().getAbsolutePath() + File.separator + "Session Folders" + File.separator + "Default";
		projDir = new File(dirPath);
		if (!projDir.exists()) projDir.mkdirs();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       if (mDrawerToggle.onOptionsItemSelected(item)) {
           return true;
       }
       
       return super.onOptionsItemSelected(item);
	}
	
	// to change up caret
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	
	// navigation drawer click listener
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	    
	}

    public String getFolderName() {
        if(activeFolder==null) return "";
        return activeFolder.getName();
    }

    public String getTemplateName() {
        if(activeTemplate==null) return "";
        return activeTemplate.name;
    }

	public void setActiveDir(File file) {
		activeFolder = file;
	}
	
	public void setActiveTmp(Template tmp) {
		activeTemplate = tmp;
	}
	
	public void setActiveSession(Session session) {
		activeSession = session;
	}

    public Session getActiveSession(){return activeSession;}

    public String getActivePath() {return activeFolder.getAbsolutePath();}

    public Session makeSession(String name, String loc) {
        activeSession = new Session(name, loc, activeFolder.getAbsolutePath());
        activeSession.template = activeTemplate;
        return activeSession;
    }

    public void selectItem(int position) {
    	
        // update the main content by replacing fragments
        Fragment fragment = null;
        
        switch (position) {
        case 0:
        	//Bundle bundle = new Bundle();
        	//bundle.putString("edttext", "From Activity");
        	// set Fragmentclass Arguments
            fragment = new FolderFragment();
            //fragment.setArguments(bundle);
            break;
        case 1:
            fragment = new TemplateFragment();
            break;
        case 2:
            fragment = new SessionFragment();
            break;
 
        default:
            break;
        }
        
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    public static final class SaveState {
        public static Bundle savedInstanceState;
    }

    private void makeSomeToast(final String message) {
        final Context context = getApplicationContext();
        final CharSequence text = message;
        final int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
