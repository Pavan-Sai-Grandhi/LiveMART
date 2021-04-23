package com.example.livemart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.livemart.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity
{
    private String DOB,verify="Not Verified";
    private EditText Security,phone,NewPassword,ConfirmPassword ;
    private Button VerifyDob,ChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Security = findViewById(R.id.dob);
        phone = findViewById(R.id.find_phone_number);
        NewPassword = findViewById(R.id.new_password);
        ConfirmPassword = findViewById(R.id.confirm_new_password);
        VerifyDob = findViewById(R.id.verify_security);
        ChangePassword = findViewById(R.id.change_password);


        VerifyDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(phone.getText().toString());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        String data1,data2;
                        data1= Security.getText().toString();
                        data2= snapshot.child("dob").getValue(String.class);

                        if(data1.equals(data2))
                        {
                            verify="verified";
                            Toast.makeText(ForgotPasswordActivity.this, "Number Verified",Toast.LENGTH_SHORT).show();
                            NewPassword.setVisibility(View.VISIBLE);
                            ConfirmPassword.setVisibility(View.VISIBLE);
                            ChangePassword.setVisibility(View.VISIBLE);

                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, "Number NOt Verified",Toast.LENGTH_SHORT).show();

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(phone.getText().toString()).child("password");
                ref.setValue(NewPassword.getText().toString());
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
            }
        });




    }
}