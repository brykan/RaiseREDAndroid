package co.createlou.RaiseRED;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private DatabaseReference mDatabase;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText confirmPassword;
    private EditText fullName;
    private Button buttonSignup;
    private Button camButton;
    private ProgressDialog progressDialog;

    private byte[] imageData;

    //defining firebaseauth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);


        //initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmTextPassword);
        fullName = (EditText) findViewById(R.id.editTextName);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);

        camButton = (Button) findViewById(R.id.button4);
        camButton.setOnClickListener(this);
    }

    private void registerUser() {
        //initializing intent for post-register transition
        final Intent mainIntent = new Intent(CreateAccountActivity.this, MainActivity.class);

        //getting email and password from edit texts
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmpassword = confirmPassword.getText().toString().trim();
        final String fullname = fullName.getText().toString();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show();
        }
        if (!TextUtils.equals(password, confirmpassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        //if the email and password are not empty and password is confirmed
        //displaying a progress dialog

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        //creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(),fullname);
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://raisered-39293.appspot.com");
                            StorageReference profileRef = storageRef.child("images/"+user.getUid());
                            UploadTask uploadTask = profileRef.putBytes(imageData);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            });
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(CreateAccountActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                        } else {
                            //display some message here
                            Toast.makeText(CreateAccountActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                });
    }
    private void writeNewUser(String userId, String name) {
        User user = new User(name);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).setValue(user);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSignup) {
            //calling register method on click
            registerUser();
        }else if(view.getId() == R.id.button4){ //&& hasPermissionInManifest(getBaseContext(), "CAMERA")) {
            Intent camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (camintent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(camintent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        camButton = (Button) findViewById(R.id.button4);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            BitmapDrawable bdrawable = new BitmapDrawable(getResources(),imageBitmap);
            camButton.setBackground(bdrawable);
            encodeBitmap(imageBitmap);
        }
    }
    public void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageData = baos.toByteArray();
    }


}



