package trips.sampleapp.loop.ms.tripssampleapp;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        drawPath();
    }

    public void drawPath() {
        Trip trip = trips.byEntityId(entityId);
        if (trip == null) {
            trip = drives.byEntityId(entityId);
            if (trip == null) return;
        }
        GeospatialPoint firstPoint = trip.path.points.get(0);

        PolylineOptions options = new PolylineOptions()
                .add(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees))
                .width(10)
                .color(Color.BLUE)
                .geodesic(true).clickable(true);

        mMap.addMarker(new MarkerOptions().position(new LatLng(firstPoint.latDegrees,firstPoint.longDegrees)).title("Trip starts"));
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
        mMap.addMarker(new MarkerOptions().position(latLng).title("Trip ends"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

    }
}
