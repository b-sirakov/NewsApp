package com.example.nikoleta.newsapp.model;

public class News {

    private String title;
    private String author;
    private String text;
    private String image;

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

    public static boolean stringInputValidation(String string){
        return (string != null && !string.isEmpty()) ? true : false;
    }

}
