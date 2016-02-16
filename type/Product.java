package com.christofan.oneuptest.type;

public class Product {

    protected int image;
    protected String name;
    protected double price;

    public Product() {
        image = 0;
        name = "";
        price = 0;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
