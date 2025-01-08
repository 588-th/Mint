package com.example.mint.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mint.R;
import com.example.mint.adapters.CategoryAdapter;
import com.example.mint.classes.DataVerification;
import com.example.mint.classes.DatabaseHelper;
import com.example.mint.models.Category;
import com.example.mint.models.OperationType;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateOperationActivity extends AppCompatActivity {

    //region Fields
    RecyclerView recyclerViewCategories;
    RadioButton radioButtonIncome;
    RadioButton radioButtonExpense;
    EditText editTextSum;
    EditText editTextComment;
    ImageView imageViewPhoto;

    private final ArrayList<Category> categories = new ArrayList<>();
    private Category selectedCategory;

    CategoryAdapter.OnClickListener onClickListener = (category, position) -> {
        selectedCategory = category;
    };

    CategoryAdapter.onClickDeleteListener onClickDeleteListener = (category, position) -> {
        deleteCategoryFromDatabase(category.getId());
        categories.remove(position);
        updateAdapter();
    };
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_operation);

        initFields();
        loadCategoriesFromDatabase();
        updateAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategoriesFromDatabase();
        updateAdapter();
    }
    //endregion

    //region Methods
    public void addCategory(View view) {
        Intent intent = new Intent(this, CreateCategoryActivity.class);
        startActivity(intent);
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    public void selectPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));

                imageViewPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initFields(){
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        radioButtonIncome = findViewById(R.id.radioButtonIncome);
        radioButtonExpense = findViewById(R.id.radioButtonExpense);
        editTextSum = findViewById(R.id.editTextSum);
        editTextComment = findViewById(R.id.editTextComment);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCategoriesFromDatabase() {
        categories.clear();

        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getReadableDatabase())
        {
            String[] projection =
                    {
                            DatabaseHelper.COLUMN_CATEGORY_ID,
                            DatabaseHelper.COLUMN_CATEGORY_NAME,
                            DatabaseHelper.COLUMN_CATEGORY_ICON
                    };

            try (Cursor cursor = db.query(
                    DatabaseHelper.TABLE_CATEGORIES,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            )) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndexId = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID);
                    int columnIndexName = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME);
                    int columnIndexIcon = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ICON);

                    do {
                        if (columnIndexId >= 0 && columnIndexName >= 0 && columnIndexIcon >= 0) {
                            int categoryId = cursor.getInt(columnIndexId);
                            String categoryName = cursor.getString(columnIndexName);
                            byte[] iconBytes = cursor.getBlob(columnIndexIcon);

                            Bitmap categoryIcon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);

                            Category category = new Category(categoryId, categoryName, categoryIcon);
                            categories.add(category);
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    private void deleteCategoryFromDatabase(long categoryId) {
        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase()) {

            String operationSelection = DatabaseHelper.COLUMN_OPERATION_CATEGORY_ID + " = ?";
            String[] operationSelectionArgs = {String.valueOf(categoryId)};
            db.delete(DatabaseHelper.TABLE_OPERATIONS, operationSelection, operationSelectionArgs);

            String categorySelection = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
            String[] categorySelectionArgs = {String.valueOf(categoryId)};
            db.delete(DatabaseHelper.TABLE_CATEGORIES, categorySelection, categorySelectionArgs);
        }
    }

    public void saveOperation(View view){
        OperationType type = (radioButtonIncome.isChecked()) ? OperationType.Income : OperationType.Expense;
        String sum = editTextSum.getText().toString();
        String comment = editTextComment.getText().toString();
        String date = getCurrentDate();

        if (!radioButtonIncome.isChecked() && !radioButtonExpense.isChecked()){
            Toast.makeText(getApplicationContext(), "Type cannot be none", Toast.LENGTH_LONG).show();
            return;
        }

        if (sum.equals("")) {
            Toast.makeText(getApplicationContext(), "Sum cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (!DataVerification.sum(sum)) {
            Toast.makeText(getApplicationContext(), "Invalid sum format. Maximum two decimal places allowed", Toast.LENGTH_LONG).show();
            return;
        }

        if (categories.size() == 0){
            Toast.makeText(getApplicationContext(), "Create a category", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedCategory == null){
            selectedCategory = categories.get(0);
        }

        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase())
        {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_OPERATION_CATEGORY_ID, Objects.requireNonNull(selectedCategory).getId());
            values.put(DatabaseHelper.COLUMN_OPERATION_TYPE, type.toString());
            values.put(DatabaseHelper.COLUMN_OPERATION_SUM, sum);
            values.put(DatabaseHelper.COLUMN_OPERATION_COMMENT, comment);
            values.put(DatabaseHelper.COLUMN_OPERATION_DATE, date);

            if (imageViewPhoto.getDrawable() != null){
                byte[] photoBytes = getPhotoBytes();
                values.put(DatabaseHelper.COLUMN_OPERATION_PHOTO, photoBytes);
            }

            db.insert(DatabaseHelper.TABLE_OPERATIONS, null, values);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        finish();
    }

    private byte[] getPhotoBytes() {
        imageViewPhoto.setDrawingCacheEnabled(true);
        imageViewPhoto.buildDrawingCache();
        Bitmap photoBitmap = imageViewPhoto.getDrawingCache();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void updateAdapter() {
        CategoryAdapter adapter = new CategoryAdapter(this, categories, onClickListener, onClickDeleteListener);
        recyclerViewCategories.setAdapter(adapter);
    }

    private String getCurrentDate(){
        Date currentDate = new Date();
        String dateFormatPattern = "dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern, Locale.getDefault());
        return dateFormat.format(currentDate);
    }
    //endregion
}