package com.example.allen.quranquery_android;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by allen on 15/10/21.
 */
public class SuraObject {
    public String suraID;
    public String suraName;
    public  int max_aya_num = 0;
    public Map<String, AyaObject> suraMap = new HashMap<String, AyaObject>();
}
