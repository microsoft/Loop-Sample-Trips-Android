package trips.sampleapp.loop.ms.tripssampleapp;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

    TripsViewAdapter adapter;
    public static Drives localDrives;// = Trips.createAndLoad(Trips.class, Trip.class);
    TextView txtTripCount;
    TextView txtUserId;

    private ListView tripListView;
    private long tripStartTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list);

        localDrives = Drives.createAndLoad(Drives.class, Drive.class);

        List<Trip> drives = getSortedDrives(new ArrayList<Trip>(localDrives.itemList.values()));
        adapter = new TripsViewAdapter(this,
                R.layout.tripview, drives);

        tripListView = (ListView)findViewById(R.id.tripslist);
        View header = (View)getLayoutInflater().inflate(R.layout.tripsheader, null);
        tripListView.addHeaderView(header);



        txtTripCount = (TextView)header.findViewById(R.id.tripCount);
        txtTripCount.setText("Drives: "+adapter.getCount());
        tripListView.setAdapter(adapter);

        localDrives.registerItemChangedCallback("Drives", new IProfileItemChangedCallback()
        {
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
                txtUserId.setText("UserId: "+LoopSDK.userId);
               loadDrives();
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrives();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

    public void updateDrivesInUI()
    {
        localDrives.load();
        final List<Trip> drives = getSortedDrives(new ArrayList<Trip>(localDrives.itemList.values()));
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.update(drives);
                txtTripCount.setText("Drives: "+adapter.getCount());
                SampleAppApplication.setPeopleProperty("Drives", adapter.getCount());
            }
        });
    }

    public void loadDrives()
    {
        if (LoopSDK.isInitialized() && !TextUtils.isEmpty(LoopSDK.userId)) {
           // LoopSDK.forceSync();
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
            //LoopSDK.forceSync();
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

    public static Comparator<Trip> CreatedByComparator = new Comparator<Trip>() {
        public int compare(Trip item1, Trip item2) {
            Date createdAt1 = item1.createdAt;
            Date createdAt2 = item2.createdAt;

            //ascending order
            if (createdAt1 == null || createdAt2 == null)
                return 1;
            return createdAt2.compareTo(createdAt1);
        }
    };

    public static List<Trip> getSortedDrives(List<Trip> drives) {
        Collections.sort(drives, CreatedByComparator);
        return drives;
    }
}
