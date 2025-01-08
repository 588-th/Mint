package com.example.mint.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mint.R;
import com.example.mint.classes.DatabaseHelper;
import com.example.mint.models.Category;
import com.example.mint.models.Operation;
import com.example.mint.models.OperationType;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.ViewHolder>{

    //region Fields
    private final LayoutInflater inflater;
    private final List<Operation> operations;
    private final OperationAdapter.OnClickListener onClickListener;
    private final OperationAdapter.onClickDeleteListener onClickDeleteListener;
    //endregion

    //region Interfaces
    public interface OnClickListener{
        void onClick(Operation operation, int position);
    }

    public interface onClickDeleteListener{
        void onClick(Operation operation, int position);
    }
    //endregion

    //region Constructors
    public OperationAdapter(Context context, List<Operation> operations, OperationAdapter.OnClickListener onClickListener, OperationAdapter.onClickDeleteListener onClickDeleteListener) {
        this.operations = operations;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
        this.onClickDeleteListener = onClickDeleteListener;
    }
    //endregion

    //region Methods
    @Override
    @NotNull
    public OperationAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_operation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OperationAdapter.ViewHolder holder, int position) {
        Operation operation = operations.get(position);

        long categoryId = operation.getCategoryId();
        DatabaseHelper dbHelper = new DatabaseHelper(inflater.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try
        {
            Category category = dbHelper.getCategoryById(categoryId);

            holder.imageViewCategoryIcon.setImageBitmap(category.getIcon());
            holder.textViewCategoryName.setText(category.getName());
            double sum = operation.getSum();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedSum = decimalFormat.format(sum);
            holder.textViewSum.setText(formattedSum + "₽");
            holder.textViewDate.setText(operation.getDate());
            holder.imageViewDelete.setImageResource(R.drawable.ic_trash_can);

            holder.itemView.setOnClickListener(v -> onClickListener.onClick(operation, position));
            holder.imageViewDelete.setOnClickListener(v -> onClickDeleteListener.onClick(operation, position));

            int textColor;
            if (operation.getType() == OperationType.Income){
                textColor = ContextCompat.getColor(inflater.getContext(), R.color.blue);
            }
            else{
                textColor = ContextCompat.getColor(inflater.getContext(), R.color.red);
            }
            holder.textViewSum.setTextColor(textColor);

        } finally
        {
            if (db != null && db.isOpen())
            {
                db.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return operations.size();
    }
    //endregion

    //region ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageViewCategoryIcon;
        final TextView textViewCategoryName;
        final TextView textViewSum;
        final TextView textViewDate;
        final ImageView imageViewDelete;
        ViewHolder(View view){
            super(view);
            imageViewCategoryIcon = view.findViewById(R.id.imageViewCategoryIcon);
            textViewCategoryName = view.findViewById(R.id.textViewCategoryName);
            textViewSum = view.findViewById(R.id.textViewSum);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewDelete = view.findViewById(R.id.imageViewDelete);
        }
    }
    //endregion
}
