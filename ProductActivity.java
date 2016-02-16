package com.christofan.oneuptest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.christofan.oneuptest.adapter.ProductAdapter;
import com.christofan.oneuptest.dialog.PaymentCompleteDialogFragment;
import com.christofan.oneuptest.type.Product;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener,
        DialogInterface.OnClickListener{

    private static final int PAYMENT_ACTIVITY_CODE = 0;

    private RecyclerView listProduct;
    private ProductAdapter adapterProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        listProduct = (RecyclerView)findViewById(R.id.ListProduct);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        listProduct.setLayoutManager(layoutManager);

        adapterProduct = new ProductAdapter(this, this);
        listProduct.setAdapter(adapterProduct);
    }

    @Override
    public void onItemClick(Product obj) {
        Intent paymentIntent = new Intent(this, PaymentActivity.class);
        paymentIntent.putExtra("product_price", obj.getPrice());
        startActivityForResult(paymentIntent, PAYMENT_ACTIVITY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_ACTIVITY_CODE && resultCode == RESULT_OK) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    PaymentCompleteDialogFragment f = new PaymentCompleteDialogFragment();
                    f.show(getSupportFragmentManager(), "PAYMENT_COMPLETE_TAG");
                }
            });
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            SharedPreferences pref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
            pref.edit().clear().commit();
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
