package com.example.libraryapp.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.libraryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WritePostFragment extends Fragment {

    private TextView tvBack, tvSubmit;
    private EditText etPostTitle, etPostContent;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dongho_fragment_write_post, container, false);

        db = FirebaseFirestore.getInstance();

        tvBack = view.findViewById(R.id.tvBack);
        tvSubmit = view.findViewById(R.id.tvSubmit);
        etPostTitle = view.findViewById(R.id.etPostTitle);
        etPostContent = view.findViewById(R.id.etPostContent);

        // 1. '취소' 버튼 누르면 뒤로 가기
        tvBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // 2. '등록' 버튼 누르면 실행될 로직
        tvSubmit.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            RadioGroup rgCategory = view.findViewById(R.id.rgCategory);
            int selectedId = rgCategory.getCheckedRadioButtonId();
            android.widget.RadioButton selectedRadioButton = view.findViewById(selectedId);
            String category = selectedRadioButton.getText().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // DB에서 내 닉네임을 가져옵니다
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String myNickname = documentSnapshot.getString("nickname");

                        if (myNickname == null || myNickname.isEmpty()) {
                            myNickname = "익명";
                        }
                        savePostToFirebase(title, myNickname, content, category, uid);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "닉네임을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
        });

        return view;
    }

    // 파이어베이스에 진짜로 저장하는 함수
    private void savePostToFirebase(String title, String author, String content, String category, String uid) {
        String date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        String id = db.collection("posts").document().getId();

        long currentTime = System.currentTimeMillis();

        Post newPost = new Post(id, title, author, date, content, category, currentTime, uid);

        db.collection("posts").document(id).set(newPost)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "등록 완료!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "에러: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}