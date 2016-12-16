package co.createlou.RaiseRED;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmails;
    private EditText editTextPasswords;
    private Button buttonSignin;
    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();

        editTextEmails = (EditText) findViewById(R.id.editTextEmails);
        editTextPasswords = (EditText) findViewById(R.id.editTextPasswords);

        buttonSignin = (Button) findViewById(R.id.buttonSignin);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignin.setOnClickListener(this);
    }
    private void signinUser() {

        //initializing intent for post-register transition
        final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

        //getting email and password from edit texts
        String email = editTextEmails.getText().toString().trim();
        String password = editTextPasswords.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        //signing in a user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //Sign in Successful
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(LoginActivity.this, "Successfully Signed In", Toast.LENGTH_LONG).show();
                        } else {
                            //If sign in fails, display this message to the user
                            Toast.makeText(LoginActivity.this, "Sign In Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        //calling signin method on click
        signinUser();
    }
}
