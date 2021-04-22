package com.example.livemart.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.livemart.Interface.ItemClickListner;
import com.example.livemart.R;

public class OrdersViewHolder extends RecyclerView.ViewHolder
{
    public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
    public Button ShowOrdersBtn;


    public OrdersViewHolder(View itemView)
    {
        super(itemView);

        userName = itemView.findViewById(R.id.order_user_name);
        userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
        userTotalPrice = itemView.findViewById(R.id.order_total_price);
        userDateTime = itemView.findViewById(R.id.order_date_time);
        userShippingAddress = itemView.findViewById(R.id.order_address_city);
        ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn);
    }

}
