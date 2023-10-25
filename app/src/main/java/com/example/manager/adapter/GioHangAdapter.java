package com.example.manager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.manager.Interface.IImqgeClickListener;
import com.example.manager.R;
import com.example.manager.model.Event.TinhTongEvent;
import com.example.manager.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder>
{
    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        GioHang gioHang = gioHangList.get(position);
        holder.item_card_name.setText(gioHang.getTensp());
        holder.item_card_amount.setText(gioHang.getSoluong() + " ");
        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_cart_img);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_cart_price.setText(decimalFormat.format(gioHang.getGiasp()));
        long gia = gioHang.getSoluong() * gioHang.getGiasp();
        holder.item_card_price2.setText(decimalFormat.format(gia));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Utils.mangmuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else {
                    for (int i = 0; i<Utils.mangmuahang.size(); i++){
                        if (Utils.mangmuahang.get(i).getIdsp() == gioHang.getIdsp()){
                            Utils.mangmuahang.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });

        holder.setListener(new IImqgeClickListener() {
            @Override
            public void onImageClick(View view, int pos, int giatri)
            {
                if (giatri == 1)
                {
                    if(gioHangList.get(pos).getSoluong()>1)
                    {
                        int soluongmoi = gioHangList.get(pos).getSoluong()-1;
                        gioHangList.get(pos).setSoluong(soluongmoi);

                        holder.item_card_amount.setText(gioHangList.get(pos).getSoluong() + " ");
                        long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                        holder.item_card_price2.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    }
                    else if(gioHangList.get(pos).getSoluong() == 1)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("ALERT");
                        builder.setMessage("Do you really want to delete item out of shopping cart");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Utils.manggiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();

                    }

                }

                else if (giatri == 2)
                {
                    if (gioHangList.get(pos).getSoluong()<11)
                    {
                        int soluongmoi = gioHangList.get(pos).getSoluong()+1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                    }
                    holder.item_card_amount.setText(gioHangList.get(pos).getSoluong() + " ");
                    long gia = gioHangList.get(pos).getSoluong() * gioHangList.get(pos).getGiasp();
                    holder.item_card_price2.setText(decimalFormat.format(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());

                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgadd, imgminus, item_cart_img;
        TextView item_card_name, item_cart_price, item_card_amount, item_card_price2;
        IImqgeClickListener listener;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            item_cart_img = itemView.findViewById(R.id.item_cart_img);
            item_card_name = itemView.findViewById(R.id.item_card_name);
            item_cart_price = itemView.findViewById(R.id.item_cart_price);
            item_card_amount = itemView.findViewById(R.id.item_card_amount);
            item_card_price2 = itemView.findViewById(R.id.item_card_price2);
            imgadd = itemView.findViewById(R.id.item_cart_plus);
            imgminus = itemView.findViewById(R.id.item_cart_minus);
            checkBox = itemView.findViewById(R.id.item_cart_check);
            //click event
            imgadd.setOnClickListener(this);
            imgminus.setOnClickListener(this);

        }

        public void setListener(IImqgeClickListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View view)
        {
            if (view == imgminus)
            {
                listener.onImageClick(view,getAdapterPosition(),1);
            } else if (view == imgadd)
            {
                listener.onImageClick(view,getAdapterPosition(),2);
            }

        }
    }
}
