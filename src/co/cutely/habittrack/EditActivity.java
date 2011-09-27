package co.cutely.habittrack;

import co.cutely.habittrack.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

public class EditActivity extends Activity implements View.OnClickListener {
	protected TextView name = null;
	protected TextView count = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        
        // Get the habit parameter
        final GlobalState state = (GlobalState)this.getApplication();
        final TrackedHabit habit = state.getHabitParameter();

        // Just a sanity check
        if (habit == null) {
        	// Nothing to edit...  leave
        	finish();
        }

        // Get the parts of the interface
        name = (TextView)findViewById(R.id.viewHabitName);
        count = (TextView)findViewById(R.id.viewHabitCountText);
        
        // Setup the data
        name.setText(habit.getName());
        count.setText(Integer.toString(habit.getCount()));

        // Get the buttons
        ImageButton up = (ImageButton)findViewById(R.id.viewHabitCountUpButton);
        ImageButton down = (ImageButton)findViewById(R.id.viewHabitCountDownButton);
        Button update = (Button)findViewById(R.id.viewHabitUpdate);
        
        // Should it be save or edit
        if (state.getHabitIndex() >= 0) {
        	update.setText(R.string.button_update);
        } else {
        	update.setText(R.string.button_save);
        }
        
        // Connect handlers
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        update.setOnClickListener(this);
    }

	@Override
	public void onClick(View clickedView) {
		switch (clickedView.getId()) {
		case R.id.viewHabitCountUpButton:
			do {
				int newCount = Integer.parseInt(count.getText().toString()) + 1;
				count.setText(Integer.toString(newCount));
			} while (false);
			break;
		case R.id.viewHabitCountDownButton:
			do {
				int newCount = Integer.parseInt(count.getText().toString()) - 1;
				count.setText(Integer.toString(newCount));
			} while (false);
			break;
		case R.id.viewHabitUpdate:
			// Update the parameter based on changes
			do {
				final int newCount = Integer.parseInt(count.getText().toString());
				final String newName = name.getText().toString(); 
				final TrackedHabit habit = ((GlobalState)this.getApplication()).getHabitParameter();
				
				// Check for name
				if (newName == null || newName.length() == 0) {
					name.requestFocus();

					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(R.string.edit_blank_name_warn)
							.setPositiveButton(android.R.string.yes, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Yes, so exit
									finish();
								}
							})
							.setNegativeButton(android.R.string.no, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// No, so... don't do anything! Let them continue to edit
									return;
								}
							});
					// Finish building and show the box
					final AlertDialog alert = builder.create();
					alert.show();
					
					// Done for now
					return;
				}
				
				habit.setCount(newCount);
				habit.setName(newName);
			} while (false);
			
			// Done with the activity
			finish();
			break;
		}
	}
}
