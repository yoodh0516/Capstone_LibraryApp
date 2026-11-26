package com.example.libraryapp.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.libraryapp.R;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {
    private List<LibraryResponse> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LibraryResponse lib);
    }

    public LibraryAdapter(List<LibraryResponse> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.yelin_home_item_library, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibraryResponse item = list.get(position);
        holder.name.setText(item.name);
        holder.distance.setText(item.distance + "km");
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;
        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.text_lib_name);
            distance = v.findViewById(R.id.text_lib_distance);
        }
    }
}