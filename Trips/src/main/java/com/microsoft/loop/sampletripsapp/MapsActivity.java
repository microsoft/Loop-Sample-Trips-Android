package com.microsoft.loop.sampletripsapp;

import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.microsoft.loop.sampletripsapp.utils.TripView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.GeospatialPoint;
import ms.loop.loopsdk.profile.Path;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String entityId;
    private Trips trips;
    private Drives drives;
    Trip trip;
    TripView tripView;
    private ImageView backAction;
    private ImageView deleteDriveAction;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        entityId = this.getIntent().getExtras().getString("tripid");
        trips = Trips.createAndLoad(Trips.class, Trip.class);
        drives = Drives.createAndLoad(Drives.class, Drive.class);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        tripView = new TripView(viewGroup);

        backAction = (ImageView)findViewById(R.id.action_back_ic);
        deleteDriveAction = (ImageView)findViewById(R.id.action_delete_drive_ic);

        backAction.setClickable(true);

        backAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteDriveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (trip != null){
                trip.delete();
                finish();
            }
            }
        });

    /*    viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                // Adjust start time by 30 minutes
                Calendar startTime = Calendar.getInstance();
                startTime.setTime(trip.startedAt);
                startTime.add(Calendar.MINUTE, -30);

                Calendar endTime = Calendar.getInstance();
                endTime.setTime(trip.endedAt);
                endTime.add(Calendar.MINUTE, 30);

                String startedAtDate = dateFormat.format(startTime.getTime());
                String startedAtHour = hourFormat.format(startTime.getTime());
                String endedAtDate = dateFormat.format(endTime.getTime());
                String endedAtHour = hourFormat.format(endTime.getTime());
                String queryDate = LoopSDK.userId + " AND location AND createdAt:[\"" + startedAtDate + "T" + startedAtHour + "-07:00\" TO \"" + endedAtDate + "T" + endedAtHour + "-07:00\"]";
                clipboard.setText(queryDate);
                Toast.makeText(MapsActivity.this, "Elastic search query copied", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public void onResume()
    {
        super.onResume();
        trip = null;
        trip = trips.byEntityId(entityId);
        if (trip == null) {
            trip = drives.byEntityId(entityId);
            if (trip == null) return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        drawPath();
    }

    public void drawPath() {

        tripView.update(this, trip);

        GeospatialPoint firstPoint = trip.path.points.get(0);

        PolylineOptions options = new PolylineOptions()
                .add(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees))
                .width(10)
                .color(Color.BLUE)
                .geodesic(true).clickable(true);

        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees)).title("Trip starts");
        startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trip_start));


        mMap.addMarker(startMarker);
        LatLng latLng = new LatLng(firstPoint.latDegrees, firstPoint.longDegrees);
        for (GeospatialPoint point: trip.path.points)
        {
            latLng = new LatLng(point.latDegrees,point.longDegrees);
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(20)
                    .strokeColor(Color.RED)
                    .fillColor(Color.RED));

            options.add(latLng);
        }

        mMap.addPolyline(options);

        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(latLng).title("Trip ends");
        endMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trip_end));

        mMap.addMarker(endMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

    }
}
