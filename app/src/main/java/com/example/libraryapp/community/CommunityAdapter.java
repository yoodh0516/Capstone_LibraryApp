package com.example.libraryapp.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.libraryapp.R;
import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {

    private List<Post> postList = new ArrayList<>();
    private List<Post> allPosts = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPostList(List<Post> posts) {
        this.postList = new ArrayList<>(posts);
        this.allPosts = new ArrayList<>(posts);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        postList.clear();

        if (text.isEmpty()) {
            postList.addAll(allPosts);
        } else {
            text = text.toLowerCase();
            for (Post item : allPosts) {
                // 제목, 작성자 뿐만 아니라 '카테고리'로도 검색
                boolean matchTitle = item.getTitle().toLowerCase().contains(text);
                boolean matchAuthor = item.getAuthor().toLowerCase().contains(text);
                boolean matchCategory = item.getCategory() != null && item.getCategory().toLowerCase().contains(text);

                if (matchTitle || matchAuthor || matchCategory) {
                    postList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dongho_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        // 카테고리 텍스트뷰 변수
        TextView tvCategory, tvTitle, tvAuthor, tvDate;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // xml에서 만든 tv_category 연결
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(Post post) {
            // 카테고리 데이터 세팅 (예전 글처럼 카테고리가 없는 경우를 위한 방어 코드 포함)
            if (post.getCategory() != null && !post.getCategory().isEmpty()) {
                tvCategory.setText("[" + post.getCategory() + "]");
                tvCategory.setVisibility(View.VISIBLE);
            } else {
                tvCategory.setVisibility(View.GONE); // 카테고리가 없으면 공간을 숨김
            }

            tvTitle.setText(post.getTitle());
            tvAuthor.setText(post.getAuthor());
            tvDate.setText(post.getDate());
        }
    }
}