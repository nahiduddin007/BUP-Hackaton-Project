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


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    private TextView createNewAccTV, forgotPassTV;
    private View rootView;
    private FrameLayout parentFrameLayout;
    private EditText emailET, passET;
    private Button signInButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore mFireStore;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initialization();
        return rootView;
    }

    private void initialization() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        createNewAccTV = rootView.findViewById(R.id.create_new_account_textview);
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);
        emailET = rootView.findViewById(R.id.signin_email_editext);
        passET = rootView.findViewById(R.id.signin_password);
        signInButton = rootView.findViewById(R.id.signin_button);
        progressBar = rootView.findViewById(R.id.signin_progressbar);
        forgotPassTV = rootView.findViewById(R.id.forgot_password_textview);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createNewAccTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              setFragment(new SignUpFragment());
            }
        });

        forgotPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ForgotPassFragment());
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
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAccWithEmailPass();
            }
        });
    }

    private void signInAccWithEmailPass() {
        String email = emailET.getText().toString();
        String pass = passET.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Enter a valid email address!");
            emailET.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        signInButton.setEnabled(false);
        signInButton.setTextColor(Color.argb(50, 255, 255, 255));

        mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    sendToMainActivity();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    signInButton.setEnabled(true);
                    signInButton.setTextColor(Color.rgb(255, 255, 255));
                    Log.e("Firebase : ", task.getException().getMessage());
                }
            }
        });
    }

    private void sendToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        if (fragment instanceof ForgotPassFragment){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailET.getText())){
            if (!TextUtils.isEmpty(passET.getText()) && !(passET.getText().toString().length() <6 )){
                        signInButton.setEnabled(true);
                        signInButton.setTextColor(Color.rgb( 255,255, 255));
            }else {
                signInButton.setEnabled(false);
                signInButton.setTextColor(Color.argb(50, 255, 255, 255));
            }
        }else {
            signInButton.setEnabled(false);
            signInButton.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }
}
