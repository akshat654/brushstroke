package pilani.brushstroke.auth;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pilani.brushstroke.Nav_Activities.Profile;
import pilani.brushstroke.R;
import pilani.brushstroke.Volley.SendData;
import pilani.brushstroke.location.LocationProvider;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int RC_CHECK_GPS = 3;


    private ScrollView scrollView;
    private static final int REQUEST_CODE = 1;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mNameEditText;
    private EditText mMobileEditText;
    private EditText mStreetEditText;
    private EditText mCityEditText;
    private EditText mStateEditText;
    private EditText mPincodeEditText;

    private TextInputLayout emailInput;
    private TextInputLayout passwordInput;
    private TextInputLayout nameInput;
    private TextInputLayout mobileInput;
    private TextInputLayout streetInput;
    private TextInputLayout cityInput;
    private TextInputLayout stateInput;
    private TextInputLayout pincodeInput;

    private RelativeLayout relativeLayout;

    private ImageView mTogglePasswordImage;

    String name,email;

    private String host;

    ProgressDialog progressDialog;

boolean isfbtrue=false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isfbtrue){
            LoginManager.getInstance().logOut();
            isfbtrue=false;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        initComponents();



      /*  ImageView button=(ImageView) findViewById(R.id.backbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/
        Bundle bundle = getIntent().getExtras();
        try {
            email = bundle.getString("email");
            if (!email.isEmpty()) {
                mEmailEditText.setText(email);
                mEmailEditText.setEnabled(false);
                mPasswordEditText.setText("");
                relativeLayout.setVisibility(View.GONE);
            }
        }catch (Exception e){}
        try {
            name = bundle.getString("name");
            if (!name.isEmpty()) {
                mNameEditText.setText(name);
                mNameEditText.setEnabled(false);
            }
        }catch (Exception e){}

        try {
            isfbtrue = bundle.getBoolean("fb");
        }catch (Exception e){}



        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation(this);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            requestPermissions();
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            permissionsNotGranted();
                        }
                    }
                };
            } else {
                requestPermissions();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation(this);
            }
        }


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE
        );

    }

    private void permissionsNotGranted() {
        Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initComponents() {

        host=getString(R.string.host_url);
        scrollView = (ScrollView) findViewById(R.id.scrollViewSignup);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mNameEditText = (EditText) findViewById(R.id.name);
        mMobileEditText = (EditText) findViewById(R.id.mobile);
        mStreetEditText = (EditText) findViewById(R.id.street);
        mCityEditText = (EditText) findViewById(R.id.city);
        mStateEditText = (EditText) findViewById(R.id.state);
        mPincodeEditText = (EditText) findViewById(R.id.pincode);

        relativeLayout= (RelativeLayout) findViewById(R.id.RelativeLayoutPassword);
        emailInput = (TextInputLayout) findViewById(R.id.email_layout);
        passwordInput = (TextInputLayout) findViewById(R.id.password_layout);
        nameInput = (TextInputLayout) findViewById(R.id.name_layout);
        mobileInput = (TextInputLayout) findViewById(R.id.mobile_layout);
        streetInput = (TextInputLayout) findViewById(R.id.street_layout);
        cityInput = (TextInputLayout) findViewById(R.id.city_layout);
        stateInput = (TextInputLayout) findViewById(R.id.state_layout);
        pincodeInput = (TextInputLayout) findViewById(R.id.pincode_layout);
        mTogglePasswordImage = (ImageView) findViewById(R.id.toggle_visibility);
        Button registerButton = (Button) findViewById(R.id.button_register);

        TypedValue visibleIcon = new TypedValue();
        TypedValue slightlyVisibleIcon = new TypedValue();
        getResources().getValue(R.dimen.visibleIcone, visibleIcon, true);
        getResources().getValue(R.dimen.slightly_visible_icon, slightlyVisibleIcon, true);
        mPasswordEditText.setOnFocusChangeListener(new ImageFocusTransparencyChanger(
                mTogglePasswordImage,
                visibleIcon.getFloat(),
                slightlyVisibleIcon.getFloat()));

        mTogglePasswordImage.setOnClickListener(new PasswordToggler(mPasswordEditText));
        registerButton.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait!");
        progressDialog.setCancelable(false);

    }

    public void getLocation(Activity context) {
        LocationProvider.requestSingleUpdate(context,
                new LocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(LocationProvider.GPSCoordinates location) {
                        getAddress(floatToDouble(location.latitude), floatToDouble(location.longitude));
                    }
                });
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
         try {
                    Log.d("pin", address.getPostalCode());
                } catch (Exception e) {}
                mStreetEditText.setText(address.getAddressLine(0));
                mCityEditText.setText(address.getLocality());
                mStateEditText.setText(address.getAdminArea());
                mPincodeEditText.setText(address.getPostalCode());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private Double floatToDouble(Float f){
        return Double.parseDouble(String.valueOf(f));
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_CHECK_GPS);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation(this);
                } else {
                    //finish();
                    Toast.makeText(SignupActivity.this,"No Permission",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    public void getErrorDialog(String msg){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        final AlertDialog error = builder.create();
        error.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button_register) {
            User user = new User();
            String password;
            user.setName(mNameEditText.getText().toString());
            user.setEmail(mEmailEditText.getText().toString());
            password=mPasswordEditText.getText().toString();
            user.setMobile(mMobileEditText.getText().toString());
            user.setStreet(mStreetEditText.getText().toString());
            user.setCity(mCityEditText.getText().toString());
            user.setState(mStateEditText.getText().toString());
            user.setPincode(mPincodeEditText.getText().toString());


            passwordInput.setErrorEnabled(false);
            nameInput.setErrorEnabled(false);
            emailInput.setErrorEnabled(false);
            mobileInput.setErrorEnabled(false);
            streetInput.setErrorEnabled(false);
            cityInput.setErrorEnabled(false);
            stateInput.setErrorEnabled(false);
            pincodeInput.setErrorEnabled(false);


            String errorMessage;
            if (!User.fieldValid(user.getName())){
                errorMessage = getString(R.string.error_username);
                nameInput.setErrorEnabled(true);
                nameInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, nameInput.getTop());
                getErrorDialog(getString(R.string.error_username));
                return;
            }
            if (!user.emailValid(user.getEmail())){
                errorMessage = getString(R.string.error_email);
                emailInput.setErrorEnabled(true);
                emailInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, emailInput.getTop());
                getErrorDialog(getString(R.string.error_email));
                return;
            }
            if (email.isEmpty()) {
                if (!User.passwordValid(password)) {
                    errorMessage = getString(R.string.error_password);
                    passwordInput.setErrorEnabled(true);
                    passwordInput.setError(errorMessage);
                    scrollView.smoothScrollTo(0, passwordInput.getTop());
                    getErrorDialog(getString(R.string.error_password));
                    return;
                }
            }
            if (!User.mobileValid(user.getMobile())){
                errorMessage = getString(R.string.error_mobile);
                mobileInput.setErrorEnabled(true);
                mobileInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, mobileInput.getTop());
                getErrorDialog(getString(R.string.error_mobile));
                return;
            }
            if (!User.fieldValid(user.getStreet())){
                errorMessage = getString(R.string.error_street);
                streetInput.setErrorEnabled(true);
                streetInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, streetInput.getTop());
                getErrorDialog(getString(R.string.error_street));

                return;
            }
            if (!User.fieldValid(user.getCity())){
                errorMessage = getString(R.string.error_city);
                cityInput.setErrorEnabled(true);
                cityInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, cityInput.getTop());
                getErrorDialog(getString(R.string.error_city));
                return;
            }
            if (!User.fieldValid(user.getState())){
                errorMessage = getString(R.string.error_state);
                stateInput.setErrorEnabled(true);
                stateInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, stateInput.getTop());
                getErrorDialog(getString(R.string.error_state));
                return;
            }
            if (!User.fieldValid(user.getPincode())){
                errorMessage = getString(R.string.error_pincode);
                pincodeInput.setErrorEnabled(true);
                pincodeInput.setError(errorMessage);
                scrollView.smoothScrollTo(0, pincodeInput.getTop());
                getErrorDialog(getString(R.string.error_pincode));
                return;
            }

            new UserLoginTask(user,password).execute();
        }
    }



    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

       String url=host+"auth/register.php";
        User user;
        String password;

        UserLoginTask(User user,String password) {
           this.user=user;
            this.password=password;
        }


        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            String message;
                            boolean success = jsonResponse.getBoolean("error");
                            message =jsonResponse.getString("message") ;

                            if (!success) {
                                progressDialog.dismiss();
                                Toast.makeText(SignupActivity.this,"Register Successfully",Toast.LENGTH_LONG).show();
                                Intent start = new Intent(SignupActivity.this, Profile.class);
                                startActivity(start);
                            }else {
                               progressDialog.dismiss();
                                String s="";
                                if(message.equals("Email")){
                                    s="Email already exists!";
                                }else if(message.equals("Mobile")){
                                    s="Invalid mobile number!";
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setMessage("Register Failed! \n "+ s)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignupActivity.this,"Please check your connection! ",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                };

                SendData sendData = new SendData(url,user,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(sendData);

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Toast.makeText(SignupActivity.this,"Please check your connection! ",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (!success) {
                finish();
               progressDialog.dismiss();
            } else {
               /* password.setError(getString(R.string.error_incorrect_password));
                password.requestFocus();*/
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
