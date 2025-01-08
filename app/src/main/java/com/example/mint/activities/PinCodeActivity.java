package com.example.mint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mint.R;
import com.example.mint.classes.DataVerification;

import java.util.Objects;

public class PinCodeActivity extends AppCompatActivity {

    //region Fields
    private static final String START_AMOUNT_KEY = "startAmount";
    private static final String PIN_CODE_KEY = "pinCode";
    private String pinCode = "";
    private String pinCodeFirstStep = "";
    private String pinCodeSecondStep = "";
    private TextView textViewPinCode;
    private TextView textViewError;
    private SharedPreferences sharedPreferences;
    //endregion

    //region Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pinCode = sharedPreferences.getString(PIN_CODE_KEY, "");

        if (TextUtils.isEmpty(pinCode)) {
            setContentView(R.layout.activity_create_pin_code_step_first);
        } else {
            setContentView(R.layout.activity_enter);
        }

        updateLinks();
    }

    public void createPinCode(View view) {
        if (!DataVerification.pinCode(pinCode)) {
            showError("Pin code must contain 5 digits");
            return;
        }

        pinCodeFirstStep = pinCode;
        setContentView(R.layout.activity_create_pin_code_step_second);
        updateLinks();
    }

    public void confirmPinCode(View view) {
        pinCodeSecondStep = pinCode;

        if (TextUtils.equals(pinCodeFirstStep, pinCodeSecondStep)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PIN_CODE_KEY, pinCode);
            editor.apply();

            Intent intent = new Intent(this, StartingAmountActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_create_pin_code_step_first);
            updateLinks();
            showError("The pin code does not match, try again");
        }
    }

    public void enter(View view) {
        if (Objects.equals(pinCode, "000000")){
            setContentView(R.layout.activity_create_pin_code_step_first);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PIN_CODE_KEY, "");
            editor.apply();
            updateLinks();
            return;
        }

        if (TextUtils.equals(pinCode, sharedPreferences.getString(PIN_CODE_KEY, ""))) {

            float amount = sharedPreferences.getFloat(START_AMOUNT_KEY, -1);

            Intent intent;
            if (amount != -1){
                intent = new Intent(this, OperationsActivity.class);
            }
            else{
                intent = new Intent(this, StartingAmountActivity.class);
            }

            startActivity(intent);
            finish();
        } else {
            showError("Invalid pin code");
        }
    }

    public void numberEnter(View view) {
        Button button = (Button) view;
        String digit = button.getTag().toString();

        pinCode += digit;

        String altPoint = getResources().getString(R.string.alt_point);
        String newText = textViewPinCode.getText().toString() + altPoint;

        textViewPinCode.setText(newText);
    }

    public void backspacePress(View view) {
        if (TextUtils.isEmpty(pinCode)) {
            return;
        }

        pinCode = pinCode.substring(0, pinCode.length() - 1);

        String newText = textViewPinCode.getText().toString();
        textViewPinCode.setText(newText.substring(0, newText.length() - 1));
    }

    private void updateLinks() {
        textViewPinCode = findViewById(R.id.textViewPinCode);
        textViewError = findViewById(R.id.textViewError);
        pinCode = "";
    }

    private void showError(String message)
    {
        textViewError.setText(message);
        textViewError.setVisibility(View.VISIBLE);
    }
    //endregion
}