package com.microsoft.loop.sampletripsapp;

/**
 * Created by on 5/24/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.loop.sampletripsapp.utils.TripView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Visit;


public class TripsViewAdapter extends ArrayAdapter<Trip> {

    Context context;
    int layoutResourceId;
    List<Trip> trips = new ArrayList<>();

    public TripsViewAdapter(Context context, int layoutResourceId, List<Trip> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        update(data);
    }

    public void update(List<Trip> data) {

        this.trips.clear();

        for (Trip trip : data) {
            if (trip.isValid())
                this.trips.add(trip);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (trips != null)
            return trips.size();
        else
            return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TripView holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new TripView(row);
            row.setTag(holder);
            row.setClickable(true);

        } else {
            holder = (TripView) row.getTag();
        }

        if (trips.isEmpty()) return row;
        final Trip trip = (Trip) trips.get(position);
        holder.update(context, trip);

        row.setClickable(true);

        final View finalRow = row;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalRow.setSelected(true);
                Intent myIntent = new Intent(context, MapsActivity.class);
                myIntent.putExtra("tripid", trip.entityId); //Optional parameters
                context.startActivity(myIntent);
            }
        });

        return row;
    }
}

