package com.dropbox.core.examples.android;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;

import java.lang.reflect.Field;


/**
 * Activity that shows information about the currently logged in user
 */
public class UserActivity extends DropboxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Button loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(UserActivity.this, getString(R.string.app_key));
            }
        });

        Button filesButton = (Button)findViewById(R.id.files_button);
        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FilesActivity.getIntent(UserActivity.this, ""));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {
            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
            findViewById(R.id.type_text).setVisibility(View.VISIBLE);
            findViewById(R.id.files_button).setEnabled(true);

        } else {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_text).setVisibility(View.GONE);
            findViewById(R.id.name_text).setVisibility(View.GONE);
            findViewById(R.id.type_text).setVisibility(View.GONE);
            findViewById(R.id.files_button).setEnabled(false);

        }
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
                ((TextView) findViewById(R.id.type_text)).setText(result.getAccountType().name());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // enable visible icons in action bar
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Field field = menu.getClass().
                            getDeclaredField("mOptionalIconsVisible");
                    field.setAccessible(true);
                    field.setBoolean(menu, true);
                } catch (Exception e) {
                    //Logger.w(TAG, "onMenuOpened(" + featureId + ", " + menu + ")", e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                // User chose the "Settings" item, show the app settings UI...
                finish();
                moveTaskToBack(true);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem unregister = menu.findItem(R.id.action_not_logged);
        MenuItem register = menu.findItem(R.id.action_logged);
        if(hasToken())
        {
            register.setVisible(true);
        }
        else
        {
            unregister.setVisible(true);
        }
        return true;
    }
}
