package com.example.maciej.recipemaster;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> images = new ArrayList<String>();

    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images.addAll(images);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public String getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View imageView;

        if (convertView == null) {
            imageView = LayoutInflater.from(context).inflate(R.layout.recipe_image, parent, false);
        } else {
            imageView = convertView;
        }

        bindImageToView(getItem(position), imageView);

        return imageView;
    }


    private void bindImageToView(String imageUrl, View imageView) {
        ImageView image = (ImageView) imageView.findViewById(R.id.recipe_image);
        Picasso.with(context).load(imageUrl).into(image);
    }
}
