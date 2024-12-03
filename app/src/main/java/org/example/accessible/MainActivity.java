package org.example.accessible;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button start,stop;
    private WindowService mService;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WindowService.LocalBinder binder = (WindowService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }

        start=findViewById(R.id.startButton);
        stop=findViewById(R.id.stopButton);

        start.setOnClickListener(view -> {
            if (CheckRoot.isRooted()) {
                Intent intent = new Intent(this, WindowService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }else Toast.makeText(this,"Root permission is required",Toast.LENGTH_SHORT).show();
        });

        stop.setOnClickListener(view -> {

            if (mBound) {
                mService.closeService();
                unbindService(mConnection);
                Toast.makeText(this,"Service unbounded",Toast.LENGTH_SHORT).show();
                mBound=false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}