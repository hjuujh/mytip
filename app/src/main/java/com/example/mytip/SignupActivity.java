package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GlideBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.util.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText editem, editpw, editnm;
    private TextView text1, text2, text3;
    private String email, pw, name;
    private Button btn1, btn2, btn3;
    private boolean duplicationpass = false, blankpass = false ;
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
//        btn1.setOnClickListener(view -> {
//

//                if (duplicationpass){
//                    blankCheck();
//                    if(blankpass){
//                        signUp();
//                        alter.setTitle("회원가입");
//                        alter.setMessage("회원가입에 성공했습니다.");
//                        alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
//                            public void onClick(DialogInterface dialog,int which){
//                                finish();
//                            }
//                        });
//                        alter.show();
//                    }
//                }
//                else {
//                    alter.setTitle("ID 중복확인");
//                    alter.setMessage("ID 중복 확인이 필요합니다.");
//                    alter.setPositiveButton("Ok",new DialogInterface.OnClickListener(){
//                        public void onClick(DialogInterface dialog,int which){
//                        }
//                    });
//                    alter.show();
//                }
//        });

        btn2 = (Button)findViewById(R.id.cancel);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        btn3 = (Button)findViewById(R.id.duplication);
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                email = editid.getText().toString();
//                duplication(id);
//            }
//        });
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
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            text1.setText("이메일이 잘못되었습니다.");
//            text1.setTextColor(Color.RED);
//            text1.setVisibility(View.VISIBLE);
//            return false;
//        }
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println(email);

                if (task.isSuccessful()) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
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
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SignupActivity.this,message, Toast.LENGTH_SHORT).show();
                    return;  //해당 메소드 진행을 멈추고 빠져나감.
                }

            }
        });
//        firebaseAuth.signInAnonymously().addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                    String email = user.getEmail();
//                    String uid = user.getUid();
//
//                    HashMap<Object,String> usermap = new HashMap<>();
//
//                    usermap.put("uid",uid);
//                    usermap.put("email",email);
//                    usermap.put("pw",pw);
//                    usermap.put("name",name);
//
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    db.collection("users").document(uid)
//                            .set(usermap)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG,"DocumentSnapshot successfully written");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error writing document", e);
//                                }
//                            });
//                    Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }


    private void blankCheck() {

        if ( email.length() == 0 ) {
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

//    private void signUp() {
//
//        text1.setVisibility(View.GONE);
//        text2.setVisibility(View.GONE);
//        text3.setVisibility(View.GONE);
//
//        final String TAG = "";
//
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
//    }

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