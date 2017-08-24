package edu.scu.d9nguyen.calc_lorie;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalorieInfoActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private String myDbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_info);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        List<String> calorieInfoTypes = new ArrayList<String>();
        calorieInfoTypes.add("Daily");
        calorieInfoTypes.add("Weekly");

        //initializing myDbPath in a round-about way
        openDatabase();
        db.close();
        CalorieInfoPagerAdapter cipa = new CalorieInfoPagerAdapter(getApplicationContext(), calorieInfoTypes);
        cipa.setDbPath(myDbPath);

        viewPager.setAdapter(cipa);

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

}
