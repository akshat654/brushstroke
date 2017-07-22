package pilani.brushstroke.auth;

/**
 * Created by user on 21-07-2017.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pilani.brushstroke.R;

import static pilani.brushstroke.auth.User.mobileValid;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private static View view;
    private AppCompatButton buttonConfirm;
    private static EditText PhoneNo;
    private static TextView submit, back;
    private EditText editTextConfirmOtp;
    private RequestQueue requestQueue;
    String getPhoneNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_layout);
        initViews();
        setListeners();
    }


    // Initialize the views

    private void initViews() {
        PhoneNo = (EditText) view.findViewById(R.id.registered_phone);
        submit = (TextView) view.findViewById(R.id.forgot_button);
        back = (TextView) view.findViewById(R.id.backToLoginBtn);

    }


    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:

                // Replace Login Fragment on Back Presses
                new LoginActivity();
                break;

            case R.id.forgot_button:

                // Call Submit button task
                submitButtonTask();
                break;

        }

    }

    private void confirmOtp() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding the alert dialog
                alertDialog.dismiss();

                //Displaying a progressbar
                final ProgressDialog loading = ProgressDialog.show(ForgotPassword.this, "Authenticating", "Please wait while we check the entered code", false, false);

                //Getting the user entered otp from edittext
                final String otp = editTextConfirmOtp.getText().toString().trim();

                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONFIRM_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                if (response.equalsIgnoreCase("success")) {
                                    //dismissing the progressbar
                                    loading.dismiss();

                                    //Starting a new activity
                                    startActivity(new Intent(ForgotPassword.this, Success.class));
                                } else {
                                    //Displaying a toast if the otp entered is wrong
                                    Toast.makeText(ForgotPassword.this, "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();
                                    try {
                                        //Asking user to enter otp again
                                        confirmOtp();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put(Config.KEY_OTP, otp);
                        params.put(Config.KEY_PHONE,getPhoneNo );
                        return params;
                    }
                };

                //Adding the request to the queue
                requestQueue.add(stringRequest);
            }
        });
    }

    private void submitButtonTask() {
        getPhoneNo = PhoneNo.getText().toString();
        boolean m = mobileValid(getPhoneNo);
        // First check if email id is not null else show error toast
        if (getPhoneNo.equals("") || getPhoneNo.length() == 0)

            Toast.makeText(ForgotPassword.this, "Please enter your Phone Number!", Toast.LENGTH_LONG).show();

            // Check if email id is valid or not
        else if (!m)
            Toast.makeText(ForgotPassword.this, "Your Number is invalid!", Toast.LENGTH_LONG).show();

            // Else submit email id and fetch passwod or do your stuff
        else {
            final ProgressDialog loading = ProgressDialog.show(this, "Generating OTP", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GENERATE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            try {
                                //Creating the json object from the response
                                JSONObject jsonResponse = new JSONObject(response);

                                //If it is success
                                if(jsonResponse.getString(Config.TAG_RESPONSE).equalsIgnoreCase("Success")){
                                    //Asking user to confirm otp
                                    confirmOtp();
                                }else{
                                    //If not successful user may already have registered
                                    Toast.makeText(ForgotPassword.this, "Error occurred in generating OTP.Try again", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(ForgotPassword.this, error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding the parameters to the request
                    params.put(Config.KEY_PHONE, getPhoneNo);
                    return params;
                }
            };

            //Adding request the the queue
            requestQueue.add(stringRequest);;
        }
    }
}


