package edu.scu.d9nguyen.calc_lorie;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private String myDbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            openDatabase();
            //dropTables();
            createMealTable();
            createWorkoutTable();
            createCalorieTable();
            createMealListTable();
            createWorkoutListTable();
            db.close();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Error processing DB in onCreate()",
                    Toast.LENGTH_SHORT );
        }

        ImageButton mealButton = (ImageButton) findViewById( R.id.mealButton );
        mealButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent( MainActivity.this, CuisineActivity.class );
                startActivity( intent );
            }
        });

        ImageButton calorieInfoButton = (ImageButton) findViewById( R.id.calorieInfoButton );
        calorieInfoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent( MainActivity.this, CalorieInfoActivity.class );
                startActivity( intent );
            }
        });

        ImageButton workoutButton = (ImageButton) findViewById( R.id.workoutButton );
        workoutButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent( MainActivity.this, WorkoutActivity.class );
                startActivity( intent );
            }
        });

        ImageButton barcodeButton = (ImageButton) findViewById( R.id.barcodeButton );
        barcodeButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent( MainActivity.this, BarcodeActivity.class );
                startActivity( intent );
            }
        });

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

    private void createMealTable() {
        db.beginTransaction();
        try {
            db.execSQL("create table if not exists tblUserMeal (" +
                    " timestamp integer PRIMARY KEY, " +
                    "meal text, " +
                    "serving integer, " +
                    "calorie real );  ");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't create DB table tblUserMeal!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void createWorkoutTable() {
        db.beginTransaction();
        try {
            db.execSQL("create table if not exists tblUserWorkout (" +
                    " timestamp integer PRIMARY KEY, " +
                    "workout text, " +
                    "multiplier integer, " +
                    "calorie real );  ");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't create DB table tblUserWorkout!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void createCalorieTable() {
        db.beginTransaction();
        try {
            db.execSQL("create table if not exists tblUserCalorie (" +
                    " timestamp integer PRIMARY KEY, " +
                    "calorie real );  ");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't create DB table tblUserWorkout!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void createMealListTable() {
        db.beginTransaction();
        try {
            db.execSQL("create table if not exists tblMealList (" +
                    //" mid integer PRIMARY KEY autoincrement, " +
                    " meal_name text PRIMARY KEY, " +
                    "cuisine text, " +
                    "calorie_serving integer " +
                    ");");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't create DB table tblMealList!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void createWorkoutListTable() {
        db.beginTransaction();
        try {
            db.execSQL("create table if not exists tblWorkoutList (" +
                    //" wid integer PRIMARY KEY autoincrement, " +
                    " workout_name text PRIMARY KEY, " +
                    "calorie_burn integer, " +
                    "type text " +
                    ");");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't create DB table tblWorkoutList!",
                    Toast.LENGTH_SHORT ).show();
            finish();
        } finally {
            db.endTransaction();
        }
    }

    private void dropTables() {
        try {
            db.execSQL("DROP TABLE IF EXISTS tblUserMeal;");
            db.execSQL("DROP TABLE IF EXISTS tblUserWorkout;");
            db.execSQL("DROP TABLE IF EXISTS tblUserCalorie;");
            db.execSQL("DROP TABLE IF EXISTS tblMealList;");
            db.execSQL("DROP TABLE IF EXISTS tblWorkoutList;");
        }  catch (Exception e) {
            finish();
        }
    }

}
