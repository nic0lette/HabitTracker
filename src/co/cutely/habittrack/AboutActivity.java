package co.cutely.habittrack;

import co.cutely.habittrack.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * Very basic activity for the "About" screen
 */
public class AboutActivity extends Activity {
	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
	}
}
