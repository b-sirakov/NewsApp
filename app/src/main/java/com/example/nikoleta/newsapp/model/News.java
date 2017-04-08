package com.example.nikoleta.newsapp.model;

import android.graphics.Bitmap;

public class News {

    private String title;
    private String author;
    private String text;
    private String image;
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
            this.image = image;
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
    public String getImage() {
        return image;
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
