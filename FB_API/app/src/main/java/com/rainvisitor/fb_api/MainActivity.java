package com.rainvisitor.fb_api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ArrayList<PostModule> posts = new ArrayList<>();
    public CallbackManager callbackManager;
    public AccessToken accessToken;
    public String access_token;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView listView;
    public Button loginButton;
    public final String SharedPrefer_data = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Log.d("SharedPreferences", "SharedPreferences access token = " + access_token);
        new AsyncGetPost().execute();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        //宣告callback Manager
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGetPost().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        callbackManager = CallbackManager.Factory.create();
        loginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });
        //幫 LoginManager 增加callback function
        //這邊為了方便 直接寫成inner class
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {
                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();
                Log.d("FB", "access token got. token= " + loginResult.getAccessToken().getToken());
                SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
                data.edit()
                        .putString("Access_token", loginResult.getAccessToken().getToken())
                        .apply();
                //send request and call graph api
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                Log.d("FB", "complete\n" + object);
                                Log.d("FB", object.optString("name"));
                                Log.d("FB", object.optString("link"));
                                Log.d("FB", object.optString("id"));
                            }
                        });
                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            }
            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.d("FB", "CANCEL");
            }
            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FB", exception.toString());
            }
        });
    }

    private void init() {
        listView = (RecyclerView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_500, R.color.blue_500);  //設定swipeRefreshLayout更新時,圈圈的顏色
        loginButton = (Button) findViewById(R.id.fb_login);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public class AsyncGetPost extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
        private String targetURL = "https://graph.facebook.com/656114697817019/posts?fields=shares,permalink_url,story,created_time,picture,message,likes.limit(0).summary(true)&access_token=" ;
        //https://graph.facebook.com/656114697817019/posts?fields=shares,permalink_url,story,created_time,picture,message,likes.limit(0).summary(true)&access_token=
        private ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            pdLoading.setMessage("\t讀取中...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                access_token = data.getString("Access_token", "");
                targetURL = targetURL + access_token;
                // 利用string url构建URL对象
                URL mURL = new URL(targetURL);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);
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
            //Log.d("data",  result);
            try {
                posts.clear();
                JSONObject json_data = new JSONObject(result);
                JSONArray jsonArray = json_data.getJSONArray("data");
                for (int i =  0; i <= jsonArray.length() - 1; i++) {
                    PostModule module = new PostModule();
                    JSONObject dataObject = jsonArray.getJSONObject(i);
                    module.id = dataObject.optString("id");
                    module.title = "私房餐廳";
                    module.date = dataObject.optString("created_time").substring(0,10);
                    module.content = dataObject.optString("message");
                    module.likes = dataObject.getJSONObject("likes").getJSONObject("summary").optString("total_count") +" 喜歡";
                    //module.shares = dataObject.getJSONObject("shares").optString("count");
                    /*if(module.shares.isEmpty()){
                        module.shares = "0";
                    }*/
                    module.shares = "0" + " 分享";
                    module.image_URL = dataObject.optString("picture");
                    module.link_URL = dataObject.optString("permalink_url");
                    //Log.d("FB JSON Parser", "data "+ i + module.title + " " + module.date + " " + module.content + " " + module.image_URL);
                    posts.add(module);
                }
                Log.d("FB JSON Parser", "data size " + posts.size());
                ContactAdapter customAdapter = new ContactAdapter(posts);
                listView.setAdapter(customAdapter);
                //loginButton.setVisibility(View.INVISIBLE);
                loginButton.setEnabled(false);
                loginButton.setText("已登入");
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "資料取得失敗!", Toast.LENGTH_LONG).show();
                Log.e("FB JSON Parser", "Error parsing data " + e.toString());
                e.printStackTrace();
            }
        }

    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.mipmap.ic_launcher);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
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


    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<PostModule> contactList;

        public ContactAdapter(List<PostModule> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
            holder.textView_title.setText(posts.get(position).title);
            //holder.button_share.setText(posts.get(position).share.getText());
            holder.button_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this, posts.get(position).id, Toast.LENGTH_LONG).show();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, posts.get(position).link_URL);
                    startActivity(Intent.createChooser(sharingIntent, "分享至"));
                }
            });
            holder.textView_date.setText(posts.get(position).date);
            holder.textView_content.setText(posts.get(position).content);
            holder.textView_likes.setText(posts.get(position).likes);
            holder.textView_shares.setText(posts.get(position).shares);
            if (holder.imageView != null) {
                new ImageDownloaderTask(holder.imageView).execute(posts.get(position).image_URL);
            }
            //holder.imageView.setImageDrawable(posts.get(position).image.getDrawable());
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.list_fb_posts, viewGroup, false);
            return new ContactViewHolder(itemView);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textView_title;
            ImageButton button_share;
            TextView textView_date;
            TextView textView_content;
            ImageView imageView;
            TextView textView_likes;
            TextView textView_shares;
            public ContactViewHolder(View convertView) {
                super(convertView);
                textView_title = (TextView) convertView.findViewById(R.id.textView_title);
                button_share = (ImageButton) convertView.findViewById(R.id.button_share);
                textView_date = (TextView) convertView.findViewById(R.id.textView_date);
                textView_content = (TextView) convertView.findViewById(R.id.textView_content);
                textView_likes = (TextView) convertView.findViewById(R.id.textView_likes);
                textView_shares = (TextView) convertView.findViewById(R.id.textView_shares);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
            }
        }
    }
}
