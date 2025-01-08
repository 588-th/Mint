package com.example.mint.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mint.models.Category;
import com.example.mint.models.Operation;
import com.example.mint.models.OperationType;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mint_database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_ICON = "icon";

    public static final String TABLE_OPERATIONS = "operations";
    public static final String COLUMN_OPERATION_ID = "_id";
    public static final String COLUMN_OPERATION_CATEGORY_ID = "categoryId";
    public static final String COLUMN_OPERATION_TYPE = "type";
    public static final String COLUMN_OPERATION_SUM = "sum";
    public static final String COLUMN_OPERATION_COMMENT = "comment";
    public static final String COLUMN_OPERATION_DATE = "date";
    public static final String COLUMN_OPERATION_PHOTO = "photo";

    private static final String CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT, " +
                    COLUMN_CATEGORY_ICON + " BLOB);";

    private static final String CREATE_OPERATIONS_TABLE =
            "CREATE TABLE " + TABLE_OPERATIONS + " (" +
                    COLUMN_OPERATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_OPERATION_CATEGORY_ID + " INTEGER, " +
                    COLUMN_OPERATION_TYPE + " TEXT, " +
                    COLUMN_OPERATION_SUM + " REAL, " +
                    COLUMN_OPERATION_COMMENT + " TEXT, " +
                    COLUMN_OPERATION_DATE + " TEXT, " +
                    COLUMN_OPERATION_PHOTO + " BLOB, " +
                    "FOREIGN KEY (" + COLUMN_OPERATION_CATEGORY_ID + ") REFERENCES " +
                    TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_OPERATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Operation getOperationById(long operationId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_OPERATION_ID,
                COLUMN_OPERATION_CATEGORY_ID,
                COLUMN_OPERATION_TYPE,
                COLUMN_OPERATION_SUM,
                COLUMN_OPERATION_COMMENT,
                COLUMN_OPERATION_DATE,
                COLUMN_OPERATION_PHOTO
        };

        String selection = COLUMN_OPERATION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(operationId)};

        Cursor cursor = db.query(
                TABLE_OPERATIONS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Operation operation = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_ID);
            int categoryIDColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_CATEGORY_ID);
            int typeColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_TYPE);
            int sumColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_SUM);
            int commentColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_COMMENT);
            int dateColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_DATE);
            int photoColumnIndex = cursor.getColumnIndex(COLUMN_OPERATION_PHOTO);

            if (idColumnIndex >= 0 && categoryIDColumnIndex >= 0 && typeColumnIndex >= 0 &&
                    sumColumnIndex >= 0 && commentColumnIndex >= 0 && dateColumnIndex >= 0) {

                int operationCategoryID = cursor.getInt(categoryIDColumnIndex);
                String operationTypeString = cursor.getString(typeColumnIndex);
                OperationType operationType = OperationType.getEnumFromString(operationTypeString);
                double operationSum = cursor.getDouble(sumColumnIndex);
                String operationComment = cursor.getString(commentColumnIndex);
                String operationDate = cursor.getString(dateColumnIndex);

                byte[] iconBytes = cursor.getBlob(photoColumnIndex);
                Bitmap operationPhoto = null;
                if (iconBytes != null)
                    operationPhoto = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);

                operation = new Operation(operationId, operationCategoryID, operationType, operationSum, operationComment, operationDate, operationPhoto);
            }

            cursor.close();
        }

        db.close();

        return operation;
    }

    public Category getCategoryById(long categoryId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_CATEGORY_ID,
                COLUMN_CATEGORY_NAME,
                COLUMN_CATEGORY_ICON
        };

        String selection = COLUMN_CATEGORY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};

        Cursor cursor = db.query(
                TABLE_CATEGORIES,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Category category = null;

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_CATEGORY_NAME);
            int iconColumnIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ICON);

            if (nameColumnIndex >= 0 && iconColumnIndex >= 0) {
                String categoryName = cursor.getString(nameColumnIndex);
                byte[] iconBytes = cursor.getBlob(iconColumnIndex);
                Bitmap categoryIcon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);

                category = new Category(categoryId, categoryName, categoryIcon);
            }

            cursor.close();
        }

        db.close();

        return category;
    }
}