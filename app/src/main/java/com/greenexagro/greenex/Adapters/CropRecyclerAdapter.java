package com.greenexagro.greenex.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.Models.Crop;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aniket on 19/9/16.
 */
public class CropRecyclerAdapter extends RecyclerView.Adapter<CropRecyclerAdapter.CropViewHolder> {

    private List<Crop> cropList;
    private Crop crop;
    private Context context;
    private SharedPreferences sharedPreferences;
    private String token;
    private Integer pos;

    public CropRecyclerAdapter(List<Crop> cropList, Context context) {
        this.context = context;
        this.cropList = cropList;
    }

    @Override
    public CropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crop_card_view, parent, false);
        return new CropViewHolder(view);

    }

    @Override
    public void onBindViewHolder(CropViewHolder holder, int position) {

        crop = cropList.get(position);
        pos = position;

        holder.tvCropTitle.setText(crop.getName());
        holder.tvCropArea.setText(crop.getArea().toString() + " acre");

    }

    @Override
    public int getItemCount() {
        return cropList.size();
    }

    public class CropViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCropTitle, tvCropArea;
        private ImageButton btnDelete;

        public CropViewHolder(View itemView) {
            super(itemView);
            tvCropTitle = (TextView) itemView.findViewById(R.id.tv_CardView_CropTitle);
            tvCropArea = (TextView) itemView.findViewById(R.id.tv_CardView_CropArea);
            btnDelete = (ImageButton) itemView.findViewById(R.id.img_CardView_CropDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    crop = cropList.get(getAdapterPosition());
                    sharedPreferences = context.getSharedPreferences("greenex", Context.MODE_PRIVATE);
                    token = sharedPreferences.getString("Token", "");

                    String tag_json_obj = "json_obj_req";

                    String url = AppController.getInstance().getServer() + "user/crops/delete";

                    final ProgressDialog pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("id", crop.getId().toString());

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
                                            Log.e("Position", pos.toString() + crop.getName());
                                            cropList.remove(getAdapterPosition());
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
