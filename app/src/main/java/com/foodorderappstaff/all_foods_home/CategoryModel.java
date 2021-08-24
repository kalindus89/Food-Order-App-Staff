package com.foodorderappstaff.all_foods_home;

public class CategoryModel {

    String name;
    String image;

    public CategoryModel() {

    }

    public CategoryModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
