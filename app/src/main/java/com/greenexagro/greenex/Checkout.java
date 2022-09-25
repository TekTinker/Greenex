package com.greenexagro.greenex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Adapters.CheckoutRecyclerAdapter;
import com.greenexagro.greenex.Models.CartItem;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CheckoutRecyclerAdapter adapter;
    private List<CartItem> cartItemsList;
    private EditText etName, etEmail, etMobile, etTaluka, etDist, etAddress, etZIP;
    private TextView tvGrandTotal;
    private Button btnEditCustomer, btnPlaceOrder;
    private ProgressDialog progressDialog;
    private String token;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        etName = (EditText) findViewById(R.id.etCheckout_Name);
        etEmail = (EditText) findViewById(R.id.etCheckout_Email);
        etMobile = (EditText) findViewById(R.id.etCheckout_Mobile);
        etTaluka = (EditText) findViewById(R.id.etCheckout_Taluka);
        etDist = (EditText) findViewById(R.id.etCheckout_Dist);
        etAddress = (EditText) findViewById(R.id.etCheckout_Address);
        etZIP = (EditText) findViewById(R.id.etCheckout_Zip);
        btnEditCustomer = (Button) findViewById(R.id.btCheckout_Submit);
        btnPlaceOrder = (Button) findViewById(R.id.btn_checkout_Confirm);
        tvGrandTotal = (TextView) findViewById(R.id.tv_Checkout_GrandTotal);

        btnEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EditProfile.class);

                intent.putExtra("Name", etName.getEditableText().toString());
                intent.putExtra("Email", etEmail.getEditableText().toString());
                intent.putExtra("Mobile", etMobile.getEditableText().toString());
                intent.putExtra("Taluka", etTaluka.getEditableText().toString());
                intent.putExtra("Dist", etDist.getEditableText().toString());
                intent.putExtra("Address", etAddress.getEditableText().toString());
                intent.putExtra("ZIP", etZIP.getEditableText().toString());
                intent.putExtra("token", token);

                startActivityForResult(intent, 1);
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeOrder();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.checkout_recyclerView);
        recyclerView.hasFixedSize();

        cartItemsList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token", "");

        adapter = new CheckoutRecyclerAdapter(cartItemsList, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        loadCartItems();
        loadCustomer();
    }

    private void loadCartItems() {

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

                        Integer total=0;

                        try {
                            JSONObject jsonObjectCart = response.getJSONObject("cart");
                            JSONArray jsonArrayItems = jsonObjectCart.getJSONArray("cart_items");

                            for (int i = 0; i < jsonArrayItems.length(); i++) {
                                JSONObject item = jsonArrayItems.getJSONObject(i);
                                CartItem cartItem = new CartItem();
                                cartItem.setCart_id(jsonObjectCart.getString("cart_id"));
                                cartItem.setPackage_name(item.getString("package"));
                                cartItem.setImg(AppController.getInstance().getGreenex() + "images/products/" + item.getString("img"));
                                cartItem.setPackage_id(item.getString("package_id"));
                                cartItem.setId(item.getString("id"));
                                cartItem.setPrice(item.getString("price"));
                                cartItem.setQuantity(item.getString("quantity"));
                                cartItem.setProduct_name(item.getString("product_name"));
                                cartItem.setProduct_id(item.getString("product_id"));
                                cartItemsList.add(cartItem);
                                total += Integer.parseInt(item.getString("price")) * Integer.parseInt(item.getString("quantity"));
                            }

                            if (cartItemsList.size() == 0) {
                                Toast.makeText(getBaseContext(), "No items in your cart.", Toast.LENGTH_LONG).show();
                            } else {
                                tvGrandTotal.setText("\u20B9" + total.toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d("TAG", "Error: " + error.getStackTrace());
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    protected void loadCustomer() {
        String url = AppController.getInstance().getServer() + "user/profile";
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
                            if (response.getString("success").equalsIgnoreCase("1")) {
                                etName.setText(response.getJSONObject("result").getString("name"));
                                etEmail.setText(response.getJSONObject("result").getString("email"));
                                etMobile.setText(response.getJSONObject("result").getString("mobile"));
                                etAddress.setText(response.getJSONObject("result").getString("address"));
                                etTaluka.setText(response.getJSONObject("result").getString("taluka"));
                                etDist.setText(response.getJSONObject("result").getString("district"));
                                etZIP.setText(response.getJSONObject("result").getString("pin"));

                                etName.setFocusable(false);
                                etEmail.setFocusable(false);
                                etMobile.setFocusable(false);
                                etAddress.setFocusable(false);
                                etTaluka.setFocusable(false);
                                etDist.setFocusable(false);
                                etZIP.setFocusable(false);
                            } else {
                                Toast.makeText(getBaseContext(), "Session expired. Login again", Toast.LENGTH_LONG).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getBaseContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("BUY Error", e.getMessage() + " response :" + response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    protected void placeOrder() {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = AppController.getInstance().getServer() + "order/add";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("payment", "cod1");
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API", response.toString());

                        try {
                            if(response.getString("success").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                finishAffinity();
                            } else {
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // hide the progress dialog
                        VolleyLog.d("API", "Error: " + error.toString());
                        Toast.makeText(getBaseContext(), "Unable to contact server.", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }

                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                loadCustomer();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }


}
