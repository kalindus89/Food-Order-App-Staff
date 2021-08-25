package com.foodorderappstaff.specific_foods_list;

public class FoodsModel {

    String name;
    String image;
    String description;
    String discount;
    String price;
    String menuID;

    public FoodsModel() {
    }

    public FoodsModel(String name, String image, String description, String discount, String price, String menuID) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.discount = discount;
        this.price = price;
        this.menuID = menuID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
