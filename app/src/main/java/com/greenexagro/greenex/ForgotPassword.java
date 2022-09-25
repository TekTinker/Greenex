package com.greenexagro.greenex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.greenexagro.greenex.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private EditText etLogin;
    private Button btnReset;
    private TextView tvHasPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvHasPin = (TextView) findViewById(R.id.tvForgotPassAlreadyHavePin);

        tvHasPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetPin.class));
            }
        });

        final ProgressDialog pDialog = new ProgressDialog(this);

        etLogin = (EditText) findViewById(R.id.etForgotPassLogin);
        btnReset = (Button) findViewById(R.id.btForgotPassReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag_json_obj = "json_obj_req";

                String url = AppController.getInstance().getServer() + "forgot";

                pDialog.setMessage("Loading...");
                pDialog.show();

                Map<String, String> params = new HashMap<String, String>();
                params.put("login", etLogin.getEditableText().toString());

                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        url, parameters,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                pDialog.dismiss();

                                Log.d("API", response.toString());

                                try {
                                    if (response.getString("success").equalsIgnoreCase("1")) {
                                        Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), ResetPin.class));

                                    } else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")) {
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

                                pDialog.dismiss();

                                VolleyLog.d("API", "Error: " + error.toString());
                                Toast.makeText(getBaseContext(), "Unable to contact server.", Toast.LENGTH_LONG).show();
                            }

                        });

                AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
            }
        });

    }

}
