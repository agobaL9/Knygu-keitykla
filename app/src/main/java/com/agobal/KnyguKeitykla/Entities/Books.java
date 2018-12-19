package com.agobal.KnyguKeitykla.Entities;


import java.io.Serializable;

public class Books implements Serializable {

    private String BookName;
    private String BookAuthor;
    private String BookPublisher;
    private Integer BookYear;
    private String BookCondition;
    private String BookCategory;
    private String BookImage;
    private String BookTradable;

    private String UserID;

    public Books (String BookName, String BookAuthor, String BookPublisher, Integer BookYear, String BookCondition, String BookCategory, String BookImage, String BookTradable, String UserID)
    {
        this.BookName = BookName;
        this.BookAuthor = BookAuthor;
        this.BookPublisher = BookPublisher;
        this.BookYear = BookYear;
        this.BookCondition = BookCondition;
        this.BookCategory = BookCategory;
        this.BookImage = BookImage;
        this.BookTradable = BookTradable;
        this.UserID = UserID;
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

    public String getUserID() { return UserID; }

    public void setUserID(String userID) { UserID = userID; }

    public String toString(){
        return "name : " + BookName + "\nAge : " + BookAuthor + "\nColor : " + BookImage;
    }


}
