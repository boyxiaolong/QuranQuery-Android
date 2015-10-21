package com.example.allen.quranquery_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Xml;
import android.view.MotionEvent;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private QuranData quranData = new QuranData();
    private SuraObject suraObject;
    private  AyaObject ayaObject = new AyaObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream stream = getResources().openRawResource(R.raw.quran);

        try {
            parseFile(stream);
        } catch (IOException| XmlPullParserException e1) {
            e1.printStackTrace();
        }
    }

    private void searchCb(String res) {
        StringBuilder builder = new StringBuilder();
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
                    builder.append(aya.ayaContent + "/n");
                }
            }
        }

        TextView textView = (TextView)findViewById(R.id.quran_text);
        textView.setText(builder.toString());
    }
    public boolean parseFile(InputStream stream) throws XmlPullParserException, IOException{
        if (stream == null) {
            return false;
        }

        TextView textView = (TextView)findViewById(R.id.quran_text);
        textView.setMovementMethod(new ScrollingMovementMethod());
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
                        ayaObject.ayaContent = parser.nextText();

                        res.append(ayaObject.ayaContent);
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

                    }
                }

                eventType = parser.next();
            }
        } finally {
            stream.close();
        }

        textView.setText(res.toString());

        return true;
    }

    private float x1,x2;
    static final float Min_Distance = 150;

    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getY();
                float delta = x2 - x1;
                if (Math.abs(delta) < Min_Distance)
                {
                    //Toast.makeText(this, "left2right swipe", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(this, QueryActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String res = intent.getStringExtra(QueryActivity.KeyWord_Str);
        if (res != null) {
            searchCb(res);
            return;
        }
    }
}
