package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.livemart.Model.Cart;
import com.example.livemart.Model.Products;
import com.example.livemart.Prevalent.Prevalent;
import com.example.livemart.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProductsActivity extends AppCompatActivity {
    private RecyclerView productsList;
    private DatabaseReference cartListRef, productUpdateRef;

    private String userPhone, pid, ToO;
    private Button ConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_products);

        userPhone = getIntent().getExtras().get("uPhone").toString();
        pid = getIntent().getExtras().get("pid").toString();
        ToO = getIntent().getExtras().get("ToO").toString();
        ConfirmButton = findViewById(R.id.confirm_process_btn1);

        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);

        productUpdateRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Prevalent.currentOnlineUser.getUser());

        if(Prevalent.currentOnlineUser.getUser().equals("Customer")){
            cartListRef = FirebaseDatabase.getInstance().getReference()
                    .child("Customer items").child(Prevalent.currentOnlineUser.getPhone()).child(pid);
        }
        else if(Prevalent.currentOnlineUser.getUser().equals("Retailer")&&(ToO.equals("1"))){
            cartListRef = FirebaseDatabase.getInstance().getReference()
                            .child("Customer items").child(userPhone).child(pid);
        }
        else if(Prevalent.currentOnlineUser.getUser().equals("Retailer")&&(ToO.equals("0"))){
            cartListRef = FirebaseDatabase.getInstance().getReference()
                    .child("Retailer items").child(Prevalent.currentOnlineUser.getPhone()).child(pid);
        }
        else {
            cartListRef = FirebaseDatabase.getInstance().getReference()
                            .child("Retailer items").child(userPhone).child(pid);
        }
        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model)
            {

                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                holder.txtProductPrice.setText("Price " + model.getPrice() + "$");
                holder.txtProductName.setText(model.getPname());

                ConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfirmButton.setVisibility(View.INVISIBLE);
                        productUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Products productsData = snapshot.child(model.getPid()).getValue(Products.class);
                                int remainingQuantity = Integer.parseInt(productsData.getPquantity()) - Integer.parseInt(model.getQuantity());
                                productUpdateRef.child(model.getPid()).child("pquantity").setValue(String.valueOf(remainingQuantity));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}