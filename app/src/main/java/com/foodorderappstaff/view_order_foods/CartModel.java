package com.foodorderappstaff.view_order_foods;

import java.util.Date;

public class CartModel {

    Date orderTime;
    String price;
    String productID;
    String productName;
    String quantity;
    String status;
    String itemTotal;
    String orderID;

    public CartModel() {
    }

    public CartModel(Date orderTime, String price, String productID, String productName, String quantity, String status, String itemTotal, String orderID) {
        this.orderTime = orderTime;
        this.price = price;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
        this.itemTotal = itemTotal;
        this.orderID = orderID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }
}
