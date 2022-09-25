package com.greenexagro.greenex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Buy extends AppCompatActivity {

    private Button btnBuy, btnAdd;
    private TextView tvName;
    private Spinner spPackage;
    private NetworkImageView networkImageView;
    private ImageLoader mImageLoader;
    private HashMap<Integer, String> packageMap;
    private ArrayList<String> packageList;
    private EditText etQuantity;
    private ProgressDialog pDialog;

    SharedPreferences sharedPreferences;

    String token;

    private String id = "", packageID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        mImageLoader = AppController.getInstance().getImageLoader();

        id = getIntent().getStringExtra("id");

        sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token", "");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd = (Button) findViewById(R.id.btnBuy_Add);
        btnBuy = (Button) findViewById(R.id.btnBuy_Buy);
        spPackage = (Spinner) findViewById(R.id.spBuy_Package);
        tvName = (TextView) findViewById(R.id.tvBuy_Name);
        networkImageView = (NetworkImageView) findViewById(R.id.img_Buy);
        etQuantity = (EditText) findViewById(R.id.etBuyQuantity);

        String id = getIntent().getStringExtra("id");

        String url = AppController.getInstance().getServer() + "product_details?id=" + id;

        final String imgUrl = AppController.getInstance().getGreenex() + "images/products/";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        Log.e("TAG", response.toString());
                        try {
                            tvName.setText(response.getString("name"));
                            networkImageView.setImageUrl(imgUrl + response.getString("img"), mImageLoader);

                            JSONArray jsonArray = response.getJSONArray("packages");
                            packageList = new ArrayList<String>();
                            packageMap = new HashMap<Integer, String>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                packageList.add(
                                        jsonArray.getJSONObject(i).getString("package") + " - Rs." +
                                        jsonArray.getJSONObject(i).getString("price")
                                );
                                packageMap.put(i, jsonArray.getJSONObject(i).getString("id"));
                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_spinner_item, packageList);

                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPackage.setAdapter(spinnerAdapter);

                            spPackage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    packageID = packageMap.get(i);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    btnAdd.setEnabled(false);
                                    btnBuy.setEnabled(false);
                                }
                            });


                        } catch (JSONException e) {
                            Toast.makeText(getBaseContext(), "Session expired. Login again", Toast.LENGTH_LONG).show();
                            AppController.getInstance().logout();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                VolleyLog.d("TAG", "Error: " + error.getStackTrace());
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
                finish();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
                startActivity(new Intent(getBaseContext(), Checkout.class));
            }
        });
    }

    protected void addToCart(){
        String tag_json_obj = "json_obj_req";

        String url = AppController.getInstance().getServer() + "cart/add";
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("package_id", packageID);
        params.put("quantity", etQuantity.getEditableText().toString());

        Log.e("Params", params.toString());

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API", response.toString());
                        pDialog.dismiss();

                        try {
                            if(response.getString("success").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            } else {
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("BUY Error", e.getMessage() + " response :" + response.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // hide the progress dialog
                        VolleyLog.d("API", "Error: " + error.toString());
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }

                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

}
