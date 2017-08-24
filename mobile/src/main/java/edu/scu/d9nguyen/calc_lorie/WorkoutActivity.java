package edu.scu.d9nguyen.calc_lorie;

import android.content.Context;
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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private RecyclerView recList;
    private WorkoutAdapter wa;
    //Todo: change to DB
    private List<Workout> workoutList;
    private int selMultiplier = 1; //starts at one minute or mile
    private SQLiteDatabase db;
    private String myDbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        recList = (RecyclerView) findViewById( R.id.workoutList );
        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation( LinearLayoutManager.VERTICAL );
        recList.setLayoutManager( llm );

        populateWorkoutTable();
        workoutList = populateWorkoutList();

        Log.i( "WorkoutActivity", Integer.toString( workoutList.size() ) );

        wa = new WorkoutAdapter( workoutList, new WorkoutAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Workout w) {
                final Workout selWorkout = w;
                final String workoutType = w.getType();
                View layout = null;
                final AlertDialog dialog;
                //change layout depending on workout type
                if (workoutType.equals("Duration")) {
                    //set up dialog for duration-based workout
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = inflater.inflate(R.layout.activity_workout_duration_confirmation, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this)
                            .setView(layout);
                    dialog = builder.create();
                    dialog.show();
                    Button okWorkoutButton = (Button) layout.findViewById( R.id.okWorkoutButton );
                    Button cancelWorkoutButton = (Button) layout.findViewById( R.id.cancelWorkoutButton );
                    final SeekBar durSeekBar = (SeekBar) layout.findViewById( R.id.durSeekBar );
                    final TextView durCountText = (TextView) layout.findViewById( R.id.durCountText );
                    durCountText.setText( "1" );
                    //set seekbar action listener
                    durSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        public void onStartTrackingTouch(SeekBar seekBar) {}
                        public void onStopTrackingTouch(SeekBar seekBar) {}
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            Log.i("WorkoutActivity", Integer.toString(progress + 1));
                            durCountText.setText( Integer.toString(progress + 1) );
                            selMultiplier = progress + 1;
                        }
                    });
                    okWorkoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double totalCal = selMultiplier * selWorkout.getCalorieRating();
                            openDatabase();
                            insertCalorieInfoToDb( selWorkout.getName(), selMultiplier, totalCal );
                            db.close();
                            dialog.cancel();
                            finish();
                        }
                    });
                    cancelWorkoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                } else if (workoutType.equals("Distance")) {
                    //set up dialog for distance-based workout
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    layout = inflater.inflate(R.layout.activity_workout_distance_confirmation, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutActivity.this)
                            .setView(layout);
                    dialog = builder.create();
                    dialog.show();
                    Button okWorkoutButton = (Button) layout.findViewById( R.id.okWorkoutButton );
                    Button cancelWorkoutButton = (Button) layout.findViewById( R.id.cancelWorkoutButton );
                    final SeekBar distSeekBar = (SeekBar) layout.findViewById( R.id.distSeekBar );
                    final TextView distCountText = (TextView) layout.findViewById( R.id.distCountText );
                    distCountText.setText( "1" );
                    //set seekbar action listener
                    distSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        public void onStartTrackingTouch(SeekBar seekBar) {}
                        public void onStopTrackingTouch(SeekBar seekBar) {}
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            Log.i("WorkoutActivity", Integer.toString(progress + 1));
                            distCountText.setText( Integer.toString(progress + 1) );
                            selMultiplier = progress + 1;
                        }
                    });
                    okWorkoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double totalCal = selMultiplier * selWorkout.getCalorieRating();
                            openDatabase();
                            insertCalorieInfoToDb( selWorkout.getName(), selMultiplier, totalCal );
                            db.close();
                            dialog.cancel();
                            finish();
                        }
                    });
                    cancelWorkoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }
            }
        });
        recList.setAdapter( wa );

    }

    /*
    private List<Workout> populateWorkoutList() {
        List<Workout> result = new ArrayList<Workout>();
        //Todo: Use DB here
        result.add( new Workout("Cycling", "Distance", -700) );
        result.add( new Workout("Walking", "Distance", -300) );
        result.add( new Workout("Running", "Distance", -900) );
        result.add( new Workout("Weight-Lifting", "Duration", -50) );
        result.add( new Workout("Bench-Pressing", "Duration", -40) );
        return result;
    }
    */

    private void populateWorkoutTable() {
        openDatabase();
        db.beginTransaction();
        try {
            db.execSQL("insert into tblWorkoutList(workout_name, type, calorie_burn) values ('Cycling', 'Distance', -700);");
            db.execSQL("insert into tblWorkoutList(workout_name, type, calorie_burn) values ('Walking', 'Distance', -300);");
            db.execSQL("insert into tblWorkoutList(workout_name, type, calorie_burn) values ('Running', 'Distance', -900);");
            db.execSQL("insert into tblWorkoutList(workout_name, type, calorie_burn) values ('Weight-Lifting', 'Duration', -50);");
            db.execSQL("insert into tblWorkoutList(workout_name, type, calorie_burn) values ('Bench-Pressing', 'Duration', -40);");
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            //finish();
            Log.i( "WorkoutActivity", ex.getMessage() );
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private List<Workout> populateWorkoutList() {
        List<Workout> result = new ArrayList<Workout>();
        openDatabase();
        Cursor c = db.rawQuery("select * from tblWorkoutList;", null);
        while( c.moveToNext() ) {
            String workoutName = c.getString( c.getColumnIndex("workout_name") );
            String workoutType = c.getString( c.getColumnIndex("type") );
            double workoutCalBurn = c.getDouble( c.getColumnIndex("calorie_burn") );
            result.add( new Workout(workoutName, workoutType, workoutCalBurn) );
        }
        db.close();
        return result;

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

    private void insertCalorieInfoToDb( String workoutName,
                                        int workoutMultiplier,
                                        double workoutTotalCalorie ) {
        db.beginTransaction();
        try {
            workoutName = workoutName.replace( "'", "''" );
            long timeStamp = System.currentTimeMillis();
            String queryString1 =
                    "insert into tblUserWorkout(timestamp, workout, multiplier, calorie) " +
                            "values (" +
                            Long.toString(timeStamp) +
                            ", '" +
                            workoutName +
                            "', " +
                            Integer.toString(workoutMultiplier) +
                            ", " +
                            Double.toString(workoutTotalCalorie) +
                            ");";
            String queryString2 =
                    "insert into tblUserCalorie(timestamp, calorie) " +
                            "values (" +
                            Long.toString(timeStamp) +
                            ", " +
                            Double.toString(workoutTotalCalorie) +
                            ");";
            Log.i( "WorkoutActivity", queryString1 );
            Log.i( "WorkoutActivity", queryString2 );
            db.execSQL( queryString1 );
            db.execSQL( queryString2 );
            db.setTransactionSuccessful();
        } catch( SQLException ex ) {
            Toast.makeText( getApplicationContext(),
                    "Can't insert data to DB for WorkoutActivity!",
                    Toast.LENGTH_SHORT ).show();
            Log.i( "WorkoutActivity", ex.getMessage() );
            finish();
        } finally {
            db.endTransaction();
        }
    }


}
