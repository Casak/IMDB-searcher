package ru.casak.IMDB_searcher;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private static final String TAG = "CardsAdapter";
    static List<String> dataList = new ArrayList<String>();

    public CardsAdapter(){
        for(int i = 0; i<10; i++)
            dataList.add("Title #"+i);
    }

    @Override
    public int getItemCount(){
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.getTextView().setText(dataList.get(position));

        Log.d(TAG, "Element " + position + " set.");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View cardLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.film_card, null);
        return new ViewHolder(cardLayoutView);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView)view.findViewById(R.id.title);
        }

        public TextView getTextView(){
            return textView;
        }
    }
}
