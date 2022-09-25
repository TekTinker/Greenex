package com.greenexagro.greenex;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Adapters.CropRecyclerAdapter;
import com.greenexagro.greenex.Models.Crop;
import com.greenexagro.greenex.app.AppController;
import com.greenexagro.greenex.utils.EndlessRecyclerOnScrollListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Farms extends AppCompatActivity implements Validator.ValidationListener {

    protected View cropsView;
    private RecyclerView recyclerView;
    private CropRecyclerAdapter adapter;
    private List<Crop> cropList;
    private String page = "1";
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String token;
    private Button btnAdd;
    private Validator validator;

    @NotEmpty
    private EditText etName;

    @NotEmpty
    @Digits(integer = 10, message = "Should be a number")
    private EditText etArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Crops");

        validator = new Validator(this);
        validator.setValidationListener(this);

        sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token", "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        btnAdd = (Button) findViewById(R.id.btnFarms_Add);
        etArea = (EditText) findViewById(R.id.etFarms_Area);
        etName = (EditText) findViewById(R.id.etFarms_Crop);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    validator.validate();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.crops_recyclerView);
        recyclerView.hasFixedSize();

        cropList = new ArrayList<>();

        adapter = new CropRecyclerAdapter(cropList, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        loadCrops();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                Integer c =  Integer.parseInt(page) + 1;
                page = c.toString();
                loadCrops();
            }
        });
    }

    private void loadCrops(){

        progressDialog.show();

        String url = AppController.getInstance().getServer() + "user/crops?page=" + page;

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
                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int i = 0; i < jsonArray.length(); i++){
                                Crop crop = new Crop();
                                crop.setId(Integer.parseInt(jsonArray.getJSONObject(i).getString("id")));
                                crop.setName(jsonArray.getJSONObject(i).getString("crop_name"));
                                crop.setArea(Integer.parseInt(jsonArray.getJSONObject(i).getString("area")));

                                Log.e("Crop", crop.getId().toString() + crop.getName() + crop.getArea().toString());

                                cropList.add(crop);
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

    @Override
    public void onValidationSucceeded() {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = AppController.getInstance().getServer() + "user/crops/add";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("crop", etName.getEditableText().toString());
        params.put("area", etArea.getEditableText().toString());

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
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                startActivity(getIntent());
                                finish();
                            } else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            } else {
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getBaseContext(), "Unable to contact server.", Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }

                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
