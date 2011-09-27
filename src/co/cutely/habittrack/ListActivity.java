package co.cutely.habittrack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.cutely.habittrack.R;

import net.iharder.base64.Base64;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The main activity - lists habits and links to the rest of the application activities.
 *
 */
public class ListActivity extends Activity implements AdapterView.OnItemClickListener {
	// Log messages tag
	final private String LOG_TAG = "HabitTracker";
	
	// Shared preferences name & key
	final private String PREFERENCES_NAME = "co.cutely.habtrack.PREFS";
	final private String PREFERENCES_KEY = "HABITS_LIST";
	
	protected SharedPreferences mPreferences;
	protected ArrayList<TrackedHabit> mHabitsList = new ArrayList<TrackedHabit>();
	protected HabitAdapter mHabitAdapter;

	/*
	 * Helper method to read/deserialize the list of habits from a string.
	 * 
	 * "unchecked" is suppressed for the entire function because Eclipse didn't like
	 * it as a line level suppression.  Deserializing the data, it can only be checked
	 * to the ArrayList<?> level AFIAK.  
	 */
    @SuppressWarnings("unchecked")
    private void loadPreferences() {
        // Get shared preferences and save a reference to it
        mPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (mPreferences.contains(PREFERENCES_KEY)) {
        	final String serialisedObject = mPreferences.getString(PREFERENCES_KEY, "");
        	if (serialisedObject != null && serialisedObject.length() > 0) {
        		try {
					final Object decoded = Base64.decodeToObject(serialisedObject);
					if (decoded instanceof ArrayList<?>) {
						this.mHabitsList = (ArrayList<TrackedHabit>)decoded;
					}
				} catch (IOException ex) {
					Log.w(LOG_TAG, "Exception while decoding from preferences: " + ex.getMessage());
				} catch (ClassNotFoundException ex) {
					Log.w(LOG_TAG, "Exception while decoding from preferences: " + ex.getMessage());
				}
        	}
        }
    }
    
	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Load data from the app preferences
        loadPreferences();
        
        // Get the list view
        final ListView habits = (ListView)findViewById(R.id.habitsList);
        mHabitAdapter = new HabitAdapter(this, mHabitsList);
        habits.setAdapter(mHabitAdapter);
        habits.setOnItemClickListener(this);
        
        // Register for long press
        registerForContextMenu(habits);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		// Update a habit?
		final GlobalState state = (GlobalState)this.getApplication();
		final TrackedHabit habit = state.getHabitParameter();
		if (habit != null) {
			final int index = state.getHabitIndex();

			// If the index is -1 it means "add"
			if (index >= 0) {
				mHabitsList.set(index, habit);
				
				// Set doesn't notify of a change, so do that now
				mHabitAdapter.notifyDataSetChanged();
			} else {
				final String habitName = habit.getName();
				if (habitName != null && habitName.length() > 0) {
					// Add & notify the adapter the dataset has changed
					mHabitsList.add(habit);
					mHabitAdapter.notifyDataSetChanged();
				}
			}
		}
		
		// Clear up the parameter
		state.setHabitParameter(null);
	}
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	final SharedPreferences.Editor edit = mPreferences.edit();
    	String serialisedString;
		try {
			serialisedString = Base64.encodeObject(this.mHabitsList);
	    	edit.putString(PREFERENCES_KEY, serialisedString);
	    	edit.commit();
		} catch (IOException ex) {
			Log.w(LOG_TAG, "Exception while encoding for preferences: " + ex.getMessage());
		}
    }

	/**
     * Inner class to implement the required bits of a custom ArrayAdapter
     * to support the "habit_list_item" ListView item
     *
     */
    private class HabitAdapter extends ArrayAdapter<TrackedHabit> {
    	private LayoutInflater mInflater;
    	
    	public HabitAdapter(final Context context, final List<TrackedHabit> habits) {
    		super(context, 0, habits);
    		
    		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	

    	@Override
        public View getView(int position, final View providedView, final ViewGroup parent) {
    		final View view;
    		
    		// See if the view was passed in or if it needs to be inflated
    		if (providedView == null) {
                view = mInflater.inflate(R.layout.habit_list_item, null);
            } else {
                view = providedView;
            }
    		
    		final TrackedHabit habitItem = this.getItem(position);
    		
    		final TextView nameField = (TextView)view.findViewById(R.id.habitListName);
    		final TextView countField = (TextView)view.findViewById(R.id.habitListCount);

    		nameField.setText(habitItem.getName());
    		countField.setText(Integer.toString(habitItem.getCount()));
    		
    		return view;
    	}
    }

    /**
     * Click handler for a habit in the list
     * 
     * @param parent 	The AdapterView where the click happened.
     * @param view 	The view within the AdapterView that was clicked (this will be a view provided by the adapter)
     * @param position 	The position of the view in the adapter.
     * @param id 	The row id of the item that was clicked. 
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Set the parameter for the view activity
		final GlobalState app = (GlobalState)this.getApplication();
		app.setHabitParameter(mHabitsList.get(position));
		app.setHabitIndex(position);

		// Start the view intent
		final Intent editIntent = new Intent(ListActivity.this, EditActivity.class);
        startActivity(editIntent);
	}
	
	/**
	 * Create the options menu from XML
	 * @param menu The menu to populate
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}

	/**
	 * Handler for the options menu
	 * @param item The menu item selected
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menuAdd:
			// Set the parameter for the view activity -- a new habit
			final GlobalState app = (GlobalState)this.getApplication();
			app.setHabitParameter(new TrackedHabit());
			app.setHabitIndex(-1);

			// Start the view intent
			final Intent editIntent = new Intent(ListActivity.this, EditActivity.class);
	        startActivity(editIntent);
	        break;
	    case R.id.menuAbout:
			// Start the view intent
			final Intent aboutIntent = new Intent(ListActivity.this, AboutActivity.class);
	        startActivity(aboutIntent);
	        break;
	    default:
	    	// Pass it along?
	        return super.onOptionsItemSelected(item);
	    }
	    
	    // Handled above
	    return true;
	}
	

	/**
	 * Handler for long press/context menu
	 * @param menu 		The context menu that is being built
	 * @param view 		The view for which the context menu is being built
	 * @param menuInfo	Extra information about the item for which the context menu should be shown. This information 
	 * 					will vary depending on the class of view. 
	 */
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context, menu);
	}
	
	/**
	 * Handler for the context menu
	 * @param item		The menu item selected
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		switch (item.getItemId()) {
		case R.id.delete:
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.delete_title)
					.setMessage(R.string.delete_warn)
					.setIcon(getResources().getDrawable(R.drawable.ic_dialog_alert))
					.setPositiveButton(android.R.string.yes, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Yes, delete it.
							mHabitsList.remove(menuInfo.position);
							mHabitAdapter.notifyDataSetChanged();
						}
					})
					.setNegativeButton(android.R.string.no, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// No, don't delete it
							return;
						}
					});
			// Finish building and show the box
			final AlertDialog alert = builder.create();
			alert.show();
			
			// Done for now
			break;
		case R.id.reset:
			final TrackedHabit resetHabit = mHabitsList.get(menuInfo.position);
			resetHabit.setCount(0);
			mHabitAdapter.notifyDataSetChanged();
		}
		return true;
	}
}
