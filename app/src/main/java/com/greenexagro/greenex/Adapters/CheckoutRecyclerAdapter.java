package com.greenexagro.greenex.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.greenexagro.greenex.Edit;
import com.greenexagro.greenex.Models.CartItem;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aniket on 14/9/16.
 */
public class CheckoutRecyclerAdapter extends RecyclerView.Adapter<CheckoutRecyclerAdapter.CheckoutViewHolder> {
    private List<CartItem> cartItemList;

    private ImageLoader mImageLoader;
    private Context context;
    private CartItem cartItem;
    private SharedPreferences sharedPreferences;
    private String token;
    private Integer pos;

    public CheckoutRecyclerAdapter(List<CartItem> cartItemList, Context context) {
        this.context = context;
        this.cartItemList = cartItemList;
    }


    @Override
    public CheckoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_item_card_view, parent, false);

        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutViewHolder holder, int position) {

        mImageLoader = AppController.getInstance().getImageLoader();

        cartItem = cartItemList.get(position);

        Integer price = Integer.parseInt(cartItem.getPrice()) * Integer.parseInt(cartItem.getQuantity());

        holder.imageView.setImageUrl(cartItem.getImg(), mImageLoader);
        holder.tvPackageName.setText("Package : " + cartItem.getPackage_name());
        holder.tvProductName.setText(cartItem.getProduct_name());
        holder.tvPrice.setText("Price : \u20B9" + cartItem.getPrice());
        holder.tvQuantity.setText("Quantity : " + cartItem.getQuantity());
        holder.tvTotal.setText("Total : \u20B9" + price.toString());

    }

    @Override
    public int getItemCount() {

        return cartItemList.size();
    }

    class CheckoutViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName, tvQuantity, tvPrice, tvPackageName, tvTotal;
        private NetworkImageView imageView;

        public CheckoutViewHolder(final View itemView) {
            super(itemView);
            tvProductName = (TextView) itemView.findViewById(R.id.tv_cartView_checkoutItem_ProductName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_cartView_checkoutItem_Quantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_cartView_checkoutItem_Price);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_cartView_checkoutItem_PriceTotal);
            tvPackageName = (TextView) itemView.findViewById(R.id.tv_cartView_checkoutItem_PackageName);
            imageView = (NetworkImageView) itemView.findViewById(R.id.img_cardView_checkoutItem);

        }
    }
}
