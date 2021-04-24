package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livemart.Model.Cart;
import com.example.livemart.Prevalent.Prevalent;
import com.example.livemart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button NextProcessBtn, ConfirmButton;
    private TextView txtTotalAmount, txtMsg1, modeOfPayment;
    private RadioGroup modeOfPurchase ;
    private RadioButton cod;
    private String orderRandomKey, saveCurrentDate, saveCurrentTime;
    private DatabaseReference cartListRef;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        modeOfPurchase = findViewById(R.id.mop);
        modeOfPayment = findViewById(R.id.mode_of_payment);
        cod = findViewById(R.id.cod);
        ConfirmButton = findViewById(R.id.confirm_process_btn);
        cartListRef =  FirebaseDatabase.getInstance().getReference().child("Cart List");


        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        //txtMsg1 = (TextView) findViewById(R.id.msg1);

        modeOfPurchase.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                i = modeOfPurchase.getCheckedRadioButtonId();
                if (i == R.id.offline){
                    NextProcessBtn.setVisibility(View.INVISIBLE);
                    modeOfPayment.setVisibility(View.INVISIBLE);
                    cod.setVisibility(View.INVISIBLE);
                    ConfirmButton.setVisibility(View.VISIBLE);
                }
                else if(i == R.id.online) {
                    ConfirmButton.setVisibility(View.INVISIBLE);
                    NextProcessBtn.setVisibility(View.VISIBLE);
                    modeOfPayment.setVisibility(View.VISIBLE);
                    cod.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(CartActivity.this,"Please select mode of purchase",Toast.LENGTH_SHORT).show();
                }
            }
        });



        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmOrder();
            }
        });

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (overTotalPrice!=0){
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CartActivity.this,"No items in the cart",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void ConfirmOrder() {
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
        ordersMap.put("totalAmount", String.valueOf(overTotalPrice));
        ordersMap.put("name", Prevalent.currentOnlineUser.getName());
        ordersMap.put("phone", Prevalent.currentOnlineUser.getPhone());
        ordersMap.put("address", "Not applicable");
        ordersMap.put("city", "");
        ordersMap.put("mode", "Offline");
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
                                        Toast.makeText(CartActivity.this, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
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
                    Toast.makeText(CartActivity.this, "Wait for order updates",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
       // CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child(Prevalent.currentOnlineUser.getUser())
                                .child(Prevalent.currentOnlineUser.getPhone()), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                holder.txtProductPrice.setText("Price " + model.getPrice() + "$");
                holder.txtProductName.setText(model.getPname());

                int oneTyprProductTPrice = Integer.parseInt(model.getPrice()) * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTyprProductTPrice;

                txtTotalAmount.setText("Total Price: Rs." + String.valueOf(overTotalPrice));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i == 0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1)
                                {
                                    cartListRef.child(Prevalent.currentOnlineUser.getUser())
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Item removed successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

//    private void CheckOrderState() {
//        DatabaseReference ordersRef;
//        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
//
//        ordersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                if (dataSnapshot.exists())
//                {
//                    String shippingState = dataSnapshot.child("state").getValue().toString();
//                    String userName = dataSnapshot.child("name").getValue().toString();
//
//                    if (shippingState.equals("shipped"))
//                    {
//                        txtTotalAmount.setText("Dear " + userName + "\n order is shipped successfully.");
//                        recyclerView.setVisibility(View.GONE);
//
//                        txtMsg1.setVisibility(View.VISIBLE);
//                        txtMsg1.setText("Congratulations, your final order has been Shipped successfully. Soon you will received your order at your door step.");
//                        NextProcessBtn.setVisibility(View.GONE);
//
//                        Toast.makeText(CartActivity.this, "you can purchase more products, once you received your first final order.", Toast.LENGTH_SHORT).show();
//                    }
//                    else if(shippingState.equals("not shipped"))
//                    {
//                        txtTotalAmount.setText("Shipping State = Not Shipped");
//                        recyclerView.setVisibility(View.GONE);
//
//                        txtMsg1.setVisibility(View.VISIBLE);
//                        NextProcessBtn.setVisibility(View.GONE);
//
//                        Toast.makeText(CartActivity.this, "you can purchase more products, once you received your first final order.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}