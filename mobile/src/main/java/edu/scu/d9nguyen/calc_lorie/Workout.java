package edu.scu.d9nguyen.calc_lorie;

/**
 * Created by Tritium on 5/22/2016.
 */
public class Workout {

    private String wName;
    private String wType;
    private double wCalorieRating;

    public Workout( String wName, String wType, double wCalorieRating ) {
        this.wName = wName;
        this.wType = wType;
        this.wCalorieRating = wCalorieRating;
    }

    public String getName() {
        return wName;
    }

    public String getType() {
        return wType;
    }

    public double getCalorieRating() {
        return wCalorieRating;
    }

}
