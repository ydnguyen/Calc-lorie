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
public class CuisineAdapter extends RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>{

    private List<Cuisine> cuisineList;
    private OnItemClickListener listener;

    public static class CuisineViewHolder extends RecyclerView.ViewHolder {

        protected TextView vCuisine;
        protected View myView;

        public CuisineViewHolder(View v) {
            super(v);
            myView = v;
            vCuisine = (TextView) v.findViewById(R.id.cuisineText);
        }

        public void bind(final Cuisine c, final OnItemClickListener listener) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( c );
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick( Cuisine c );
    }

    public CuisineAdapter( List<Cuisine> cuisineList, OnItemClickListener listener ) {
        this.cuisineList = cuisineList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() { return cuisineList.size(); }

    @Override
    public void onBindViewHolder( CuisineViewHolder holder, int i ) {
        Cuisine c = cuisineList.get(i);
        holder.bind( c, listener );
        holder.vCuisine.setText( c.getName() );
    }

    @Override
    public CuisineViewHolder onCreateViewHolder(ViewGroup viewGroup, int i ) {
        View itemView = LayoutInflater
                .from( viewGroup.getContext() )
                .inflate( R.layout.cuisine_item_layout, viewGroup, false );
        return new CuisineViewHolder( itemView );
    }


}
