package com.example.mint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mint.R;
import com.example.mint.models.Category;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    //region Fields
    private final LayoutInflater inflater;
    private final List<Category> categories;
    private final CategoryAdapter.OnClickListener onClickListener;
    private final CategoryAdapter.onClickDeleteListener onClickDeleteListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    //endregion

    //region Interfaces
    public interface OnClickListener{
        void onClick(Category category, int position);
    }

    public interface onClickDeleteListener{
        void onClick(Category category, int position);
    }
    //endregion

    //region Constructors
    public CategoryAdapter(Context context, List<Category> categories, CategoryAdapter.OnClickListener onClickListener, CategoryAdapter.onClickDeleteListener onClickDeleteListener) {
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
        this.onClickDeleteListener = onClickDeleteListener;

        if (!categories.isEmpty()) {
            selectedPosition = 0;
        }
    }
    //endregion

    //region Methods
    @Override
    @NotNull
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.imageViewIcon.setImageBitmap(category.getIcon());
        holder.textViewName.setText(category.getName());
        holder.imageViewDelete.setImageResource(R.drawable.ic_trash_can);

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            onClickListener.onClick(category, clickedPosition);

            notifyItemChanged(selectedPosition);
            selectedPosition = clickedPosition;
            holder.selectionIndicator.setBackgroundColor(ContextCompat.getColor(inflater.getContext(), R.color.blue));
        });

        holder.imageViewDelete.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            onClickDeleteListener.onClick(category, clickedPosition);
        });

        int backgroundColor = (position == selectedPosition) ?
                ContextCompat.getColor(inflater.getContext(), R.color.blue) :
                ContextCompat.getColor(inflater.getContext(), R.color.second_background);
        holder.selectionIndicator.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    //endregion

    //region ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View selectionIndicator;
        final ImageView imageViewIcon;
        final TextView textViewName;
        final ImageView imageViewDelete;
        ViewHolder(View view){
            super(view);
            selectionIndicator = view.findViewById(R.id.selectionIndicator);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            textViewName = view.findViewById(R.id.textViewName);
            imageViewDelete = view.findViewById(R.id.imageViewDelete);
        }
    }
    //endregion
}