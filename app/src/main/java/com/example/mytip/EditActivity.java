package com.example.mytip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class EditActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid, name, pw;
    private TextView emailtext, pwtext, nametext, pwcheck, namecheck;
    private Button edit, cancel;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        firebaseAuth =  FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();

        edit = (Button) findViewById(R.id.edit);
        cancel = (Button) findViewById(R.id.cancel);
        emailtext = (TextView) findViewById(R.id.emailtext);
        emailtext.setText(user.getEmail());
        pwtext = (TextView) findViewById(R.id.pwtext);
        nametext = (TextView) findViewById(R.id.nametext);
        nametext.setText(user.getDisplayName());
        namecheck = (TextView) findViewById(R.id.namecheck);
        pwcheck = (TextView) findViewById(R.id.pwcheck);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namecheck.setVisibility(View.GONE);
                pwcheck.setVisibility(View.GONE);

                System.out.println(user.getDisplayName());
                name = nametext.getText().toString().trim();
                pw = pwtext.getText().toString().trim();

                if(isValidName() && isValidPasswd()){
                    edit();
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void edit() {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("##");
                            System.out.println("##");
                            System.out.println("##");
                            System.out.println("##");
                            System.out.println("User profile updated.");
                            db.collection("users").document(uid)
                                    .update("name", name)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            System.out.println("성공");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                            db.collection("performance reviews")
                                    .whereEqualTo("uid", uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    db.collection("performance reviews").document(document.getId())
                                                            .update("uname", name)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    System.out.println("성공");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });

                                                    db.collection("movie reviews").document()
                                                            .update("uname", name)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    System.out.println("성공");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });

                            db.collection("movie reviews")
                                    .whereEqualTo("uid", uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    db.collection("movie reviews").document(document.getId())
                                                            .update("uname", name)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    System.out.println("성공");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });

                            user.updatePassword(pw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        System.out.println("User password updated.");
                                    }
                                }
                            });

                            Toast.makeText(EditActivity.this, "정보변경 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    private boolean isValidName() {
        if (name.isEmpty()) {
            namecheck.setText("이름을 입력해주세요.");
            namecheck.setTextColor(Color.RED);
            namecheck.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
    private boolean isValidPasswd() {
        if (pw.isEmpty()) {
            pwcheck.setText("비밀번호를 입력해주세요.");
            pwcheck.setTextColor(Color.RED);
            pwcheck.setVisibility(View.VISIBLE);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pw).matches()) {
            pwcheck.setText("비밀번호는 영어, 숫자 6자리이상 입니다.");
            pwcheck.setTextColor(Color.RED);
            pwcheck.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }
}