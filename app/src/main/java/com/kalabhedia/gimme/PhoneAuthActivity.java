package com.kalabhedia.gimme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private LinearLayout mPhoneLayout;
    private LinearLayout mCodeLayout;

    private EditText mPhoneText;
    private EditText mCodeText;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mSendButton;

    private FirebaseAuth mAuth;
    private int btnType = 0;

    private String TAG = "PhoneAuthActivity";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    private TextView mErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        mAuth = FirebaseAuth.getInstance();

        mPhoneLayout = (LinearLayout) findViewById(R.id.phoneLayout);
        mCodeLayout = (LinearLayout) findViewById(R.id.codeLayout);

        mPhoneText = (EditText) findViewById(R.id.phoneEditText);
        mCodeText = (EditText) findViewById(R.id.codeEditText);

        mPhoneBar = (ProgressBar) findViewById(R.id.progressBarPhoneNumber);
        mCodeBar = (ProgressBar) findViewById(R.id.progressBarVerification);

        mErrorTextView = (TextView) findViewById(R.id.errorTextView);


        mSendButton = (Button) findViewById(R.id.send_verification_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnType == 0) {

                    String phonenumber = mPhoneText.getText().toString();

                    if (phonenumber.length() == 10) {
                        mPhoneBar.setVisibility(View.VISIBLE);
                        mPhoneText.setEnabled(false);
                        mSendButton.setEnabled(false);
                        phonenumber = "+91" + phonenumber;
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phonenumber,
                                60,
                                TimeUnit.SECONDS,
                                PhoneAuthActivity.this,
                                mCallBacks);
                    } else
                        Toast.makeText(PhoneAuthActivity.this, "Phone Number must be of 10 digits", Toast.LENGTH_SHORT).show();
                } else {
                    mSendButton.setEnabled(false);
                    mCodeBar.setVisibility(View.VISIBLE);
                    String verificationCode = mCodeText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mErrorTextView.setText("There was some error in verification");
                mErrorTextView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                mPhoneBar.setVisibility(View.GONE);
                mCodeLayout.setVisibility(View.VISIBLE);

                mSendButton.setText("Verify Code");

                btnType = 1;
                mSendButton.setEnabled(true);


            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Toast.makeText(PhoneAuthActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("MyTAG", "onComplete: " + (isNew ? "new user" : "old user"));
                            if (isNew == false)
                                startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));//starts main activity after successful login
                            else
                                startActivity(new Intent(PhoneAuthActivity.this,newUserActivity.class));//start newUser activity if user is new
                            finish();

                        } else {

                            mErrorTextView.setText("There was some error in logging in");
                            mErrorTextView.setVisibility(View.VISIBLE);

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
