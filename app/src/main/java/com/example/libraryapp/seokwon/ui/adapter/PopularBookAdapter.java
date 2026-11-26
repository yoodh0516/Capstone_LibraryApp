package com.example.libraryapp.seokwon.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.R;
import com.example.libraryapp.seokwon.data.local.AppDatabase;
import com.example.libraryapp.seokwon.data.local.FavoriteBook;
import com.example.libraryapp.seokwon.data.model.Book;

import java.util.List;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/ui/adapter/PopularBookAdapter.java
 * 설명: RecyclerView에 도서 데이터를 연결해주는 어댑터 클래스.
 * Book 리스트를 받아서 item_book.xml 레이아웃에 데이터를 채워넣습니다.
 */
public class PopularBookAdapter extends RecyclerView.Adapter<PopularBookAdapter.BookViewHolder> {

    // --- 리스너 인터페이스 추가 ---
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    // --------------------------

    // 1. 데이터 저장소 (책 목록)
    private List<Book> bookList;

    // 생성자: 외부에서 책 리스트를 받아옵니다.
    public PopularBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    // 2. 화면 생성 (뷰 홀더 만들기)
    // 리스트의 한 칸(item_book.xml)을 화면에 그릴 준비를 합니다.
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_book.xml 파일을 가져와서 View 객체로 만듭니다(Inflate).
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seokwon_item_book, parent, false);
        return new BookViewHolder(view);
    }

    // 3. 데이터 연결 (바인딩)
    // 만들어진 뷰 홀더에 실제 데이터(제목, 이미지 등)를 끼워 넣습니다.
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position); // 현재 순서에 맞는 책 데이터를 가져옴

        // 텍스트 설정
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        // 이미지 로딩 (Glide 라이브러리 사용)
        Glide.with(holder.itemView.getContext())
                .load(book.getImageUrl())           // 이미지 주소
                .centerCrop()                       // 이미지를 뷰 크기에 꽉 차게 조절
                .into(holder.ivCover);              // 이미지를 넣을 뷰


        // --- 아이템 클릭 리스너 설정 ---
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(book);
            }
        });

        // --------------------------------------------------------------------------
        // [즐겨찾기 연동 로직] Room DB를 사용하여 별 모양(빈 별/꽉 찬 별) 제어
        // --------------------------------------------------------------------------
        Context context = holder.itemView.getContext();
        AppDatabase db = AppDatabase.getInstance(context);

        // DB 작업은 백그라운드 스레드에서 수행
        new Thread(() -> {
            boolean isFavorite = db.favoriteBookDao().isFavorite(book.getTitle());

            // UI 업데이트는 메인 스레드에서
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isFavorite) {
                    holder.ivStar.setImageResource(android.R.drawable.btn_star_big_on);
                    holder.ivStar.setColorFilter(android.graphics.Color.parseColor("#FFD700"));
                } else {
                    holder.ivStar.setImageResource(android.R.drawable.btn_star_big_off);
                    holder.ivStar.setColorFilter(android.graphics.Color.parseColor("#757575"));
                }
            });
        }).start();

        // 3. 별 아이콘 클릭 이벤트 처리
        holder.ivStar.setOnClickListener(v -> {
            new Thread(() -> {
                // 클릭 순간 다시 DB 확인 (백그라운드 스레드)
                boolean currentFav = db.favoriteBookDao().isFavorite(book.getTitle());

                if (currentFav) {
                    // 이미 즐겨찾기 됨 -> 삭제 (백그라운드 스레드)
                    db.favoriteBookDao().deleteByTitle(book.getTitle());
                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.ivStar.setImageResource(android.R.drawable.btn_star_big_off);
                        holder.ivStar.setColorFilter(android.graphics.Color.parseColor("#757575"));
                    });
                } else {
                    // 즐겨찾기 안 됨 -> DB에 저장 (백그라운드 스레드)
                    FavoriteBook newFav = new FavoriteBook(book.getTitle(), book.getAuthor(), book.getImageUrl(), book.getIsbn13());
                    db.favoriteBookDao().insert(newFav);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.ivStar.setImageResource(android.R.drawable.btn_star_big_on);
                        holder.ivStar.setColorFilter(android.graphics.Color.parseColor("#FFD700"));
                    });
                }
            }).start();
        });
    }


    // 4. 아이템 개수 반환
    // 리스트가 총 몇 개인지 알려줍니다.
    @Override
    public int getItemCount() {
        return (bookList != null) ? bookList.size() : 0;
    }

    // --- 내부 클래스: 뷰 홀더 (View Holder) ---
    // item_book.xml 안에 있는 뷰들을 찾아놓고 관리하는 역할
    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvAuthor;

        ImageView ivStar; // 신규: 별 아이콘 변수

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // findViewById로 뷰들을 찾아서 변수에 연결
            ivCover = itemView.findViewById(R.id.iv_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_book_author);
            ivStar = itemView.findViewById(R.id.iv_bookmark_star); // 연결
        }
    }
}