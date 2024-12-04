package org.example.accessible;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Foreground {
    private static final String CHANNEL_ID = "foreground_service_channel";
    // 定义通知的ID


    Context context;

    public Foreground(Context context) {
        this.context = context;
    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道对象，设置渠道名称、描述和重要性级别
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // 获取系统的通知管理器服务
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            // 使用通知管理器创建通知渠道
            manager.createNotificationChannel(channel);
        }
    }

    public Notification createNotification() {
        // 使用 NotificationCompat.Builder 构建通知对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Accessible")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.accessible);
        return builder.build();
    }
}
