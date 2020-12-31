package com.jaydeep.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;

    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgotPasswordLink;

    private DatabaseReference UserRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeField();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AllowUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            UserEmail.setError("Email is Required");
            return;
        }
        if(TextUtils.isEmpty(password)){
            UserPassword.setError("Password is Required");
            return;
        }
        if(password.length() < 6){
            UserPassword.setError("Password Must Be Atleast 6 Characters");
            return;
        }
        loadingBar.setTitle("Sign in");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    String currentUserID = fAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().toString();

//                    UserRef.child("Users").child(currentUserID).setValue("");

                    UserRef.child(currentUserID).child("device_token")
                            .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Logged in Successful...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Error ! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitializeField() {
        LoginButton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button );
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        ForgotPasswordLink = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
    }
}
