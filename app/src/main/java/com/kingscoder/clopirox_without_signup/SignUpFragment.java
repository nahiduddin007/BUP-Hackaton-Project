package com.kingscoder.clopirox_without_signup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {

    private TextView alreadyHaveAccTV;
    private FrameLayout parentFrameLayout;
    private View rootView;
    private EditText emailET, fullNameET, passET, confirmPassET;
    private Button signUpButton, signUpWithGoogleButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar mProgressBar;
    private FirebaseFirestore mFireStore;

    public SignUpFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        initialization();
        return rootView;
    }

    private void initialization() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        alreadyHaveAccTV = rootView.findViewById(R.id.already_have_account_textview);
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);
        emailET = rootView.findViewById(R.id.signin_email_editext);
        fullNameET = rootView.findViewById(R.id.full_name_editext);
        passET = rootView.findViewById(R.id.signin_password);
        confirmPassET = rootView.findViewById(R.id.signup_confirm_password);
        signUpButton = rootView.findViewById(R.id.signup_button);
//        signUpWithGoogleButton = rootView.findViewById(R.id.signup_with_google_button);
        mProgressBar = rootView.findViewById(R.id.progressBar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAccTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        emailET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        passET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        emailET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        confirmPassET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccountWithEmailPass();
            }
        });
//
//        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               createNewAccountWithGoogle();
//            }
//        });

    }

    private void createNewAccountWithEmailPass() {
        final String email = emailET.getText().toString();
        String pass = passET.getText().toString();
        String confirmPass = confirmPassET.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Enter a valid email address!");
            emailET.requestFocus();
            return;
        }

        if (!pass.equals(confirmPass)){
            confirmPassET.setError("Password doesn't match!");
            confirmPassET.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);
        signUpButton.setTextColor(Color.argb(50, 255, 255, 255));

        mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                   User user = new User(fullNameET.getText().toString(), email);

                    mFireStore.collection("USERS").document(mFirebaseAuth.getUid()).set(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        sendToMainActivity();
                                    } else {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        signUpButton.setEnabled(true);
                                        signUpButton.setTextColor(Color.rgb(255, 255, 255));
                                        Log.e("Firebase : ", task.getException().getMessage());
                                    }
                                }
                            });
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    signUpButton.setEnabled(true);
                    signUpButton.setTextColor(Color.rgb(255, 255, 255));
                    Log.e("Firebase : ", task.getException().getMessage());
                }
            }
        });
    }

    private void createNewAccountWithGoogle() {

    }

    private void sendToMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailET.getText())){
            if (!TextUtils.isEmpty(fullNameET.getText())){
                if (!TextUtils.isEmpty(passET.getText()) && !(passET.getText().toString().length() <6 )){
                    if (!TextUtils.isEmpty(confirmPassET.getText())){
                        signUpButton.setEnabled(true);
                        signUpButton.setTextColor(Color.rgb( 255,255, 255));
                    }else {
                        signUpButton.setEnabled(false);
                        signUpButton.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                }else {
                    signUpButton.setEnabled(false);
                    signUpButton.setTextColor(Color.argb(50, 255, 255, 255));
                }
            }else {
                signUpButton.setEnabled(false);
                signUpButton.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signUpButton.setEnabled(false);
            signUpButton.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}
