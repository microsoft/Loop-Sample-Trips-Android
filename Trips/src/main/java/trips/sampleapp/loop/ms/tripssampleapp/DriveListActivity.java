package trips.sampleapp.loop.ms.tripssampleapp;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.GeoCoder;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
//import ms.loop.loopsdk.profile.IProfileListener;
import ms.loop.loopsdk.profile.Item;
import ms.loop.loopsdk.profile.ItemList;
import ms.loop.loopsdk.profile.Profile;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.signal.Signal;
import ms.loop.loopsdk.util.LoopDate;
import ms.loop.loopsdk.util.LoopError;
import trips.sampleapp.loop.ms.tripssampleapp.model.ServerDrives;

public class DriveListActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver;
    private TripsViewAdapter adapter;
    private ListView tripListView;

    private Switch locationSwitch;
    private TextView locationText;

    public static Drives localDrives;// = Trips.createAndLoad(Trips.class, Trip.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list);

        localDrives = Drives.createAndLoad(Drives.class, Drive.class);

        List<Trip> drives = new ArrayList<Trip>(localDrives.sortedByStartedAt());
        adapter = new TripsViewAdapter(this,
                R.layout.tripview, drives);

        tripListView = (ListView)findViewById(R.id.tripslist);
        tripListView.setAdapter(adapter);

        localDrives.registerItemChangedCallback("Drives", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {
                updateDrivesInUI();
            }
            @Override
            public void onItemAdded(String entityId) {
                updateDrivesInUI();
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });

        IntentFilter intentFilter = new IntentFilter("android.intent.action.onInitialized");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               loadDrives();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);

        locationSwitch = (Switch) this.findViewById(R.id.locationswitch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean isLocationOn = SampleAppApplication.isLocationTurnedOn(DriveListActivity.this);
                if (isChecked && !isLocationOn)
                    SampleAppApplication.openLocationServiceSettingPage(DriveListActivity.this);

                else if (!isChecked && isLocationOn)
                    SampleAppApplication.openLocationServiceSettingPage(DriveListActivity.this);
            }
        });
        locationText = (TextView) this.findViewById(R.id.txtlocationtracking);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrives();
        checkLocationEnabled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public void updateDrivesInUI()
    {
        localDrives.load();
        final List<Trip> drives = new ArrayList<Trip>(localDrives.sortedByStartedAt());
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.update(drives);
                SampleAppApplication.setPeopleProperty("Drives", adapter.getCount());
            }
        });
    }

    public void loadDrives()
    {
        if (LoopSDK.isInitialized() && !TextUtils.isEmpty(LoopSDK.userId)) {
            LoopSDK.forceSync();
            download(true);
        }
        if (localDrives.itemList.size() > 0 || !LoopSDK.isInitialized() || TextUtils.isEmpty(LoopSDK.userId)) {
            updateDrivesInUI();
            return;
        }

        localDrives.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {
                updateDrivesInUI();}

            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.profile_full) {
            loadDrives();
            return true;
        }
        if (id == R.id.trips) {
            Intent myIntent = new Intent(this, TripsListActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(myIntent);
        }
        if (id == R.id.drives) {
            Intent myIntent = new Intent(this, DriveListActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(myIntent);
        }

        if (id == R.id.send_signals) {
            LoopSDK.forceSync();
        }

        if (id == R.id.settings) {
            Intent myIntent = new Intent(this, SettingActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void download(boolean overwrite) {
        ServerDrives.Instance.download(overwrite, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {
               updateDrivesInUI();
            }

            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }

    public void checkLocationEnabled() {
        if (SampleAppApplication.isLocationTurnedOn(this)) {
            locationText.setText("Location Tracking Enabled!");
            locationSwitch.setChecked(true);
        } else {
            locationText.setText("Enable Location Tracking!");
            locationSwitch.setChecked(false);
        }
    }
}
