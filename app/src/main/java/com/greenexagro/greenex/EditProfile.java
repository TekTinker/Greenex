package com.greenexagro.greenex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements Validator.ValidationListener {

    private Button btnSubmit;
    private ProgressDialog pDialog;
    private String token;

    @NotEmpty
    @Email
    private EditText etEmail;

    @NotEmpty
    private EditText etName, etTaluka, etDistrict, etAddress;

    @NotEmpty
    @Digits(integer = 10, message = "Enter valid mobile")
    @Length(min = 10, max = 10)
    private EditText etMobile;

    @NotEmpty
    @Digits(integer = 6)
    @Length(min = 6, max = 6, message = "Enter valid pin")
    private EditText etPin;


    private Validator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmail = (EditText) findViewById(R.id.etEditProfile_Email);

        etName = (EditText) findViewById(R.id.etEditProfile_Name);

        etMobile = (EditText) findViewById(R.id.etEditProfile_Mobile);

        etTaluka = (EditText) findViewById(R.id.etEditProfile_Taluka);

        etDistrict = (EditText) findViewById(R.id.etEditProfile_Dist);

        etAddress = (EditText) findViewById(R.id.etEditProfile_Address);

        etPin = (EditText) findViewById(R.id.etEditProfile_Zip);

        Intent intent = getIntent();

        etName.setText(intent.getStringExtra("Name"));
        etMobile.setText(intent.getStringExtra("Mobile"));
        etEmail.setText(intent.getStringExtra("Email"));
        etAddress.setText(intent.getStringExtra("Address"));
        etTaluka.setText(intent.getStringExtra("Taluka"));
        etDistrict.setText(intent.getStringExtra("Dist"));
        etPin.setText(intent.getStringExtra("ZIP"));
        token = intent.getStringExtra("token");


        validator = new Validator(this);

        validator.setValidationListener(this);

        btnSubmit = (Button) findViewById(R.id.btEditProfile_Submit);

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

        String url = AppController.getInstance().getServer() + "user/profile/edit";

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("name", etName.getEditableText().toString());
        params.put("email", etEmail.getEditableText().toString().trim());
        params.put("mobile", etMobile.getEditableText().toString().trim());
        params.put("address", etAddress.getEditableText().toString());
        params.put("taluka", etTaluka.getEditableText().toString());
        params.put("district", etDistrict.getEditableText().toString());
        params.put("pin", etPin.getEditableText().toString());
        params.put("token", token);

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

                                pDialog.dismiss();

                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();

                            } else if (response.getString("success").equalsIgnoreCase("0") && response.getString("code").equalsIgnoreCase("1")){
                                Toast.makeText(getBaseContext(), response.getString("result"), Toast.LENGTH_SHORT).show();
                                AppController.getInstance().logout();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finishAffinity();
                            } else {

                                JSONObject jsonObjectResult = null;

                                jsonObjectResult = response.getJSONObject("result");

                                Iterator<String> iter = jsonObjectResult.keys();

                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    try {

                                        JSONArray value = (JSONArray) jsonObjectResult.get(key);

                                        Toast.makeText(getBaseContext(), value.get(0).toString(), Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }


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
