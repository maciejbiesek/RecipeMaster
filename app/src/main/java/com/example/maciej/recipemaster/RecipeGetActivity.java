package com.example.maciej.recipemaster;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import static com.example.maciej.recipemaster.Constants.*;


public class RecipeGetActivity extends ActionBarActivity {

    private String userName;
    private String userPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_get);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userName = "Logged as " + intent.getStringExtra(USER_NAME);
            userPhotoUrl = intent.getStringExtra(USER_PHOTO);

            // Log.e("DEBUG", userName + ", " + userPhotoUrl);
        }
        else {
            userName = "You're not logged in";
        }

        bindFooter();

        if (isOnline()) {
            (new AsyncRecipeDownload()).execute();
        }
        else {
            Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindFooter() {
        TextView fbName = (TextView) findViewById(R.id.fb_name);
        ImageView fbPhoto = (ImageView) findViewById(R.id.fb_photo);

        fbName.setText(userName);
        Picasso.with(this).load(userPhotoUrl).into(fbPhoto);
    }

    private void bindRecipeToView(Recipe recipe) {
        TextView titleText = (TextView) findViewById(R.id.recipe_title);
        TextView descriptionText = (TextView) findViewById(R.id.recipe_description);
        TextView ingredientsText = (TextView) findViewById(R.id.recipe_ingredients);
        TextView preparingText = (TextView) findViewById(R.id.recipe_preparing);
        ImageView image1 = (ImageView) findViewById(R.id.recipe_image1);
        ImageView image2 = (ImageView) findViewById(R.id.recipe_image2);
        ImageView image3 = (ImageView) findViewById(R.id.recipe_image3);

        titleText.setText(recipe.getTitle() + ":");
        descriptionText.setText(recipe.getDescription());

        String ingredients = "";
        for (String elem : recipe.getIngredients()) {
            ingredients += "- " + elem + "\n";
        }
        ingredientsText.setText(ingredients);

        String preparing = "";
        for (int i = 0; i < recipe.getPreparing().size(); i++) {
            preparing += i + 1 + ". " + recipe.getPreparing().get(i) + "\n\n";
        }
        preparingText.setText(preparing);


        Picasso.with(this).load(recipe.getImages().get(0)).into(image1);
        Picasso.with(this).load(recipe.getImages().get(1)).into(image2);
        Picasso.with(this).load(recipe.getImages().get(2)).into(image3);

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_get, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class AsyncRecipeDownload extends AsyncTask<String, Void, Recipe> {

        private RestProvider restProvider;

        @Override
        protected void onPostExecute(Recipe result) {
            super.onPostExecute(result);

            bindRecipeToView(result);

            ViewAnimator viewAnimator = (ViewAnimator) findViewById(R.id.animator);
            viewAnimator.setDisplayedChild(1);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restProvider = new RestProvider();
        }

        @Override
        protected Recipe doInBackground(String... params) {
            try {
                restProvider.parseJSON();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return restProvider.getRecipe();
        }
    }
}
