package trips.sampleapp.loop.ms.tripssampleapp.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ms.loop.loopsdk.profile.Trip;
import trips.sampleapp.loop.ms.tripssampleapp.R;

/**
 * Created on 6/22/16.
 */
public class TripView {

    private TextView txtDriveFrom;
    private TextView txtDriveTo;
    private TextView txtTime;
    private TextView txtDuration;
    private TextView txtDistance;
    private ImageView imgDirectionIcon;
    private ImageView imgDirectionIcon2;
    private ImageView driveToUnknown;
    private ImageView driveFromUnknown;

    private final SimpleDateFormat dateFormatWithDay = new SimpleDateFormat("MM/dd h:mm a", Locale.US);
    private final SimpleDateFormat dateFormat        = new SimpleDateFormat("h:mm a", Locale.US);

    public TripView(View view) {

        txtTime = (TextView) view.findViewById(R.id.drive_time);
        txtDistance = (TextView) view.findViewById(R.id.drive_distance);
        txtDriveFrom = (TextView) view.findViewById(R.id.drive_from);
        txtDriveTo = (TextView) view.findViewById(R.id.drive_to);
        txtDuration = (TextView) view.findViewById(R.id.drive_duration);
        imgDirectionIcon = (ImageView) view.findViewById(R.id.drive_direction_icon);
        imgDirectionIcon2 = (ImageView) view.findViewById(R.id.drive_direction_icon2);
        driveFromUnknown = (ImageView) view.findViewById(R.id.drive_from_location_unknwon);
        driveToUnknown = (ImageView) view.findViewById(R.id.drive_to_location_unknwon);
    }
    public void update(Context context, Trip trip){
        txtDriveFrom.setText(getTripStartLocation(trip));

        String strEndPlace = getTripEndLocation(trip);
        if (TextUtils.isEmpty(strEndPlace)){
            imgDirectionIcon2.setVisibility(View.VISIBLE);
            imgDirectionIcon.setVisibility(View.GONE);
        }
        else {
            imgDirectionIcon.setVisibility(View.VISIBLE);
            imgDirectionIcon2.setVisibility(View.GONE);
        }
        txtDriveTo.setText(getTripEndLocation(trip));
        txtDistance.setText(getTripDistance(trip));
        txtTime.setText(getTripTimeInfo(trip));
        txtDuration.setText(getTripDuration(trip));
    }

    private String getTripStartLocation(Trip trip) {

        if (TextUtils.isEmpty(trip.startLocale.getFriendlyName())) {
            driveFromUnknown.setVisibility(View.VISIBLE);
            return "UNKNOWN";
        }
        driveFromUnknown.setVisibility(View.GONE);
        return trip.startLocale.getFriendlyName().toUpperCase(Locale.US);
    }

    private String getTripEndLocation(Trip trip) {

        String start = TextUtils.isEmpty(trip.startLocale.getFriendlyName()) ? "Unknown" : trip.startLocale.getFriendlyName();
        String end = TextUtils.isEmpty(trip.endLocale.getFriendlyName()) ? "Unknown" : trip.endLocale.getFriendlyName();

        if (start.equalsIgnoreCase(end)) {
            driveToUnknown.setVisibility(View.GONE);
            return "";
        }

        if (end.equals("unknown")) {
            driveToUnknown.setVisibility(View.VISIBLE);
        } else {
            driveToUnknown.setVisibility(View.GONE);
        }
        return end.toUpperCase(Locale.US);
    }

    private String getTripDistance(Trip trip) {
        Double dist = trip.getRouteDistanceInKilometers();
        Double miles = dist * 0.621371;
        return String.format(Locale.US, "%.2f mi.",dist, miles);
    }

    private String getTripTimeInfo(Trip trip) {
        String start = dateFormatWithDay.format(trip.startedAt);
        if (DateUtils.isToday(trip.startedAt.getTime())){
            start = "TODAY "+ dateFormat.format(trip.startedAt);
        }
        String end = dateFormat.format(trip.endedAt);
        Double dur = trip.getDurationMinutes();

        return String.format(Locale.US, "%s - %s", start, end, dur);
    }

    private String getTripDuration(Trip trip)
    {
        long diffInSeconds = (trip.endedAt.getTime() - trip.startedAt.getTime()) / 1000;

        long diff[] = new long[] {0, 0, 0 };
        diff[2] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;

        return String.format(Locale.US,
                "%s%d:%s%d:%s%d",
                diff[0] < 9 ? "0" : "",
                diff[0],
                diff[1] < 9 ? "0": "",
                diff[1],
                diff[2] < 9 ? "0":"",
                diff[2]);
    }

}
