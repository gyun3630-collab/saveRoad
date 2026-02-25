package com.example.mytest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WithdrawalActivity extends AppCompatActivity {

    private TextView textViewConfirm;
    private EditText editTextPassword;
    private Button buttonConfirm, buttonCancel;
    private boolean isEmailEntered = false;
    private boolean isAgreed = false;

    //팝업
    Button txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdrawalactivity_layout);


    }
}