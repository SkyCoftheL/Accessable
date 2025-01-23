package org.example.accessible;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyDataBases extends SQLiteOpenHelper {


    public MyDataBases(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSQL = "CREATE TABLE my_table (" +
                "id INTEGER PRIMARY KEY, " +
                "single TEXT, " +
                "double TEXT, "+
                "longpress TEXT)";
        sqLiteDatabase.execSQL(createTableSQL);
        Log.d("TAG", "onCreate: created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static int getId(SQLiteDatabase sqLiteDatabase){
        int id = -1;
        // 定义要查询的列
        String[] columns = {"id"};

        // 定义查询条件，这里查询 single 列大于 10 的记录
        String selection = "id ==?";
        String[] selectionArgs = {"1"};

        Cursor cursor = sqLiteDatabase.query(
                "my_table",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                // 获取列的索引
                int idIndex = cursor.getColumnIndex("id");

                // 获取数据
                id= cursor.getInt(idIndex);

            } while (cursor.moveToNext());
        }

        // 关闭游标
        cursor.close();
        return id;
    }

    public static String getSingle(SQLiteDatabase sqLiteDatabase){
        String single = null;
        // 定义要查询的列
        String[] columns = {"single"};

        // 定义查询条件，这里查询 single 列大于 10 的记录
        String selection = "id ==?";
        String[] selectionArgs = {"1"};

        Cursor cursor = sqLiteDatabase.query(
                "my_table",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                // 获取列的索引
                int idIndex = cursor.getColumnIndex("single");

                // 获取数据
                single= cursor.getString(idIndex);

            } while (cursor.moveToNext());
        }

        // 关闭游标
        cursor.close();
        return single;
    }

    public static String getDouble(SQLiteDatabase sqLiteDatabase){
        String Double = null;
        // 定义要查询的列
        String[] columns = {"double"};

        // 定义查询条件，这里查询 single 列大于 10 的记录
        String selection = "id ==?";
        String[] selectionArgs = {"1"};

        Cursor cursor = sqLiteDatabase.query(
                "my_table",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                // 获取列的索引
                int idIndex = cursor.getColumnIndex("double");

                // 获取数据
                Double= cursor.getString(idIndex);

            } while (cursor.moveToNext());
        }

        // 关闭游标
        cursor.close();
        return Double;
    }

    public static String getLongPress(SQLiteDatabase sqLiteDatabase){
        String longpress = null;
        // 定义要查询的列
        String[] columns = {"longpress"};

        // 定义查询条件，这里查询 single 列大于 10 的记录
        String selection = "id ==?";
        String[] selectionArgs = {"1"};

        Cursor cursor = sqLiteDatabase.query(
                "my_table",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            do {
                // 获取列的索引
                int idIndex = cursor.getColumnIndex("longpress");

                // 获取数据
                longpress= cursor.getString(idIndex);

            } while (cursor.moveToNext());
        }

        // 关闭游标
        cursor.close();
        return longpress;
    }
}
