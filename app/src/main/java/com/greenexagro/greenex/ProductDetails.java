package com.greenexagro.greenex;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.greenexagro.greenex.Models.Product;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetails extends AppCompatActivity {

    private NetworkImageView networkImageView;
    private WebView webViewUsage, webViewDes, webViewContents;
    private ImageLoader mImageLoader;
    private TextView tvName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mImageLoader  = AppController.getInstance().getImageLoader();

        networkImageView = (NetworkImageView) findViewById(R.id.img_ProductDetails);
        webViewUsage = (WebView) findViewById(R.id.webProductDetails_usage);
        webViewDes = (WebView) findViewById(R.id.webProductDetails_Des);
        webViewContents = (WebView) findViewById(R.id.webProductDetails_Contents);
        tvName = (TextView) findViewById(R.id.tvProductDetails_Name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Details");

        String id = getIntent().getStringExtra("id");

        String url = AppController.getInstance().getServer() + "product_details?id=" + id;

        final String imgUrl = AppController.getInstance().getGreenex() + "images/products/";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.e("TAG", response.toString());
                        try {
                            String CurrentString = response.getString("contents");
                            String[] separated = CurrentString.split(",");
                            String contents = "<div style=\"text-align:center\">";

                            for(int i=0; i<separated.length; i++){



                                contents += separated[i] + "<br>";
                            }
                            contents += "</div>";
                            tvName.setText(response.getString("name"));
                            webViewUsage.loadData(response.getString("usage"), "text/html; charset=UTF-8", null);
                            webViewDes.loadData(response.getString("description"), "text/html; charset=UTF-8", null);
                            webViewContents.loadData(contents, "text/html; charset=UTF-8", null);
                            networkImageView.setImageUrl(imgUrl + response.getString("img"), mImageLoader);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d("TAG", "Error: " + error.toString());
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

}
