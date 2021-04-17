package com.example.livemart.Retailer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.livemart.R;

public class RetailerCategoryActivity extends AppCompatActivity {
    private ImageView Vegetable, Dairy, Beverages, Staples, Personal, Snacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_category);

        Vegetable = findViewById(R.id.vegetable);
        Dairy = findViewById(R.id.dairy);
        Beverages = findViewById(R.id.beverages);
        Staples = findViewById(R.id.staples);
        Personal = findViewById(R.id.personal);
        Snacks = findViewById(R.id.snacks);

        Vegetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Vegetable");
                startActivity(intent);
            }
        });

        Dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Dairy");
                startActivity(intent);
            }
        });

        Beverages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Beverages");
                startActivity(intent);
            }
        });

        Snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Snacks");
                startActivity(intent);
            }
        });

        Staples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Staples");
                startActivity(intent);
            }
        });

        Personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RetailerCategoryActivity.this, RetailerAddNewActivity.class);
                intent.putExtra("category", "Personal");
                startActivity(intent);
            }
        });
    }
}