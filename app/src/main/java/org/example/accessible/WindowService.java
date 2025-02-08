package org.example.accessible;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;


public class WindowService extends Service {

    private static final int NOTIFICATION_ID = 1;

    private Context context;
    private BroadcastReceiver mConfigurationChangeReceiver;
    private FloatWindows floatWindows;
    private final IBinder mBinder = new LocalBinder();

    private Foreground foreground;
    private Notification notification;


    private  boolean isRemoveFloatingWindow=false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

        foreground=new Foreground(context);
        foreground.createNotificationChannel();



        floatWindows=new FloatWindows(context);



        floatWindows.initFloatWindows();
        floatWindows.addFloatingWindow();
        Toast.makeText(context,getString(R.string.service_start),Toast.LENGTH_SHORT).show();
        Log.d("TAG", "onCreate: service started");
        mConfigurationChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
                    floatWindows.adjustFloatingWindowPosition();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mConfigurationChangeReceiver, filter);

        notification=foreground.createNotification();
        startForeground(NOTIFICATION_ID,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);

        if (!isRemoveFloatingWindow) isRemoveFloatingWindow=floatWindows.removeFloatingWindow();

        if (mConfigurationChangeReceiver!= null) {
            unregisterReceiver(mConfigurationChangeReceiver);
        }
        Log.d("TAG", "onDestroy: service destroyed");
    }

    public void closeService(){
        isRemoveFloatingWindow=floatWindows.removeFloatingWindow();
        stopSelf();
    }



    public class LocalBinder extends Binder {
        public WindowService getService() {
            return WindowService.this;
        }
    }
}
// TODO: 2024/12/3 如通过广播接收器），那么模拟物理按键的悬浮窗在屏幕发生改变时是可以跟着改变位置的。 
// TODO: 2024/12/3 服务保活未实现




