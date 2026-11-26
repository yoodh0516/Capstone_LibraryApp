package com.example.libraryapp.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;

public class PostDetailFragment extends Fragment {

    private Post post; // 현재 게시글 정보
    private FirebaseFirestore db;
    private CommentAdapter adapter;
    private List<Comment> commentList = new ArrayList<>(); // 댓글 담을 리스트

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dongho_fragment_post_detail, container, false);

        db = FirebaseFirestore.getInstance();

        // 1. 데이터 받기
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable("post_data");
        }

        // 2. 게시글 내용 표시
        if (post != null) {
            TextView tvAuthor = view.findViewById(R.id.tv_detail_author);
            TextView tvDate = view.findViewById(R.id.tv_detail_date);
            TextView tvContent = view.findViewById(R.id.tv_detail_content);

            tvAuthor.setText(post.getAuthor());
            tvDate.setText(post.getDate());
            tvContent.setText(post.getContent());

            // =================================================================
            // 🔥 [추가된 부분] 본인 글인지 확인하고 '글 삭제' 기능 활성화
            // =================================================================
            View btnDeletePost = view.findViewById(R.id.btn_post_delete);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // 현재 유저가 로그인되어 있고, (중요!) 게시글의 UID와 내 UID가 같다면?
            // 주의: Post 객체에 getUid() 메서드가 없다면 모델 클래스에 추가하셔야 합니다!
            if (auth.getCurrentUser() != null && post.getUid() != null
                    && auth.getCurrentUser().getUid().equals(post.getUid())) {

                btnDeletePost.setVisibility(View.VISIBLE); // 내 글이니까 삭제 버튼 보이기!

                btnDeletePost.setOnClickListener(v -> {
                    // 진짜 지울 건지 한 번 더 물어보는 팝업(AlertDialog)
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle("게시글 삭제")
                            .setMessage("정말로 이 글을 삭제하시겠습니까?")
                            .setPositiveButton("삭제", (dialog, which) -> {
                                // 파이어베이스에서 게시글 폭파! (컬렉션 이름이 "posts"인지 확인하세요)
                                db.collection("posts").document(post.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            // 성공했으면 현재 상세화면을 닫고 이전 화면(목록)으로 돌아감
                                            if (getActivity() != null) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .setNegativeButton("취소", null)
                            .show();
                });
            } else {
                btnDeletePost.setVisibility(View.GONE); // 내 글이 아니면 얄짤없이 숨김
            }
            // =================================================================
        }

        // 3. 댓글 리스트 설정
        RecyclerView recyclerView = view.findViewById(R.id.recycler_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        // 4. 댓글 불러오기 시작
        loadComments();

        // 5. 댓글 등록 버튼 이벤트
        EditText etInput = view.findViewById(R.id.et_comment_input);
        view.findViewById(R.id.btn_comment_send).setOnClickListener(v -> {

            // 1. 로그인
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getContext(), "댓글을 달려면 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
                return;
            }

            // 2. 내용 입력 확인
            String content = etInput.getText().toString();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 닉네임 팝업(showNicknameDialog)을 지우고, 여기서 바로 DB를 뒤집니다
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String myNickname = documentSnapshot.getString("nickname");
                        if (myNickname == null || myNickname.isEmpty()) {
                            myNickname = "익명";
                        }

                        // DB에서 찾은 닉네임으로 바로 저장 함수 호출!
                        saveComment(myNickname, content);
                        etInput.setText(""); // 전송 후 입력창 비우기
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "닉네임을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show());
        });

        return view;
    }

    // 파이어베이스에 댓글 저장
    private void saveComment(String nickname, String content) {
        String id = db.collection("comments").document().getId();
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(new Date());

        // postId를 같이 저장해야 나중에 "이 글의 댓글"만 불러올 수 있음
        Comment comment = new Comment(id, post.getId(), nickname, content, date);

        db.collection("comments").document(id).set(comment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "댓글 등록 완료!", Toast.LENGTH_SHORT).show();
                    loadComments(); // 목록 새로고침
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "에러: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // 파이어베이스에서 댓글 불러오기
    private void loadComments() {
        if (post == null) return;

        db.collection("comments")
                .whereEqualTo("postId", post.getId()) // [중요] 이 게시글 ID와 같은 댓글만 가져와라
                .orderBy("date", Query.Direction.ASCENDING) // 작성 순서대로 (오래된 게 위로)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    commentList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        commentList.add(doc.toObject(Comment.class));
                    }
                    adapter.notifyDataSetChanged(); // 화면 갱신
                })


                .addOnFailureListener(e -> Toast.makeText(getContext(), "불러오기 실패: " + e.getMessage(), Toast.LENGTH_LONG).show());


    }
}