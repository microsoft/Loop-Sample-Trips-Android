package trips.sampleapp.loop.ms.tripssampleapp;

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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Trip;


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

        for (Trip trip: data) {
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
        TripHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TripHolder();
            holder.txtTime = (TextView)row.findViewById(R.id.txtTime);
            holder.txtDistance = (TextView)row.findViewById(R.id.txtdistance);
            holder.txtLocation = (TextView)row.findViewById(R.id.txtLocation);
            holder.txtTotalTime = (TextView)row.findViewById(R.id.txtTotalTime);
            holder.existOnServer = (TextView)row.findViewById(R.id.exitsonserver);

            row.setTag(holder);
            row.setClickable(true);
        }
        else
        {
            holder = (TripHolder)row.getTag();
        }


        if (trips.isEmpty()) return row;


        final Trip trip = (Trip)trips.get(position);


        if (trip.startLocale.getFriendlyName().equalsIgnoreCase("unknown"))
        {
            trip.updateStartLocale(false);
        }
        if (trip.endLocale.getFriendlyName().equalsIgnoreCase("unknown"))
        {
            trip.updateEndLocale(false);
        }
        holder.txtLocation.setText(getTripLocationInfo(trip));
        holder.txtDistance.setText(getTripDistance(trip));
        holder.txtTime.setText(getTripTimeInfo(trip));
        holder.txtTotalTime.setText(getTripDistanceTime(trip));
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

    static class TripHolder
    {
        TextView txtDistance;
        TextView txtTime;
        TextView txtLocation;
        TextView txtTotalTime;
        TextView existOnServer;
    }

    public String getTripLocationInfo(Trip trip) {

        String start = TextUtils.isEmpty(trip.startLocale.getFriendlyName()) ? "Unknown" : trip.startLocale.getFriendlyName();
        String end = TextUtils.isEmpty(trip.endLocale.getFriendlyName()) ? "Unknown" : trip.endLocale.getFriendlyName();

        if (start.equalsIgnoreCase(end)) {

            if (start.equalsIgnoreCase("Unknown")) {
                return String.format("%s", start);
            }
            else {
                return String.format("Within %s", start);
            }
        }
        return String.format("%s to %s",start, end);
    }

    public String getTripDistance(Trip trip) {
        Double dist = trip.getRouteDistanceInKilometers();
        Double miles = dist * 0.621371;
        return String.format("%.2f km (%.2f miles)",dist, miles);
    }

    public String getTripDistanceTime(Trip trip) {
        Double dur = trip.getDurationMinutes();
        return String.format("%.2f mins", dur);
    }

    public String getTripTimeInfo(Trip trip) {

        SimpleDateFormat format = new SimpleDateFormat("EEE h:mm a (MM/dd)");
        String start = format.format(trip.startedAt);
        String end = "";
        if (trip.endedAt != null) {
            end = format.format(trip.endedAt);
        }Double dur = trip.getDurationMinutes();

        return String.format("%s - %s", start, end, dur);
    }
}

