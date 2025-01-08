package com.example.mint.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mint.R;
import com.example.mint.adapters.IconAdapter;
import com.example.mint.classes.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CreateCategoryActivity extends AppCompatActivity {

    //region Fields
    ArrayList<Bitmap> icons = new ArrayList<>();
    Bitmap selectedIcon;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        loadIcons();
        setRecyclerViewContent();
    }
    //endregion

    //region Methods
    private void setRecyclerViewContent(){
        RecyclerView recyclerView = findViewById(R.id.recyclerViewIcons);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        IconAdapter.OnClickListener iconClickListener = (icon, position) -> selectedIcon = icon;

        IconAdapter adapter = new IconAdapter(this, icons, iconClickListener);
        recyclerView.setAdapter(adapter);
    }

    private void loadIcons(){
        int[] drawableResourceIds =
                {
                R.drawable.ic_book,
                R.drawable.ic_brush,
                R.drawable.ic_camera,
                R.drawable.ic_cutlery,
                R.drawable.ic_gamepad,
                R.drawable.ic_home,
                R.drawable.ic_microphone,
                R.drawable.ic_monitor,
                R.drawable.ic_planetpng,
                R.drawable.ic_safebox,
                R.drawable.ic_shopping,
                R.drawable.ic_wifi,
                R.drawable.ic_bus,
                R.drawable.ic_light,
        };

        for (int resourceId : drawableResourceIds)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            icons.add(bitmap);
        }
    }

    public void createCategory(View view){
        EditText editText = findViewById(R.id.editTextName);
        String name = editText.getText().toString();

        if (name.equals("")) {
            Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedIcon == null){
            if (icons.size() == 0){
                return;
            }
            selectedIcon = icons.get(0);
        }

        try (DatabaseHelper dbHelper = new DatabaseHelper(this);
             SQLiteDatabase db = dbHelper.getWritableDatabase())
        {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, name);

            byte[] iconBytes = convertBitmapToByteArray(selectedIcon);
            values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, iconBytes);

            db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        }

        finish();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    //endregion
}