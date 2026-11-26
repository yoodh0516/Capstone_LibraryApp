package com.example.libraryapp.seokwon.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.R;
import com.example.libraryapp.seokwon.data.local.AppDatabase;
import com.example.libraryapp.seokwon.data.local.FavoriteLibrary;

import java.util.List;

public class FavoriteLibraryAdapter extends RecyclerView.Adapter<FavoriteLibraryAdapter.LibraryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FavoriteLibrary library);
    }

    private List<FavoriteLibrary> libraryList;
    private OnItemClickListener listener;

    public FavoriteLibraryAdapter(List<FavoriteLibrary> libraryList) {
        this.libraryList = libraryList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seokwon_item_favorite_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        FavoriteLibrary library = libraryList.get(position);
        holder.tvName.setText(library.getName());
        holder.tvAddress.setText(library.getAddress());
        holder.tvTel.setText(library.getTel());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(library);
            }
        });

        // --- 즐겨찾기 상태 연동 ---
        AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
        new Thread(() -> {
            boolean isFavorite = db.favoriteLibraryDao().isFavorite(library.getName());
            new Handler(Looper.getMainLooper()).post(() -> {
                updateStarIcon(holder.ivStar, isFavorite);
            });
        }).start();

        // --- 즐겨찾기 버튼 클릭 이벤트 ---
        holder.ivStar.setOnClickListener(v -> {
            new Thread(() -> {
                boolean currentFav = db.favoriteLibraryDao().isFavorite(library.getName());
                if (currentFav) {
                    db.favoriteLibraryDao().deleteByName(library.getName());
                    new Handler(Looper.getMainLooper()).post(() -> {
                        updateStarIcon(holder.ivStar, false);
                    });
                } else {
                    db.favoriteLibraryDao().insert(library);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        updateStarIcon(holder.ivStar, true);
                    });
                }
            }).start();
        });
    }

    private void updateStarIcon(ImageView ivStar, boolean isFavorite) {
        if (isFavorite) {
            ivStar.setImageResource(android.R.drawable.btn_star_big_on);
            ivStar.setColorFilter(android.graphics.Color.parseColor("#FFD700"));
        } else {
            ivStar.setImageResource(android.R.drawable.btn_star_big_off);
            ivStar.setColorFilter(android.graphics.Color.parseColor("#757575"));
        }
    }

    @Override
    public int getItemCount() {
        return libraryList != null ? libraryList.size() : 0;
    }

    public void setLibraryList(List<FavoriteLibrary> newLibraryList) {
        this.libraryList = newLibraryList;
        notifyDataSetChanged();
    }

    static class LibraryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;
        TextView tvTel;
        ImageView ivStar;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_library_name);
            tvAddress = itemView.findViewById(R.id.tv_library_address);
            tvTel = itemView.findViewById(R.id.tv_library_tel);
            ivStar = itemView.findViewById(R.id.iv_library_star);
        }
    }
}
