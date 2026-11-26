package com.example.libraryapp.community;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommunityAdapter adapter;
    private FirebaseFirestore db;
    private EditText etSearch; // 검색창 변수 추가

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dongho_fragment_community, container, false);

        db = FirebaseFirestore.getInstance();

        // 리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommunityAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                // 상세 화면 생성
                PostDetailFragment detailFragment = new PostDetailFragment();

                // 데이터(Post)를 짐싸서 전달
                Bundle bundle = new Bundle();
                bundle.putSerializable("post_data", post);
                detailFragment.setArguments(bundle);

                // 화면 교체 (프래그먼트 이동)
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment) // fragment_container는 activity_main에 있는 틀
                        .addToBackStack(null) // 뒤로가기 버튼 누르면 목록으로 돌아오게
                        .commit();
            }
        });

        // 검색창 설정
        etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 글자가 바뀔 때마다 어댑터에 검색어를 전달해서 필터링
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 버튼 및 데이터 로딩
        view.findViewById(R.id.btn_write).setOnClickListener(v -> {
            // 파이어베이스에서 현재 로그인된 유저가 있는지 확인
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // 로그인이 안 되어 있다면
                Toast.makeText(getContext(), "글을 쓰려면 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();

                // 로그인 화면으로 보내기 (팝업처럼 열림)
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new WritePostFragment()) // 프래그먼트 교체
                        .addToBackStack(null)
                        .commit();
            }
        });

        loadPostsRealtime();

        return view;
    }

    private void loadPostsRealtime() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) return;

                        List<Post> postList = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Post post = doc.toObject(Post.class);
                            postList.add(post);
                        }
                        adapter.setPostList(postList);
                    }
                });
    }
}