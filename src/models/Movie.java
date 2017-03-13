package models;

/**
 * Created by Nadim Dahdouli on 2017-03-13.
 */
public class Movie {

    private int ID;
    String title;
    int price;
    int runtime;
    int agelimit;


    public Movie(int ID, String title, int price, int runtime, int agelimit) {
        this.ID = ID;
        this.title = title;
        this.price = price;
        this.runtime = runtime;
        this.agelimit = agelimit;
    }
}
