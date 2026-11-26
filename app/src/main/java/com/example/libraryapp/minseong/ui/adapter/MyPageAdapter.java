package com.example.libraryapp.minseong.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.libraryapp.R;
import java.util.List;

public class MyPageAdapter extends RecyclerView.Adapter<MyPageAdapter.MenuViewHolder> {

    // 데이터 모델
    public static class MenuItem {
        String title;
        boolean isDestructive; // true면 빨간색(회원탈퇴 등)

        public MenuItem(String title, boolean isDestructive) {
            this.title = title;
            this.isDestructive = isDestructive;
        }
    }

    private List<MenuItem> menuList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String title);
    }

    public MyPageAdapter(List<MenuItem> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 아까 1번에서 만든 item_mypage_menu.xml을 여기서 불러옵니다!
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.minseong_item_mypage_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuTitle;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(menuList.get(pos).title);
                }
            });
        }

        public void bind(MenuItem item) {
            tvMenuTitle.setText(item.title);
            if (item.isDestructive) {
                tvMenuTitle.setTextColor(Color.parseColor("#FF3B30")); // 빨간색
            } else {
                tvMenuTitle.setTextColor(Color.parseColor("#333333")); // 검은색
            }
        }
    }
}