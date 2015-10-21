package com.example.allen.quranquery_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
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

    public boolean parseFile(InputStream stream) throws XmlPullParserException, IOException{
        if (stream == null) {
            return false;
        }

        TextView textView = (TextView)findViewById(R.id.quran_text);
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
                        suraObject.suraName = parser.getAttributeValue(null, "name");
                    }
                    else if (parser.getName().equalsIgnoreCase("aya")) {
                        ayaObject.ayaID = parser.getAttributeValue(null, "id");
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
}
