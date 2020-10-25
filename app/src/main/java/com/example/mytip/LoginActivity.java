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

import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText editid, editpw;
    private TextView text1, text2;
    private String id, pw, pass;
    private Button btn1, btn2;
    private AlertDialog.Builder alter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editid = (EditText)findViewById(R.id.et_id);
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
                id = editid.getText().toString();
                pw = editpw.getText().toString();

                if ( id.length() == 0 ) {
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

                    users(id, pw);
                }
            }
        });
    }

    public void users(String id, final String pw){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            final String TAG = "";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                        pass = (String) doc.getData().get("password");
                        if(pw.equals(pass)){
//                            Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
//                            Intent intent = new Intent(getApplicationContext(),TicketActivity.class);
                            Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                        else{
                            text1.setText("비밀번호가 틀렸습니다.");
                            text1.setTextColor(Color.RED);
                            text1.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                        text2.setText("아이디가 존재하지 않습니다.");
                        text2.setTextColor(Color.RED);
                        text2.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    Log.d(TAG, "Get failed with ", task.getException());
                    System.out.println("Get failed with " + task.getException());
                }
            }
        });
    }
}