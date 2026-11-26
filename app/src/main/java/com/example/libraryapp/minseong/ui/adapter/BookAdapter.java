package com.example.libraryapp.minseong.ui.adapter;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.R;
import com.example.libraryapp.minseong.model.Book;
import com.example.libraryapp.minseong.model.BookExistResponse;
import com.example.libraryapp.minseong.model.LibResponseData;
import com.example.libraryapp.minseong.model.LibraryResponse;
import com.example.libraryapp.minseong.network.LibraryService;
import com.example.libraryapp.minseong.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList = new ArrayList<>();

    // 🔥 authKey 변수 완전 삭제!
    private String currentRegionCode; // 지역 코드

    // 사용자 위치 정보 및 거리 저장소
    private double userLat;
    private double userLng;
    private Map<String, Float> distanceMap = new HashMap<>();

    // 🚨 생성자에서도 authKey를 빼고, 위도/경도/지역코드만 받습니다.
    public BookAdapter(double userLat, double userLng, String regionCode) {
        this.userLat = userLat;
        this.userLng = userLng;
        this.currentRegionCode = regionCode;
    }

    public void setBookList(List<Book> list) {
        this.bookList = list;
        this.distanceMap.clear();
        notifyDataSetChanged();
    }

    public void addBookList(List<Book> moreBooks) {
        if (moreBooks != null && !moreBooks.isEmpty()) {
            int startPosition = this.bookList.size();
            this.bookList.addAll(moreBooks);
            notifyItemRangeInserted(startPosition, moreBooks.size());
        }
    }

    private void sortAndNotify() {
        Collections.sort(bookList, (b1, b2) -> {
            Float d1 = distanceMap.get(b1.getIsbn());
            Float d2 = distanceMap.get(b2.getIsbn());

            if (d1 != null && d2 != null) {
                return Float.compare(d1, d2);
            }
            if (d1 == null && d2 != null) return 1;
            if (d1 != null && d2 == null) return -1;
            return 0;
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.minseong_iitem_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        // 🟢 tvIsbn 추가됨
        TextView tvTitle, tvAuthor, tvIsbn;
        ImageView ivBookCover;
        Button btnStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            // 🟢 tvIsbn 매핑 추가
            tvIsbn = itemView.findViewById(R.id.tvIsbn);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            btnStatus = itemView.findViewById(R.id.btnStatus);
        }

        public void bind(Book book) {
            // 투명 망토 (도서관 없는 책 숨기기)
            if ("소장 도서관 없음".equals(book.getLibraryName()) || "조회 실패".equals(book.getLibraryName())) {
                itemView.setVisibility(View.GONE);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.height = 0;
                params.width = 0;
                params.topMargin = 0;
                params.bottomMargin = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                itemView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.topMargin = 10;
                params.bottomMargin = 10;
                itemView.setLayoutParams(params);
            }

            tvTitle.setText(book.getTitle());

            // 🟢 ISBN 데이터 바인딩 추가
            if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
                tvIsbn.setText("ISBN: " + book.getIsbn());
            } else {
                tvIsbn.setText("ISBN 정보 없음");
            }

            Glide.with(itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivBookCover);

            if (book.getLibraryName() != null) {
                // 거리가 있으면 거리 표시
                Float dist = distanceMap.get(book.getIsbn());
                if (dist != null) {
                    String distanceStr = String.format("%.1fkm", dist / 1000.0f);
                    tvAuthor.setText(book.getAuthor() + " | " + book.getLibraryName() + " (" + distanceStr + ")");
                } else {
                    tvAuthor.setText(book.getAuthor() + " | " + book.getLibraryName());
                }

                if ("N".equals(book.getLoanAvailable())) {
                    btnStatus.setText("대출 중");
                    btnStatus.setBackgroundColor(Color.LTGRAY);
                } else {
                    btnStatus.setText("대출 가능");
                    btnStatus.setBackgroundColor(Color.parseColor("#4A90E2"));
                }
            } else {
                tvAuthor.setText(book.getAuthor() + " | 확인 중...");
                btnStatus.setText("-");
                btnStatus.setBackgroundColor(Color.LTGRAY);

                // 중복 호출 방어막
                if (!book.isChecking()) {
                    book.setChecking(true);
                    findLibrary(book);
                }
            }
        }

        private void findLibrary(Book book) {
            LibraryService service = RetrofitClient.getApiService();

            // 🔥 authKey 제거하고 요청!
            service.getLibrariesByBook(book.getIsbn(), currentRegionCode, "json", 50).enqueue(new Callback<LibraryResponse>() {
                @Override
                public void onResponse(Call<LibraryResponse> call, Response<LibraryResponse> response) {
                    int currentPos = bookList.indexOf(book);
                    if (currentPos == -1) return;

                    if (response.isSuccessful() && response.body() != null) {
                        LibResponseData data = response.body().getResponse();
                        if (data != null && data.getLibs() != null && !data.getLibs().isEmpty()) {

                            try {
                                Collections.sort(data.getLibs(), (l1, l2) -> {
                                    float[] d1 = new float[1];
                                    float[] d2 = new float[1];
                                    Location.distanceBetween(userLat, userLng,
                                            Double.parseDouble(l1.getLib().getLatitude()), Double.parseDouble(l1.getLib().getLongitude()), d1);
                                    Location.distanceBetween(userLat, userLng,
                                            Double.parseDouble(l2.getLib().getLatitude()), Double.parseDouble(l2.getLib().getLongitude()), d2);
                                    return Float.compare(d1[0], d2[0]);
                                });

                                String libName = data.getLibs().get(0).getLib().getLibName();
                                String libCode = data.getLibs().get(0).getLib().getLibCode();

                                float[] minDistance = new float[1];
                                Location.distanceBetween(userLat, userLng,
                                        Double.parseDouble(data.getLibs().get(0).getLib().getLatitude()),
                                        Double.parseDouble(data.getLibs().get(0).getLib().getLongitude()), minDistance);

                                book.setLibraryName(libName);
                                book.setLoanAvailable("Y");
                                distanceMap.put(book.getIsbn(), minDistance[0]);

                                sortAndNotify();
                                checkLoanStatus(book, libCode);

                            } catch (Exception e) {
                                book.setLibraryName(data.getLibs().get(0).getLib().getLibName());
                                book.setLoanAvailable("Y");
                                notifyItemChanged(currentPos);
                            }

                        } else {
                            book.setLibraryName("소장 도서관 없음");
                            book.setLoanAvailable("N");
                            sortAndNotify();
                        }
                    } else {
                        book.setLibraryName("조회 실패");
                        book.setLoanAvailable("N");
                        sortAndNotify();
                    }
                }
                @Override
                public void onFailure(Call<LibraryResponse> call, Throwable t) {
                    int currentPos = bookList.indexOf(book);
                    if (currentPos != -1) {
                        book.setLibraryName("조회 실패");
                        book.setLoanAvailable("N");
                        sortAndNotify();
                    }
                }
            });
        }

        private void checkLoanStatus(Book book, String libCode) {
            LibraryService service = RetrofitClient.getApiService();

            // 🔥 authKey 제거하고 요청!
            service.getBookExistence(book.getIsbn(), libCode, "json").enqueue(new Callback<BookExistResponse>() {
                @Override
                public void onResponse(Call<BookExistResponse> call, Response<BookExistResponse> response) {
                    int currentPos = bookList.indexOf(book);
                    if (currentPos == -1) return;

                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().getResponse().getResult().getLoanAvailable();
                        if ("N".equals(status)) {
                            book.setLoanAvailable(status);
                            notifyItemChanged(currentPos);
                        }
                    }
                }
                @Override
                public void onFailure(Call<BookExistResponse> call, Throwable t) {
                    Log.e("BookStatus", "통신 실패: " + t.getMessage());
                }
            });
        }
    }
}