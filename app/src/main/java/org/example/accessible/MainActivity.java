package org.example.accessible;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.XposedBridge;

public class MainActivity extends AppCompatActivity {

    private Button start,stop,save;
    private TextView inforview,urltext;
    private WindowService mService;
    private boolean mBound;

    private  MyDataBases myDataBases;
    private final static String dataBaseName="MyDatabase.db";
    private final static int dataBaseVersion=1;
    private EditText singleEdit,doubleEdit,longPressEdit;
    private ContentValues updateValues;

    private final ServiceConnection mConnection = new ServiceConnection() {
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
        save=findViewById(R.id.saveButton);
        inforview=findViewById(R.id.infortextview);
        urltext=findViewById(R.id.urltext);

        //urltext.setAutoLinkMask(Linkify.WEB_URLS);



        singleEdit=findViewById(R.id.SingleTapEdit);
        doubleEdit=findViewById(R.id.DoubleTapEdit);
        longPressEdit=findViewById(R.id.LongPressEdit);



        myDataBases =new MyDataBases(this,dataBaseName,null,dataBaseVersion);
        SQLiteDatabase db = myDataBases.getWritableDatabase();

        singleEdit.setText(myDataBases.getSingle(db));
        doubleEdit.setText(myDataBases.getDouble(db));
        longPressEdit.setText(myDataBases.getLongPress(db));

        updateValues = new ContentValues();

        start.setOnClickListener(view -> {
            if (CheckRoot.isRooted()) {
                Intent intent = new Intent(this, WindowService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                inforview.setText("Service is running");
            }else {
                Toast.makeText(this,"Root permission is required",Toast.LENGTH_SHORT).show();
                inforview.setText("Root permission is required");
            }
        });

        stop.setOnClickListener(view -> {

            if (mBound) {
                mService.closeService();
                unbindService(mConnection);
                Toast.makeText(this,"Service unbounded",Toast.LENGTH_SHORT).show();
                inforview.setText("Service unbounded");
                mBound=false;
            }
        });

        save.setOnClickListener(view -> {
            String whereClause = "id =?";
            String[] whereArgs ={"1"};
            int rowsAffected=0;
            long newRowId=0;

            String singletap=singleEdit.getText().toString();
            String doubletap=doubleEdit.getText().toString();
            String longpress=longPressEdit.getText().toString();
//            if (!singletap.equals("")) updateValues.put("single", singletap);
//            else updateValues.put("single",0);
//
//            if (!doubletap.equals("")) updateValues.put("double", doubletap);
//            else updateValues.put("double",0);
//
//            if (!longpress.equals("")) updateValues.put("longpress", longpress);
//            else updateValues.put("longpress",0);

            updateValues.put("single", singletap);
            updateValues.put("double", doubletap);
            updateValues.put("longpress",longpress);



           if (myDataBases.getId(db)==1)  {
               rowsAffected = db.update("my_table", updateValues,whereClause,whereArgs);
               Log.d("MainA", "update: ");
           }
           else  {
               newRowId = db.insert("my_table", null, updateValues);
               Log.d("MainA", "insert: ");
           }


            if (newRowId > 0||rowsAffected>0) {
                Toast.makeText(this,"Saved,Restart the service to enable",Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this,"Check",Toast.LENGTH_SHORT).show();




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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // 确保后续通过getIntent获取的是最新的意图
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