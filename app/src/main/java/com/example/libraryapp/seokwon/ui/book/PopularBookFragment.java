package com.example.libraryapp.seokwon.ui.book;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import com.example.libraryapp.seokwon.data.api.RetrofitClient;
import com.example.libraryapp.seokwon.data.model.Book;
import com.example.libraryapp.seokwon.data.model.BookResponse;
import com.example.libraryapp.seokwon.ui.adapter.PopularBookAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/ui/book/PopularBookFragment.java
 */
public class PopularBookFragment extends Fragment {

    private static final String TAG = "PopularBookFragment"; // Tag for logging

    private RecyclerView recyclerView;
    private PopularBookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    // Fragment는 onCreate 대신 onCreateView에서 화면을 그립니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 기존 activity_popular_book.xml 레이아웃을 그대로 재사용합니다.
        View view = inflater.inflate(R.layout.seokwon_activity_popular_book, container, false);

        initUI(view);
        fetchBooksFromApi();

        return view;
    }

    // findViewById를 쓸 때 앞에 view. 을 붙여야 합니다.
    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_popular_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PopularBookAdapter(bookList);
        recyclerView.setAdapter(adapter);

        // --- 아이템 클릭 시 상세 화면으로 이동 ---
        adapter.setOnItemClickListener(book -> {
            Log.d(TAG, "onItemClick: Book clicked. Title: " + (book != null ? book.getTitle() : "null"));
            try {
                BookDetailFragment detailFragment = BookDetailFragment.newInstance(book);
                Log.d(TAG, "onItemClick: BookDetailFragment instance created.");
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null) // 뒤로가기 버튼으로 목록으로 돌아올 수 있게 함
                        .commit();
                Log.d(TAG, "onItemClick: Fragment transaction committed.");
            } catch (Exception e) {
                Log.e(TAG, "onItemClick: Error during fragment transaction: " + e.getMessage(), e);
                Toast.makeText(getContext(), "상세 화면 로딩 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // [중요] Fragment 방식에서는 하단 네비게이션 버튼(btnHome, btnBookmark 등)을
        // 여기서 제어하지 않습니다. (MainActivity가 담당)
    }

    private void fetchBooksFromApi() {
        // [수정 필수] API 키 입력

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDt = sdf.format(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDt = sdf.format(calendar.getTime());

        RetrofitClient.getService().getPopularBooks(startDt, endDt, "json")
                .enqueue(new Callback<BookResponse>() {
                    @Override
                    public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BookResponse result = response.body();
                            if (result.response == null || result.response.docs == null) return;

                            List<Book> newBooks = new ArrayList<>();
                            for (BookResponse.DocWrapper wrapper : result.response.docs) {
                                newBooks.add(wrapper.book);
                            }

                            bookList.clear();
                            bookList.addAll(newBooks);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "fetchBooksFromApi: " + newBooks.size() + " books loaded and adapter notified.");

                        } else {
                            Log.e(TAG, "fetchBooksFromApi: Data loading failed. Response not successful or body is null.");
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "데이터 로딩 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BookResponse> call, Throwable t) {
                        Log.e(TAG, "fetchBooksFromApi: API call failed: " + t.getMessage(), t);
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}