package com.example.allen.quranquery_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    InputStream stream = getResources().openRawResource(R.raw.quran);

    try {
        parseFile(stream);
    } catch (IOException| XmlPullParserException e1){
        e1.printStackTrace();
    }
}

    public boolean parseFile(InputStream stream) throws XmlPullParserException, IOException{
        if (stream == null) {
            return false;
        }

        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);
            parser.nextTag();

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase("qurantext")){
                    }
                }
                else if (eventType == XmlPullParser.END_TAG){
                    if (parser.getName().equalsIgnoreCase("")){

                    }
                }

                eventType = parser.next();
            }
        } finally {
            stream.close();
        }
        return true;
    }
}
