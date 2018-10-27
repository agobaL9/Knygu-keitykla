package com.agobal.KnyguKeitykla.Entities;


public class MyBook {

    String BookName;
    String BookAuthor;
    String BookImage;

    public MyBook (String BookName, String BookAuthor, String BookImage)
    {
        this.BookName = BookName;
        this.BookAuthor = BookAuthor;
        this.BookImage = BookImage;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor = bookAuthor;
    }

    public String getBookImage() {
        return BookImage;
    }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }


}
