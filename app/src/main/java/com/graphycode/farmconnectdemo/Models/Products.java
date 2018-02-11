package com.graphycode.farmconnectdemo.Models;

/**
 * Created by kay on 11/27/17.
 */

public class Products {
   private String Product_Key, UserID, Product_Name, Category, Price, Quantity, Location,
            Farm_Name, Description, DateCreated, Image, Audio, Video;


    public Products() {
    }

    public Products(String product_Key, String userID, String product_Name,String category, String price, String quantity,
                    String location, String farm_Name, String description, String dateCreated, String image,
                    String audio, String video) {
        Product_Key = product_Key;
        UserID = userID;
        Product_Name = product_Name;
        Category = category;
        Price = price;
        Quantity = quantity;
        Location = location;
        Farm_Name = farm_Name;
        Description = description;
        DateCreated = dateCreated;
        Image = image;
        Audio = audio;
        Video = video;
    }

    public String getProduct_Key() {
        return Product_Key;
    }

    public void setProduct_Key(String product_Key) {
        Product_Key = product_Key;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getFarm_Name() {
        return Farm_Name;
    }

    public void setFarm_Name(String farm_Name) {
        Farm_Name = farm_Name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }
}
