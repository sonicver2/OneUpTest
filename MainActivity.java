package com.christofan.oneuptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.auth0.api.ParameterBuilder;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockActivity;
import com.auth0.lock.LockContext;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView textMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMain = (TextView)findViewById(R.id.TextMain);

        SharedPreferences pref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        String refreshToken = pref.getString(getString(R.string.pref_auth0_refresh_token), "");
        if (refreshToken.equals("")) {
            showLogin();
        } else {
            final Lock lock = LockContext.getLock(this);
            Map<String, Object> parameters = new ParameterBuilder()
                    .clearAll()
                    .set("refresh_token", refreshToken)
                    .set("api_type", "app")
                    .asDictionary();
            lock.getAuthenticationAPIClient().delegation().addParameters(parameters).start(new BaseCallback<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> payload) {
                    final String idToken = (String) payload.get("id_token");
                    //final String token_type = (String) payload.get("token_type");
                    //final Integer expires_in = Integer.parseInt(payload.get("expires_in").toString());

                    SharedPreferences pref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(getString(R.string.pref_auth0_id_token), idToken);
                    editor.apply();

                    lock.getAuthenticationAPIClient().tokenInfo(idToken).start(new BaseCallback<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile payload) {
                            //textMain.setText(payload.getName());
                            Intent productIntent = new Intent(MainActivity.this, ProductActivity.class);
                            startActivity(productIntent);
                            finish();
                        }

                        @Override
                        public void onFailure(Throwable error) {

                        }
                    });
                }

                @Override
                public void onFailure(Throwable error) {
                    showLogin();
                }
            });
        }
    }

    protected void showLogin() {
        Intent lockIntent = new Intent(this, LockActivity.class);
        startActivity(lockIntent);

        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UserProfile profile = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
                Token token = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER);

                SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getString(R.string.pref_auth0_id_token), token.getIdToken());
                editor.putString(getString(R.string.pref_auth0_refresh_token), token.getRefreshToken());
                editor.apply();

                //textMain.setText("Hello " + profile.getName());
                //Toast.makeText(MainActivity.this, "Welcome " + profile.getName(), Toast.LENGTH_SHORT).show();
                Intent productIntent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(productIntent);
                finish();
            }
        }, new IntentFilter(Lock.AUTHENTICATION_ACTION));
    }
}
