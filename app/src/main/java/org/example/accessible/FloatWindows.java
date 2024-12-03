package org.example.accessible;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatWindows {

    private static final String[] COMMANDSRECENT = {
            //"su", // 请求 Root 权限
            "input keyevent 187" // RECENT_APPS 键 (可能需要根据设备调整)
    };

    private static final String[] COMMANDSHOME = {
            //"su", // 请求 Root 权限
            "input keyevent 3", // HOME 键
    };

    // TODO: 2024/12/3 使用按键事件来处理，免root方案 

    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;
    private GestureDetector gestureDetector;
    private Context context;


    public FloatWindows(Context context) {
        this.context=context;
    }

    public void initFloatWindows(){
        gestureDetector=new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                try {
                    executeRootCommands(COMMANDSHOME);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                try {
                    executeRootCommands(COMMANDSRECENT);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                try {
                    executeRootCommands(COMMANDSRECENT);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
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
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager!=null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            floatingView = inflater.inflate(R.layout.floating_window_layout, null);

            floatingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });


            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

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
        if (floatingView!=null&&windowManager!=null){
            windowManager.removeView(floatingView);
            return true;
        }

        return false;
    }

}

