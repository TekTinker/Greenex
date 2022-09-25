package com.greenexagro.greenex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.greenexagro.greenex.app.AppController;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    Button btnSignup, btnLogin;
    TextView tvForgotPassword;

    @NotEmpty
    EditText etLogin;

    @Password(min = 6, scheme = Password.Scheme.ANY)
    EditText etPassword;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignup = (Button) findViewById(R.id.btSignUp);
        btnLogin = (Button) findViewById(R.id.btLogin);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgot);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        final Validator validator = new Validator(this);
        validator.setValidationListener(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), Register.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ForgotPassword.class));
            }
        });


    }

    @Override
    public void onValidationSucceeded() {

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = AppController.getInstance().getServer() + "login";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("login", etLogin.getEditableText().toString().trim());
        params.put("password", etPassword.getEditableText().toString());

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API", response.toString());

                        try {
                            if(response.getString("success").equalsIgnoreCase("1")){
                                sharedPreferences = getSharedPreferences("greenex", Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("ID", response.getJSONObject("user").getString("id"));

                                editor.putString("Token", response.getString("result"));

                                editor.putString("Name", response.getJSONObject("user").getString("name"));

                                editor.putString("Login", etLogin.getEditableText().toString());

                                editor.apply();

                                startActivity(new Intent(getApplicationContext(), Navigation.class));

                                finishAffinity();

                            }
                            else {
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
