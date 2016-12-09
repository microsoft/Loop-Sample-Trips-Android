package com.microsoft.loop.triptracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.microsoft.loop.triptracker.utils.LoopUtils;
import com.microsoft.loop.triptracker.utils.TripView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ms.loop.loopsdk.profile.GeospatialPoint;
import ms.loop.loopsdk.profile.Trip;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String entityId;
    Trip trip;
    TripView tripView;
    private View backAction;
    private ImageView deleteDriveAction;

    private MapsInfoWindowAdapter mapsInfoWindowAdapter;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private Marker selectedMarker;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (this.getIntent().hasExtra("tripid")) {
            entityId = this.getIntent().getExtras().getString("tripid");
        }

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        tripView = new TripView(viewGroup);

        backAction = (View)findViewById(R.id.action_back_ic);
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

        mapsInfoWindowAdapter = new MapsInfoWindowAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        trip = null;
        trip = LoopUtils.getTrip(entityId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setInfoWindowAdapter(mapsInfoWindowAdapter);
        mMap.setOnMarkerClickListener(this);
        drawPath();
    }

    public void drawPath() {

        if (trip == null) return;

        tripView.update(this, trip, true);

        GeospatialPoint firstPoint = trip.path.get(0);

        PolylineOptions options = new PolylineOptions()
                .add(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees))
                .width(10)
                .color(R.color.mappath)
                .geodesic(true).clickable(true);

        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees)).title(trip.startLocale.getFriendlyName());
        startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trip_start));


        Marker marker = mMap.addMarker(startMarker);
        marker.showInfoWindow();
        selectedMarker = marker;
        LatLng latLng = new LatLng(firstPoint.latDegrees, firstPoint.longDegrees);
        for (GeospatialPoint point: trip.path)
        {
            latLng = new LatLng(point.latDegrees,point.longDegrees);
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(30)
                    .strokeWidth(0)
                    .strokeColor(R.color.mappathcircle)
                    .fillColor(R.color.mappath));

            options.add(latLng);
        }

        mMap.addPolyline(options);

        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(latLng).title(trip.endLocale.getFriendlyName());
        endMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_trip_end));

        mMap.addMarker(endMarker).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedMarker = marker;
        return false;
    }

    class MapsInfoWindowAdapter implements InfoWindowAdapter{

        private final View myContentsView;

        @SuppressLint("InflateParams")
        MapsInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.maps_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.txtinfowindowlocationname));
            tvTitle.setText(marker.getTitle().toUpperCase(Locale.US));
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }
}
