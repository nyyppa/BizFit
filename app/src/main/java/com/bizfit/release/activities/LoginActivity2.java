package com.bizfit.release.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bizfit.release.BuildConfig;
import com.bizfit.release.DebugPrinter;
import com.bizfit.release.R;
import com.bizfit.release.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

/**
 * Created by Atte Ylivrronen on 18.4.2017.
 */

public class LoginActivity2  extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    @Nullable
    GoogleSignInAccount acct;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        String IDToken=null;
        if(BuildConfig.DEBUG)
        {
            IDToken=getString(R.string.server_client_id);
        }
        else
        {
            IDToken=getString(R.string.server_client_id_release);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //handleSignInResult(googleSignInResult);

                }
            });
        }
    }
    private void handleSignInResult(@NonNull GoogleSignInResult result) {

        if (result.isSuccess()) {
            //findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            continueToCoachPage();



        } else {
            // Signed out, show unauthenticated UI.
        }
    }


    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId())
        {
            case R.id.sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void continueToCoachPage(){
        Intent intent2=new Intent(LoginActivity2.this,MainPage.class);
        intent2.putExtra("userName",acct.getEmail());
        intent2.putExtra("firstName",acct.getGivenName());
        intent2.putExtra("lastName",acct.getFamilyName());
        intent2.putExtra("loggedIn", true);
        Intent intent3 = getIntent();
        DebugPrinter.Debug("userName:"+acct.getEmail());
        DebugPrinter.Debug("firstName:"+acct.getGivenName());
        DebugPrinter.Debug("lastName:"+acct.getFamilyName());
        if(intent3.hasExtra("coachID"))
        {
            intent2.putExtra("coachID", intent3.getStringExtra("coachID"));
            intent3.removeExtra("coachID");
        }
        User.mGoogleApiClient=mGoogleApiClient;
        startActivity(intent2);
    }
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }
}
