package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.livemart.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;
    private String orderRandomKey, saveCurrentDate, saveCurrentTime;
    private DatabaseReference cartListRef;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price =  Rs. " + totalAmount, Toast.LENGTH_SHORT).show();


        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        phoneEditText = (EditText) findViewById(R.id.shippment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shippment_address);
        cityEditText = (EditText) findViewById(R.id.shippment_city);

        cartListRef =  FirebaseDatabase.getInstance().getReference().child("Cart List");

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                check();
            }
        });
    }

    private void check()
    {
        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full name.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your city name.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderRandomKey = saveCurrentDate + saveCurrentTime;
        final DatabaseReference ordersRef;
        final DatabaseReference userOrder = FirebaseDatabase.getInstance().getReference()
                .child("UOrders")
                .child(Prevalent.currentOnlineUser.getPhone()).child(orderRandomKey);

        if(Prevalent.currentOnlineUser.getUser().equals("Customer")){
            ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child("Retailer")
                    .child(orderRandomKey);
        }
        else {
            ordersRef = FirebaseDatabase.getInstance().getReference()
                    .child("Orders")
                    .child("Wholesaler")
                    .child(orderRandomKey);
        }

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("pid", orderRandomKey);
        ordersMap.put("userPhone", Prevalent.currentOnlineUser.getPhone());
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("mode", "Online");
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "Order Placed");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    final DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference()
                            .child(Prevalent.currentOnlineUser.getUser()+" items")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .child(orderRandomKey);

                    DatabaseReference cartRef = cartListRef.child(Prevalent.currentOnlineUser.getUser())
                            .child(Prevalent.currentOnlineUser.getPhone());

                    cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot itemCode : snapshot.getChildren()){
                                String itemCodeKey = itemCode.getKey();
                                for (DataSnapshot itemDetails : itemCode.getChildren()) {
                                    String data = itemDetails.getValue(String.class);
                                    itemRef.child(itemCodeKey).child(itemDetails.getKey()).setValue(data);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    cartListRef
                            .child(Prevalent.currentOnlineUser.getUser())
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        userOrder.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                   Toast.makeText(ConfirmFinalOrderActivity.this, "Wait for order updates",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}