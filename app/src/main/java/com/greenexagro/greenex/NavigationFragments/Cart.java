package com.greenexagro.greenex.NavigationFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Adapters.CartItemRecyclerAdapter;
import com.greenexagro.greenex.Checkout;
import com.greenexagro.greenex.Models.CartItem;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment {

    protected View cartView;
    private RecyclerView recyclerView;
    private CartItemRecyclerAdapter adapter;
    private List<CartItem> cartItemsList;
    private ProgressDialog progressDialog;
    private String token;
    private Button btnPlaceOrder;
    protected SharedPreferences sharedPreferences;


    public Cart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cartView = inflater.inflate(R.layout.fragment_cart, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");

        recyclerView = (RecyclerView) cartView.findViewById(R.id.cart_recyclerView);
        recyclerView.hasFixedSize();

        cartItemsList = new ArrayList<>();
        sharedPreferences = this.getActivity().getSharedPreferences("greenex", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token", "");

        adapter = new CartItemRecyclerAdapter(cartItemsList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        btnPlaceOrder = (Button) cartView.findViewById(R.id.btn_cart_Order);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Checkout.class));
            }
        });

        return cartView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cartItemsList.clear();
        loadCartItems();
        adapter.notifyDataSetChanged();
    }

    private void loadCartItems(){

        progressDialog.show();

        String url = AppController.getInstance().getServer() + "cart";

        Map<String, String> params = new HashMap<String, String>();

        params.put("token", token);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        Log.e("TAG", response.toString());

                        try {
                            JSONObject jsonObjectCart = response.getJSONObject("cart");
                            JSONArray jsonArrayItems = jsonObjectCart.getJSONArray("cart_items");

                            for(int i = 0; i < jsonArrayItems.length(); i++){
                                JSONObject item = jsonArrayItems.getJSONObject(i);
                                CartItem cartItem = new CartItem();
                                cartItem.setCart_id(jsonObjectCart.getString("cart_id"));
                                cartItem.setPackage_name(item.getString("package"));
                                cartItem.setImg(AppController.getInstance().getGreenex() +  "images/products/" + item.getString("img"));
                                cartItem.setPackage_id(item.getString("package_id"));
                                cartItem.setId(item.getString("id"));
                                cartItem.setPrice(item.getString("price"));
                                cartItem.setQuantity(item.getString("quantity"));
                                cartItem.setProduct_name(item.getString("product_name"));
                                cartItem.setProduct_id(item.getString("product_id"));
                                cartItemsList.add(cartItem);
                            }

                            if(cartItemsList.size() == 0){
                                Toast.makeText(getContext(), "No items in your cart.", Toast.LENGTH_LONG).show();
                                btnPlaceOrder.setVisibility(View.INVISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnPlaceOrder.setVisibility(View.INVISIBLE);
                        }



                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d("TAG", "Error: " + error.getStackTrace());
                btnPlaceOrder.setVisibility(View.INVISIBLE);
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

}
