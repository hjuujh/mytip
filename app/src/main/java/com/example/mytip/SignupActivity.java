package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText editem, editpw, editnm;
    private TextView text1, text2, text3;
    private String email, pw, name;
    private Button btn1, btn2;
    private AlertDialog.Builder alter;
    final String TAG = "";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setLogLevel(Logger.Level.DEBUG);

        editem = (EditText)findViewById(R.id.et_email);
        editpw = (EditText)findViewById(R.id.et_pass);
        editnm = (EditText)findViewById(R.id.et_name);
        text1 = (TextView)findViewById(R.id.blankid);
        text2 = (TextView)findViewById(R.id.blankpw);
        text3 = (TextView)findViewById(R.id.blankna);

        alter = new AlertDialog.Builder(SignupActivity.this);

        btn1 = (Button)findViewById(R.id.register);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text1.setVisibility(View.GONE);
                text2.setVisibility(View.GONE);
                text3.setVisibility(View.GONE);
                email = editem.getText().toString().trim();
                pw = editpw.getText().toString().trim();
                name = editnm.getText().toString().trim();

                signUp();
            }
        });

        btn2 = (Button)findViewById(R.id.cancel);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void signUp() {

        if(isValidEmail() && isValidPasswd() && isValidName()) {
            createUser();
        }

    }

    private boolean isValidEmail() {
        if (email.isEmpty()) {
            text1.setText("이메일을 입력해주세요.");
            text1.setTextColor(Color.RED);
            text1.setVisibility(View.VISIBLE);
            return false;
        }
        else {
            return true;
        }
    }

    private boolean isValidPasswd() {
        if (pw.isEmpty()) {
            text2.setText("비밀번호를 입력해주세요.");
            text2.setTextColor(Color.RED);
            text2.setVisibility(View.VISIBLE);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pw).matches()) {
            text2.setText("비밀번호는 영어, 숫자 6자리이상 입니다.");
            text2.setTextColor(Color.RED);
            text2.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidName() {
        if (name.isEmpty()) {
            text3.setText("이름을 입력해주세요.");
            text3.setTextColor(Color.RED);
            text3.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private void createUser() {
        FirebaseAuthInvalidCredentialsException invail = new FirebaseAuthInvalidCredentialsException("a","b");
        FirebaseAuthUserCollisionException duplicate = new FirebaseAuthUserCollisionException("a","b");
        String invaildexception = invail.getClass().getName();
        String duplicateexception = duplicate.getClass().getName();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                    String email = user.getEmail();
                    String uid = user.getUid();

                    HashMap<Object,String> usermap = new HashMap<>();

                    usermap.put("uid",uid);
                    usermap.put("email",email);
                    usermap.put("name",name);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid)
                            .set(usermap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"DocumentSnapshot successfully written");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    finish();
                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                }
                else {
                    String message;
                    if (task.getException().getClass().getName().equals(invaildexception)){
                        message = "이메일 형식이 잘못되었습니다.";
                    }
                    else if (task.getException().getClass().getName().equals(duplicateexception)){
                        message = "이메일이 존재합니다.";
                    }
                    else{
                        message = task.getException().getMessage();
                    }
                    Toast.makeText(SignupActivity.this,message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}