package com.nail.tatproject.Fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nail.tatproject.MainActivity;
import com.nail.tatproject.R;

/**
 * Created by 70J on 2016/6/17.
 */

public class ShoppingStep2Fragment extends Fragment {
    private TextView other_option, textView_payment_method;
    private TextView products_count, products_sum, products_ship, products_all;
    private LinearLayout layout_payment_method;
    private final String ARG_SECTION_NUMBER = "section_number";
    private int ship = 0;
    private String payment[] = {"台新銀行ATM",
            "華南銀行ATM",
            "玉山銀行ATM",
            "台北富邦ATM",
            "台灣銀行ATM",
            "中國信託ATM",
            "第一銀行ATM",
            "全家、OK及萊爾富超商代碼繳款",
            "7-11 ibon代碼繳款",
            "信用卡一次付清"};
    private String paymentType[]={"ATM_TAISHIN",
            "ATM_HUANAN",
            "ATM_ESUN",
            "ATM_FUBON",
            "ATM_BOT",
            "ATM_CHINATRUST",
            "ATM_FIRST",
            "CVS_CVS",
            "CVS_IBON",
            "Credit_CreditCard"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ShoppingStep2Fragment", "onCreate");
        setRetainInstance(true);
        ship = 0;
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
        Log.e("ShoppingStep2Fragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_step2, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        products_count = (TextView) view.findViewById(R.id.products_count);
        products_sum = (TextView) view.findViewById(R.id.products_sum);
        products_ship = (TextView) view.findViewById(R.id.products_ship);
        products_all = (TextView) view.findViewById(R.id.products_all);
        other_option = (TextView) view.findViewById(R.id.other_option);
        textView_payment_method = (TextView) view.findViewById(R.id.textView_payment_method);
        layout_payment_method = (LinearLayout) view.findViewById(R.id.layout_payment_method);
        other_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "你已經按了其他選項", Toast.LENGTH_LONG).show();
                SharedPreferences data = getActivity().getSharedPreferences("data", 0);
                ship = 0;
                data.edit()
                        .putInt("products_ship", ship)
                        .apply();
            }
        });
        layout_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
                dialog_list.setTitle("請選擇付款方式");
                dialog_list.setItems(payment, new DialogInterface.OnClickListener() {
                    @Override
                    //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences data = getActivity().getSharedPreferences("data", 0);
                        data.edit()
                                .putString("paymentType", paymentType[which])
                                .apply();
                        textView_payment_method.setText(payment[which]);
                    }
                });
                dialog_list.show();
            }
        });
        SharedPreferences data = getActivity().getSharedPreferences("data", 0);
        int sum = data.getInt("products_sum", 0);
        int discount = data.getInt("products_discount", 0);
        int count = data.getInt("products_count", 0);
        int total = sum - discount;
        products_count.setText("共" + count + "項商品");
        products_sum.setText("$" + String.format("%,d", total));
        products_ship.setText("$" + String.format("%,d", ship));
        products_all.setText("$" + String.format("%,d", total + ship));
        data.edit()
                .putInt("paymentType", ship)
                .putString("paymentType", "null")
                .apply();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ShoppingStep2Fragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("ShoppingStep2Fragment", "onPause");

    }
}
