package com.example.maciej.recipemaster;


import java.util.List;

public class Recipe {

    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> preparing;
    private List<String> images;

    public Recipe(String title, String description, List<String> ingredients, List<String> preparing, List<String> images) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.preparing = preparing;
        this.images = images;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getPreparing() { return preparing; }
    public List<String> getImages() { return images; }

}
