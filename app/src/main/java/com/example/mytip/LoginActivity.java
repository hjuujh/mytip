package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText editem, editpw;
    private TextView text1, text2;
    private String email, pw, pass;
    private Button btn1, btn2;
    private AlertDialog.Builder alter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser cuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth =  FirebaseAuth.getInstance();

        editem = (EditText)findViewById(R.id.et_em);
        editpw = (EditText)findViewById(R.id.et_pass);
        text1 = (TextView)findViewById(R.id.pwfail);
        text2 = (TextView)findViewById(R.id.idfail);
        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        alter = new AlertDialog.Builder(LoginActivity.this);

        btn1 = (Button)findViewById(R.id.register);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });

        btn2 = (Button)findViewById(R.id.login);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editem.getText().toString().trim();
                pw = editpw.getText().toString().trim();

                if ( email.length() == 0 ) {
                    alter.setTitle("입력 확인");
                    alter.setMessage("ID를 입력해주세요.");
                    alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                        }
                    });
                    alter.show();
                }
                else if ( pw.length() == 0 ) {
                    alter.setTitle("입력 확인");
                    alter.setMessage("비밀번호를 입력해주세요.");
                    alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                        }
                    });
                    alter.show();
                }
                else{
                    text1.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);

                    login();
                }


            }
        });
    }

    private void login() {
        FirebaseAuthInvalidUserException invail = new FirebaseAuthInvalidUserException("a","b");
        String invaildexception = invail.getClass().getName();

        firebaseAuth.signInWithEmailAndPassword(email,pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ReviewListActivity.class);
                            startActivity(intent);
                        }
                        else {
                            String message;
                            String errorCode;
                            if(task.getException().getClass().getName().equals(invaildexception)){
                                message = "이메일이 존재하지 않습니다.";
                            }
                            else {
                                errorCode = ((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode();
                                if(errorCode.equals("ERROR_INVALID_EMAIL")){
                                    message = "이메일 형식이 잘못되었습니다.";
                                }
                                else if(errorCode.equals("ERROR_WRONG_PASSWORD")){
                                    message = "비밀번호가 틀렸습니다.";
                                }
                                else{
                                    message = errorCode;
                                }
                            }
                            Toast.makeText(LoginActivity.this,message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
}
