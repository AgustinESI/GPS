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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TemperaturaRVAdapter extends RecyclerView.Adapter<TemperaturaRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TemperaturaRVModal> temperaturaRVModalList;

    public TemperaturaRVAdapter(Context context, ArrayList<TemperaturaRVModal> temperaturaRVModalList) {
        this.context = context;
        this.temperaturaRVModalList = temperaturaRVModalList;
    }

    @NonNull
    @Override
    public TemperaturaRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemperaturaRVAdapter.ViewHolder holder, int position) {
        TemperaturaRVModal modal = temperaturaRVModalList.get(position);
        holder.temperatureTV.setText(modal.getTemperatura() + "ºC");
        holder.windTV.setText(modal.getVelocidad_viento() + "Km/h");
        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionIV);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try {
            Date date = input.parse(modal.getHora());
            holder.timeTV.setText(output.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return temperaturaRVModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionIV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
