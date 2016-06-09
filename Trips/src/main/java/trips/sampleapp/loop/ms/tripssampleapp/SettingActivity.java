package trips.sampleapp.loop.ms.tripssampleapp;

import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ms.loop.loopsdk.core.LoopSDK;

public class SettingActivity extends AppCompatActivity {

    TextView txtUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        txtUserId = (TextView)this.findViewById(R.id.userid);
        txtUserId.setText("UserId: "+ LoopSDK.userId);

        txtUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(LoopSDK.userId);
                Toast.makeText(SettingActivity.this, "Id copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
