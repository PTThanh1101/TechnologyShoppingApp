package com.example.manager.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.manager.R;
import com.example.manager.adapter.LoaiSpAdapter;
import com.example.manager.adapter.SanPhamMoiAdapter;
import com.example.manager.model.LoaiSp;
import com.example.manager.model.SanPhamMoi;
import com.example.manager.model.User;
import com.example.manager.retrofit.ApiBanHang;
import com.example.manager.retrofit.RetrofitClient;
import com.example.manager.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar Hometoolbar;
    ViewFlipper HomeviewFlipper;
    RecyclerView HomerecyclerView;
    NavigationView HomenavigationView;
    ListView HomelistView;
    DrawerLayout HomedrawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if(Paper.book().read("user")!= null)
        {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        Anhxa();
        ActionHomeToolBar();
        ActionHomeViewFlipper();
        if (KiemTraKetNoi(this))
        {
            Toast.makeText(getApplicationContext(), "Da co Internet", Toast.LENGTH_LONG).show();
            ActionHomeViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else
        {
            Toast.makeText(getApplicationContext(), "Khong co Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        messageModel -> {


                                        }, throwable -> {
                                                Log.d("get Token that bai", throwable.getMessage());
                                        }

                                    ));
                        }
                    }
                });

    }

    private void getEventClick()
    {
        HomelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                switch (i)
                {
                    case 0:
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        break;
                    case 1:
                        Intent phone = new Intent(getApplicationContext(), PhoneActivity.class);
                        phone.putExtra("loai",1);
                        startActivity(phone);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), PhoneActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent quanli = new Intent(getApplicationContext(), QuanLyActivity.class);
                        startActivity(quanli);
                        break;
                    case 7:
                        // xoa key cua user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangxuat = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangxuat);
                        finish();
                        break;
                }

            }
        });
    }

    private void getSpMoi()
    {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel ->
                        {
                            if (sanPhamMoiModel.isSuccess())
                            {
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                HomerecyclerView.setAdapter(spAdapter);
                            }
                        },
                        throwable ->
                        {
                            Toast.makeText(getApplicationContext(),"Cannot connect to Server" + throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }

                ));
    }

    private void getLoaiSanPham()
    {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess())
                            {
                                mangloaisp = loaiSpModel.getResult();

                                //Khoi tao adapter
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                HomelistView.setAdapter(loaiSpAdapter);
                            }
                        })

        );
    }

    private void ActionHomeViewFlipper()
    {
        List<String> MangQuangCao = new ArrayList<>();
        MangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Le-hoi-phu-kien-800-300.png");
        MangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-HC-Tra-Gop-800-300.png");
        MangQuangCao.add("https://mauweb.monamedia.net/thegioididong/wp-content/uploads/2017/12/banner-Samsung-Big-Note-8-800-300-GIF-1.gif");
        for (int i=0;i<MangQuangCao.size();i++)
        {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(MangQuangCao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            HomeviewFlipper.addView(imageView);
        }
        HomeviewFlipper.setFlipInterval(3000);
        HomeviewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slight_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slight_out_right);
        HomeviewFlipper.setInAnimation(slide_in);
        HomeviewFlipper.setOutAnimation(slide_out);
    }

    private void ActionHomeToolBar()
    {
        setSupportActionBar(Hometoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Hometoolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        Hometoolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HomedrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void Anhxa()
    {
        imgSearch = findViewById(R.id.imgsearch);
        Hometoolbar = findViewById(R.id.HomeToolBar);
        HomeviewFlipper = findViewById(R.id.HomeFlipperView);
        HomerecyclerView = findViewById(R.id.HomeProductView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        HomerecyclerView.setLayoutManager(layoutManager);
        HomerecyclerView.setHasFixedSize(true);
        HomelistView = findViewById(R.id.HomeListView);
        HomenavigationView = findViewById(R.id.HomeNavigationView);
        HomedrawerLayout = findViewById(R.id.HomedrawerLayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.frameCart);
        //Khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null)
        {
            Utils.manggiohang = new ArrayList<>();
        }else
        {
            int totalItem = 0;
            for (int i = 0; i<Utils.manggiohang.size(); i++)
            {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i<Utils.manggiohang.size(); i++)
        {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    public boolean KiemTraKetNoi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // Có kết nối Wi-Fi
                return true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                // Có kết nối mạng di động (3G, 4G, 5G)
                return true;
            }
        }

        return false; // Không có kết nối mạng nào
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}