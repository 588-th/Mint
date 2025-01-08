package com.example.mint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.example.mint.adapters.CategoryAdapter;
import com.example.mint.adapters.OperationAdapter;
import com.example.mint.classes.DatabaseHelper;
import com.example.mint.models.Operation;
import com.example.mint.models.OperationType;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OperationsActivity extends AppCompatActivity {

    //region Fields
    private static final String START_AMOUNT_KEY = "startAmount";
    private RecyclerView recyclerViewOperations;
    private TextView textViewAmount;
    private ArrayList<Operation> operations = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    OperationAdapter.OnClickListener onClickListener = (operation, position) -> {
        openOperationView(operation.getId());
    };

    OperationAdapter.onClickDeleteListener onClickDeleteListener = (operation, position) -> {
        deleteOperationFromDatabase(operation.getId());
        operations.remove(position);
        updateAdapter();
        countAmount();
    };
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);

        initFields();
        loadOperations();
        updateAdapter();
        countAmount();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadOperations();
        updateAdapter();
        countAmount();
    }
    //endregion

    //region Methods
    public void addOperation(View view){
        Intent intent = new Intent(this, CreateOperationActivity.class);
        startActivity(intent);
    }

    public void openOperationView(long operationId){
        Intent intent = new Intent(this, OperationViewActivity.class);
        intent.putExtra("OPERATION_ID", operationId);
        startActivity(intent);
    }

    private void initFields(){
        textViewAmount = findViewById(R.id.textViewAmount);
        recyclerViewOperations = findViewById(R.id.recyclerViewOperations);
        recyclerViewOperations.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void countAmount(){
        Operation operation;
        float amount = sharedPreferences.getFloat(START_AMOUNT_KEY, 0);
        if (operations != null){
            for (int i = 0; i < operations.size(); i++){
                operation = operations.get(i);
                if (operation.getType() == OperationType.Income)
                    amount += operation.getSum();
                else
                    amount -= operation.getSum();
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedAmount = decimalFormat.format(amount);

        textViewAmount.setText(formattedAmount + "₽");

        if (amount >= 0)
            textViewAmount.setTextColor(ContextCompat.getColor(this, R.color.blue));
        else
            textViewAmount.setTextColor(ContextCompat.getColor(this, R.color.red));
    }

    private void updateAdapter(){
        OperationAdapter adapter = new OperationAdapter(this, operations, onClickListener, onClickDeleteListener);
        recyclerViewOperations.setAdapter(adapter);
    }

    private void loadOperations(){
        operations.clear();

        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getReadableDatabase())
        {
            String[] projection = {
                    DatabaseHelper.COLUMN_OPERATION_ID,
                    DatabaseHelper.COLUMN_OPERATION_CATEGORY_ID,
                    DatabaseHelper.COLUMN_OPERATION_TYPE,
                    DatabaseHelper.COLUMN_OPERATION_SUM,
                    DatabaseHelper.COLUMN_OPERATION_COMMENT,
                    DatabaseHelper.COLUMN_OPERATION_DATE,
            };

            Cursor cursor = db.query(
                    DatabaseHelper.TABLE_OPERATIONS,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst())
            {
                int columnIndexId = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_ID);
                int columnIndexCategoryId = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_CATEGORY_ID);
                int columnIndexType = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_TYPE);
                int columnIndexSum = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_SUM);
                int columnIndexComment = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_COMMENT);
                int columnIndexDate = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION_DATE);

                do
                {
                    int operationId = cursor.getInt(columnIndexId);
                    int operationCategoryId = cursor.getInt(columnIndexCategoryId);
                    String operationType = cursor.getString(columnIndexType);
                    String operationSum = cursor.getString(columnIndexSum);
                    String operationComment = cursor.getString(columnIndexComment);
                    String operationDate = cursor.getString(columnIndexDate);
                    
                    Operation operation = new Operation(operationId, operationCategoryId, OperationType.getEnumFromString(operationType), Double.parseDouble(operationSum), operationComment, operationDate, null);
                    operations.add(operation);
                }
                while (cursor.moveToNext());

                cursor.close();
            }
        }
    }

    private void deleteOperationFromDatabase(long operationId) {
        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase())
        {
            String selection = DatabaseHelper.COLUMN_OPERATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(operationId)};
            db.delete(DatabaseHelper.TABLE_OPERATIONS, selection, selectionArgs);
        }
    }
    //endregion
}