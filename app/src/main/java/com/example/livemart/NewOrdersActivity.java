package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.livemart.Model.Orders;
import com.example.livemart.Prevalent.Prevalent;
import com.example.livemart.ViewHolder.OrdersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewOrdersActivity extends AppCompatActivity
{
    private RecyclerView ordersList;
    private DatabaseReference ordersRef, UOrdersRef, ROrdersRef;
    private String ToO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_orders);

        ToO = getIntent().getExtras().get("ToO").toString();
        UOrdersRef = FirebaseDatabase.getInstance().getReference().child("UOrders");

        if(ToO.equals("0")){
            ordersRef = FirebaseDatabase.getInstance().getReference().child("UOrders").child(Prevalent.currentOnlineUser.getPhone());
        }
        else{
            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getUser());
        }

        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersRef, Orders.class)
                        .build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrdersViewHolder holder, final int position, @NonNull final Orders model)
                    {
                        if(ToO.equals("0")){
                            holder.userName.setText("Shipping Status : "+model.getState());
                            holder.userPhoneNumber.setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.userName.setText("Name: " + model.getName());
                            holder.userPhoneNumber.setText("Phone: " + model.getPhone());
                        }

                        holder.userTotalPrice.setText("Total Amount =  Rs." + model.getTotalAmount());
                        holder.userDateTime.setText("Order at: " + model.getDate() + "  " + model.getTime());
                        holder.userShippingAddress.setText("Shipping Address: " + model.getAddress() + ", " + model.getCity());

                        holder.ShowOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String phone = model.getUserPhone();
                                String pid = model.getPid();
                                Intent intent = new Intent(NewOrdersActivity.this, UserProductsActivity.class);
                                intent.putExtra("uPhone", phone);
                                intent.putExtra("pid", pid);
                                intent.putExtra("ToO", ToO);
                                startActivity(intent);
                            }
                        });

                        if(ToO.equals("1")){
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "confirm",
                                                    "Ship Products",
                                                    "Out for delivery",
                                                    "Delivered"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(NewOrdersActivity.this);
                                    builder.setTitle("Choose the order status :");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            if(i==0){
                                                model.setState("Order Confirmed");
                                                UOrdersRef.child(model.getUserPhone())
                                                        .child(model.getPid())
                                                        .child("state")
                                                        .setValue("Order Confirmed");
                                                DatabaseReference PQuantity, OQuantity;
                                            }
                                            if(i==1){
                                                model.setState("Products Shipped");
                                                UOrdersRef.child(model.getUserPhone())
                                                        .child(model.getPid())
                                                        .child("state")
                                                        .setValue("Products Shipped");
                                            }
                                            if(i==2){
                                                model.setState("Out for delivery");
                                                UOrdersRef
                                                        .child(model.getUserPhone())
                                                        .child(model.getPid())
                                                        .child("state")
                                                        .setValue("Out for delivery");
                                            }
                                            if(i==3){
                                                ordersRef.child(model.getPid()).removeValue();
                                                UOrdersRef.child(model.getUserPhone())
                                                        .child(model.getPid())
                                                        .child("state")
                                                        .setValue("Delivered");
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }

                    }

                    @NonNull
                    @Override
                    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new OrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }
}