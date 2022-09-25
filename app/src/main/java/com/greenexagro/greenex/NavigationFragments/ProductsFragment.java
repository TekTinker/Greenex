package com.greenexagro.greenex.NavigationFragments;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Adapters.ProductRecyclerAdapter;
import com.greenexagro.greenex.Models.Product;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;
import com.greenexagro.greenex.utils.EndlessRecyclerOnScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aniket on 12/9/16.
 */
public class ProductsFragment  extends Fragment {

    protected View productsView;
    private RecyclerView recyclerView;
    private ProductRecyclerAdapter adapter;
    private List<Product> productList;
    private String page = "1";
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        productsView = inflater.inflate(R.layout.fragment_products,container,false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");

        tvRefresh = (TextView) productsView.findViewById(R.id.tvRefresh);

        swipeRefreshLayout = (SwipeRefreshLayout) productsView.findViewById(R.id.product_swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });

        recyclerView = (RecyclerView) productsView.findViewById(R.id.product_recyclerView);
        recyclerView.hasFixedSize();

        productList = new ArrayList<>();

        adapter = new ProductRecyclerAdapter(productList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        loadProducts();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                int lastFirstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                Integer c =  Integer.parseInt(page) + 1;
                page = c.toString();
                loadProducts();
            }
        });

        return productsView;
    }

    private void loadProducts(){

        progressDialog.show();

        String url = AppController.getInstance().getServer() + "product?page=" + page;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        Log.e("TAG", response.toString());

                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int i = 0; i < jsonArray.length(); i++){
                                Product product = new Product();
                                product.setId(Integer.parseInt(jsonArray.getJSONObject(i).getString("id")));
                                product.setName(jsonArray.getJSONObject(i).getString("name"));
                                product.setImg(AppController.getInstance().getGreenex() +  "images/products/" + jsonArray.getJSONObject(i).getString("img"));

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
                progressDialog.dismiss();
                VolleyLog.d("TAG", "Error: " + error.getStackTrace());
                // hide the progress dialog
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);

        swipeRefreshLayout.setRefreshing(false);
        if(productList.size() == 0){
            recyclerView.setVisibility(View.GONE);
            tvRefresh.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvRefresh.setVisibility(View.GONE);
        }
    }
}
