package org.example.accessible;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckRoot {

    private final static String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};


    public static boolean isRooted() {
        // 检查常见的root相关文件或目录是否存在
        for (String path : paths) {
            if (new File(path).exists()) {
                Log.d("RootCheck", "Root file or directory exists: " + path);

                return true;
            }
        }

        // 尝试执行su命令来判断（此方法在部分设备可能不准确或受限）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if (line!= null && line.contains("uid=0")) {
                    Log.d("RootCheck", "Su command execution indicates root access: " + line);
                    return true;
                }
            } catch (IOException e) {
                Log.e("RootCheck", "Error checking root via su command: " + e.getMessage());
            } finally {
                if (process!= null) {
                    process.destroy();
                }
            }
        }

        return false;
    }
}