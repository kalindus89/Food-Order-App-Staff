package com.foodorderappstaff.updateBanners;

public class BannerModel {

    String name;
    String foodId;
    String image;

    public BannerModel() {

    }

    public BannerModel(String name, String image, String foodId) {
        this.name = name;
        this.image = image;
        this.foodId = foodId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
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
