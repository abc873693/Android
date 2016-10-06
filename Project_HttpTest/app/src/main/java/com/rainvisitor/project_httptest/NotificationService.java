package com.rainvisitor.project_httptest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificationService extends Service
{
    private final String SharedPrefer_data = "data";
    private String web_cookie;
    private int count;
    public ArrayList<RecordModule> records = new ArrayList<>();

    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        NotificationService getService()
        {
            return  NotificationService.this;
        }
    }
    private LocalBinder mLocBin = new LocalBinder();

    public void myMethod()
    {
        Log.d("LOG_TAG", "myMethod()");
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        Log.d("LOG_TAG", "onBind");
        return mLocBin;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        // TODO Auto-generated method stub
        Log.d("LOG_TAG", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO Auto-generated method stub
        Log.d("LOG_TAG", "onStartCommand");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
                count = data.getInt("record_amount", 0);
                //web_cookie = data.getString("Cookies", "07aenpa8d9ut1p1ajji9svmj97");
                //Log.d("Cookies", web_cookie);
                /*data.edit()
                        .putInt("record_amount", records.size())
                        .apply();*/
                Log.d("LOG_TAG", "開始搜尋");
                new AsyncGetRecord().execute();
            }
        }, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        Log.d("LOG_TAG", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("LOG_TAG", "onDestroy");
        // TODO Auto-generated method stub
    }

    public class AsyncGetRecord extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        private String targetURL = "http://yoyotv.sytes.net/jsontest.php";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // 利用string url构建URL对象
                URL mURL = new URL(targetURL);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);
                conn.setRequestProperty("Cookie", web_cookie);
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    String state = getStringFromInputStream(is);
                    return state;
                } else {
                    return ("Connect Error!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            String Place = "";
            try {
                records.clear();
                Log.d("JSON Parser", "result :\n " + result);
                JSONObject json_data = new JSONObject(result);
                JSONArray jsonArray = json_data.getJSONArray("Record");
                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    RecordModule module = new RecordModule();
                    JSONObject notifyObject = jsonArray.getJSONObject(i);
                    module.uid = notifyObject.optString("Uid");
                    module.date = notifyObject.optString("Date");
                    module.idx = notifyObject.optString("Inx");
                    module.place = jsonArray.getJSONObject(i).optString("Place");
                    if(i==jsonArray.length() - 1)Place = module.place;
                    records.add(module);
                }
                Log.d("JSON Parser", "data size =  " + records.size() + "\ncount = " + count);
                if(records.size()!=count){
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(NotificationService.this);
                    // 設定小圖示、大圖示、狀態列文字、時間、內容標題、內容訊息和內容額外資訊
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("通知")
                            .setContentText("地點:" + Place)
                            .setContentInfo("1");
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = builder.build();
                    // 使用CUSTOM_EFFECT_ID為編號發出通知
                    manager.notify(0, notification);
                }
                SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
                data.edit()
                        .putInt("record_amount", records.size())
                        .apply();
            } catch (Exception e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                e.printStackTrace();
            }
        }

    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }
}