package com.agobal.KnyguKeitykla.Entities;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class MyBook implements Serializable {

    private String BookName;
    private String BookAuthor;
    private String BookPublisher;
    private Integer BookYear;
    private String BookCondition;
    private String BookCategory;
    private String BookImage;
    private String BookTradable;
    private String BookKey;
    private String BookCity;
    private String BookChange;



    public MyBook (String BookName, String BookAuthor, String BookPublisher, Integer BookYear, String BookCondition, String BookCategory, String BookImage, String BookTradable, String BookKey, String BookCity, String BookChange)
    {
        this.BookName = BookName;
        this.BookAuthor = BookAuthor;
        this.BookPublisher = BookPublisher;
        this.BookYear = BookYear;
        this.BookCondition = BookCondition;
        this.BookCategory = BookCategory;
        this.BookImage = BookImage;
        this.BookTradable = BookTradable;
        this.BookKey = BookKey;
        this.BookCity = BookCity;
        this.BookChange =BookChange;


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

    public String getBookPublisher() { return BookPublisher; }

    public void setBookPublisher(String bookPublisher) { BookPublisher = bookPublisher; }

    public Integer getBookYear() { return BookYear; }

    public void setBookYear(Integer bookYear) { BookYear = bookYear; }

    public String getBookCondition() { return BookCondition; }

    public void setBookCondition(String bookCondition) { BookCondition = bookCondition; }

    public String getBookCategory() { return BookCategory; }

    public void setBookCategory(String bookCategory) { BookCategory = bookCategory; }

    public String getBookImage() { return BookImage; }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }

    public String getBookTradable()
    {
        return BookTradable;
    }

    public void setBookTradable(String bookTradable) {
        BookTradable = bookTradable;
    }


    public String getBookKey() { return BookKey; }

    public void setBookKey(String bookKey) { BookKey = bookKey; }

    public String getBookCity() { return BookCity; }

    public void setBookCity(String bookCity) { BookCity = bookCity; }

    public String getBookChange() { return BookChange; }

    public void setBookChange(String bookChangeMoney) { BookChange = bookChangeMoney; }



    @NonNull
    public String toString(){
        return "name : " + BookName + "\nAge : " + BookAuthor + "\nColor : " + BookImage;
    }


}
