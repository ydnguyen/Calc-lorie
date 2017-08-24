package edu.scu.d9nguyen.calc_lorie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MealActivity extends AppCompatActivity {

    private RecyclerView recList;
    private MealAdapter ma;
    //Todo: change to DB
    private List<Meal> mealList;
    private int selServingSize = 1; //starts at one serving
    private SQLiteDatabase db;
    private String myDbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        recList = (RecyclerView) findViewById( R.id.mealList );
        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation( LinearLayoutManager.VERTICAL );
        recList.setLayoutManager( llm );

        Bundle cuisineBundle = getIntent().getExtras();
        String selCuisine = cuisineBundle.getString( "cuisine" );
        populateMealTable();
        mealList = populateMealList( selCuisine );

        Log.i( "MealActivity", Integer.toString( mealList.size() ) );

        ma = new MealAdapter( mealList, new MealAdapter.OnItemClickListener() {
            @Override
            public void onItemClick( Meal m ) {
                /*
                Toast.makeText( getApplicationContext(),
                        "Meal Selected: " +
                                m.getName() +
                                " and calorie content is: " +
                                Integer.toString(m.getCaloriePerServing()),
                        Toast.LENGTH_SHORT ).show();
                        */
                final Meal selMeal = m;
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_meal_confirmation, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MealActivity.this)
                        .setView(layout);
                final AlertDialog dialog = builder.create();
                dialog.show();
                Button okServButton = (Button) layout.findViewById( R.id.okServButton );
                Button cancelServButton = (Button) layout.findViewById( R.id.cancelServButton );
                final SeekBar servSeekBar = (SeekBar) layout.findViewById( R.id.servSeekBar );
                final TextView servCountText = (TextView) layout.findViewById( R.id.servCountText );
                servCountText.setText( "1" );

                servSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onStartTrackingTouch( SeekBar seekBar ) {}
                    public void onStopTrackingTouch( SeekBar seekBar ) {}
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                        Log.i( "MealActivity", Integer.toString(progress+1) );
                        servCountText.setText( Integer.toString(progress+1 ) );
                        selServingSize = progress + 1;
                    }
                });

                okServButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        double totalCal = selServingSize * selMeal.getCaloriePerServing();
                        /*
                        Toast.makeText(
                                getApplicationContext(),
                                "Total calorie to add is: " +
                                    Integer.toString(selServingSize * selMeal.getCaloriePerServing()),
                                Toast.LENGTH_SHORT
                                ).show();
                                */
                        openDatabase();
                        insertCalorieInfoToDb( selMeal.getName(), selServingSize, totalCal );
                        db.close();
                        dialog.cancel();
                        finish();
                    }
                });
                cancelServButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        dialog.cancel();
                    }
                });
            }
        });
        recList.setAdapter( ma );
    }

    /*
    private List<Meal> populateMealList( String selCuisine ) {
        List<Meal> result = new ArrayList<Meal>();
        //Todo: Use DB here
        switch( selCuisine ) {
            case "American":
                result.add( new Meal("Cereal", 310) );
                result.add( new Meal("Hamburger", 270) );
                result.add( new Meal("Cheeseburger", 300) );
                result.add( new Meal("Cheese Pizza", 240) );
                result.add( new Meal("Pepperoni Pizza", 300) );
                result.add( new Meal("Hot Dog", 250) );
                break;
            case "Chinese":
                result.add( new Meal("Orange Chicken", 380) );
                result.add( new Meal("Sweet and Sour Chicken", 230) );
                result.add( new Meal("Chow Mein", 280) );
                break;
            case "Japanese":
                result.add( new Meal("Sushi Roll (California Rolls)", 240) );
                result.add( new Meal("Sushi Roll (Spicy Tuna)", 290) );
                result.add( new Meal("Sushi Roll (Salmon)", 400) );
                result.add( new Meal("Udon Noodles", 210) );
                result.add( new Meal("Miso Soup", 25) );
                result.add( new Meal("Beef Teriyaki Rice Bowl", 560) );
                break;
            case "Desert":
                result.add( new Meal("Ice Cream", 270) );
                result.add( new Meal("Pudding", 290) );
                result.add( new Meal("Chocolate Mousse", 350) );
                result.add( new Meal("Cheesecake", 250) );
                result.add( new Meal("Apple Pie", 410) );
                result.add( new Meal("Pecan Pie", 500) );
                break;
        }
        return result;
    }
    */

    private List<Meal> populateMealList( String selCuisine ) {
        List<Meal> result = new ArrayList<Meal>();
        openDatabase();
        Cursor c = db.rawQuery("select * from tblMealList where cuisine='" + selCuisine + "';", null);
        while( c.moveToNext() ) {
            String mealName = c.getString( c.getColumnIndex("meal_name") );
            double mealCalServ = c.getDouble( c.getColumnIndex("calorie_serving") );
            result.add( new Meal(mealName, mealCalServ) );
        }
        db.close();
        return result;
    }

    private void populateMealTable() {
        openDatabase();
        db.beginTransaction();
        try {
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Cereal', 'American', 310);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Hamburger', 'American', 270);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Cheeseburger', 'American', 300);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Cheese Pizza', 'American', 240);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Pepperoni Pizza', 'American', 300);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Hot Dog', 'American', 250);");

            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Orange Chicken', 'Chinese', 380);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Sweet and Sour Chicken', 'Chinese', 230);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Chow Mein', 'Chinese', 280);");

            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Sushi Roll (California Rolls)', 'Japanese', 240);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Sushi Roll (Spicy Tuna)', 'Japanese', 290);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Sushi Roll (Salmon)', 'Japanese', 400);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Udon Noodles', 'Japanese', 210);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Miso Soup', 'Japanese', 25);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Beef Teriyaki Rice Bowl', 'Japanese', 560);");

            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Ice Cream', 'Desert', 270);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Pudding', 'Desert', 290);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Chocolate Mousse', 'Desert', 350);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Cheesecake', 'Desert', 250);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Apple Pie', 'Desert', 410);");
            db.execSQL("insert into tblMealList(meal_name, cuisine, calorie_serving) values ('Pecan Pie', 'Desert', 500);");

            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            //finish();
            Log.i( "MealActivity", ex.getMessage() );
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private void openDatabase() {
        try {
            File storagePath = getApplication().getFilesDir();
            myDbPath = storagePath + "/" + "user_calorie_profile";
            db = SQLiteDatabase.openDatabase( myDbPath, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch ( SQLiteException e ) {
            Toast.makeText( getApplicationContext(),
                    "Can't open DB!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        }
    }

    private void insertCalorieInfoToDb( String mealName,
                                        int mealServingSize,
                                        double mealTotalCalorie ) {
        db.beginTransaction();
        try {
            mealName = mealName.replace( "'", "''" );
            long timeStamp = System.currentTimeMillis();
            String queryString1 =
                    "insert into tblUserMeal(timestamp, meal, serving, calorie) " +
                            "values (" +
                            Long.toString(timeStamp) +
                            ", '" +
                            mealName +
                            "', " +
                            Integer.toString(mealServingSize) +
                            ", " +
                            Double.toString(mealTotalCalorie) +
                            ");";
            String queryString2 =
                    "insert into tblUserCalorie(timestamp, calorie) " +
                            "values (" +
                            Long.toString(timeStamp) +
                            ", " +
                            Double.toString(mealTotalCalorie) +
                            ");";
            Log.i( "MealActivity", queryString1 );
            Log.i( "MealActivity", queryString2 );
            db.execSQL( queryString1 );
            db.execSQL( queryString2 );
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't insert data to DB for MealActivity!",
                    Toast.LENGTH_SHORT ).show();
            Log.i( "MealActivity", ex.getMessage() );
            finish();
        } finally {
            db.endTransaction();
        }
    }

}
