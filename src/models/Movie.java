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


    /**
     * Represents a movie
     *
     * @param ID       movie ID
     * @param title    movie title
     * @param price    movie price
     * @param runtime  runtime in minutes
     * @param agelimit age limit for the movie
     */
    public Movie(int ID, String title, int price, int runtime, int agelimit) {
        this.ID = ID;
        this.title = title;
        this.price = price;
        this.runtime = runtime;
        this.agelimit = agelimit;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", runtime=" + runtime +
                ", agelimit=" + agelimit +
                '}';
    }
}
