package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livemart.Model.Users;
import com.example.livemart.Prevalent.Prevalent;
import com.example.livemart.Retailer.RetailerCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber, InputPassword, EnterOtp;
    private Button LoginButton, SendOtp, VerifyOtp;
    private ProgressDialog loadingBar;
    private TextView Customer, Retailer, Wholesaler;

    private String parentDbName = "Customer", verificationId, verificationStatus ;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = findViewById(R.id.login_btn);
        SendOtp = findViewById(R.id.send_otp);
        VerifyOtp = findViewById(R.id.verify_otp);
        InputPassword = findViewById(R.id.login_password_input);
        InputPhoneNumber = findViewById(R.id.login_phone_number_input);
        EnterOtp = findViewById(R.id.enter_OTP);
        Customer = findViewById(R.id.Customer);
        Retailer = findViewById(R.id.Retailer);
        Wholesaler = findViewById(R.id.Wholesaler);
        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginUser();
            }
        });

        Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login as Customer");
                Customer.setVisibility(View.INVISIBLE);
                Retailer.setVisibility(View.VISIBLE);
                Wholesaler.setVisibility(View.VISIBLE);
                parentDbName = "Customer";
            }
        });

        Wholesaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login as Wholesaler");
                Customer.setVisibility(View.VISIBLE);
                Wholesaler.setVisibility(View.INVISIBLE);
                Retailer.setVisibility(View.VISIBLE);
                parentDbName = "Wholesaler";
            }
        });

        Retailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login as Retailer");
                Wholesaler.setVisibility(View.VISIBLE);
                Customer.setVisibility(View.VISIBLE);
                Retailer.setVisibility(View.INVISIBLE);
                parentDbName = "Retailer";
            }
        });
    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){
                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)){
                        if (usersData.getPassword().equals(password)){
                            if(usersData.getUser().equals(parentDbName)){
                                if (parentDbName.equals("Customer"))
                                {
                                    Toast.makeText(LoginActivity.this, "Welcome "+usersData.getName()+", you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser = usersData;
                                    startActivity(intent);
                                }
                                else if (parentDbName.equals("Retailer"))
                                {
                                    Toast.makeText(LoginActivity.this, "Welcome "+usersData.getName()+", you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("user", usersData.getUser());
                                    Prevalent.currentOnlineUser = usersData;
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Welcome "+usersData.getName()+", you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, RetailerCategoryActivity.class);
                                    intent.putExtra("user", usersData.getUser());
                                    startActivity(intent);
                                }
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"You're not a "+parentDbName, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Password is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}