package com.example.allen.quranquery_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class QueryActivity extends AppCompatActivity {
    static public String KeyWord_Str = "KeyWord_";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
    }

    public void onQueryBtnClick(View view) {
        EditText textView = (EditText)findViewById(R.id.keywordtextView);
        if (textView.getText().length() > 0) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(QueryActivity.KeyWord_Str, textView.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
