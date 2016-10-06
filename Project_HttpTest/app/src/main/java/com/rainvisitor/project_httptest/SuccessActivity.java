package com.rainvisitor.project_httptest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SuccessActivity extends AppCompatActivity {
    public ArrayList<RecordModule> records = new ArrayList<>();
    private String web_cookie;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomListView listView;
    private Button btn_logout;
    private final String SharedPrefer_data = "data";
    private NotificationService mMyService = null;
    private  CheckBox chk_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        init();
        web_cookie = getIntent().getExtras().getString("Cookies");
        SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
        data.edit()
                .putString("Cookies", web_cookie)
                .apply();
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        new AsyncGetRecord().execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGetRecord().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        chk_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    Toast.makeText(SuccessActivity.this, "背景通知開啟", Toast.LENGTH_LONG).show();
                    mMyService = null;
                    Intent it = new Intent(SuccessActivity.this, NotificationService.class);
                    startService(it); //開始Service
                }else{
                    Toast.makeText(SuccessActivity.this, "背景通知關閉", Toast.LENGTH_LONG).show();
                    mMyService = null;
                    Intent it = new Intent(SuccessActivity.this, NotificationService.class);
                    stopService(it); //結束Service
                }
            }
        });
    }


    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            // TODO Auto-generated method stub
            mMyService = ((NotificationService.LocalBinder)serviceBinder).getService();
        }

        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
            Log.d("service", "onServiceDisconnected()" + name.getClassName());
        }
    };

    public void logout(View arg0) {
        SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
        data.edit()
                .putBoolean("keep", false)
                .apply();
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        startActivity(intent);
        SuccessActivity.this.finish();
    }

    public void renew(View arg0) {
        new AsyncGetRecord().execute();
        Bitmap largeIcon = BitmapFactory.decodeResource(
                getResources(), R.mipmap.ic_launcher);
        // 建立NotificationCompat.Builder物件
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
    // 設定小圖示、大圖示、狀態列文字、時間、內容標題、內容訊息和內容額外資訊
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Basic Notification")
                .setContentText("Demo for basic notification control.")
                .setContentInfo("3");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        // 使用CUSTOM_EFFECT_ID為編號發出通知
        manager.notify(0, notification);
        SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
    }

    private void init() {
        listView = (CustomListView) findViewById(R.id.listView);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_500, R.color.blue_500);  //設定swipeRefreshLayout更新時,圈圈的顏色
        chk_service = (CheckBox) findViewById(R.id.chk_service);
    }

    public class AsyncGetRecord extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        private String targetURL = "http://yoyotv.sytes.net/jsontest.php";
        private ProgressDialog pdLoading = new ProgressDialog(SuccessActivity.this);

        @Override
        protected void onPreExecute() {
            pdLoading.setMessage("\t讀取中...");
            pdLoading.setCancelable(false);
            pdLoading.show();
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
            pdLoading.dismiss();
            try {
                records.clear();
                JSONObject json_data = new JSONObject(result);
                JSONArray jsonArray = json_data.getJSONArray("Record");
                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    RecordModule module = new RecordModule();
                    JSONObject notifyObject = jsonArray.getJSONObject(i);
                    module.uid = notifyObject.optString("Uid");
                    module.date = notifyObject.optString("Date");
                    module.idx = notifyObject.optString("Inx");
                    module.place = jsonArray.getJSONObject(i).optString("Place");
                    records.add(module);
                }
                SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
                data.edit()
                        .putInt("record_amount", records.size())
                        .apply();
                Log.d("JSON Parser", "data size " + records.size());
            } catch (Exception e) {
                Toast.makeText(SuccessActivity.this, "資料取得失敗!", Toast.LENGTH_LONG).show();
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

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public Object getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(SuccessActivity.this);
                convertView = inflater.inflate(R.layout.list_custom, parent, false);
                holder = new ViewHolder();
                holder.textView_UID = (TextView) convertView.findViewById(R.id.textView_UID);
                holder.textView_place = (TextView) convertView.findViewById(R.id.textView_place);
                holder.textView_idx = (TextView) convertView.findViewById(R.id.textView_idx);
                holder.textView_date = (TextView) convertView.findViewById(R.id.textView_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView_UID.setText(records.get(position).uid);
            holder.textView_place.setText(records.get(position).place);
            holder.textView_idx.setText(records.get(position).idx);
            holder.textView_date.setText(records.get(position).date);
            return convertView;
        }

        class ViewHolder {
            TextView textView_UID;
            TextView textView_idx;
            TextView textView_place;
            TextView textView_date;
        }
    }
}