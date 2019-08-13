package com.example.testtools.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testtools.R;

public class Activity_InputSN extends AppCompatActivity {

    private static final int SEARCH_CANCELLED = 0;
    private static final int SEARCH_SUCCESSFUL = 1;
    private Button getTest;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_sn_);

        getTest = findViewById(R.id.getTest);
        editText = findViewById(R.id.editText);

        getTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // SN: Serial Number
                String SN = editText.getText().toString();
                // Return (resultMessage, SN) to parent activity
                intent.putExtra("sendMessage", SN);
                setResult(SEARCH_SUCCESSFUL, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        // Return "Search Cancelled" if users press "Back" key
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.putExtra("cancelMessage", "Search Cancelled");
            setResult(SEARCH_CANCELLED, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
