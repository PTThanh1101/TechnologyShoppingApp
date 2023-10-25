package com.example.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.manager.R;
import com.example.manager.adapter.GioHangAdapter;
import com.example.manager.model.Event.TinhTongEvent;
import com.example.manager.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

public class GioHangActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnBuy;
    GioHangAdapter adapter;
    long totalPrice = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        initView();
        initControl();

        if(Utils.mangmuahang!=null)
        {
            Utils.mangmuahang.clear();
        }
        totalPrice();
    }

    private void totalPrice()
    {
         totalPrice = 0;
        for (int i = 0; i<Utils.mangmuahang.size(); i++)
        {
            totalPrice = totalPrice + Utils.mangmuahang.get(i).getGiasp()*Utils.mangmuahang.get(i).getSoluong();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

        tongtien.setText(decimalFormat.format(totalPrice));
    }

    private void initControl()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(Utils.manggiohang.size()== 0)
        {
            giohangtrong.setVisibility(View.VISIBLE);
        }else
        {
            adapter = new GioHangAdapter(getApplicationContext(),Utils.manggiohang);
            recyclerView.setAdapter(adapter);
        }

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("Total",totalPrice);
                Utils.manggiohang.clear();

                startActivity(intent);


            }
        });

    }

    private void initView()
    {
        giohangtrong = findViewById(R.id.txtEmptyCart);
        tongtien = findViewById(R.id.txtTotal);
        toolbar = findViewById(R.id.GHtoolbar);
        recyclerView = findViewById(R.id.recycleview_cart);
        btnBuy = findViewById(R.id.btnBuy);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event)
    {
        if (event !=null)
        {
            totalPrice();
        }
    }
}