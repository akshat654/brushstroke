package pilani.brushstroke.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import pilani.brushstroke.Nav_Activities.Profile;
import pilani.brushstroke.R;

public class MainRegistration extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private RadioButton rbCustomer;
    private RadioButton rbBeautician;

    private Button emailButton;
    private Button facebookButton;
    private Button googleButton;

    private LoginButton loginButton;
    private CallbackManager callbackManager;


    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 2;

    private Intent next;
    private Intent next2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainregistration);


      /*  ImageView button=(ImageView) findViewById(R.id.backbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
*/


        initcomponent();

        next = new Intent(MainRegistration.this, Profile.class);
        next2 = new Intent(MainRegistration.this, SignupActivity.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                                        String gender;
                                        String birthday;

                                        Log.i("LoginActivity",
                                                response.toString());
                                        try {
                                            id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "http://graph.facebook.com/" + id + "/picture?type=large");

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            name = object.getString("name");
                                           // Log.d(TAG, name);
                                            email = object.getString("email");
                                            gender = object.getString("gender");
                                            birthday = object.getString("birthday");

                                           // Log.d(TAG, name + "\n" + id + "\n" + email + "\n" + gender + "\n" + birthday);

                                            String Email = email;
                                            String Name = name;

                                            next.putExtra("email", Email);
                                            next.putExtra("name", Name);

                                            LoginManager.getInstance().logOut();

                                            startActivity(next);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainRegistration.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
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
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });
    }

    private void initcomponent() {

        facebookButton = (Button) findViewById(R.id.facebook_signup);
        googleButton = (Button) findViewById(R.id.google_signup);
        emailButton = (Button) findViewById(R.id.email_button);

        emailButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();


        if (id == R.id.facebook_signup) {
           // Log.d(TAG, "facebook button clicked");
            loginButton.performClick();

        } else if (id == R.id.google_signup) {
            googleSignIn();


        } else if(id == R.id.email_button) {

            String useremail = "";
            String username = "";

            next.putExtra("email",useremail);
            next.putExtra("name",username);

            startActivity(next);
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String Email = acct.getEmail();
            String Name = acct.getDisplayName();
            Uri img = acct.getPhotoUrl();


            next.putExtra("email",Email);
            next.putExtra("name",Name);
            next.putExtra("img",img);

            startActivity(next);


        } else {
           // Log.d(TAG, result.getStatus().toString());
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,LoginActivity.class));
    }
}
