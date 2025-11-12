package com.aryan.digital_wallet_main.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aryan.digital_wallet_main.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClicked(String category);
    }

    private final Context context;
    private final List<String> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public CategoryAdapter(Context context, List<String> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.button.setText(category);

        // Optional: Highlight selected item (style change)
        if (selectedPosition == position) {
            holder.button.setStrokeColorResource(R.color.primary);
            holder.button.setStrokeWidth(3);
        } else {
            holder.button.setStrokeColorResource(R.color.black);
            holder.button.setStrokeWidth(1);
        }

        holder.button.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            listener.onCategoryClicked(category);
            notifyDataSetChanged(); // update highlight
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialButton button;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (MaterialButton) itemView;
        }
    }
}
