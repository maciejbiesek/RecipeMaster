package com.example.maciej.recipemaster;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import static com.example.maciej.recipemaster.Constants.*;


public class MainActivity extends ActionBarActivity {

    private LoginButton loginButton;
    private ImageButton getButton;
    private ImageView getButtonLabel;
    private ImageView loginButtonLabel;
    private CallbackManager callbackManager;
    private Profile user;
    private boolean isLayoutAlpha = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        showActionBar();

        getButtonLabel = (ImageView) findViewById(R.id.get_button_label);
        getButtonLabel.setVisibility(View.INVISIBLE);

        getButton = (ImageButton) findViewById(R.id.get_button);
        getButton.setVisibility(View.INVISIBLE);
        getButton.setOnClickListener(onClickListener);

        loginButtonLabel = (ImageView) findViewById(R.id.login_button_label);
        loginButtonLabel.setVisibility(View.INVISIBLE);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setBackgroundResource(R.drawable.fb_button_ico);

        loginButton.setReadPermissions("public_profile");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "You have successfully logged in!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Log.i("DEBUG", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i("DEBUG", "error");
                    }
                });

        ImageButton optionsButton = (ImageButton) findViewById(R.id.options);
        optionsButton.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.get_button: {
                    startGetActivity();
                    break;
                }
                case R.id.options: {
                    ImageView backgroundImage = (ImageView) findViewById(R.id.background_image);
                    ImageButton button = (ImageButton) view;
                    if (!isLayoutAlpha) {
                        backgroundImage.setAlpha(0.30f);
                        button.setImageResource(R.drawable.cancel);
                        getButtonLabel.setVisibility(View.VISIBLE);
                        getButton.setVisibility(View.VISIBLE);
                        loginButtonLabel.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.VISIBLE);
                        isLayoutAlpha = true;
                    }
                    else {
                        backgroundImage.setAlpha(1f);
                        button.setImageResource(R.drawable.plus);
                        getButtonLabel.setVisibility(View.INVISIBLE);
                        getButton.setVisibility(View.INVISIBLE);
                        loginButtonLabel.setVisibility(View.INVISIBLE);
                        loginButton.setVisibility(View.INVISIBLE);
                        isLayoutAlpha = false;
                    }
                    break;
                }
            }
        }
    };

    private void startGetActivity() {
        Intent intent = new Intent(this, RecipeGetActivity.class);
        if (AccessToken.getCurrentAccessToken() != null) {
            user = Profile.getCurrentProfile();
            String name = user.getName();
            String photoUri = String.valueOf(user.getProfilePictureUri(100, 100));
            intent.putExtra(USER_NAME, name);
            intent.putExtra(USER_PHOTO, photoUri);
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // MENU

    private void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View cView = getLayoutInflater().inflate(R.layout.main_menu, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        actionBar.setCustomView(cView, layout);
    }
}
