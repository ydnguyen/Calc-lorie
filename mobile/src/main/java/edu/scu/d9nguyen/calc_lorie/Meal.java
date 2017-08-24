package edu.scu.d9nguyen.calc_lorie;

/**
 * Created by Tritium on 5/21/2016.
 */
public class Meal {

    private String mName;
    private double mCaloriePerServing;

    public Meal( String mName, double mCaloriePerServing ) {
        this.mName = mName;
        this.mCaloriePerServing = mCaloriePerServing;
    }

    public String getName() {
        return mName;
    }

    public double getCaloriePerServing() {
        return mCaloriePerServing;
    }

}
