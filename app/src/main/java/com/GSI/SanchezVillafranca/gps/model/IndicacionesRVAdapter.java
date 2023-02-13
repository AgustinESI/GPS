package com.GSI.SanchezVillafranca.gps.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.GSI.SanchezVillafranca.gps.R;

import java.util.ArrayList;

public class IndicacionesRVAdapter extends RecyclerView.Adapter<IndicacionesRVAdapter.ViewHolder> {

    private Context context;

    private ArrayList<IndicacionesRVModal> indicacionesRVModals;

    public IndicacionesRVAdapter(Context context, ArrayList<IndicacionesRVModal> indicacionesRVModals) {
        this.context = context;
        this.indicacionesRVModals = indicacionesRVModals;
    }


    @NonNull
    @Override
    public IndicacionesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.indications_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicacionesRVAdapter.ViewHolder holder, int position) {
        IndicacionesRVModal modal = indicacionesRVModals.get(position);
        holder.rv_indications.setText(modal.getInstruction());
        holder.rv_icon.setImageResource(modal.getIcon());
    }

    @Override
    public int getItemCount() {
        return indicacionesRVModals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rv_indications;
        private ImageView rv_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_indications = itemView.findViewById(R.id.et_indications);
            rv_icon = itemView.findViewById(R.id.rv_icon);
        }
    }

}
