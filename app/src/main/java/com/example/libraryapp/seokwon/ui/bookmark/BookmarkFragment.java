package com.example.libraryapp.seokwon.ui.bookmark;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import com.example.libraryapp.home.HomeLibraryDetailActivity;
import com.example.libraryapp.home.LibraryService;
import com.example.libraryapp.seokwon.data.local.AppDatabase;
import com.example.libraryapp.seokwon.data.local.FavoriteBook;
import com.example.libraryapp.seokwon.data.local.FavoriteLibrary;
import com.example.libraryapp.seokwon.data.model.Book;
import com.example.libraryapp.seokwon.ui.adapter.FavoriteLibraryAdapter;
import com.example.libraryapp.seokwon.ui.adapter.PopularBookAdapter;
import com.example.libraryapp.seokwon.ui.book.BookDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * BookmarkFragment: 즐겨찾기한 도서 및 도서관 목록을 표시합니다.
 * 도서/도서관 탭 전환과 아이템 클릭 시 상세 페이지 이동을 처리합니다.
 */
public class BookmarkFragment extends Fragment {

    private static final String TAG = "BookmarkFragment";

    private TextView tabLibrary;
    private TextView tabBook;
    private RecyclerView recyclerView;

    private int currentTab = 0; // 0: 도서관, 1: 도서
    private LibraryService libraryService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seokwon_activity_bookmark, container, false);
        initUI(view);
        setupTabs();
        initRetrofit();
        return view;
    }

    private void initUI(View view) {
        tabLibrary = view.findViewById(R.id.tab_library);
        tabBook = view.findViewById(R.id.tab_book);
        recyclerView = view.findViewById(R.id.rv_bookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * 도서관 상세 정보를 가져오기 위한 Retrofit 초기화
     */
    private void initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.196.136.121/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        libraryService = retrofit.create(LibraryService.class);
    }

    private void setupTabs() {
        tabLibrary.setOnClickListener(v -> {
            if (currentTab == 0) return;
            currentTab = 0;
            updateTabUI();
            loadLibraryBookmarks();
        });

        tabBook.setOnClickListener(v -> {
            if (currentTab == 1) return;
            currentTab = 1;
            updateTabUI();
            loadBookBookmarks();
        });
        updateTabUI();
    }

    /**
     * 선택된 탭에 따라 상단 탭 UI 색상을 변경합니다.
     */
    private void updateTabUI() {
        if (currentTab == 0) {
            tabLibrary.setBackgroundColor(Color.parseColor("#333333"));
            tabLibrary.setTextColor(Color.WHITE);
            tabBook.setBackgroundColor(Color.parseColor("#E0E0E0"));
            tabBook.setTextColor(Color.parseColor("#757575"));
        } else {
            tabBook.setBackgroundColor(Color.parseColor("#333333"));
            tabBook.setTextColor(Color.WHITE);
            tabLibrary.setBackgroundColor(Color.parseColor("#E0E0E0"));
            tabLibrary.setTextColor(Color.parseColor("#757575"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentTab == 0) loadLibraryBookmarks();
        else loadBookBookmarks();
    }

    /**
     * DB에서 즐겨찾기한 도서관 목록을 불러와 리스트를 구성합니다.
     */
    private void loadLibraryBookmarks() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                List<FavoriteLibrary> savedLibraries = db.favoriteLibraryDao().getAllFavoriteLibraries();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        FavoriteLibraryAdapter adapter = new FavoriteLibraryAdapter(savedLibraries);
                        adapter.setOnItemClickListener(library -> {
                            // 상세 페이지 액티비티 대신, 지도로 이동하여 해당 도서관 핀을 보여줍니다.
                            if (getActivity() instanceof com.example.libraryapp.MainActivity) {
                                ((com.example.libraryapp.MainActivity) getActivity()).navigateToHomeWithLibrary(
                                        library.getName(),
                                        library.getLatitude(),
                                        library.getLongitude(),
                                        library.getAddress(),
                                        library.getTel(),
                                        library.getHomepage(),
                                        library.getOperatingTime(),
                                        library.getClosedDays()
                                );
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "도서관 로딩 중 오류 발생", e);
            }
        }).start();
    }

    /**
     * DB에서 즐겨찾기한 도서 목록을 불러와 리스트를 구성합니다.
     */
    private void loadBookBookmarks() {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                List<FavoriteBook> savedBooks = db.favoriteBookDao().getAllFavoriteBooks();

                List<Book> bookList = new ArrayList<>();
                for (FavoriteBook fav : savedBooks) {
                    bookList.add(new Book(fav.title, fav.author, fav.imageUrl, fav.isbn13));
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        PopularBookAdapter adapter = new PopularBookAdapter(bookList);
                        adapter.setOnItemClickListener(book -> {
                            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book);
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, detailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                        recyclerView.setAdapter(adapter);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "도서 로딩 중 오류 발생", e);
            }
        }).start();
    }
}
