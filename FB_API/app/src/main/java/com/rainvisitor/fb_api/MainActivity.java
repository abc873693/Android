package com.rainvisitor.fb_api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public ArrayList<PostModule> posts = new ArrayList<>();
    public String access_token = "EAAa2ZBnr08RMBAD2jm2CqGliLKacHHPxBbgpf67cAoZAGzDWTZBLg8QST9nQbpZCUGC9EhQKx10xd062G5ApM4mxn6JiZBLL8ABQNi76mACGkP2tj3Ro1OfZAgL72VUZCWbcMfZC0eZCYveQPbALFN8RwMZBEctqhH0F0ZD";
    String page_name = "", page_picture_url = "";
    public final String page_id = "496974947026732";
    //https://graph.facebook.com/496974947026732/posts?fields=shares,permalink_url,story,created_time,picture,message,likes.limit(0).summary(true)&access_token=
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView listView;
    public final String SharedPrefer_data = "data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        new AsyncGetAccessToken().execute();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = listView.getChildLayoutPosition(v);
                Toast.makeText(MainActivity.this, itemPosition, Toast.LENGTH_LONG).show();
            }
        });
        //宣告callback Manager
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncGetPost().execute();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void init() {
        listView = (RecyclerView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_500, R.color.blue_500);  //設定swipeRefreshLayout更新時,圈圈的顏色
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public class AsyncGetAccessToken extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        private String app_id = "1890036781216019";
        private String client_secret=  "436042e789f65561149034da5c736774";
        private String targetURL = "https://graph.facebook.com/v2.6/oauth/access_token?client_id=" + app_id + "&client_secret="
                + client_secret + "&grant_type=client_credentials";
        //https://graph.facebook.com/v2.6/oauth/access_token?client_id=1890036781216019&client_secret=436042e789f65561149034da5c736774&grant_type=client_credentials
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL mURL = new URL(targetURL);
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(10000);
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    return getStringFromInputStream(is);
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
            Log.d("data", result);
            try {
                posts.clear();
                JSONObject json_data = new JSONObject(result);
                access_token = json_data.optString("access_token");
                Log.d("access_token", access_token);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "資料取得失敗!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                new AsyncGetName().execute();
                new AsyncGetPost().execute();
            }
        }
    }

    public class AsyncGetName extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        SharedPreferences data = getSharedPreferences(SharedPrefer_data, 0);
        private String targetURL = "https://graph.facebook.com/v2.6/" + page_id + "?fields=picture,name&access_token=";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //access_token = data.getString("Access_token", "");
                Log.d("access_token", access_token);
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
                    return getStringFromInputStream(is);
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
            Log.d("data", result);
            try {
                posts.clear();
                JSONObject json_data = new JSONObject(result);
                page_name = json_data.optString("name");
                page_picture_url = json_data.getJSONObject("picture").getJSONObject("data").optString("url");
                Log.d("page", "name = " + page_name);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "資料取得失敗!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }

    public class AsyncGetPost extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        private String targetURL = "https://graph.facebook.com/v2.6/" + page_id + "/posts?fields=full_picture,shares,permalink_url,story,created_time,message,likes.limit(0).summary(true),source,attachments{subattachments},type&locale=zh_TW&limit=25&offset=0&show_expired=true&access_token=";
        //https://graph.facebook.com/656114697817019/posts?fields=shares,permalink_url,story,created_time,picture,message,likes.limit(0).summary(true)&access_token=
        //posts?fields=full_picture,shares,permalink_url,story,created_time,message,likes.limit(0).summary(true),source,attachments{subattachments},type&locale=zh_TW&limit=25&offset=0&show_expired=true
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
            Log.d("data", result);
            try {
                posts.clear();
                JSONObject json_data = new JSONObject(result);
                JSONArray jsonArray = json_data.getJSONArray("data");
                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    PostModule module = new PostModule();
                    JSONObject dataObject = jsonArray.getJSONObject(i);
                    module.id = dataObject.optString("id");
                    module.title = page_name;
                    String tmp_date = dataObject.optString("created_time");
                    module.date = tmp_date.substring(0, 10) + " " + tmp_date.substring(11, 19);
                    module.content = dataObject.optString("message");
                    module.likes = dataObject.getJSONObject("likes").getJSONObject("summary").optString("total_count") + " 喜歡";
                    if (dataObject.has("shares")) {
                        module.shares = dataObject.getJSONObject("shares").optString("count");
                    } else module.shares = "0";
                    module.shares += " 分享";
                    if (dataObject.has("full_picture")) {
                        module.image_picture_URL = dataObject.optString("full_picture");
                    } else module.image_picture_URL = "";
                    module.image_page_URL = page_picture_url;
                    module.link_URL = dataObject.optString("permalink_url");
                    if (dataObject.has("source")) {
                        module.video_link_URL = dataObject.optString("source");
                    } else module.video_link_URL = "";
                    if (dataObject.has("attachments")) {
                        String tmp = dataObject.getJSONObject("attachments").getJSONArray("data").toString();
                        JSONObject attachObject = new JSONObject(tmp.substring(1, tmp.length() - 1));
                        if (attachObject.has("subattachments")) {
                            module.image_json = attachObject.getJSONObject("subattachments").toString();
                        } else module.image_json = "";
                    } else module.image_json = "";
                    module.type = dataObject.optString("type");
                    Log.d("FB JSON Parser", "data " + i + module.title + " " + module.date + " " + module.content + " " + module.image_picture_URL + " " + module.shares + "\n" + module.image_json);
                    posts.add(module);
                }
                Log.d("FB JSON Parser", "data size " + posts.size());
                ContactAdapter customAdapter = new ContactAdapter(posts);
                listView.setAdapter(customAdapter);
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
                        imageView.setImageDrawable(null);
                        imageView.setVisibility(View.GONE);
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
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
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
            if (holder.imageView_post != null) {
                if (posts.get(position).image_picture_URL != null) {
                    holder.imageView_post.setVisibility(View.VISIBLE);
                    new ImageDownloaderTask(holder.imageView_post).execute(posts.get(position).image_picture_URL);
                }
            }
            if (holder.imageView_page != null) {
                if (posts.get(position).image_page_URL != null) {
                    new ImageDownloaderTask(holder.imageView_page).execute(posts.get(position).image_page_URL);
                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = new Activity();
                    String url =  posts.get(position).link_URL;
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    Bitmap icon = BitmapFactory
                            .decodeResource(getResources(), R.drawable.share);
                    builder.setActionButton(icon, "share",
                            Utils.createSharePendingIntent(MainActivity.this
                                    , url));
                    builder.setToolbarColor(
                            ContextCompat.getColor(MainActivity.this, R.color.blue_300));
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(MainActivity.this
                            , Uri.parse(url));
                }
            });
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
            ImageView imageView_post;
            ImageView imageView_page;
            TextView textView_likes;
            TextView textView_shares;
            CardView cardView;

            public ContactViewHolder(View convertView) {
                super(convertView);
                textView_title = (TextView) convertView.findViewById(R.id.textView_title);
                button_share = (ImageButton) convertView.findViewById(R.id.button_share);
                textView_date = (TextView) convertView.findViewById(R.id.textView_date);
                textView_content = (TextView) convertView.findViewById(R.id.textView_content);
                textView_likes = (TextView) convertView.findViewById(R.id.textView_likes);
                textView_shares = (TextView) convertView.findViewById(R.id.textView_shares);
                imageView_post = (ImageView) convertView.findViewById(R.id.imageView_post);
                imageView_page = (ImageView) convertView.findViewById(R.id.imageView_page);
                cardView = (CardView) convertView.findViewById(R.id.card_view);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        callWebView(posts.get(position).link_URL);
                    }
                });
                imageView_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        /*String type =  posts.get(position).type;
                        if(type.equals("photo"))callImageView(posts.get(position).image_picture_URL, posts.get(position).image_json);
                        else if(type.equals("video"))callVidioView(posts.get(position).video_link_URL, "fb_video");*/
                        callWebView(posts.get(position).link_URL);
                    }
                });
            }
        }
    }

    public void callWebView(String url) {
        /*Intent intent = new Intent(MainActivity.this, WebActivity.class);
        intent.putExtra("url", url);
        //intent.putExtra("type", type);
        startActivity(intent);*/
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.blue_300));
        customTabsIntent.launchUrl(this, Uri.parse(url));
        Log.d("URL",url);
    }

    public void callVidioView(String url, String type) {
        Intent intent = new Intent(MainActivity.this, Custom_VideoView.class);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    public void callImageView(String url, String json) {
        Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
        intent.putExtra("image_json", json);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
