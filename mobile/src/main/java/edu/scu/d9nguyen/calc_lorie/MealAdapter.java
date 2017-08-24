package edu.scu.d9nguyen.calc_lorie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tritium on 5/21/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> mealList;
    private OnItemClickListener listener;

    public static class MealViewHolder extends RecyclerView.ViewHolder {

        protected TextView vMeal;
        protected View myView;

        public MealViewHolder(View v) {
            super(v);
            myView = v;
            vMeal = (TextView) v.findViewById(R.id.mealText);
        }

        public void bind(final Meal m, final OnItemClickListener listener) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( m );
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Meal m);
    }

    public MealAdapter( List<Meal> mealList, OnItemClickListener listener ) {
        this.mealList = mealList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() { return mealList.size(); }

    @Override
    public void onBindViewHolder( MealViewHolder holder, int i ) {
        Meal m = mealList.get(i);
        holder.bind( m, listener );
        holder.vMeal.setText( m.getName() );
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i ) {
        View itemView = LayoutInflater
                .from( viewGroup.getContext() )
                .inflate( R.layout.meal_item_layout, viewGroup, false );
        return new MealViewHolder( itemView );
    }


}
