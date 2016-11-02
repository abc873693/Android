package com.nail.tatproject.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by 70J on 2016/10/29.
 */
public class TATDB {
    // 表格名稱
    public static final String History_TABLE_NAME = "History";
    public static final String Favorite_TABLE_NAME = "Favorite";
    public static final String Shopping_TABLE_NAME = "Shopping";

    // 表格欄位名稱
    public static final String PRODUCTID = "ProductID";
    public static final String ADDTIME = "AddTime";
    public static final String COUNT = "Count";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_History_TABLE =
            "CREATE TABLE " + History_TABLE_NAME + " (" +
                    PRODUCTID + " INTEGER NOT NULL, " +
                    ADDTIME + " INTEGER NOT NULL, " +
                    COUNT + " INTEGER NOT NULL)";

    public static final String CREATE_Favorite_TABLE =
            "CREATE TABLE " + Favorite_TABLE_NAME + " (" +
                    PRODUCTID + " INTEGER NOT NULL, " +
                    ADDTIME + " INTEGER NOT NULL, " +
                    COUNT + " INTEGER NOT NULL)";

    public static final String CREATE_Shopping_TABLE =
            "CREATE TABLE " + Shopping_TABLE_NAME + " (" +
                    PRODUCTID + " INTEGER NOT NULL, " +
                    ADDTIME + " INTEGER NOT NULL, " +
                    COUNT + " INTEGER NOT NULL)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public TATDB(Context context) {
        db = TATDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public TATItem insert(String TABLE_NAME, TATItem item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(PRODUCTID, item.getProductID());
        cv.put(ADDTIME, item.getAddTime());
        cv.put(COUNT, item.getCount());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 回傳結果
        return item;
    }

    // 修改參數指定的物件
    public boolean update(String TABLE_NAME, TATItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(PRODUCTID, item.getProductID());
        cv.put(ADDTIME, item.getAddTime());
        cv.put(COUNT, item.getCount());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = PRODUCTID + "=" + item.getProductID();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(String TABLE_NAME, String ProductID) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = PRODUCTID + "=" + ProductID;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // 讀取所有記事資料
    public List<TATItem> getAll(String TABLE_NAME) {
        List<TATItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public TATItem get(String TABLE_NAME, String ProductID) {
        // 準備回傳結果用的物件
        TATItem item = null;
        // 使用編號為查詢條件
        String where = PRODUCTID + "=" + ProductID;
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public TATItem getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        TATItem result = new TATItem();
        result.setProductID(cursor.getString(0));
        result.setAddTime(cursor.getLong(1));
        result.setCount(cursor.getInt(2));

        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount(String TABLE_NAME) {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    // 建立範例資料
    public void sample() {
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            int x = r.nextInt(3111) - 1;
            TATItem item = new TATItem(x + "", new Date().getTime());
            insert(Shopping_TABLE_NAME, item);
        }
    }
}
