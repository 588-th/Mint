package com.example.mint.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.example.mint.classes.DatabaseHelper;
import com.example.mint.models.Category;
import com.example.mint.models.Operation;

import java.text.DecimalFormat;

public class OperationViewActivity extends AppCompatActivity {

    //region Fields
    ImageView imageViewCategoryIcon;
    TextView textViewCategoryName;
    TextView textViewSum;
    TextView textViewDate;
    TextView textViewType;
    TextView textViewComment;
    ImageView imageViewPhoto;
    long operationId;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_view);

        Intent intent = getIntent();
        operationId = intent.getLongExtra("OPERATION_ID", 0);

        initFields();
        loadData(operationId);
    }

    //region Methods
    public void openOperationUpdate(View view){
        Intent intent = new Intent(this, ChangeOperationActivity.class);
        intent.putExtra("OPERATION_ID", operationId);
        startActivity(intent);
        finish();
    }

    public void deleteOperationFromDatabase(View view) {
        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase())
        {
            String selection = DatabaseHelper.COLUMN_OPERATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(operationId)};
            db.delete(DatabaseHelper.TABLE_OPERATIONS, selection, selectionArgs);
            finish();
        }
    }

    private void initFields(){
        imageViewCategoryIcon = findViewById(R.id.imageViewCategoryIcon);
        textViewCategoryName = findViewById(R.id.textViewCategoryName);
        textViewSum = findViewById(R.id.textViewSum);
        textViewDate = findViewById(R.id.textViewDate);
        textViewType = findViewById(R.id.textViewType);
        textViewComment = findViewById(R.id.textViewComment);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
    }

    private void loadData(long operationID) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Operation operation = dbHelper.getOperationById(operationID);
            Category category = dbHelper.getCategoryById(operation.getCategoryId());

            imageViewCategoryIcon.setImageBitmap(category.getIcon());
            textViewCategoryName.setText(category.getName());
            double sum = operation.getSum();
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedSum = decimalFormat.format(sum);
            textViewSum.setText(formattedSum + "₽");
            textViewType.setText(operation.getType().toString());
            textViewDate.setText(operation.getDate());
            textViewComment.setText(operation.getComment());

            Bitmap photo = operation.getPhoto();
            if (photo != null) {
                imageViewPhoto.setImageBitmap(operation.getPhoto());
            }

        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    //endregion
}
