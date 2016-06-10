package trips.sampleapp.loop.ms.tripssampleapp;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.signal.Signal;
import ms.loop.loopsdk.util.LoopDate;
import ms.loop.loopsdk.util.LoopError;
import trips.sampleapp.loop.ms.tripssampleapp.model.ServerTrips;

public class TripsListActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver;
    private TripsViewAdapter adapter;
    public static Trips localTrips;
    private ListView tripListView;

    private Switch locationSwitch;
    private TextView locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list);

        localTrips = Trips.createAndLoad(Trips.class, Trip.class);
        adapter = new TripsViewAdapter(this,
                R.layout.tripview, new ArrayList<Trip>(localTrips.itemList.values()));

        tripListView = (ListView)findViewById(R.id.tripslist);
        tripListView.setAdapter(adapter);

        localTrips.registerItemChangedCallback("Trips", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {
                updateTripsInUI();
            }

            @Override
            public void onItemAdded(String entityId) {
                updateTripsInUI();
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });

        IntentFilter intentFilter = new IntentFilter("android.intent.action.onInitialized");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadTrips();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);

        locationSwitch = (Switch) this.findViewById(R.id.locationswitch);
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean isLocationOn = SampleAppApplication.isLocationTurnedOn(TripsListActivity.this);
                if (isChecked && !isLocationOn)
                    SampleAppApplication.openLocationServiceSettingPage(TripsListActivity.this);

                else if (!isChecked && isLocationOn)
                    SampleAppApplication.openLocationServiceSettingPage(TripsListActivity.this);
            }
        });
        locationText = (TextView) this.findViewById(R.id.txtlocationtracking);
    }

    public void onResume() {
        super.onResume();
        loadTrips();
        checkLocationEnabled();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public void updateTripsInUI()
    {
        localTrips.load();
        final List<Trip> trips = localTrips.getSortedItems();
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.update(trips);
                SampleAppApplication.setPeopleProperty("Trips", adapter.getCount());
            }
        });
    }

    public void loadTrips() {
        if (LoopSDK.isInitialized() && !TextUtils.isEmpty(LoopSDK.userId)) {
            LoopSDK.forceSync();
            download(true);
        }
        if (localTrips.itemList.size() > 0 || !LoopSDK.isInitialized() || TextUtils.isEmpty(LoopSDK.userId)) {
            updateTripsInUI();
            return;
        }

        localTrips.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {
                updateTripsInUI();
            }
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
            loadTrips();
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
        ServerTrips.Instance.download(overwrite, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter.update(new ArrayList<Trip>(localTrips.itemList.values()));
                    }
                });
            }
            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }
    public static Trip getTrip(String entityId)
    {
        return localTrips.byEntityId(entityId);
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
