package tech.beepbeep.beept05;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChargerListAdapter extends RecyclerView.Adapter<ChargerListAdapter.ViewHolder> {

    public static final String TAG = "ChargerListAdapter";
    public Context context;
    public static List<ChargerObject> listOfChargers;
    public ClickListener clickListener;

    // data is passed into the constructor
    ChargerListAdapter(Context context, List<ChargerObject> listOfChargers, ClickListener clickListener) {
        this.context = context;
        this.listOfChargers = listOfChargers;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View chargerView = inflater.inflate(R.layout.charger_unit, parent, false);

        return new ViewHolder(chargerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil((float) listOfChargers.size() / 2);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
        public void bind(int position) {
            Log.i(TAG, "Position " + position);

            int firstHalf = position * 2;
            int secondHalf = firstHalf + 1;

            TextView textView1 = itemView.findViewById(R.id.textView1);
            TextView textView11 = itemView.findViewById(R.id.textView11);
            TextView textView2 = itemView.findViewById(R.id.textView2);
            TextView textView22 = itemView.findViewById(R.id.textView22);

            CardView cardView1 = itemView.findViewById(R.id.chargerView1);
            CardView cardView2 = itemView.findViewById(R.id.chargerView2);
            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "it be working, im position: " + firstHalf);
                }
            });
            cardView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "it be working, im position: " + secondHalf);
                }
            });

            textView1.setText(listOfChargers.get(firstHalf).getChargerName());
            textView11.setText(listOfChargers.get(firstHalf).getChargerPower());

            if (secondHalf < listOfChargers.size()) {
                textView2.setText(listOfChargers.get(secondHalf).getChargerName());
                textView22.setText(listOfChargers.get(secondHalf).getChargerPower());
            } else {
                itemView.findViewById(R.id.chargerView2).setVisibility(View.GONE);
                itemView.findViewById(R.id.space).setVisibility(View.GONE);
            }

        }
    }

}
