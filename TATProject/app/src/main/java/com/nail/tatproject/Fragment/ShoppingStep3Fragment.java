package com.nail.tatproject.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecpay.tw.mobilesdk.BANKNAME;
import com.ecpay.tw.mobilesdk.CreateTrade;
import com.ecpay.tw.mobilesdk.ENVIRONMENT;
import com.ecpay.tw.mobilesdk.OptionalATM;
import com.ecpay.tw.mobilesdk.OptionalCVS;
import com.ecpay.tw.mobilesdk.PAYMENTTYPE;
import com.ecpay.tw.mobilesdk.PaymentActivity;
import com.ecpay.tw.mobilesdk.STORETYPE;
import com.google.gson.Gson;
import com.nail.tatproject.Config;
import com.nail.tatproject.MainActivity;
import com.nail.tatproject.R;
import com.nail.tatproject.SQLite.TATDB;
import com.nail.tatproject.SQLite.TATItem;
import com.nail.tatproject.TATApplication;
import com.nail.tatproject.moudle.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 70J on 2016/6/17.
 */
public class ShoppingStep3Fragment extends Fragment {
    private LinearLayout list_product, show_product;
    private TextView shrink, final_total;
    private TextView products_sum, products_discount, products_ship, products_total, products_count;
    private TextView checkout;
    private EditText name, cell, address;
    private final String ARG_SECTION_NUMBER = "section_number";
    private int count;
    private TATApplication Global;
    private RecyclerView listView;
    private TextView country, section;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<TATItem> IDs = new ArrayList<>();
    private ArrayList<ArrayList<Section>> TAIWAN = new ArrayList<>();
    private ArrayList<String> COUNTRY = new ArrayList<>();
    private int COUNTRY_INDEX = -1;
    public int error = 0;
    private int SUM = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingStep3Fragment", "onCreate");
        setRetainInstance(true);
        products.clear();
        IDs.clear();
        TAIWAN.clear();
        COUNTRY.clear();
        Global = (TATApplication) getActivity().getApplicationContext();
        for (TATItem item : Global.tatdb.getAll(TATDB.Shopping_TABLE_NAME)) {
            String id = item.getProductID();
            long addtime = item.getAddTime();
            int count = item.getCount();
            Log.d("SQLite date", "id=" + id + " addtime" + addtime + " count" + count);
            IDs.add(new TATItem(id, addtime, count));
        }
        for (TATItem i : IDs) {
            new AsyncGetProduct().execute("http://tatvip.ezsale.tw/tat/api/getprod.ashx", i.getProductID(), i.getCount() + "");
        }
        String str = getResources().getString(R.string.source);
        //Log.d("Source", str);
        try {
            JSONObject taiwan = new JSONObject(str);
            Iterator keys = taiwan.keys();
            while (keys.hasNext()) {
                String dynamicKey = (String) keys.next();
                //Log.d("Taiwan",dynamicKey);
                COUNTRY.add(dynamicKey);
                JSONObject object = taiwan.getJSONObject(dynamicKey);
                sear(object.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Section{
        String name;
        String number;
        Section(String nw_name,String  nw_num){
            name = nw_name;
            number = nw_num;
        }
    }
    public void sear(String json) {
        try {
            ArrayList<Section> tmp = new ArrayList<>();
            //Log.d("json",json);
            JSONObject object = new JSONObject(json);
            Iterator keys = object.keys();
            while (keys.hasNext()) {
                String dynamicKey = (String) keys.next();
                String line = object.optString(dynamicKey);
                tmp.add(new Section(dynamicKey,line));
                //Log.d(dynamicKey,line);
            }
            TAIWAN.add(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidd) {
        if (hidd) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //導入Tab分頁的Fragment Layout
        Log.e("ShoppingStep3Fragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_step3, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        list_product = (LinearLayout) view.findViewById(R.id.list_porduct);
        show_product = (LinearLayout) view.findViewById(R.id.show_product);
        shrink = (TextView) view.findViewById(R.id.shrink);
        final_total = (TextView) view.findViewById(R.id.final_total);
        products_sum = (TextView) view.findViewById(R.id.products_sum);
        products_discount = (TextView) view.findViewById(R.id.products_discount);
        products_ship = (TextView) view.findViewById(R.id.products_ship);
        products_total = (TextView) view.findViewById(R.id.products_total);
        products_count = (TextView) view.findViewById(R.id.products_count);
        checkout = (TextView) view.findViewById(R.id.checkout);
        listView = (RecyclerView) view.findViewById(R.id.listView_product);
        country = (TextView) view.findViewById(R.id.spinner_country);
        section = (TextView) view.findViewById(R.id.spinner_section);
        name = (EditText) view.findViewById(R.id.name);
        cell = (EditText) view.findViewById(R.id.cell);
        address = (EditText) view.findViewById(R.id.address);
        SharedPreferences data = getActivity().getSharedPreferences("data", 0);
        count = data.getInt("products_count", 0);
        int sum = data.getInt("products_sum", 0);
        int discount = data.getInt("products_discount", 0);
        int ship = data.getInt("products_ship", 0);
        SUM = sum - discount + discount;
        listView.setNestedScrollingEnabled(false);
        listView.setHasFixedSize(true);
        final_total.setText("$" + String.format("%,d", SUM));
        products_sum.setText("$" + sum);
        products_discount.setText("-$" + discount);
        products_ship.setText("$" + ship);
        products_total.setText("$" + String.format("%,d", SUM));
        products_count.setText("共" + count + "項商品，總計");
        list_product.setVisibility(View.GONE);
        shrink.setText("總計 " + count + " 項產品  ▽");
        show_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_product.getVisibility() == View.VISIBLE) {
                    list_product.setVisibility(View.GONE);
                    shrink.setText("總計 " + count + " 項產品  ▽");
                } else {
                    list_product.setVisibility(View.VISIBLE);
                    shrink.setText("總計 " + count + " 項產品  △");
                }
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
                dialog_list.setTitle("請選擇縣市");
                String s[] = new String[COUNTRY.size()];
                s = COUNTRY.toArray(s);
                dialog_list.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        COUNTRY_INDEX = which;
                        country.setText(COUNTRY.get(which));
                        section.setText("請選擇地區");
                    }
                });
                dialog_list.show();
            }
        });
        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
                dialog_list.setTitle("請選擇地區");
                String s[];
                if (COUNTRY_INDEX != -1) {
                    ArrayList<Section> tmp;
                    s = new String[TAIWAN.get(COUNTRY_INDEX).size()];
                    tmp = TAIWAN.get(COUNTRY_INDEX);
                    int x=0;
                    for(Section i:tmp){
                        s[x++] = i.number + " "+ i.name;
                    }
                } else s = null;
                dialog_list.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (COUNTRY_INDEX != -1) {
                            section.setText(TAIWAN.get(COUNTRY_INDEX).get(which).name);
                        }
                    }
                });
                dialog_list.show();
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(name.getText().toString().trim())){
                    Toast.makeText(v.getContext(), "請輸入姓名！", Toast.LENGTH_LONG).show();
                }else if(cell.getText().length()!=10 || ("".equals(cell.getText().toString().trim())) || !(cell.getText().toString().substring(0,2).equals("09"))){
                    Toast.makeText(v.getContext(), "請輸入正確電話號碼格式！", Toast.LENGTH_LONG).show();
                }else if("請選擇縣市".equals(country.getText().toString().trim())){
                    Toast.makeText(v.getContext(), "請選擇縣市！", Toast.LENGTH_LONG).show();
                }else if("請選擇地區".equals(section.getText().toString().trim())){
                    Toast.makeText(v.getContext(), "請選擇地區！", Toast.LENGTH_LONG).show();
                }else if("".equals(address.getText().toString().trim())){
                    Toast.makeText(v.getContext(), "請輸入地址！", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(v.getContext(), "你的姓名："+name.getText()+"\n你的電話："+cell.getText()+"\n你的地址："+country.getText()+section.getText()+address.getText(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    SharedPreferences data = getActivity().getSharedPreferences("data", 0);
                    String type = data.getString("paymentType","null");
                    PAYMENTTYPE paymentType = PAYMENTTYPE.ALL;
                    if(type.substring(0,3).equals("ATM")){
                        //自動櫃員機(ATM)
                        paymentType = PAYMENTTYPE.ATM;
                        //選擇性參數，允許繳費有效天數(1~60)
//				OptionalATM oOptionalATM = new OptionalATM(7);
                        OptionalATM oOptionalATM = new OptionalATM(7, BANKNAME.parse2BankName(type.substring(4,type.length())));
                        intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oOptionalATM);
                        Log.d("paymentType", "ATM for " + type.substring(4,type.length()-1) +" " + BANKNAME.parse2BankName(type.substring(4,type.length())));
                    }else if(type.substring(0,3).equals("CVS")){
                        //超商代碼(CVS)
                        paymentType = PAYMENTTYPE.CVS;
                        //選擇性參數，交易描述1~4，會出現在超商繳費平台螢幕上
//				OptionalCVS oOptionalCVS = new OptionalCVS("測試1", "測試2", "", "", null);
                        OptionalCVS oOptionalCVS = new OptionalCVS("測試1", "測試2", "", "", STORETYPE.parse2StoreType(type.substring(4,type.length())));
                        intent.putExtra(PaymentActivity.EXTRA_OPTIONAL, oOptionalCVS);
                        Log.d("paymentType", "ATM for " + type.substring(4,type.length()-1) +" " + STORETYPE.parse2StoreType(type.substring(4,type.length())));
                    }else if(type.substring(0,6).equals("Credit")){
                        //信用卡
                        paymentType = PAYMENTTYPE.CREDIT;
                        Log.d("paymentType", "CREDIT");
                    }else{
                        paymentType = PAYMENTTYPE.ALL;
                        Log.d("paymentType", "ALL");
                    }
                    CreateTrade oCreateTrade = new CreateTrade(
                            Config.MerchantID_test,         //廠商編號
                            Config.AppCode_test,            //App代碼
                            Config.getMerchantTradeNo(),    //廠商交易編號
                            Config.getMerchantTradeDate(),  //廠商交易時間
                            SUM,        //交易金額
                            Config.TradeDesc_test,          //交易描述
                            Config.ItemName_test,           //商品名稱
                            paymentType,                    //預設付款方式
                            ENVIRONMENT.STAGE);             //介接環境 : STAGE為測試，OFFICIAL為正式

                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, oCreateTrade);
                    startActivityForResult(intent, Config.REQUEST_CODE);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        section.setText("請選擇地區");
        country.setText("請選擇縣市");
        name.setText("");
        cell.setText("");
        address.setText("");
        section.setText("國姓鄉");
        country.setText("南投縣");
        name.setText("房志剛");
        cell.setText("0988584450");
        address.setText("家");
        Log.e("ShoppingStep3Fragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ShoppingStep3Fragment", "onPause");
    }

    public class Items {
        Items(int newid) {
            ID = newid;
        }
        int ID = 1;
    }

    class AsyncGetProduct extends AsyncTask<String, Integer, Integer> {
        //================================================================
        String Reply;
        int receive_count = 1;

        @Override
        protected Integer doInBackground(String... param) {
            //get Data 單存取資料
            Gson gson = new Gson();
            String content ;
            try {
                content = "CheckM=" + URLEncoder.encode("286e5560eeac9d7ecb7ecbb6968148c7", "UTF-8");
                content += "&SiteID=" + URLEncoder.encode("778", "UTF-8");
                content += "&Type=" + URLEncoder.encode("4", "UTF-8");
                int id = Integer.valueOf(param[1]);
                receive_count = Integer.valueOf(param[2]);
                content += "&Items=" + gson.toJson(new Items(id));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return 0;
            }
            Reply = makeHttpRequest(param[0], "POST", content);
            if (Reply == null) return 0;
            else return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //此method是在doInBackground完成以後，才會呼叫的
            super.onPostExecute(result);
            if (result == 1) {
                try {
                    //http://tatex.ezsale.tw/upload/1SP-OK-001(1).JPG
                    JSONObject json_data = new JSONObject(Reply);
                    com.nail.tatproject.moudle.Product module = new com.nail.tatproject.moudle.Product();
                    module.id = json_data.optInt("ID");
                    module.subid = json_data.optInt("SubID");
                    module.image_URL = json_data.optString("Img1");
                    //module.price = json_data.optInt("Value1");
                    module.price = 1500;
                    module.name = json_data.optString("Title");
                    if (!json_data.isNull("Stock")) {
                        Log.d("Stock",json_data.getJSONArray("Stock").toString());
                        JSONArray jsonArray = json_data.getJSONArray("Stock");
                        JSONObject dataObject = jsonArray.getJSONObject(0);
                        module.type = dataObject.optString("SizeName") + dataObject.optString("ColorName") + module.id;
                        if (dataObject.has("Num")) {
                            module.product_max = dataObject.optInt("Num");
                        } else
                            module.product_max = 10;
                    } else module.product_max = 10;
                    module.count = receive_count;
                    products.add(module);
                    Log.d("Product", "ID=" + module.id + " SubID=" + module.subid + " URL=" + module.image_URL + " price=" + module.price + " count=" + module.count + " max=" + module.product_max);
                    ContactAdapter customAdapter = new ContactAdapter(products);
                    listView.setAdapter(customAdapter);
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    llm.setAutoMeasureEnabled(true);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    listView.setLayoutManager(llm);
                } catch (Exception e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                    e.printStackTrace();
                }
            } else {
                error++;
                if (error == 1) Toast.makeText(getActivity(), "資料讀取錯誤", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //=============================================================
    }

    public String makeHttpRequest(String temp_url, String method, String urlParameters) {
        HttpURLConnection conn = null;
        try {
            // 建立連線
            URL url = new URL(temp_url);
            conn = (HttpURLConnection) url.openConnection();
            //===============================
            conn.setDoOutput(true);
            // Read from the connection. Default is true.
            conn.setDoInput(true);
            // conn method
            conn.setRequestMethod(method);

            // Post 请求不能使用缓存
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Send request
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuffer sb = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            System.out.println("makeHttpRequest Error:" + e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
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

    public class ContactAdapter extends RecyclerView.Adapter<ShoppingStep3Fragment.ContactAdapter.ContactViewHolder> {

        private List<Product> contactList;


        public ContactAdapter(List<com.nail.tatproject.moudle.Product> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ShoppingStep3Fragment.ContactAdapter.ContactViewHolder holder, final int position) {
            holder.textView_name.setText(products.get(position).name);
            holder.textView_type.setText(products.get(position).type);
            String price = "$" + String.format("%,d", products.get(position).price * products.get(position).count);
            holder.textView_count.setText("X " + products.get(position).count);
            holder.textView_price.setText(price);
            if (holder.imageView_product != null) {
                if (products.get(position).image_URL != null) {
                    new ShoppingStep3Fragment.ImageDownloaderTask(holder.imageView_product).execute(products.get(position).image_URL);
                }
            }
        }

        @Override
        public ShoppingStep3Fragment.ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.list_product_simple, viewGroup, false);
            return new ShoppingStep3Fragment.ContactAdapter.ContactViewHolder(itemView, i);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView_product;
            TextView textView_name;
            TextView textView_type;
            TextView textView_count;
            TextView textView_price;

            public ContactViewHolder(View convertView, final int position) {
                super(convertView);
                imageView_product = (ImageView) convertView.findViewById(R.id.product_image);
                textView_name = (TextView) convertView.findViewById(R.id.product_name);
                textView_type = (TextView) convertView.findViewById(R.id.product_type);
                textView_count = (TextView) convertView.findViewById(R.id.product_count);
                textView_price = (TextView) convertView.findViewById(R.id.product_price);
            }
        }
    }
}
