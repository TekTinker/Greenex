package com.greenexagro.greenex.NavigationFragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.ChangePassword;
import com.greenexagro.greenex.EditProfile;
import com.greenexagro.greenex.Farms;
import com.greenexagro.greenex.LoginActivity;
import com.greenexagro.greenex.R;
import com.greenexagro.greenex.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    protected View view;
    private Button btnEdit, btnPassword, btnFarms;
    private EditText etName, etEmail, etMobile, etTaluka, etDist, etAddress, etZIP;
    private TextView tvUID;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private String token;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                load();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        etName = (EditText) view.findViewById(R.id.etProfile_Name);
        etEmail = (EditText) view.findViewById(R.id.etProfile_Email);
        etMobile = (EditText) view.findViewById(R.id.etProfile_Mobile);
        etTaluka = (EditText) view.findViewById(R.id.etProfile_Taluka);
        etDist = (EditText) view.findViewById(R.id.etProfile_Dist);
        etAddress = (EditText) view.findViewById(R.id.etProfile_Address);
        etZIP = (EditText) view.findViewById(R.id.etProfile_ZIP);
        tvUID = (TextView) view.findViewById(R.id.tvProfile_UID);
        btnEdit = (Button) view.findViewById(R.id.btnProfile_Edit);
        btnFarms = (Button) view.findViewById(R.id.btnProfile_Farms);
        btnPassword = (Button) view.findViewById(R.id.btnProfile_Pass);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfile.class);

                intent.putExtra("Name",etName.getEditableText().toString());
                intent.putExtra("Email",etEmail.getEditableText().toString());
                intent.putExtra("Mobile",etMobile.getEditableText().toString());
                intent.putExtra("Taluka",etTaluka.getEditableText().toString());
                intent.putExtra("Dist",etDist.getEditableText().toString());
                intent.putExtra("Address",etAddress.getEditableText().toString());
                intent.putExtra("ZIP",etZIP.getEditableText().toString());
                intent.putExtra("token",token);

                startActivityForResult(intent, 1);
            }
        });

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChangePassword.class));
            }
        });

        btnFarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), Farms.class));
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences("greenex", Context.MODE_PRIVATE);

        token = sharedPreferences.getString("Token", null);

        load();

        return view;
    }

    protected void load(){
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
                                if(response.getJSONObject("result").getString("role").equalsIgnoreCase("consultant")){
                                    btnFarms.setVisibility(View.INVISIBLE);
                                }
                                tvUID.setText(response.getJSONObject("result").getString("uid"));
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
                                Toast.makeText(getContext(), "Session expired. Login again", Toast.LENGTH_LONG).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                getActivity().finishAffinity();
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

}