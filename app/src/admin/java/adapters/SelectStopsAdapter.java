package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.choptransit.R;
import app.com.choptransit.models.response.Stop;


public class SelectStopsAdapter extends RecyclerView.Adapter<SelectStopsAdapter.ViewHolder> {
    ArrayList<Stop> arrayList;
    ArrayList<Stop> checkedStops;
    Context context;
    StopSelectedCallback callback;

    public SelectStopsAdapter(ArrayList<Stop> arrayList, ArrayList<Stop> checkedStops, Context context, StopSelectedCallback callback) {
        this.arrayList = arrayList;
        this.context = context;
        this.callback = callback;
        this.checkedStops = checkedStops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_item_select_stops, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stop stop = arrayList.get(position);
        holder.stopName.setText(stop.getStopName());
        holder.cbSelectStop.setVisibility(View.VISIBLE);

        for (Stop checkedStop : checkedStops) {
            if (checkedStop.getId().equals(stop.getId())) {
                holder.cbSelectStop.setChecked(true);
                break;
            }
        }

        holder.cbSelectStop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                holder.tvOrderNo.setVisibility(View.VISIBLE);
            }
            callback.onStopSelected(stop, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stopName, tvOrderNo;
        CheckBox cbSelectStop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(R.id.tvStopName);
            cbSelectStop = itemView.findViewById(R.id.cbSelectStop);
            tvOrderNo = itemView.findViewById(R.id.tvOrderNo);
        }
    }

    public interface StopSelectedCallback {
        void onStopSelected(Stop stop, boolean isChecked);
    }
}
