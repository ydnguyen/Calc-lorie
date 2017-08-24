package edu.scu.d9nguyen.calc_lorie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tritium on 5/22/2016.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    private OnItemClickListener listener;

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        protected TextView vWorkout;
        protected View myView;

        public WorkoutViewHolder(View v) {
            super(v);
            myView = v;
            vWorkout = (TextView) v.findViewById(R.id.workoutText);
        }

        public void bind(final Workout w, final OnItemClickListener listener) {
            myView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick( w );
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Workout w);
    }

    public WorkoutAdapter( List<Workout> workoutList, OnItemClickListener listener ) {
        this.workoutList = workoutList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() { return workoutList.size(); }

    @Override
    public void onBindViewHolder( WorkoutViewHolder holder, int i ) {
        Workout w = workoutList.get(i);
        holder.bind( w, listener );
        holder.vWorkout.setText( w.getName() );
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int i ) {
        View itemView = LayoutInflater
                .from( viewGroup.getContext() )
                .inflate( R.layout.workout_item_layout, viewGroup, false );
        return new WorkoutViewHolder( itemView );
    }


}
