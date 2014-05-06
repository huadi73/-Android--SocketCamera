package huadi.com.socketcamera;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingActivity extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			onCreatePreferenceActivity();
		}
		else
		{
			onCreatePreferenceFragment();
		}
	}

	@SuppressWarnings("deprecation") //api <= 10
	private void onCreatePreferenceActivity()
	{
		addPreferencesFromResource(R.xml.preference);
	}

	@SuppressLint("NewApi") //api >= 11(3.0)
	private void onCreatePreferenceFragment()
	{
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
	}
	
	@SuppressLint("NewApi")
	public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }
}