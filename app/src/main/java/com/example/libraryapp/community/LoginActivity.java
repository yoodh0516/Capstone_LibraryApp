package com.example.libraryapp.community; // 본인 패키지명으로 확인!

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.libraryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongho_activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 1. 이미 로그인된 상태에서 실수로 이 화면이 열렸다면?
        // 그냥 창을 닫아서 원래 화면으로 돌려보냅니다.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            finish();
            return;
        }

        EditText etEmail = findViewById(R.id.etLoginEmail);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToSignup = findViewById(R.id.tvGoToSignup);

        // 2. 로그인 버튼을 눌렀을 때
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            finish(); // 창을 닫기만 하면 원래 있던 화면(커뮤니티 등)으로 자연스럽게 돌아감
                        } else {
                            Toast.makeText(this, "로그인 실패: 아이디나 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 회원가입 글자 클릭 시
        tvGoToSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}