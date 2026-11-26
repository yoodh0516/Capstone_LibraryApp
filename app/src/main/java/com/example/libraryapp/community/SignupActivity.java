package com.example.libraryapp.community; // 본인 패키지명으로 확인!

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.libraryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongho_activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etEmail = findViewById(R.id.etSignupEmail);
        EditText etPassword = findViewById(R.id.etSignupPassword);
        EditText etNickname = findViewById(R.id.etSignupNickname);
        Button btnSignupSubmit = findViewById(R.id.btnSignupSubmit);

        btnSignupSubmit.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. 파이어베이스에 이메일/비번으로 계정 생성
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // 2. 가입 성공 시, DB에 닉네임 저장 (마이페이지 팀원을 위해!)
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("email", email);
                                userInfo.put("nickname", nickname);

                                // "users"라는 컬렉션 안에 사용자의 고유UID 이름으로 문서 생성
                                db.collection("users").document(uid)
                                        .set(userInfo)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                                            finish(); // 성공하면 가입 화면 닫기 (로그인 화면으로 돌아감)
                                        });
                            }
                        } else {
                            Toast.makeText(this, "가입 실패: 이미 있는 이메일이거나 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}