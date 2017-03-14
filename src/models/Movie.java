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
        this(title, price, runtime, agelimit);
        this.ID = ID;
    }

    public Movie(String title, int price, int runtime, int agelimit) {
        this.title = title;
        this.price = price;
        this.runtime = runtime;
        this.agelimit = agelimit;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getRuntime() {
        return runtime;
    }

    public int getAgelimit() {
        return agelimit;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public void setAgelimit(int agelimit) {
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
