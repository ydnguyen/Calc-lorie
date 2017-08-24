package edu.scu.d9nguyen.calc_lorie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class CuisineActivity extends AppCompatActivity {

    private RecyclerView recList;
    private CuisineAdapter ca;
    //Todo: change to DB
    //private List<String> cuisineList = Arrays.asList( "American", "Chinese", "French" );
    private List<Cuisine> cuisineList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);
        recList = (RecyclerView) findViewById( R.id.cuisineList );
        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation( LinearLayoutManager.VERTICAL );
        recList.setLayoutManager( llm );

        cuisineList = new ArrayList<Cuisine>();
        cuisineList.add( new Cuisine("American") );
        cuisineList.add( new Cuisine("Chinese") );
        cuisineList.add( new Cuisine("Japanese") );
        cuisineList.add( new Cuisine("Desert") );

        Log.i( "CuisineActivity", Integer.toString( cuisineList.size() ) );

        ca = new CuisineAdapter( cuisineList, new CuisineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick( Cuisine c ) {
                Intent mealIntent = new Intent( CuisineActivity.this, MealActivity.class );
                mealIntent.putExtra( "cuisine", c.getName() );
                startActivity( mealIntent );
            }
        });
        recList.setAdapter( ca );

    }

}
