package com.GSI.SanchezVillafranca.gps.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.GSI.SanchezVillafranca.gps.R;

import java.util.ArrayList;

public class GasStationRVAdapter extends RecyclerView.Adapter<GasStationRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GasStationRVModal> gasStationRVModals;
    private OnItemClickListener listener;

    public GasStationRVAdapter(Context context, ArrayList<GasStationRVModal> gasStationRVModals, OnItemClickListener listener) {
        this.context = context;
        this.gasStationRVModals = gasStationRVModals;
        this.listener = listener;
    }


    @NonNull
    @Override
    public GasStationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_gasstation_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GasStationRVAdapter.ViewHolder holder, int position) {
        GasStationRVModal modal = gasStationRVModals.get(position);
        holder.gas_station_rv_time.setText(modal.getTime());
        holder.gas_station_rv_et_indications_95.setText(modal.getGasoline_95() == "" ? "No disponible" : modal.getGasoline_95() + " €/L");
        holder.gas_station_rv_et_indications_98.setText(modal.getGasoline_98() == "" ? "No disponible" : modal.getGasoline_98() + " €/L");
        holder.gas_station_rv_localization.setText(modal.getInd() + ". " + modal.getProvince() + ": " + modal.getMunicipality() + " - " + modal.getAddress());
        holder.gas_station_rv_et_e.setText(modal.getDiesel_e() == "" ? "No disponible" : modal.getDiesel_e() + " €/L");
        holder.gas_station_rv_et_e10.setText(modal.getDiesel_10e() == "" ? "No disponible" : modal.getDiesel_10e() + " €/L");
        holder.gas_station_rv_name.setText(modal.getName());

    }

    @Override
    public int getItemCount() {
        return gasStationRVModals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private EditText gas_station_rv_localization;
        private EditText gas_station_rv_et_indications_95;
        private EditText gas_station_rv_et_indications_98;
        private EditText gas_station_rv_et_e;
        private EditText gas_station_rv_et_e10;
        private EditText gas_station_rv_name;
        private EditText gas_station_rv_time;
        private Button gas_station_button_search;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gas_station_rv_localization = itemView.findViewById(R.id.gas_station_rv_localization);
            gas_station_rv_et_indications_98 = itemView.findViewById(R.id.gas_station_rv_et_indications_98);
            gas_station_rv_et_indications_95 = itemView.findViewById(R.id.gas_station_rv_et_indications_95);
            gas_station_rv_et_e = itemView.findViewById(R.id.gas_station_rv_et_e);
            gas_station_rv_et_e10 = itemView.findViewById(R.id.gas_station_rv_et_e10);
            gas_station_rv_name = itemView.findViewById(R.id.gas_station_rv_name);
            gas_station_rv_time = itemView.findViewById(R.id.gas_station_rv_time);
            gas_station_button_search = itemView.findViewById(R.id.gas_station_button_search);
            gas_station_button_search.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(getAdapterPosition());
            }
        }

    }

}
