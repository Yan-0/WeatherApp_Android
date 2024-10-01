package com.shreyan.weather_forecast_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<Forecast> forecastList;

    public ForecastAdapter(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast forecast = forecastList.get(position);
        holder.dateView.setText(forecast.getDate());
        holder.highTempView.setText(String.format("%.0f°C", forecast.getHighTemp()));
        holder.lowTempView.setText(String.format("%.0f°C", forecast.getLowTemp()));
        holder.precipView.setText(String.format("%.1f mm", forecast.getPrcp()));
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public void updateForecastList(List<Forecast> newForecastList) {
        this.forecastList.clear();
        this.forecastList.addAll(newForecastList);
        notifyDataSetChanged();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dateView, highTempView, lowTempView, precipView;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateView);
            highTempView = itemView.findViewById(R.id.highTempView);
            lowTempView = itemView.findViewById(R.id.lowTempView);
            precipView = itemView.findViewById(R.id.precipView);
        }
    }
}
