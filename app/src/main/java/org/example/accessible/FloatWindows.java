package org.example.accessible;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;


public class FloatWindows {

    private MyDataBases myDataBases;

    private final static String dataBaseName="MyDatabase.db";
    private final static int dataBaseVersion=1;


    public void setCOMMANDSsingle(String[] COMMANDSsingle) {
        this.COMMANDSsingle = COMMANDSsingle;
    }

    public void setCOMMANDSdouble(String[] COMMANDSdouble) {
        this.COMMANDSdouble = COMMANDSdouble;
    }

    public void setCOMMANDSlongpress(String[] COMMANDSlongpress) {
        this.COMMANDSlongpress = COMMANDSlongpress;
    }

    private   String[] COMMANDSsingle = {
            //"su", // 请求 Root 权限
            "input keyevent " // RECENT_APPS 键 (可能需要根据设备调整)
    };



    private   String[] COMMANDSdouble = {
            //"su", // 请求 Root 权限
            "input keyevent 3", // HOME 键
    };

    private   String[] COMMANDSlongpress ={""};

    // TODO: 2024/12/3 使用按键事件来处理，免root方案

    private final WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;
    private GestureDetector gestureDetector;
    private final Context context;

    private final DisplayMetrics displayMetrics ;

    private int pixelWX;

    private SQLiteDatabase db;

    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private Handler mainHandler;


    public FloatWindows(Context context) {
        this.context=context;
        displayMetrics= new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        myDataBases =new MyDataBases(context,dataBaseName,null,dataBaseVersion);
        db = myDataBases.getWritableDatabase();

        // 创建 HandlerThread
        handlerThread = new HandlerThread("BackgroundThread");
        handlerThread.start();


        // 在 HandlerThread 的 Looper 上创建 Handler
        backgroundHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    executeRootCommands((String[]) msg.obj);
                } catch (IOException | InterruptedException e) {
                    Log.e("FloatWindows", "Error executing root commands", e);
                    Toast.makeText(context,context.getString(R.string.runtimeErr),Toast.LENGTH_SHORT).show();
                    // 可以使用 Toast 通知用户
                }

                //handlerThread.quitSafely();
            }
        };


        // 在主线程上创建 Handler 用于更新 UI
        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 这里可以添加 UI 更新操作，例如更新悬浮窗的显示等
            }
        };
    }

    public void initFloatWindows(){
        COMMANDSsingle[0]="input keyevent "+MyDataBases.getSingle(db);
        COMMANDSdouble[0]="input keyevent "+MyDataBases.getDouble(db);
        COMMANDSlongpress[0]="input keyevent "+MyDataBases.getLongPress(db);


        gestureDetector=new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {

                Message message = Message.obtain();
                message.obj = COMMANDSsingle;
                backgroundHandler.sendMessage(message);
                return true;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {

                Message message = Message.obtain();
                message.obj = COMMANDSdouble;
                backgroundHandler.sendMessage(message);
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {

                Message message = Message.obtain();
                message.obj = COMMANDSlongpress;
                backgroundHandler.sendMessage(message);
            }



            @Override
            public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {


                TranslateAnimation animation = new TranslateAnimation(
                        e1.getX(), e2.getY(),
                        e1.getX(), e2.getY());
                animation.setDuration(0);
                animation.setFillAfter(true);
                floatingView.startAnimation(animation);


                pixelWX =Resources.getSystem().getDisplayMetrics().widthPixels;

                if (e2.getRawX()>=(pixelWX/2)) {
                    layoutParams.x = pixelWX;
                }else {
                    layoutParams.x=0;
                }
                layoutParams.y= (int) e2.getRawY();



                floatingView.post(new Runnable() {
                    @Override
                    public void run() {
                        windowManager.updateViewLayout(floatingView, layoutParams);
                    }
                });


                return true;


            }
        });

    }

    private void executeRootCommands(String[] commands) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());

        for (String command : commands) {
            os.writeBytes(command + "\n");
            os.flush();
        }
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        process.waitFor();



    }

    public void addFloatingWindow() {

        if (windowManager!=null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            floatingView = inflater.inflate(R.layout.floating_window_layout, null);


            floatingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });




            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                            WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.x = width;
            layoutParams.y = height * 2 / 5; // Adjust the initial position as needed
            layoutParams.alpha = 0.7f;
            windowManager.addView(floatingView, layoutParams);

        }
        else Log.d("TAG", "addFloatingWindow: check windowsManager");
    }


    public void adjustFloatingWindowPosition() {



        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;



        layoutParams.x=screenWidth;
        layoutParams.y=screenHeight*2/5;




        // 更新悬浮窗位置
        windowManager.updateViewLayout(floatingView, layoutParams);
    }



    public boolean removeFloatingWindow(){

        handlerThread.quitSafely();
        if (floatingView!=null&&windowManager!=null){
            windowManager.removeView(floatingView);
            return true;
        }

        return false;
    }

}