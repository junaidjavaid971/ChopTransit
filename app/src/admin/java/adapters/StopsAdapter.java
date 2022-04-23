package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.choptransit.R;
import app.com.choptransit.models.response.Stop;


public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder> {
    ArrayList<Stop> arrayList;
    ArrayList<Stop> tempList;
    Context context;
    StopButtonCallback callback;

    public StopsAdapter(ArrayList<Stop> arrayList, Context context, StopButtonCallback callback) {
        this.arrayList = arrayList;
        this.context = context;
        this.callback = callback;
    }

    public void update(ArrayList<Stop> tempList) {
        this.arrayList.clear();
        arrayList.addAll(tempList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        if (tempList == null) {
            tempList = new ArrayList<>(arrayList);
        }
        if (text.isEmpty()) {
            arrayList.clear();
            arrayList.addAll(tempList);
            tempList = null;
        } else {
            ArrayList<Stop> result = new ArrayList<>();
            text = text.toLowerCase();
            //after clearing the array again you are using same array to find the items from
            for (Stop stop : tempList) {
                if (stop.getStopName().toLowerCase().contains(text)) {
                    result.add(stop);
                }
            }
            //you have cleared all the contains here
            arrayList.clear();
            // and added only result related items here
            arrayList.addAll(result);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_item_stops, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stop stop = arrayList.get(position);
        holder.stopName.setText(stop.getStopName());

        holder.itemView.setOnClickListener(v -> {
            if (holder.layoutControls.getVisibility() == View.GONE) {
                holder.layoutControls.setVisibility(View.VISIBLE);
            } else {
                holder.layoutControls.setVisibility(View.GONE);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            callback.onDeleteClicked(stop);
        });

        holder.ivEdit.setOnClickListener(v -> {
            callback.onEditClicked(stop);
        });
        holder.ivDirections.setOnClickListener(v -> {
            callback.onViewClicked(stop);
        });
    }

    public interface StopButtonCallback {
        void onDeleteClicked(Stop stop);

        void onEditClicked(Stop stop);

        void onViewClicked(Stop stop);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stopName;
        ImageView ivDelete, ivEdit, ivDirections, ivArrow;
        ConstraintLayout layoutControls;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(R.id.tvStopName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDirections = itemView.findViewById(R.id.ivDirections);
            layoutControls = itemView.findViewById(R.id.controlsLayout);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}
