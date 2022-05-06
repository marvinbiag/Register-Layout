package com.example.adcms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = null;
    public static final String msg = null;
    EditText mFirstName,mEmail,mPassword,mConPassword,mPhone,mLastName,mAge,mGender,mAddress;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName = findViewById(R.id.firstname);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mConPassword = findViewById(R.id.password2);
        mPhone = findViewById(R.id.Phone);
        mLastName = findViewById(R.id.lastname);
        mAge = findViewById(R.id.age);
        mGender = findViewById(R.id.gender);
        mAddress = findViewById(R.id.address);
        mRegisterBtn = findViewById(R.id.loginBtn);
        mLoginBtn = findViewById(R.id.createText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
            finish();

        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String password2 = mConPassword.getText().toString().trim();
                String firstName = mFirstName.getText().toString();
                String phone = mPhone.getText().toString();
                String lastname = mLastName.getText().toString();
                String age = mAge.getText().toString();
                String gender = mGender.getText().toString();
                String address = mAddress.getText().toString();

                if (firstName.isEmpty()){
                    mFirstName.setError("First Name is required");
                    mFirstName.requestFocus();
                    return;
                }
                if (lastname.isEmpty()){
                    mLastName.setError("Last Name is required");
                    mLastName.requestFocus();
                    return;
                }

                if (email.isEmpty()){
                    mEmail.setError("Email Address is Required");
                    mEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Invalid Email");
                    mEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()){
                    mPassword.setError("Password is Required");
                    mPassword.requestFocus();
                    return;
                }
                if (password.length() < 8){
                    mPassword.setError("Password is Week");
                    mPassword.requestFocus();
                    return;
                }
                if (password2.isEmpty()){
                    mConPassword.setError("Password Confirmation is Required");
                    mConPassword.requestFocus();
                    return;
                }
                if (!password.equals(password2)) {
                    mConPassword.setError("Password doesn't match");
                }
                if (phone.isEmpty()){
                    mPhone.setError("Phone Number is required");
                    mPhone.requestFocus();
                    return;
                }
                if (address.isEmpty()){
                    mAddress.setError("Address is required");
                    mAddress.requestFocus();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("First Name",firstName);
                            user.put("Email",email);
                            user.put("Phone",phone);
                            user.put("Last Name",lastname);
                            user.put("Age",age);
                            user.put("Gender",gender);
                            user.put("Address",address);
                            //specify if user is admin
                            user.put("isUser","1");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess:user profile is created for" + userID);

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Dashboard.class));
                            finish();

                        }else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });


            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });

    }
}