package com.greenexagro.greenex;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Adapters.ProductRecyclerAdapter;
import com.greenexagro.greenex.Models.Product;
import com.greenexagro.greenex.app.AppController;
import com.greenexagro.greenex.utils.EndlessRecyclerOnScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductRecyclerAdapter adapter;
    private List<Product> productList;
    private String page = "1";
    private String query = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent searchIntent = getIntent();

        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
            query = searchIntent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle(query);
        }

        recyclerView = (RecyclerView) findViewById(R.id.search_recyclerView);
        recyclerView.hasFixedSize();

        productList = new ArrayList<>();

        adapter = new ProductRecyclerAdapter(productList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        loadProducts();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                Integer c = Integer.parseInt(page) + 1;
                page = c.toString();
                loadProducts();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    private void loadProducts() {

        progressDialog.setMessage("Loading");
        progressDialog.show();

        String url = AppController.getInstance().getServer() + "search?page=" + page + "&name=" + query;



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", response.toString());

                        progressDialog.dismiss();

                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Product product = new Product();
                                product.setId(Integer.parseInt(jsonArray.getJSONObject(i).getString("id")));
                                product.setName(jsonArray.getJSONObject(i).getString("name"));
                                product.setImg(AppController.getInstance().getGreenex() + "images/products/" + jsonArray.getJSONObject(i).getString("img"));

                                productList.add(product);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getStackTrace());
                // hide the progress dialog
                progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

}