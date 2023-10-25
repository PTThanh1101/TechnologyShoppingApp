package com.example.manager.utils;
import com.example.manager.adapter.GioHang;
import com.example.manager.model.User;


import java.util.ArrayList;
import java.util.List;

public class Utils
{

    //public static final String BASE_URL = "http://192.168.1.4/banhang/";
    public static final String BASE_URL = "http://10.30.65.138/banhang/";
    //IP nha`
    // public static final String BASE_URL = "http://192.168.0.194/banhang/";
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User() ;
}
