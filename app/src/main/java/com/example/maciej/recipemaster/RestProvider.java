package com.example.maciej.recipemaster;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.example.maciej.recipemaster.Constants.*;

public class RestProvider {

    private Recipe recipe;

    public void parseJSON() throws IOException, JSONException {
        String s = downloadFromUrl();
        JSONObject jsonRequest = new JSONObject(s);
        String title = jsonRequest.getString("title");
        String description = jsonRequest.getString("description");

        JSONArray jsonIngredients = jsonRequest.getJSONArray("ingredients");
        List<String> ingredients = new ArrayList<String>();
        for (int i = 0; i < jsonIngredients.length(); i++) {
            ingredients.add(jsonIngredients.getString(i));
        }

        JSONArray jsonPreparing = jsonRequest.getJSONArray("preparing");
        List<String> preparing = new ArrayList<String>();
        for (int i = 0; i < jsonPreparing.length(); i++) {
            preparing.add(jsonPreparing.getString(i));
        }

        JSONArray jsonImages = jsonRequest.getJSONArray("imgs");
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < jsonImages.length(); i++) {
            images.add(jsonImages.getString(i));
        }

        recipe = new Recipe(title, description, ingredients, preparing, images);
    }

    private String downloadFromUrl() throws IOException {
        InputStream is = null;

        try {
            java.net.URL url = new java.net.URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();

            return readStream(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readStream(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
