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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.app.AppController;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChangePassword extends AppCompatActivity implements Validator.ValidationListener {

    private Validator validator;
    private String token;
    private SharedPreferences sharedPreferences;

    @NotEmpty
    private EditText etPasswordOld;

    @NotEmpty
    @Password(min = 6)
    private EditText etPassword;

    @ConfirmPassword
    private EditText etRePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token", "");


        etPasswordOld = (EditText) findViewById(R.id.etChangePass_Old);

        etPassword = (EditText) findViewById(R.id.etChangePass_Password);

        etRePassword = (EditText) findViewById(R.id.etChangePass_Re_Password);

        validator = new Validator(this);

        validator.setValidationListener(this);

        Button btnSubmit = (Button) findViewById(R.id.btChangePass_Submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validator.validate();

            }
        });


    }

    @Override
    public void onValidationSucceeded() {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = AppController.getInstance().getServer() + "user/profile/change_password";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("old_password", etPasswordOld.getEditableText().toString());
        params.put("password", etPassword.getEditableText().toString());

        JSONObject parameters = new JSONObject(params);

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        pDialog.dismiss();

                        Log.d("API", response.toString());

                        try {
                            if (response.getString("success").equalsIgnoreCase("1")) {

                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_LONG).show();

                                AppController.getInstance().logout();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();

                            } else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            } else {
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_LONG).show();
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
