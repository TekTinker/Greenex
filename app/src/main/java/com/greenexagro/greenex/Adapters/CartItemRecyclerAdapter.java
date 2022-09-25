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
public class CartItemRecyclerAdapter extends RecyclerView.Adapter<CartItemRecyclerAdapter.CartItemViewHolder> {
    private List<CartItem> cartItemList;

    private ImageLoader mImageLoader;
    private Context context;
    private CartItem cartItem;
    private SharedPreferences sharedPreferences;
    private String token;
    private Integer pos;

    public CartItemRecyclerAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }


    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_card_view, parent, false);

        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartItemViewHolder holder, int position) {

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

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvProductName, tvQuantity, tvPrice, tvPackageName, tvTotal;
        private Button btnEdit, btnDelete;
        private NetworkImageView imageView;

        public CartItemViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvProductName = (TextView) itemView.findViewById(R.id.tv_cartView_cartItem_ProductName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_cartView_cartItem_Quantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_cartView_cartItem_Price);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_cartView_cartItem_PriceTotal);
            tvPackageName = (TextView) itemView.findViewById(R.id.tv_cartView_cartItem_PackageName);
            imageView = (NetworkImageView) itemView.findViewById(R.id.img_cardView_cartItem);
            btnEdit = (Button) itemView.findViewById(R.id.btn_cartView_cartItem_Edit);
            btnDelete = (Button) itemView.findViewById(R.id.btn_cartView_cartItem_Remove);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Edit.class);
                    intent.putExtra("id", cartItemList.get(getAdapterPosition()).getProduct_id());
                    intent.putExtra("package_id", cartItemList.get(getAdapterPosition()).getPackage_id());
                    intent.putExtra("quantity", cartItemList.get(getAdapterPosition()).getQuantity());
                    context.startActivity(intent);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cartItem = cartItemList.get(getAdapterPosition());
                    sharedPreferences = context.getSharedPreferences("greenex", Context.MODE_PRIVATE);
                    token = sharedPreferences.getString("Token", "");

                    String tag_json_obj = "json_obj_req";

                    String url = AppController.getInstance().getServer() + "cart/delete";

                    final ProgressDialog pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("id", cartItem.getId());

                    JSONObject parameters = new JSONObject(params);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            url, parameters,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    pDialog.dismiss();
                                    Log.d("API", response.toString());

                                    try {
                                        if(response.getString("success").equalsIgnoreCase("1")){
                                            Toast.makeText(context, response.getString("result"), Toast.LENGTH_SHORT).show();
                                            cartItemList.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                            notifyDataSetChanged();
                                        } else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")) {
                                            Toast.makeText(context, response.getString("result"), Toast.LENGTH_SHORT).show();
                                            AppController.getInstance().logout();
                                        } else {
                                            Toast.makeText(context, response.getString("result"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // hide the progress dialog
                                    VolleyLog.d("API", "Error: " + error.toString());
                                    Toast.makeText(context, "Unable to contact server.", Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }

                            });

                    AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
                }
            });

        }
    }
}
