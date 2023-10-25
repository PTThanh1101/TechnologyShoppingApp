package com.example.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.manager.R;
import com.example.manager.model.CreateOrder;
import com.example.manager.model.NotiSendData;
import com.example.manager.retrofit.ApiBanHang;
import com.example.manager.retrofit.ApiPushNotification;
import com.example.manager.retrofit.RetrofitClient;
import com.example.manager.retrofit.RetrofitClientNoti;
import com.example.manager.utils.Utils;
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToanActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txtTotal, txtsdt, txtmail;
    EditText edtDiachi;
    AppCompatButton btnOrder, btnZalo;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long Totalprice;
    int totalItem;
    int iddonhang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        //ZaloPay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        initView();
        countItem();
        initControl();
    }

    private void countItem()
    {
        totalItem = 0;
        for (int i = 0; i<Utils.mangmuahang.size(); i++)
        {
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initControl()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        Totalprice = (getIntent().getLongExtra("Total", 0));
        txtTotal.setText(decimalFormat.format(Totalprice));
        txtmail.setText(Utils.user_current.getEmail());
        txtsdt.setText(Utils.user_current.getMobile());

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edtDiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(), "Please enter Address",Toast.LENGTH_SHORT).show();
                }else {
                    // post data
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test",new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOrder(str_email,str_sdt,String.valueOf(Totalprice),id,str_diachi,totalItem,new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                        messageModel ->
                                        {
                                            pushNotiToUser();
                                            Toast.makeText(getApplicationContext(), "Successful payment",Toast.LENGTH_SHORT).show();
                                            Utils.mangmuahang.clear();
                                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        },
                                        throwable ->
                                        {
                                            Toast.makeText(getApplicationContext(), throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                            ));
                }
            }
        });

        btnZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edtDiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(), "Please enter Address",Toast.LENGTH_SHORT).show();
                }else {
                    // post data
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id = Utils.user_current.getId();
                    Log.d("test",new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOrder(str_email,str_sdt,String.valueOf(Totalprice),id,str_diachi,totalItem,new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel ->
                                    {
                                        pushNotiToUser();
                                        Toast.makeText(getApplicationContext(), "Successful payment",Toast.LENGTH_SHORT).show();
                                        Utils.mangmuahang.clear();
                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        requestZalo();
                                    },
                                    throwable ->
                                    {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }


            }
        });

    }

    private void requestZalo()
    {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder("10000");
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(ThanhToanActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        compositeDisposable.add(apiBanHang.updateZalo(iddonhang,token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel ->
                                        {
                                            if(messageModel.isSuccess()) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        throwable ->
                                        {
                                            Log.d("error paying", throwable.getMessage());
                                        }
                                ));

                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d("TAGggg", "onPaymentError: ");

                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void pushNotiToUser() {
        //getToken
        compositeDisposable.add(apiBanHang.getToken(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    userModel -> {
                        if (userModel.isSuccess()){

                            for (int i=0;i< userModel.getResult().size();i++){
                                Map<String,String> data = new HashMap<>();
                                data.put("title","ATTENTION");
                                data.put("body","You have a new order");
                                NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(),data);
                                ApiPushNotification apiPushNotification = new RetrofitClientNoti().getInstance().create(ApiPushNotification.class);
                                compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                notiResponse -> {

                                                },throwable -> {
                                                    Log.d("Loi noti",throwable.getMessage());

                                                }

                                        ));
                            }


                        }
                    },
                        throwable -> {
                            Log.d("logggg", throwable.getMessage());
                        }

                ));


    }

    private void initView()
    {
        btnZalo = findViewById(R.id.btnZalo);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toolbar);
        txtTotal = findViewById(R.id.txtTotal);
        txtsdt = findViewById(R.id.txtsdt);
        txtmail = findViewById(R.id.txtemail);
        edtDiachi = findViewById(R.id.edtDiachi);
        btnOrder = findViewById(R.id.btnOrder);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }


}