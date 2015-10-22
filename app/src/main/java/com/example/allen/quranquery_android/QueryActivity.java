package com.example.allen.quranquery_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class QueryActivity extends AppCompatActivity {
    static public String KeyWord_Str = "KeyWord_";
    static  public String QueryRes = "QueryRes";
    private String suraid = new String("全部");
    private String ayaid = new String("全部");
    protected SuraObject querySura = null;
    private String resStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Spinner suraspinner = (Spinner)findViewById(R.id.suraspinner);
        List<String> sura_vec = new ArrayList<String>();
        sura_vec.add("全部");
        for (int i = 1; i <= MainActivity.quranData.max_sura_num; ++i) {
            sura_vec.add("" + i);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item
        , sura_vec);

        suraspinner.setAdapter(spinnerAdapter);
        suraspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                suraid = parent.getItemAtPosition(position).toString();
                if (suraid != "全部") {
                    querySura = MainActivity.quranData.quranMap.get(suraid);
                    if (querySura != null) {
                        Spinner aysspinner = (Spinner)findViewById(R.id.ayaspinner);
                        List<String> aya_vec = new ArrayList<String>();
                        aya_vec.add("全部");
                        for (int i = 1; i <= querySura.max_aya_num; ++i) {
                            aya_vec.add("" + i);
                        }

                        ArrayAdapter<String> tmpAdapter = new ArrayAdapter<String>(QueryActivity.this, android.R.layout.simple_spinner_item
                                , aya_vec);
                        aysspinner.setAdapter(tmpAdapter);
                        aysspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ayaid = parent.getItemAtPosition(position).toString();
                                if (querySura == null) {

                                }
                                else {
                                    AyaObject aya = querySura.suraMap.get(ayaid);
                                    if (aya != null) {
                                        resStr = "[" + suraid + ":" + aya.ayaID + "]" + aya.ayaContent;
                                    }
                                }
                            }
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
        }

            public void onNothingSelected(AdapterView<?> parent) {

            }
    });
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

    public void onSerachBtnClick(View view) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(QueryActivity.QueryRes, resStr);
            intent.putExtras(bundle);
            startActivity(intent);
    }
}
