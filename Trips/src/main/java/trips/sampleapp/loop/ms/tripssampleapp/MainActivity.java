package trips.sampleapp.loop.ms.tripssampleapp;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Label;
import ms.loop.loopsdk.profile.Labels;
import ms.loop.loopsdk.profile.Locations;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;
import trips.sampleapp.loop.ms.tripssampleapp.utils.CustomTypefaceSpan;
import trips.sampleapp.loop.ms.tripssampleapp.utils.ViewUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //drives
    private BroadcastReceiver mReceiver;
    private TripsViewAdapter adapter;
    private ListView tripListView;
    private Switch locationSwitch;
    private TextView locationText;
    private static Drives localDrives;
    private static Locations knownLocations;
    private static Trips localTrips;
    private TextView termsTextView;
    private TextView privacyTextView;


    private static RelativeLayout enableLocation;

    private NavigationView navigationView;

    private static String Loop_URL = "https://www.loop.ms/";
    private static String TOU_URL = "http://go.microsoft.com/fwlink/?LinkID=530144";
    private static String PRIVACY_URL = "http://go.microsoft.com/fwlink/?LinkId=521839";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);
        navigationView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                item.setChecked(true);
            switch (id){
                case R.id.nav_drives: {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_drives_on));
                    navigationView.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_trips_off));
                    navigationView.getMenu().getItem(1).setChecked(false);
                    updateDrivesInUI();
                    break;
                }

                case R.id.nav_trips: {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_trips_on));
                    navigationView.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_drives_off));
                    navigationView.getMenu().getItem(0).setChecked(false);
                    updateDrivesInUI();
                    break;
                }

                case R.id.nav_version: {
                    navigationView.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_drives_off));
                    navigationView.getMenu().getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_trips_off));
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setText(LoopSDK.userId);
                    openUrlInBrowser(Loop_URL);
                    return false;
                }
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            return true;
            }
        });

        Menu m = navigationView.getMenu();
        for (int i=0;i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            //the method we have create in activity
            ViewUtils.applyFontToMenuItem(this, mi,"Roboto-Medium");
            if (mi.getItemId() == R.id.nav_version){
              //  mi.setTitle(BuildConfig.VERSION_NAME);
            }
        }

        navigationView.getMenu().getItem(0).setChecked(true);
        localDrives = Drives.createAndLoad(Drives.class, Drive.class);
        knownLocations = Locations.createAndLoad(Locations.class, KnownLocation.class);

        KnownLocation knownLocation = knownLocations.createItem(new Location("gps"));
        knownLocation.entityId = "nA6erglToho99RwhQi4PDcSf6gsa+3XU3fkv8ppSpoI=";
        knownLocation.labels.add("home");

        KnownLocation knownLocation1 = knownLocations.createItem(new Location("gps"));
        knownLocation1.entityId = "nA6erglToho99RwhQi4PDcSf6gsa+3XU3fkv8ppSpoH=";
        knownLocation1.labels.add("work");

        localTrips = Trips.createAndLoad(Trips.class, Trip.class);
        List<Trip> drives = new ArrayList<Trip>(localDrives.sortedByStartedAt());
        adapter = new TripsViewAdapter(this,
                R.layout.tripview, drives);

        tripListView = (ListView)findViewById(R.id.tripslist);
        tripListView.setAdapter(adapter);

        localDrives.registerItemChangedCallback("Drives", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {
            }
            @Override
            public void onItemAdded(String entityId) {
                SampleAppApplication.mixpanel.track("Drive created");
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });

        localTrips.registerItemChangedCallback("Trips", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {
            }
            @Override
            public void onItemAdded(String entityId) {
                SampleAppApplication.mixpanel.track("Trip created");
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });

        IntentFilter intentFilter = new IntentFilter("android.intent.action.onInitialized");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadDrivesAndTrips();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);

        locationSwitch = (Switch) this.findViewById(R.id.locationswitch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean isLocationOn = SampleAppApplication.isLocationTurnedOn(MainActivity.this);
                if (isChecked) {
                    if (!isLocationOn) {
                        SampleAppApplication.openLocationServiceSettingPage(MainActivity.this);
                    }
                    LoopLocationProvider.start(SignalConfig.SIGNAL_SEND_MODE_BATCH);
                }
                else {
                    LoopLocationProvider.stop();
                }

                SampleAppApplication.setSharedPrefValue(getApplicationContext(), "AppTracking", isChecked);
            }
        });
        locationText = (TextView) this.findViewById(R.id.txtlocationtracking);
        enableLocation = (RelativeLayout) this.findViewById(R.id.locationstrackingcontainer);

        termsTextView = (TextView) navigationView.findViewById(R.id.terms);

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser(TOU_URL);
            }
        });

        privacyTextView = (TextView) navigationView.findViewById(R.id.privacy);

        privacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInBrowser(PRIVACY_URL);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadDrivesAndTrips()
    {
        if (LoopSDK.isInitialized() && !TextUtils.isEmpty(LoopSDK.userId)) {
            LoopSDK.forceSync();
        }
        if (localDrives.itemList.size() > 0 || !LoopSDK.isInitialized() || TextUtils.isEmpty(LoopSDK.userId)) {
            updateDrivesInUI();
            return;
        }

        localDrives.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                if (itemCount == 0)
                {
                    loadSampleDrives();
                }
                updateDrivesInUI();
            }

            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });

        localTrips.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                if (itemCount == 0)
                {
                    loadSampleTrips();
                }
                updateDrivesInUI();}

            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrivesAndTrips();
        checkTrackingEnbaled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_drives) {
        } else if (id == R.id.nav_trips) {
        } else if (id == R.id.nav_version) {
            return false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkTrackingEnbaled() {
        if (SampleAppApplication.getBooleanSharedPrefValue(this.getApplicationContext(), "AppTracking", true)) {

            if (checkSelectedItemType().equals("TRIPS")){
                locationText.setText(this.getText(R.string.trips_recording_on));
            }
            else {
                locationText.setText(this.getText(R.string.drives_recording_on));
            }

            locationSwitch.setChecked(true);
            //enableLocation.setVisibility(View.GONE);
        } else {
            if (checkSelectedItemType().equals("TRIPS")){
                locationText.setText(this.getText(R.string.trips_recording_off));
            }
            else {
                locationText.setText(this.getText(R.string.drives_recording_off));
            }
            locationSwitch.setChecked(false);
           // enableLocation.setVisibility(View.VISIBLE);
        }
    }

    public void updateDrivesInUI() {
        localDrives.load();
        localTrips.load();
        final TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        String title = "";
        List<Trip> drives = new ArrayList<>();
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            if (mi.isChecked() && mi.getItemId() == R.id.nav_drives) {
                drives = new ArrayList<Trip>(localDrives.sortedByStartedAt());
                title = "DRIVES";
                break;

            } else if (mi.isChecked() && (mi.getItemId() == R.id.nav_trips || mi.getItemId() == R.id.nav_version)) {
                drives = new ArrayList<Trip>(localTrips.sortedByStartedAt());
                title = "TRIPS";
                break;
            }
        }

        final List<Trip> finalDrives = drives;
        final String finalTitle = title;

        runOnUiThread(new Runnable() {
            public void run() {
                titleTextView.setText(finalTitle);
                adapter.update(finalDrives);
            }
        });
    }

    public void openUrlInBrowser(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public String checkSelectedItemType() {
        final TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        return (String) titleTextView.getText();
    }

    public void loadSampleDrives()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset("sample_drives.json"));
            for (int i =0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                localDrives.createAndAddItem(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadSampleTrips()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset("sample_trips.json"));
            for (int i =0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                localTrips.createAndAddItem(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isKnownLocation(String entityId, String knownLocationType)
    {
        KnownLocation knownLocation = knownLocations.byEntityId(entityId);
        if (knownLocation == null || !knownLocation.isValid()) return false;
        Labels labels = knownLocation.labels;

        for (Label label:labels){
            if (label.name.equalsIgnoreCase(knownLocationType))
                return true;
        }
        return false;
    }
}
