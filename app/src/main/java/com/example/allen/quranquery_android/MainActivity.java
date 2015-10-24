package com.example.allen.quranquery_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    static final int LoadAllDataFinish = 1000;
    static final int SearchFinish = 1001;
    static final String serachResKey = "serachResKey";

    static public QuranData quranData = new QuranData();
    static private SuraObject suraObject = null;
    static private  AyaObject ayaObject = new AyaObject();
    static public String allDatas;
    static boolean is_init = false;
    static boolean isShowAll = false;
    private ProgressBar myProgressBar;
    private int curPage = 0;
    private TextView textView;
    android.widget.SearchView searchView;

    private Thread thread = new Thread() {
        @Override
        public void run() {
            InputStream stream = getResources().openRawResource(R.raw.quran);
            try {
                parseFile(stream);
            } catch (IOException| XmlPullParserException e1) {
                e1.printStackTrace();
            }

            is_init = true;
        }
    };

    private void showNextSura() {
        ++curPage;
        if (curPage > quranData.max_sura_num){
            curPage = 1;
        }

        String suraStr = "" + curPage;
        showPage(suraStr);
    }

    public void showPage(String suraid) {
        SuraObject value = quranData.quranMap.get(suraid);
        StringBuilder builder = new StringBuilder();

        if (value != null) {
            for (int j = 1; j <= value.max_aya_num; ++j) {
                String ayaid = "" + j;
                AyaObject aya = value.suraMap.get(ayaid);
                if (aya == null) {
                    continue;
                }

                builder.append("[" + suraid + ":" + ayaid + "]" + aya.ayaContent + "\n");
            }

            textView.setMovementMethod(new ScrollingMovementMethod());

            textView.setText(builder.toString());
        }
    }
    private Handler handler =  new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.LoadAllDataFinish:
                {
                    curPage = 1;
                    showPage("1");

                    myProgressBar.setVisibility(View.INVISIBLE);
                }
                break;
                case MainActivity.SearchFinish:
                {
                    myProgressBar.setVisibility(View.INVISIBLE);
                    String res = (String)msg.getData().get(MainActivity.serachResKey);
                    textView.setText(res);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    private void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View v = MainActivity.this.getCurrentFocus();
            if (v == null) {
                return;
            }

            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            searchView.clearFocus();
            searchView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        textView = (TextView)findViewById(R.id.quran_text);
        searchView = (android.widget.SearchView)findViewById(R.id.searchView);

        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // to avoid click x button and the edittext hidden
                return true;
            }
        });

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String str) {
                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String newText) {
                if (newText != null && newText.length() > 0) {
                    myProgressBar.setVisibility(View.VISIBLE);
                    Thread searchThread = new Thread(){
                        public void run(){
                            searchCb(newText);
                        }
                    };
                    searchThread.start();
                    hideSoftInput();
                }
                return true;
            }

        });

        if (is_init) {
            myProgressBar.setVisibility(View.INVISIBLE);

            Intent intent = getIntent();
            if (intent == null) {
                return;
            }

            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            final String QueryRes = bundle.getString(QueryActivity.QueryRes);
            if (QueryRes != null) {
                if (QueryRes.equals(QueryActivity.fullDatas)) {
                    curPage = 1;
                    showPage("1");
                    return;
                }

                textView.setText(QueryRes);
                textView.setMovementMethod(new ScrollingMovementMethod());
                return;
            }
            final String res = bundle.getString(QueryActivity.KeyWord_Str);
            if (res != null && res.length() > 0) {
                myProgressBar.setVisibility(View.VISIBLE);
                Thread searchThread = new Thread(){
                    public void run(){
                        searchCb(res);
                    }
                };
                searchThread.start();
            }

            return;
        }

        thread.start();
    }

    private void searchCb(String res) {
        StringBuilder builder = new StringBuilder();
        serachKeyWords(res, builder);

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(serachResKey, builder.toString());
        msg.setData(bundle);
        msg.what = SearchFinish;
        MainActivity.this.handler.sendMessage(msg);
    }

    static public void serachKeyWords(String res, StringBuilder builder) {
        builder.append("");

        for (int i = 1; i <= quranData.max_sura_num; ++i) {
            String suraid = "" + i;
            SuraObject value = quranData.quranMap.get(suraid);
            if (value == null) {
                continue;
            }

            for (int j = 1; j <= value.max_aya_num; ++j) {
                String ayaid = "" + j;
                AyaObject aya = value.suraMap.get(ayaid);
                if (aya == null) {
                    continue;
                }

                if (aya.ayaContent.contains(res)) {
                    builder.append("[" + suraid + ":" + ayaid + "]" + aya.ayaContent + "\n");
                }
            }
        }
    }

    public boolean parseFile(InputStream stream) throws XmlPullParserException, IOException{
        if (stream == null) {
            return false;
        }

        StringBuilder res = new StringBuilder();

        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);
            parser.nextTag();

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("qurantext")){
                        if (ayaObject == null) {
                            ayaObject = new AyaObject();
                        }
                        ayaObject.ayaContent = parser.nextText();

                        res.append("[" + suraObject.suraID + ":" + ayaObject.ayaID + "]" + ayaObject.ayaContent);
                        res.append("\n");
                    }
                    else if (parser.getName().equalsIgnoreCase("sura")) {
                        if (suraObject == null) {
                            suraObject = new SuraObject();
                        }
                        suraObject.suraID = parser.getAttributeValue(null, "id");

                        int suraid = Integer.valueOf(suraObject.suraID);
                        if (quranData.max_sura_num < suraid) {
                            quranData.max_sura_num = suraid;
                        }
                        suraObject.suraName = parser.getAttributeValue(null, "name");
                    }
                    else if (parser.getName().equalsIgnoreCase("aya")) {
                        if (ayaObject == null) {
                            ayaObject = new AyaObject();
                        }

                        ayaObject.ayaID = parser.getAttributeValue(null, "id");
                        int ayaid = Integer.valueOf(ayaObject.ayaID);
                        if (suraObject.max_aya_num < ayaid) {
                            suraObject.max_aya_num = ayaid;
                        }
                    }
                }
                else if (eventType == XmlPullParser.END_TAG){
                    if (parser.getName().equalsIgnoreCase("qurantext")){

                    }
                    else if (parser.getName().equalsIgnoreCase("sura")) {
                        quranData.quranMap.put(suraObject.suraID, suraObject);
                        suraObject = null;
                    }
                    else if (parser.getName().equalsIgnoreCase("aya")) {
                        suraObject.suraMap.put(ayaObject.ayaID, ayaObject);
                        ayaObject = null;
                    }
                }

                eventType = parser.next();
            }
        } finally {
            stream.close();
        }

        isShowAll = true;

        Message message = new Message();
        message.what = MainActivity.LoadAllDataFinish;
        boolean msgres = MainActivity.this.handler.sendMessage(message);

        return true;
    }

    private float x1,x2;
    static final float Min_Distance = 150;

    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                if (x2 - x1 > Min_Distance)
                {
                    Intent intent = new Intent();
                    intent.setClass(this, QueryActivity.class);
                    startActivity(intent);
                }
                else if (x1 - x2 > Min_Distance)
                {
                    showNextSura();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
