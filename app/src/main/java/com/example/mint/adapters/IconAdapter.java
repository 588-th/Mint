package com.example.mint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mint.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder>{

    //region Fields
    private final LayoutInflater inflater;
    private final List<Bitmap> icons;
    private final OnClickListener onClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    //endregion

    //region Interfaces
    public interface OnClickListener{
        void onClick(Bitmap icon, int position);
    }
    //endregion

    //region Constructors
    public IconAdapter(Context context, List<Bitmap> icons, OnClickListener onClickListener) {
        this.icons = icons;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;

        if (!icons.isEmpty()) {
            selectedPosition = 0;
        }
    }
    //endregion

    //region Methods
    @Override
    @NotNull
    public IconAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_icon, parent, false);
        return new IconAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap icon = icons.get(position);
        holder.imageViewIcon.setImageBitmap(icon);

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            onClickListener.onClick(icon, clickedPosition);

            notifyItemChanged(selectedPosition);
            selectedPosition = clickedPosition;
            holder.selectionIndicator.setBackgroundColor(ContextCompat.getColor(inflater.getContext(), R.color.blue));
        });

        int backgroundColor = (position == selectedPosition) ?
                ContextCompat.getColor(inflater.getContext(), R.color.blue) :
                ContextCompat.getColor(inflater.getContext(), R.color.second_background);
        holder.selectionIndicator.setBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }
    //endregion

    //region ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View selectionIndicator;
        final ImageView imageViewIcon;
        ViewHolder(View view){
            super(view);
            selectionIndicator = view.findViewById(R.id.selectionIndicator);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
        }
    }
    //endregion
}