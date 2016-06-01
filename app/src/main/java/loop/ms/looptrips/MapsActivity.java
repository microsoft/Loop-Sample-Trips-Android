package loop.ms.looptrips;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.GeospatialPoint;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Drives trips;
    private String entityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trips = Drives.createAndLoad(Drives.class, Drive.class);
        entityId = this.getIntent().getExtras().getString("tripid");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        drawPath();
    }

    public void drawPath()
    {
        Trip trip = trips.byEntityId(entityId);
        if (trip == null) return;

        LatLng marker = null;

        PolylineOptions options = new PolylineOptions();

        options.color( Color.parseColor( "#CC0000FF" ) );
        options.width( 5 );
        options.visible( true );
        options.geodesic(true);

        for (GeospatialPoint point: trip.path.points)
        {
            options.add( new LatLng(point.latDegrees,point.longDegrees ) );
            marker = new LatLng(point.latDegrees,point.longDegrees);
            mMap.addCircle(new CircleOptions()
                    .center(marker)
                    .radius(20)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.BLUE));

        }
        mMap.addMarker(new MarkerOptions().position(marker).title("Trip ends"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 12));
    }
}
