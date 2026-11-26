package com.example.libraryapp.seokwon.ui.book;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.libraryapp.MainActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.seokwon.data.api.RetrofitClient;
import com.example.libraryapp.seokwon.data.local.AppDatabase;
import com.example.libraryapp.seokwon.data.local.FavoriteBook;
import com.example.libraryapp.seokwon.data.model.Book;
import com.example.libraryapp.seokwon.data.model.BookDetailResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {

    private static final String TAG = "BookDetailFragment"; // Tag for logging

    private Book book;
    private AppDatabase db;
    private FloatingActionButton favoriteButton;
    private boolean isFavorite = false;

    private TextView tvBookTitle;
    private TextView tvBookAuthor;
    private TextView tvBookPublisher;
    private TextView tvBookDescription;
    private TextView tvBookPubYear;
    private TextView tvBookClassNo;
    private ImageView ivBookCover;

    // 데이터를 전달받기 위한 정적 팩토리 메서드
    public static BookDetailFragment newInstance(Book book) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
            if (book == null) {
                Log.e(TAG, "onCreate: Book object is null after deserialization.");
            } else {
                Log.d(TAG, "onCreate: Book object received. Title: " + book.getTitle());
            }
        } else {
            Log.e(TAG, "onCreate: Arguments are null.");
        }
        db = AppDatabase.getInstance(requireContext());
        Log.d(TAG, "onCreate: AppDatabase instance obtained.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: inflating layout.");
        return inflater.inflate(R.layout.seokwon_fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: started.");

        if (book == null) {
            Log.e(TAG, "onViewCreated: Book object is null, cannot display details.");
            Toast.makeText(getContext(), "도서 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return;
        }

        ivBookCover = view.findViewById(R.id.iv_book_cover);
        tvBookTitle = view.findViewById(R.id.tv_book_title);
        tvBookAuthor = view.findViewById(R.id.tv_book_author);
        tvBookPublisher = view.findViewById(R.id.tv_book_publisher);
        tvBookDescription = view.findViewById(R.id.tv_book_description);
        tvBookPubYear = view.findViewById(R.id.tv_book_pub_year);
        tvBookClassNo = view.findViewById(R.id.tv_book_class_no);
        favoriteButton = view.findViewById(R.id.btn_favorite);

        Log.d(TAG, "onViewCreated: Book Title: " + book.getTitle());
        Log.d(TAG, "onViewCreated: Book Author: " + book.getAuthor());
        Log.d(TAG, "onViewCreated: Book Image URL: " + book.getImageUrl());

        // initially hide until data is fetched
        tvBookPublisher.setVisibility(View.GONE);
        tvBookDescription.setVisibility(View.GONE);
        tvBookPubYear.setVisibility(View.GONE);
        tvBookClassNo.setVisibility(View.GONE);

        if (book.getTitle() != null) {
            tvBookTitle.setText(book.getTitle());
        } else {
            tvBookTitle.setText("제목 없음");
            Log.w(TAG, "onViewCreated: Book title is null.");
        }

        if (book.getAuthor() != null) {
            tvBookAuthor.setText(book.getAuthor());
        } else {
            tvBookAuthor.setText("저자 미상");
            Log.w(TAG, "onViewCreated: Book author is null.");
        }

        // initially hide until data is fetched
        tvBookPublisher.setVisibility(View.GONE);
        tvBookDescription.setVisibility(View.GONE);

        if (book.getIsbn13() != null && !book.getIsbn13().isEmpty()) {
            fetchBookDetails(book.getIsbn13(), tvBookPublisher, tvBookDescription, tvBookPubYear, tvBookClassNo);
        } else {
            Log.w(TAG, "onViewCreated: Book isbn13 is null, cannot fetch details.");
        }

        try {
            if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                Log.d(TAG, "onViewCreated: Loading image with Glide: " + book.getImageUrl());
                Glide.with(this)
                        .load(book.getImageUrl())
                        .placeholder(R.drawable.ic_book)
                        .into(ivBookCover);
            } else {
                ivBookCover.setImageResource(R.drawable.ic_book);
                Log.w(TAG, "onViewCreated: Book image URL is null or empty, using placeholder.");
            }
        } catch (Exception e) {
            ivBookCover.setImageResource(R.drawable.ic_book);
            Log.e(TAG, "onViewCreated: Error loading image with Glide: " + e.getMessage(), e);
            Toast.makeText(getContext(), "이미지 로딩 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "onViewCreated: Calling checkIfFavorite().");
        checkIfFavorite();

        Log.d(TAG, "onViewCreated: Setting favoriteButton OnClickListener.");
        favoriteButton.setOnClickListener(v -> {
            toggleFavorite();
        });

        // "도서관 검색" 버튼 추가 및 연동
        View btnSearchLibrary = view.findViewById(R.id.btn_search_library);
        if (btnSearchLibrary != null) {
            btnSearchLibrary.setOnClickListener(v -> {
                if (book != null && book.getTitle() != null) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).navigateToHomeWithSearch(book.getTitle());
                    }
                }
            });
        }
    }

    private void fetchBookDetails(String isbn13, TextView tvPublisher, TextView tvDescription, TextView tvPubYear, TextView tvClassNo) {
        Log.d(TAG, "fetchBookDetails: Requesting details for ISBN: " + isbn13);
        RetrofitClient.getService().getBookDetail(isbn13, "json")
                .enqueue(new Callback<BookDetailResponse>() {
                    @Override
                    public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                BookDetailResponse.ResponseData data = response.body().response;
                                if (data != null && data.detail != null && !data.detail.isEmpty()) {
                                    Book detailedBook = data.detail.get(0).book;
                                    
                                    if (detailedBook != null) {
                                        // Update UI
                                        if (detailedBook.getPublisher() != null && !detailedBook.getPublisher().isEmpty()) {
                                            tvPublisher.setText("출판사: " + detailedBook.getPublisher());
                                            tvPublisher.setVisibility(View.VISIBLE);
                                        }
                                        if (detailedBook.getPublicationYear() != null && !detailedBook.getPublicationYear().isEmpty()) {
                                            tvPubYear.setText("발행년도: " + detailedBook.getPublicationYear());
                                            tvPubYear.setVisibility(View.VISIBLE);
                                        }
                                        if (detailedBook.getClassNo() != null && !detailedBook.getClassNo().isEmpty()) {
                                            tvClassNo.setText("분류 번호 (KDC): " + detailedBook.getClassNo());
                                            tvClassNo.setVisibility(View.VISIBLE);
                                        }
                                        if (detailedBook.getDescription() != null && !detailedBook.getDescription().isEmpty()) {
                                            tvDescription.setText(detailedBook.getDescription());
                                            tvDescription.setVisibility(View.VISIBLE);
                                        }
                                        Log.d(TAG, "fetchBookDetails: Successfully loaded details.");
                                    }
                                } else {
                                    Log.w(TAG, "fetchBookDetails: Detail list is empty in response.");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "fetchBookDetails: Error parsing response: " + e.getMessage(), e);
                            }
                        } else {
                            Log.w(TAG, "fetchBookDetails: Response unsuccessful or body is null.");
                        }
                    }

                    @Override
                    public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                        Log.e(TAG, "fetchBookDetails: API call failed: " + t.getMessage(), t);
                    }
                });
    }

    private void checkIfFavorite() {
        Log.d(TAG, "checkIfFavorite: Starting background thread for DB operation.");
        new Thread(() -> {
            try {
                String bookTitle = book.getTitle();
                if (bookTitle == null) {
                    Log.e(TAG, "checkIfFavorite: Book title is null, cannot query favorite status.");
                    return;
                }
                Log.d(TAG, "checkIfFavorite: Querying favorite status for title: " + bookTitle);
                FavoriteBook favorite = db.favoriteBookDao().getFavoriteBookByTitle(bookTitle);
                isFavorite = (favorite != null);
                Log.d(TAG, "checkIfFavorite: Is favorite: " + isFavorite);
                // UI update on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::updateFavoriteButtonUI);
                }
            } catch (Exception e) {
                Log.e(TAG, "checkIfFavorite: Error checking favorite status: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기 상태 확인 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private void updateFavoriteButtonUI() {
        Log.d(TAG, "updateFavoriteButtonUI: Updating button to isFavorite: " + isFavorite);
        if (isFavorite) {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            favoriteButton.setSupportImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFD700"))); // Gold
        } else {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            favoriteButton.setSupportImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#757575"))); // Grey
        }
    }

    private void toggleFavorite() {
        Log.d(TAG, "toggleFavorite: Starting background thread for DB operation.");
        new Thread(() -> {
            try {
                String bookTitle = book.getTitle();
                if (bookTitle == null) {
                    Log.e(TAG, "toggleFavorite: Book title is null, cannot toggle favorite status.");
                    return;
                }

                if (isFavorite) {
                    // Remove from favorites
                    Log.d(TAG, "toggleFavorite: Removing from favorites: " + bookTitle);
                    FavoriteBook toDelete = db.favoriteBookDao().getFavoriteBookByTitle(bookTitle);
                    if (toDelete != null) {
                        db.favoriteBookDao().delete(toDelete);
                        Log.d(TAG, "toggleFavorite: Successfully deleted from favorites.");
                    } else {
                        Log.w(TAG, "toggleFavorite: Attempted to delete a non-existent favorite book: " + bookTitle);
                    }
                    isFavorite = false;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Add to favorites
                    Log.d(TAG, "toggleFavorite: Adding to favorites: " + bookTitle);
                    FavoriteBook newFavorite = new FavoriteBook(bookTitle, book.getAuthor(), book.getImageUrl(), book.getIsbn13());
                    db.favoriteBookDao().insert(newFavorite);
                    isFavorite = true;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show());
                    }
                }
                // UI update
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::updateFavoriteButtonUI);
                }
            } catch (Exception e) {
                Log.e(TAG, "toggleFavorite: Error toggling favorite status: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "즐겨찾기 토글 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }
}

