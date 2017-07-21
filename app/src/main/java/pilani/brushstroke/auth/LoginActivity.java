package pilani.brushstroke.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import pilani.brushstroke.Nav_Activities.Profile;
import pilani.brushstroke.R;
import pilani.brushstroke.Volley.SendData;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText mEmainEditText;
    private EditText mPasswordEditText;
    private Button signInButton;
    private Button facebookButton;
    private Button googleButton;


    private static final int RC_SIGN_IN = 2;
    private static final int RC_IDP = 3;

    private TextView create_new;
    private TextView forgotPassword;

    //google
    private GoogleApiClient mGoogleApiClient;

    //facebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Intent next;

    User user;
    public static Boolean isFb=false;
    public static String host_url;

    SharedPreferences profileSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* ImageView button=(ImageView) findViewById(R.id.backbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/
        host_url = getString(R.string.host_url);

        initComponents();
        next = new Intent(this, SignupActivity.class);


        if (profileSharedPreferences.getBoolean(User.ISLOGIN,false)==true){
            startActivity(next);
            finish();
        }

        user = new User();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //facebook

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        String id;
                                        String email;
                                        String name;

                                        Log.i("LoginActivity",
                                                response.toString());
                                        try {
                                            id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "http://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.i("profile_pic",
                                                        profile_pic + "");

                                            } catch (MalformedURLException e) {
                                                Toast.makeText(LoginActivity.this,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                            name = object.getString("name");
                                            //  Log.d(TAG, name);
                                            email = object.getString("email");

                                            //  Log.d(TAG, name + "\n" + id + "\n" + email + "\n" + gender + "\n" + birthday);

                                            isFb=true;
                                            LoginManager.getInstance().logOut();
                                                    new UserLoginTask(email,"fb").execute();
                                            // check1();

//
                                            //startActivity(next);

                                        } catch (JSONException e) {
                                            if(isFb){
                                                LoginManager.getInstance().logOut();
                                                isFb=false;
                                            }
                                            e.printStackTrace();
                                            Toast.makeText(LoginActivity.this,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                      //  Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        mPasswordEditText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {

                  String  email1 = mEmainEditText.getText().toString().trim();
                  String   password1 = mPasswordEditText.getText().toString().trim();

                    if (email1.length() > 0 && password1.length() > 0) {
                        new UserLoginTask(email1, password1).execute();
                    } else {
                        Toast.makeText(LoginActivity.this, "fields can't be empty!", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

    }


    private void initComponents() {

        mEmainEditText = (EditText) findViewById(R.id.editText);
        mPasswordEditText = (EditText) findViewById(R.id.editText2);
        signInButton = (Button) findViewById(R.id.button);

        facebookButton = (Button) findViewById(R.id.fb_login);
        googleButton = (Button) findViewById(R.id.google_login);

        forgotPassword = (TextView) findViewById(R.id.textView2);
        create_new=(TextView)findViewById(R.id.textView3);

        forgotPassword.setOnClickListener(this);
        create_new.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        profileSharedPreferences = getSharedPreferences(User.Profile, Context.MODE_PRIVATE);
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.fb_login) {
            loginButton.performClick();

        } else if (id == R.id.google_login) {
            googleSignIn();

        } else if (id == R.id.button) {

          String  email1 = mEmainEditText.getText().toString().trim();
          String  password1 = mPasswordEditText.getText().toString().trim();

            if (email1.length() > 0 && password1.length() > 0) {
                new UserLoginTask(email1, password1).execute();
            } else {
                Toast.makeText(LoginActivity.this, "fields can't be empty!", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.textView2) {
            startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
        } else  if (id==R.id.textView3){
            startActivity(new Intent(LoginActivity.this,MainRegistration.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            final String Email = acct.getEmail();

            new UserLoginTask(Email,"gmail").execute();

        } else {
            Toast.makeText(this,"Please check you connection!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mEmail;
        private String mPassword = "";

        private String url = host_url+"auth/login.php";
        ProgressDialog progressDialog;



        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please Wait!");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        String des="Beautician";
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.getJSONArray("user_data");

                            JSONObject JO = jsonArray.getJSONObject(0);


                            boolean error = JO.getBoolean("error");
                            String message = JO.getString("message");
                            if (!error) {
                                progressDialog.dismiss();

                                User.saveProfileCredentials(profileSharedPreferences,
                                        JO.getString("email"),
                                        JO.getString("name"),
                                        JO.getString("address"),
                                        JO.getString("address"),
                                        JO.getString("address"),
                                        JO.getString("address"),
                                        JO.getString("mobile"),
                                        JO.getString("pincode"),
                                        "notsetup"
                                        );
                                startActivity(new Intent(LoginActivity.this,Profile.class));

                            }else {
                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                                Thread.sleep(500);
                                if(isFb){
                                    LoginManager.getInstance().logOut();
                                    isFb=false;
                                }
                            }
                        } catch (JSONException e) {
                            if(isFb){
                                LoginManager.getInstance().logOut();
                                isFb=false;
                            }
                              progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            if(isFb){
                                LoginManager.getInstance().logOut();
                                isFb=false;
                            }
                           progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                };
                    SendData loginRequest = new SendData(url,mEmail, mPassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                    Thread.sleep(500);
                return true;

            } catch (InterruptedException e) {
                if(isFb){
                    LoginManager.getInstance().logOut();
                    isFb=false;
                }
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,"Please check your internet connection!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (!success) {
                finish();
                progressDialog.dismiss();
            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
    }

    @Override
    public void onBackPressed() {
       // startActivity(new Intent(this,Splash.class));
    }


}
