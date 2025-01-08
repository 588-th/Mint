package com.example.mint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;

public class StartingAmountActivity extends AppCompatActivity {

    //region Fields
    private static final String START_AMOUNT_KEY = "startAmount";
    private TextView textViewStartingAmount;
    private String startingAmount;
    private SharedPreferences sharedPreferences;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_amount);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        textViewStartingAmount = findViewById(R.id.textViewStartingAmount);
        startingAmount = "";
    }

    public void enterStartingAmount(View view) {
        if (startingAmount.equals("")){
            Toast.makeText(getApplicationContext(), "Starting amount cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(START_AMOUNT_KEY, Float.parseFloat(startingAmount));
        editor.apply();

        Intent intent = new Intent(this, OperationsActivity.class);
        startActivity(intent);
        finish();
    }

    public void numberEnter(View view) {
        Button button = (Button) view;
        String digit = button.getTag().toString();

        startingAmount += digit;
        textViewStartingAmount.setText(startingAmount);
    }

    public void backspacePress(View view) {
        if (TextUtils.isEmpty(startingAmount)) {
            return;
        }

        startingAmount = startingAmount.substring(0, startingAmount.length() - 1);
        textViewStartingAmount.setText(startingAmount);
    }
}
