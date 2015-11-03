package com.example.allen.quranquery_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class QueryActivity extends AppCompatActivity {
    static public String KeyWord_Str = "KeyWord_";
    static  public String QueryRes = "QueryRes";
    private String suraid = new String("全部");
    private String ayaid = new String("全部");
    static public String fullDatas = "fullDatas";
    protected SuraObject querySura = null;
    private String resStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
    }

}
