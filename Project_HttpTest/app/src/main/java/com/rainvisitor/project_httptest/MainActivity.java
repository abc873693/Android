package com.rainvisitor.project_httptest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String account_data = "data";
    private EditText edit_user, edit_pwd;
    private Button btn_login;
    private CheckBox chk_remember, chk_keep_login;
    String scookies = null;
    HashMap<String, String> cookieMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkAccountData();
    }

    private void init() {
        edit_user = (EditText) findViewById(R.id.edit_account);
        edit_pwd = (EditText) findViewById(R.id.edit_pwd);
        btn_login = (Button) findViewById(R.id.login);
        chk_remember = (CheckBox) findViewById(R.id.chk_remember);
        chk_keep_login = (CheckBox) findViewById(R.id.chk_keep_login);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("離開")
                    .setMessage("請問要離開程式?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        }
        return false;
    }

    private void checkAccountData() {
        SharedPreferences data = getSharedPreferences(account_data, 0);
        boolean remember = data.getBoolean("remember", false);
        boolean keep = data.getBoolean("keep", false);
        String account = data.getString("account", null);
        String password = data.getString("password", null);
        if (remember) {
            edit_user.setText(account);
            edit_pwd.setText(password);
            chk_remember.setChecked(true);

        }
        chk_keep_login.setChecked(keep);
        if (chk_keep_login.isChecked()) {
            checkLogin(btn_login);
        }
    }

    private void rememberAccountData() {
        SharedPreferences data = getSharedPreferences(account_data, 0);
        boolean remember = chk_remember.isChecked();
        String account = edit_user.getText().toString();
        String password = edit_pwd.getText().toString();
        if (remember) {
            data.edit()
                    .putBoolean("remember", true)
                    .putBoolean("keep", chk_keep_login.isChecked())
                    .putString("account", account)
                    .putString("password", password)
                    .apply();
        } else {
            data.edit()
                    .putBoolean("remember", false)
                    .putString("account", "")
                    .putString("password", "")
                    .apply();
        }
    }

    public void checkLogin(View arg0) {
        // Get text from email and passord field
        final String username = edit_user.getText().toString();
        final String password = edit_pwd.getText().toString();
        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(username, password);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        private static final int CONNECTION_TIMEOUT = 4000;
        private static final int READ_TIMEOUT = 5000;
        private final String url_connect = "http://yoyotv.sytes.net/connect.php";
        private ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        private HttpURLConnection conn;
        private URL url = null;
        CookieStore store;
        CookieManager manager;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\t登入中...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL(url_connect);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                manager = new java.net.CookieManager();
                manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(manager);
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0])
                        .appendQueryParameter("pw", params[1]);
                String query = builder.build().getEncodedQuery();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }
            try {
                conn.getHeaderFields();
                store = manager.getCookieStore();
                responseUpdateCookieHttpURL(store);
                int response_code = conn.getResponseCode();
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return (result.toString());
                } else {
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread
            pdLoading.dismiss();
            //txt_result.setText(result);
            if (result.contains("Login Success.")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                try {
                    rememberAccountData();
                    Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                    intent.putExtra("Cookies", scookies);
                    startActivity(intent);
                    MainActivity.this.finish();
                    Toast.makeText(MainActivity.this, "登入成功 歡迎使用!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result.contains("Login Failed.")) {

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "帳號密碼錯誤", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(MainActivity.this, "伺服器連接失敗", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void responseUpdateCookieHttpURL(CookieStore store) {
        boolean needUpdate = false;
        List<HttpCookie> cookies = store.getCookies();
        cookieMap = new HashMap<String, String>();
        for (HttpCookie cookie : cookies) {
            String key = cookie.getName();
            String value = cookie.getValue();
            if (cookieMap.size() == 0 || !value.equals(cookieMap.get(key))) {
                needUpdate = true;
            }
            cookieMap.put(key, value);
            //scookies = value;
            Log.d("cookie", cookie.getName() + "---->" + cookie.getValue() + "---->" + cookie.getDomain() + "------>" + cookie.getPath());
        }

    }

}
