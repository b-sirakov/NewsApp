package com.example.nikoleta.newsapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class News implements Serializable, Cloneable {


    private int id;
    private String title;
    private String author;
    private String text;
    private String imageURL;
    private Bitmap bitmapIMG;
    private String date;
    private String originalArticleURL;

    private News(){
        this.title="No title";
        this.author="Uknown Author";
        this.text="No Text";
        //this.imageURL="Unknown img url";
        this.date="Uknown date";
        this.originalArticleURL="Unknown url";
    }

    public News(String title, String author, String text, String image, String date, String originalArticleURL){
        this();
        if(stringInputValidation(title)){
            this.title = title;
        }
        if(stringInputValidation(author)){
            this.author = author;
        }
        if(stringInputValidation(text)){
            this.text = text;
        }
        if(stringInputValidation(image)){
            this.imageURL = image;
        }
        if(stringInputValidation(date)){
            this.date = date;
        }
        if(stringInputValidation(originalArticleURL)){
            this.originalArticleURL = originalArticleURL;
        }
    }
    public News(String title, String author, String text){
        this();
        if(stringInputValidation(title)){
            this.title = title;
        }
        if(stringInputValidation(author)){
            this.author = author;
        }
        if(stringInputValidation(text)){
            this.text = text;
        }
    }
    public News(String title, String author, String text, Bitmap image){
        this();
        if(stringInputValidation(title)){
            this.title = title;
        }
        if(stringInputValidation(author)){
            this.author = author;
        }
        if(stringInputValidation(text)){
            this.text = text;
        }
        this.bitmapIMG = image;
    }
    public News(String title, String author, String text, Bitmap image, String date, String originalArticleURL){
        if(stringInputValidation(title)){
            this.title = title;
        }
        if(stringInputValidation(author)){
            this.author = author;
        }
        if(stringInputValidation(text)){
            this.text = text;
        }
        if(image != null){
            this.bitmapIMG = image;
        }
        if(stringInputValidation(date)){
            this.date = date;
        }
        if(stringInputValidation(originalArticleURL)){
            this.originalArticleURL = originalArticleURL;
        }
    }


    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getText() {
        return text;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getDate() {
        return date;
    }
    public String getOriginalArticleURL() {
        return originalArticleURL;
    }

    public void setBitmapIMG(Bitmap bitmapIMG) {
        this.bitmapIMG = bitmapIMG;
    }
    public void setOriginalArticleURL(String originalArticleURL) {
        this.originalArticleURL = originalArticleURL;
    }
    public void setId(int id) {
        if(id >= 0) {
            this.id = id;
        }
    }

    public Bitmap getBitmapIMG() {
        return bitmapIMG;
    }

    public static boolean stringInputValidation(String string){
        return (string != null && !string.isEmpty()) ? true : false;
    }


}
