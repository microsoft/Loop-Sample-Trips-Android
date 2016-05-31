package loop.ms.looptrips;

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

import ms.loop.loopsdk.profile.Trip;

/**
 * Created on 5/30/16.
 */
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
            if (trip.isValid()) {
                this.trips.add(trip);
            }
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

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new TripHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtDistance = (TextView)row.findViewById(R.id.txtdistance);
            holder.txtTime = (TextView)row.findViewById(R.id.txtTime);
            holder.txtTotalTime = (TextView)row.findViewById(R.id.txtTotalTime);

            row.setTag(holder);
            row.setClickable(true);
        }
        else {
            holder = (TripHolder)row.getTag();
        }


        if (trips.isEmpty()) return row;

        final Trip trip = (Trip)trips.get(position);
        holder.txtTitle.setText(getTripLocationInfo(trip));
        holder.txtDistance.setText(getTripDistance(trip));
        holder.txtTime.setText(getTripTimeInfo(trip));
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

    static class TripHolder {
        TextView txtTitle;
        TextView txtDistance;
        TextView txtTime;
        TextView txtTotalTime;
    }

    public String getTripLocationInfo(Trip trip) {
        String start = TextUtils.isEmpty(trip.startLocale.getFriendlyName()) ? "Unknown" : trip.startLocale.getFriendlyName();
        String end = TextUtils.isEmpty(trip.endLocale.getFriendlyName()) ? "Unknown" : trip.endLocale.getFriendlyName();
        return String.format("%s to %s",start, end);
    }

    public String getTripDistance(Trip trip) {
        Double dist = trip.getRouteDistanceInKilometers();
        return String.format("%.2f km",dist);
    }

    public String getTripTimeInfo(Trip trip) {
        SimpleDateFormat format = new SimpleDateFormat("EEE dd HH:mm:ss");
        String start = format.format(trip.startedAt);
        String end = format.format(trip.endedAt);
        Double dur = trip.getDurationMinutes();
        return String.format("%s - %s (%.2f mins)", start, end, dur);
    }
}
