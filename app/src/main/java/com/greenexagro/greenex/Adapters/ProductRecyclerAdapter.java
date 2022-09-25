package com.greenexagro.greenex.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.greenexagro.greenex.Buy;
import com.greenexagro.greenex.Models.Product;
import com.greenexagro.greenex.ProductDetails;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;

import java.util.List;

/**
 * Created by aniket on 14/9/16.
 */
public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>{
    private List<Product> productList;

    private ImageLoader mImageLoader;
    private Context context;

    public ProductRecyclerAdapter(List<Product> productList) {
        this.productList = productList;
    }


    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view,parent,false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        mImageLoader = AppController.getInstance().getImageLoader();

        final Product product = productList.get(position);
        holder.tvTitle.setText(product.getName());
        holder.imageView.setImageUrl(product.getImg(), mImageLoader);

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ProductDetails.class);

                intent.putExtra("id", String.valueOf(product.getId()));

                Log.d("ProductID", String.valueOf(product.getId()) );

                context.startActivity(intent);

            }
        });

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Buy.class);

                intent.putExtra("id", String.valueOf(product.getId()));

                Log.d("ProductID", String.valueOf(product.getId()) );

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle;
        public Button btnAdd,btnDetails;
        public NetworkImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvTitle = (TextView) itemView.findViewById(R.id.tv_cardView_product_title);
            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_cardView_product_img);
            btnAdd = (Button) itemView.findViewById(R.id.btn_cardView_product_add);
            btnDetails = (Button) itemView.findViewById(R.id.btn_cardView_product_view);

        }
    }
}
