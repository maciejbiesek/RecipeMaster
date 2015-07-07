package com.example.maciej.recipemaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import static com.example.maciej.recipemaster.Constants.*;


public class RecipeGetActivity extends ActionBarActivity {

    private String userInfo;
    private String userPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_get);
        showActionBar();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userInfo = "Logged as " + intent.getStringExtra(USER_NAME);
            userPhotoUrl = intent.getStringExtra(USER_PHOTO);

            // Log.e("DEBUG", userName + ", " + userPhotoUrl);
        }
        else {
            userInfo = "You are not logged in";
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

        fbName.setText(userInfo);
        Picasso.with(this).load(userPhotoUrl).into(fbPhoto);
    }

    private void bindRecipeToView(Recipe recipe) {
        TextView titleText = (TextView) findViewById(R.id.recipe_title);
        TextView descriptionText = (TextView) findViewById(R.id.recipe_description);
        TextView ingredientsText = (TextView) findViewById(R.id.recipe_ingredients);
        TextView preparingText = (TextView) findViewById(R.id.recipe_preparing);

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

        initializeGrid(recipe);
    }

    private void initializeGrid(Recipe recipe) {
        ExpandableHeightGridView grid = (ExpandableHeightGridView) findViewById(R.id.images_grid);
        grid.setExpanded(true);
        ImageAdapter adapter = new ImageAdapter(this, recipe.getImages());
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(onClickListener);
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // MENU

    private void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        View cView = getLayoutInflater().inflate(R.layout.get_recipe_menu, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        actionBar.setCustomView(cView, layout);
    }

    public void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RecipeGetActivity.this);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RelativeLayout relativeLayout = (RelativeLayout) view;
                            ImageView image = (ImageView) relativeLayout.getChildAt(0);
                            image.buildDrawingCache();
                            Bitmap bitmap = image.getDrawingCache();
                            try {
                                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null); // bitmp, title, description
                                Toast.makeText(RecipeGetActivity.this, "Obrazek zapisano", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                Toast.makeText(RecipeGetActivity.this, "Wystąpił błąd", Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            builder.create().show();
        }
    };




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
