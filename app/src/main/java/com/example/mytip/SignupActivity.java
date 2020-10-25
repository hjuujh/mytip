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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText editid, editpw, editnm;
    private TextView text1, text2, text3;
    private String id, pw, name;
    private Button btn1, btn2, btn3;
    private boolean duplicationpass = false, blankpass = false ;
    private AlertDialog.Builder alter;
    final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editid = (EditText)findViewById(R.id.et_id);
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
                id = editid.getText().toString();
                pw = editpw.getText().toString();
                name = editnm.getText().toString();

                if (duplicationpass){
                    blankCheck();
                    if(blankpass){
                        signUp();
                        alter.setTitle("회원가입");
                        alter.setMessage("회원가입에 성공했습니다.");
                        alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                finish();
                            }
                        });
                        alter.show();
                    }
                }
                else {
                    alter.setTitle("ID 중복확인");
                    alter.setMessage("ID 중복 확인이 필요합니다.");
                    alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                        }
                    });
                    alter.show();
                }
            }
        });

        btn2 = (Button)findViewById(R.id.cancel);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn3 = (Button)findViewById(R.id.duplication);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = editid.getText().toString();
                duplication(id);
            }
        });
    }

    private void blankCheck() {

        if ( id.length() == 0 ) {
            text1.setText("ID를 입력해주세요.");
            text1.setTextColor(Color.RED);
            text1.setVisibility(View.VISIBLE);
            blankpass = false;
        }
        else if ( pw.length() == 0 ) {
            text2.setText("비밀번호를 입력해주세요.");
            text2.setTextColor(Color.RED);
            text2.setVisibility(View.VISIBLE);
            blankpass = false;
        }
        else if ( name.length() == 0 ) {
            text3.setText("이름을 입력해주세요.");
            text3.setTextColor(Color.RED);
            text3.setVisibility(View.VISIBLE);
            blankpass = false;
        }
        else {
            blankpass = true;
        }

    }

    private void signUp() {

        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);

        final String TAG = "";

//        Map<String, Object> user = new HashMap<>();
//        user.put("id", id);
//        user.put("password", pw);
//        user.put("name", name);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("users").document(id)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG,"DocumentSnapshot successfully written");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
//                    }
//                });
    }

    public void duplication(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                alter.setTitle("ID 중복확인");
                                alter.setMessage("ID 사용 불가능 합니다.");
                                alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,int which){
                                        duplicationpass = false;
                                        return;
                                    }
                                });
                                alter.show();
                                return;
                            }
                            alter.setTitle("ID 중복확인");
                            alter.setMessage("ID 사용 가능 합니다.");
                            alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog,int which){
                                    duplicationpass = true;
                                    return;
                                }
                            });
                            alter.show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            System.out.println("Error getting documents: " + task.getException());
                        }

                    }
                });
    }
}