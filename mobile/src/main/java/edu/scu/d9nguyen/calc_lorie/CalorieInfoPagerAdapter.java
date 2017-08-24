package edu.scu.d9nguyen.calc_lorie;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tritium on 5/26/2016.
 */
public class CalorieInfoPagerAdapter extends PagerAdapter {

    private Context myContext;
    private final List<String> myGraphs;

    private ArrayList<DataPoint> calorieData;
    private ArrayList<String> timeData;
    private ArrayList<Double> weekDayCalorieData;
    private ArrayList<String> weekDayData;

    private String myDbPath;
    private SQLiteDatabase db;


    public CalorieInfoPagerAdapter(Context context, List<String> calorieInfoGraphs) {
        myContext = context;
        myGraphs = calorieInfoGraphs;
    }

    public void setDbPath( String dbPath ) {
        myDbPath = dbPath;
    }

    @Override
    public int getCount() {
        return myGraphs.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate( R.layout.calorie_page_layout, null );

        GraphView graphView = (GraphView) view.findViewById( R.id.calorieGraphImage );
        TextView calorieText = (TextView) view.findViewById( R.id.calorieCountText );

        if( myGraphs.get(position).equals("Daily") ) {
            //put daily calorie graph here
            double currentCal = getCurrentDailyCalorieCount();
            LineGraphSeries<DataPoint> line_series = new LineGraphSeries<DataPoint>( );
            if( !calorieData.isEmpty() ) {
                DataPoint[] calorieDataArray = new DataPoint[calorieData.size()];
                calorieDataArray = calorieData.toArray(calorieDataArray);

                for( int i = 0; i < calorieData.size(); ++i ) {
                    Log.i( "CalorieInfoActivity", calorieDataArray[i].toString() );
                }

                line_series = new LineGraphSeries<DataPoint>(calorieDataArray);
            }
            graphView.addSeries(line_series);
            line_series.setThickness( 10 );
            graphView.getViewport().setMinX( 0.0 );
            graphView.getViewport().setMaxX( 24.0 );
            graphView.getViewport().setXAxisBoundsManual( true );
            graphView.getViewport().setScrollable( false );
            final View dailyView = view;
            line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText( dailyView.getContext(), "Series: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
                }
            });
            calorieText.setText( "Current calorie count:\n\n" + currentCal + "\nCalorie" );

        } else if( myGraphs.get(position).equals("Weekly") ) {
            //put weekly calorie graph here
            getWeeklyCalorie();
            BarGraphSeries<DataPoint> barGraphSeries = new BarGraphSeries<DataPoint>(
                    new DataPoint[] {
                            new DataPoint( 0, weekDayCalorieData.get(0) ),
                            new DataPoint( 1, weekDayCalorieData.get(1) ),
                            new DataPoint( 2, weekDayCalorieData.get(2) ),
                            new DataPoint( 3, weekDayCalorieData.get(3) ),
                            new DataPoint( 4, weekDayCalorieData.get(4) ),
                            new DataPoint( 5, weekDayCalorieData.get(5) ),
                            new DataPoint( 6, weekDayCalorieData.get(6) )
                    } );
            barGraphSeries.setSpacing( 50 );
            barGraphSeries.setDrawValuesOnTop( true );
            barGraphSeries.setValuesOnTopColor( Color.DKGRAY );
            graphView.getViewport().setScrollable( false );
            graphView.removeAllSeries();
            graphView.getGridLabelRenderer().resetStyles();
            graphView.addSeries( barGraphSeries );
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
            //staticLabelsFormatter.setHorizontalLabels( new String[] { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" } );
            String[] weekDayDataArray = new String[ weekDayData.size() ];
            weekDayDataArray = weekDayData.toArray( weekDayDataArray );
            staticLabelsFormatter.setHorizontalLabels( weekDayDataArray );
            graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            ViewGroup parent = (ViewGroup) graphView.getParent();
            parent.removeView( calorieText );
        }

        container.addView( view, 0 );
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return myGraphs.get(position);
    }

    private double getCurrentDailyCalorieCount() {
        double result = 0;
        //get today's date range in miliseconds
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );
        long todayInMili = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long tomorrowInMili = cal.getTimeInMillis();

        calorieData = new ArrayList<DataPoint>();
        calorieData.add( new DataPoint(0,0) );

        //timeData = new ArrayList<String>();
        //timeData.add("0:00:00");

        openDatabase();
        //build query
        String query = "Select * from tblUserCalorie where timestamp > " +
                Long.toString(todayInMili) +
                " AND timestamp < " +
                Long.toString(tomorrowInMili) +
                ";";
        try {
            Cursor c = db.rawQuery(query, null);
            //int counter = 1;
            while (c.moveToNext()) {
                double calorie = c.getDouble(c.getColumnIndex("calorie"));
                result += calorie;
                //calorieData.add( new DataPoint(counter, result) );
                //++counter;

                long timeStampMili = c.getLong(c.getColumnIndex("timestamp"));
                Date date = new Date( timeStampMili );
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                String formatted = formatter.format( date );
                int hr = Integer.parseInt( formatted.substring(0, formatted.indexOf(":")) );
                int min = Integer.parseInt( formatted.substring(formatted.indexOf(":") + 1, formatted.lastIndexOf(":")) );
                int sec = Integer.parseInt( formatted.substring(formatted.lastIndexOf(":") + 1, formatted.length()) );
                double numMinInHr = 60;
                double numSecInHr = 3600;
                double timeDouble = hr + (min / numMinInHr) + (sec / numSecInHr);

                Log.i( "CalorieInfoActivity", formatted + " " +
                        Integer.toString(hr) + " " +
                        Integer.toString(min) + " " +
                        Double.toString(timeDouble) );
                //timeData.add( formatted );
                calorieData.add( new DataPoint(timeDouble, result) );
            }
            db.close();
        } catch( SQLException ex ) {
        }
        return result;
    }

    private void getWeeklyCalorie() {
        //initialize arraylists
        weekDayCalorieData = new ArrayList<Double>();
        weekDayData = new ArrayList<String>();

        //get today's timestamp
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );
        /*
        long todayInMili = cal.getTimeInMillis();
        long tomorrowInMili = cal.getTimeInMillis();
        */
        cal.add(Calendar.DAY_OF_MONTH, -6);
        long todayInMili = cal.getTimeInMillis();

        //query database for calorie count each day
        for( int i = 0; i < 7; ++i ) {
            weekDayData.add(cal.getDisplayName( Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US ));
            cal.add(Calendar.DAY_OF_MONTH, 1);
            long tomorrowInMili = cal.getTimeInMillis();
            double result = 0;
            openDatabase();
            //build query
            String query = "Select * from tblUserCalorie where timestamp > " +
                    Long.toString(todayInMili) +
                    " AND timestamp < " +
                    Long.toString(tomorrowInMili) +
                    ";";
            try {
                Cursor c = db.rawQuery(query, null);
                while (c.moveToNext()) {
                    double calorie = c.getDouble(c.getColumnIndex("calorie"));
                    result += calorie;
                }
            } catch( Exception ex ) {}


            weekDayCalorieData.add( result );
            db.close();
            todayInMili = tomorrowInMili;
        }

    }

    private void openDatabase() {
        try {
            db = SQLiteDatabase.openDatabase( myDbPath, null,
                    SQLiteDatabase.CREATE_IF_NECESSARY);
        } catch ( SQLiteException e ) {
            Log.i( "CalorieInfoPagerAdapter", e.getMessage() );
        }
    }

}