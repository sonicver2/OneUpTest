package com.christofan.oneuptest.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.christofan.oneuptest.R;
import com.christofan.oneuptest.type.Product;

import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context mContext;
    private Product[] products;
    private OnItemClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cardProduct;
        public ImageView imageProduct;
        public TextView textProductName;
        public TextView textProductPrice;
        public ViewHolder.OnItemClickListener mListener;

        public ViewHolder(View v, ViewHolder.OnItemClickListener listener) {
            super(v);
            cardProduct = (CardView)v.findViewById(R.id.CardProduct);
            imageProduct = (ImageView)v.findViewById(R.id.ImageProduct);
            textProductName = (TextView)v.findViewById(R.id.TextProductName);
            textProductPrice = (TextView)v.findViewById(R.id.TextProductPrice);
            mListener = listener;

            cardProduct.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.CardProduct && mListener != null) {
                mListener.onItemClick((Product)v.getTag());
            }
        }

        public static interface OnItemClickListener {
            void onItemClick(Product obj);
        }
    }

    public ProductAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;

        products = new Product[10];
        products[0] = new Product();
        products[0].setName("Apple iPhone 6s");
        products[0].setPrice(12000000);
        products[0].setImage(R.drawable.product_apple_iphone6s);

        products[1] = new Product();
        products[1].setName("Apple iPhone 6s plus");
        products[1].setPrice(16000000);
        products[1].setImage(R.drawable.product_apple_iphone6s_plus);

        products[2] = new Product();
        products[2].setName("LG G4");
        products[2].setPrice(6500000);
        products[2].setImage(R.drawable.product_lg_g4);

        products[3] = new Product();
        products[3].setName("LG Nexus 5x");
        products[3].setPrice(8000000);
        products[3].setImage(R.drawable.product_lg_nexus5x);

        products[4] = new Product();
        products[4].setName("Microsoft Lumia 650");
        products[4].setPrice(4500000);
        products[4].setImage(R.drawable.product_microsoft_lumia_650);

        products[5] = new Product();
        products[5].setName("Microsoft Lumia 950");
        products[5].setPrice(8000000);
        products[5].setImage(R.drawable.product_microsoft_lumia_950);

        products[6] = new Product();
        products[6].setName("Samsung Galaxy Note 5");
        products[6].setPrice(9500000);
        products[6].setImage(R.drawable.product_samsung_note5);

        products[7] = new Product();
        products[7].setName("Samsung Galaxy S6 Edge");
        products[7].setPrice(10000000);
        products[7].setImage(R.drawable.product_samsung_s6edge);

        products[8] = new Product();
        products[8].setName("Sony Xperia M5");
        products[8].setPrice(5000000);
        products[8].setImage(R.drawable.product_sony_m5);

        products[9] = new Product();
        products[9].setName("Sony Xperia Z5");
        products[9].setPrice(9750000);
        products[9].setImage(R.drawable.product_sony_z5);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(v, new ViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(Product obj) {
                if (mListener != null) {
                    mListener.onItemClick(obj);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Product obj = products[position];
        holder.cardProduct.setTag(obj);
        holder.imageProduct.setImageResource(obj.getImage());
        holder.textProductName.setText(obj.getName());
        holder.textProductPrice.setText(String.format(Locale.GERMAN, "%,.0f", obj.getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.length;
    }

    public static interface OnItemClickListener {
        void onItemClick(Product obj);
    }
}
