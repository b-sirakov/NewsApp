package com.example.nikoleta.newsapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class News implements Serializable {

    private String title;
    private String author;
    private String text;
    private String imageURL;
    private Bitmap bitmapIMG;

    public News(String title, String author, String text, String image){

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
            // TODO add URL validations
            this.imageURL = image;
        }
    }

    public News(String title, String author, String text){

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

    public void setBitmapIMG(Bitmap bitmapIMG) {
        this.bitmapIMG = bitmapIMG;
    }

    public Bitmap getBitmapIMG() {
        return bitmapIMG;
    }

    public static boolean stringInputValidation(String string){
        return (string != null && !string.isEmpty()) ? true : false;
    }

}
