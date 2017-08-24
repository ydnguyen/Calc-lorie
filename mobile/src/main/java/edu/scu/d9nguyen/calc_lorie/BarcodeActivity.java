package edu.scu.d9nguyen.calc_lorie;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class BarcodeActivity extends AppCompatActivity {

    final private int reqCode = 1234;
    //private String myBarcode = "096619734191";
    //private String myBarcode = "017082877284";
    private String myBarcode;
    private String myFoodBarcodeName = "";
    private double myCalPerServ = 0;
    private int selServingSize = 1; //starts at one serving

    private SQLiteDatabase db;
    private String myDbPath;

    //private boolean showAddBarcode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        Button barcodeButton = (Button) findViewById( R.id.startBarcodeButton );
        barcodeButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult( intent, reqCode );
            }
        });


        /*
        String stringUrl = new String( "https://api.outpan.com/v2/products/" + myBarcode );

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if( (networkInfo != null) && networkInfo.isConnected() ) {
            new ConnectionTask().execute( stringUrl );
        } else {
            Toast.makeText( getApplicationContext(),
                    "Network connectivity not available",
                    Toast.LENGTH_SHORT ).show();
        }
        */
        Button confirmButton = (Button) findViewById( R.id.confirmBarcodeButton );
        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                inflateServingLayout();
            }
        });

    }

    @Override
    public void onActivityResult( int reqCode, int resultCode, Intent intent ) {
        if( reqCode == this.reqCode ) {
            if( resultCode == RESULT_OK ) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                TextView barcodeContentText = (TextView) findViewById( R.id.barcodeText );
                barcodeContentText.setText( contents );
                myBarcode = contents;
            }
        }

        //04963406 convert to 049000006346
        if( myBarcode.length() == 8 ) {
            myBarcode = myBarcode.substring(0,3) +
                    "00000" +
                    myBarcode.substring(3, myBarcode.length()-2) +
                    myBarcode.charAt(myBarcode.length()-1);
        }
        Log.i( "BarcodeActivity", myBarcode );
        String stringUrl = new String( "https://api.outpan.com/v2/products/" + myBarcode );

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if( (networkInfo != null) && networkInfo.isConnected() ) {
            new ConnectionTask().execute( stringUrl );
        } else {
            Toast.makeText( getApplicationContext(),
                    "Network connectivity not available",
                    Toast.LENGTH_SHORT ).show();
        }

        /*
        if( showAddBarcode ) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_meal_confirmation, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this)
                    .setView(layout);
            final AlertDialog dialog = builder.create();
            dialog.show();
            Button okServButton = (Button) layout.findViewById( R.id.okServButton );
            Button cancelServButton = (Button) layout.findViewById( R.id.cancelServButton );
            cancelServButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    dialog.cancel();
                }
            });

            showAddBarcode = false;
        }
        */

    }

    private void inflateServingLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_meal_confirmation, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this)
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
                double totalCal = selServingSize * myCalPerServ;
                        /*
                        Toast.makeText(
                                getApplicationContext(),
                                "Total calorie to add is: " +
                                    Integer.toString(selServingSize * selMeal.getCaloriePerServing()),
                                Toast.LENGTH_SHORT
                                ).show();
                                */
                openDatabase();
                insertCalorieInfoToDb( myFoodBarcodeName, selServingSize, totalCal );
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


    private class ConnectionTask extends AsyncTask<String, Void, String> {

        Exception myException;
        AlertDialog alertDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(BarcodeActivity.this).create();
        }

        @Override
        protected String doInBackground( String... urls ) {
            String result  = "";
            try {
                URL url = new URL( urls[0] + "?apikey=" + "26983b40a60057b8ebe7a73120ea69fa" );
                Log.i( "ConnectionTask", url.toString() );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod( "GET" );
                conn.setDoInput( true );
                /*
                String key = "26983b40a60057b8ebe7a73120ea69fa" + ":";
                String authEncBytes = new String(Base64.encode(key.getBytes(), Base64.NO_WRAP));
                Log.i( "ConnectionTask", "Setting up connection" );
                conn.setRequestProperty("Authorization", "Basic " + authEncBytes);
                Log.i( "ConnectionTask", "Basic " + authEncBytes );
                */

                conn.connect();
                InputStream in = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);

                char[] charArray = new char[1024];
                //StringBuffer sb = new StringBuffer();
                isr.read( charArray );
                String jsonStr = new String( charArray );
                JSONObject obj = new JSONObject( jsonStr );
                //Log.i( "ConnectionTask", "Printing result of GET" );
                //Log.i( "ConnectionTask", obj.toString() );
                result = obj.getString("name") + "\n\n";
                myFoodBarcodeName = obj.getString("name");
                JSONObject attr = obj.getJSONObject("attributes");
                Iterator iter = attr.keys();
                while( iter.hasNext() ) {
                    String key = (String) iter.next();
                    if( key.equals("Calorie/Serving") ) {
                        String calPerServ = attr.getString( "Calorie/Serving" );
                        result = result + calPerServ + "\nCal/Serv";
                        myCalPerServ = Double.parseDouble( calPerServ );
                    }
                }
                /*
                for( int i = 0; i<attrArray.length(); ++i ) {
                    JSONObject j = attrArray.getJSONObject(i);
                    result = j.getString("Calorie/Serving");
                }
                while ((numCharsRead = isr.read(charArray)) > 0) {
                    sb.append(charArray, 0, numCharsRead);
                    Log.i( "BarcodeActivity", charArray.toString() );
                }
                */
                conn.disconnect();
            } catch( MalformedURLException ex ) {
                Log.i( "ConnectionTask", ex.getMessage() );
                myException = ex;
                return result;
            } catch( IOException ex ) {
                Log.i( "ConnectionTask", ex.getMessage() );
                myException = ex;
                return result;
            } catch( JSONException ex ) {
                Log.i( "ConnectionTask", ex.getMessage() );
                myException = ex;
                return result;
            }

            return result;

        }

        @Override
        protected void onPostExecute( String result ) {
            /*
            if( !result.equals("") ) {
                TextView barcodeText = (TextView) findViewById(R.id.barcodeText);
                barcodeText.setText( result );
            } else {
                TextView barcodeText = (TextView) findViewById(R.id.barcodeText);
                //ViewGroup parent = (ViewGroup) barcodeText.getParent();
                //parent.removeView( barcodeText );
                barcodeText.setText( result );
                Toast.makeText( getApplicationContext(),
                        "Barcode not found",
                        Toast.LENGTH_SHORT).show();
            }
            */
            super.onPostExecute( result );
            if( myException != null ) {
                Log.i( "ConnectionTask", "Exception caught!" );
                TextView barcodeText = (TextView) findViewById(R.id.barcodeText);
                barcodeText.setText( "" );
                //showAddBarcode = true;
                /*
                alertDialog.setTitle("Barcode doesn't exist in database");
                alertDialog.setCanceledOnTouchOutside( true );
                alertDialog.show();
                */
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_barcode_add, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeActivity.this)
                        .setView(layout);
                alertDialog = builder.create();
                alertDialog.setTitle("Barcode doesn't exist in database");
                alertDialog.setCanceledOnTouchOutside( true );
                Button okButton = (Button) layout.findViewById( R.id.okBarcodeButton );
                Button cancelButton = (Button) layout.findViewById( R.id.cancelBarcodeButton );
                final EditText itemText = (EditText) layout.findViewById( R.id.itemEditText );
                final EditText calServText = (EditText) layout.findViewById( R.id.calServEditText );

                okButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        String itemName = itemText.getText().toString();
                        String calServVal = calServText.getText().toString();
                        if( (!itemName.equals("")) &&
                                (!calServVal.equals("")) ) {
                            try {
                                Double.parseDouble( calServVal );

                                //String stringUrl = new String( "https://api.outpan.com/v2/products/" + myBarcode );
                                String[] params = { myBarcode, itemName, calServVal };
                                new PostBarcodeTask().execute( params );
                                alertDialog.cancel();
                            } catch( NumberFormatException ex ) {
                                alertDialog.cancel();
                            }
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.show();

            } else {
                TextView barcodeText = (TextView) findViewById(R.id.barcodeText);
                barcodeText.setText(result);
            }
        }

    }

    //Todo: Finish Post Request to outpan.com
    private class PostBarcodeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                /*
                URL url = new URL(params[0] + "?apikey=" + "26983b40a60057b8ebe7a73120ea69fa");
                Log.i("ConnectionTask", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                */
                String barcode = params[0];
                String itemName = params[1];
                String calServVal = params[2];
                URL url = new URL( "https://api.outpan.com/v2/products/" +
                        barcode +
                        "/name" +
                        "?apikey=" + "26983b40a60057b8ebe7a73120ea69fa" );
                Log.i( "PostBarcodeTask", url.toString() );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput( true );
                conn.setDoOutput( true );
                conn.setRequestProperty("name", itemName);
                conn.connect();
                conn.disconnect();

                URL url2 = new URL( "https://api.outpan.com/v2/products/" +
                        barcode +
                        "/attribute" +
                        "?apikey=" + "26983b40a60057b8ebe7a73120ea69fa" );
                Log.i( "PostBarcodeTask", url2.toString() );
                conn = (HttpURLConnection) url2.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("name", "Calorie/Serving");
                conn.setRequestProperty("value", calServVal);
                conn.connect();
                conn.disconnect();

            } catch (MalformedURLException ex) {
                Log.i("ConnectionTask", ex.getMessage());
                return "Failure";
            } catch (IOException ex) {
                Log.i("ConnectionTask", ex.getMessage());
                return "Failure";
            }

            return "Success";

        }

        @Override
        protected void onPostExecute( String result ) {
            if( result.equals("Success") ) {
                Toast.makeText( getApplicationContext(),
                        "Successfully added item to database",
                        Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( getApplicationContext(),
                        "Failed to add item to database",
                        Toast.LENGTH_SHORT ).show();
            }
        }
    }

}
