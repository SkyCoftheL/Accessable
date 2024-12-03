package org.example.accessible;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;



public class WindowService extends Service {

    private Context context;
    private BroadcastReceiver mConfigurationChangeReceiver;
    private FloatWindows floatWindows;
    private final IBinder mBinder = new LocalBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        floatWindows=new FloatWindows(context);
        floatWindows.initFloatWindows();
        floatWindows.addFloatingWindow();
        Toast.makeText(context,"service started",Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConfigurationChangeReceiver!= null) {
            unregisterReceiver(mConfigurationChangeReceiver);
        }
    }

    public void closeService(){
        floatWindows.removeFloatingWindow();
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




