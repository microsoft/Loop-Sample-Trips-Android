package loop.ms.looptrips;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.util.LoopError;

public class MainActivity extends AppCompatActivity {

    private Drives drives;
    private TripsViewAdapter tripsViewAdapter;
    private ListView tripListView;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drives = Drives.createAndLoad(Drives.class, Drive.class);
        tripsViewAdapter = new TripsViewAdapter(this,
                R.layout.tripview, new ArrayList<Trip>(drives.itemList.values()));

        tripListView = (ListView)findViewById(R.id.tripslist);
        drives.registerItemChangedCallback("Trips", new IProfileItemChangedCallback()
        {
            @Override
            public void onItemChanged(String entityId) {

               // trips.load();
                final Map<String, Drive> itemList = drives.itemList;

                runOnUiThread(new Runnable() {
                    public void run() {
                        tripsViewAdapter.update(new ArrayList<Trip>(itemList.values()));
                    }
                });
            }

            @Override
            public void onItemAdded(String entityId) {}

            @Override
            public void onItemRemoved(String entityId) {}
        });

        tripListView.setAdapter(tripsViewAdapter);

        IntentFilter intentFilter = new IntentFilter("android.intent.action.onInitialized");

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (LoopSDK.isInitialized()) {
                    LoopSDK.forceSync();
                    download(true);
                }
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }

    public void download(boolean overwrite) {
        drives.download(overwrite, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        tripsViewAdapter.update(new ArrayList<Trip>(drives.itemList.values()));
                    }
                });
            }
            @Override
            public void onProfileDownloadFailed(LoopError error) {
            }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }
}
