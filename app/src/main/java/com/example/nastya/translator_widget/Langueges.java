package com.example.nastya.translator_widget;

import java.util.HashMap;

/**
 * Created by nastya on 07.09.16.
 */
public class Langueges {

    private static HashMap<String, String> langs;
    static {
        langs = new HashMap<>();
        langs.put("Russian", "ru");
        langs.put("English", "en");
        langs.put("French", "fr");
        langs.put("German", "de");
        langs.put("Spanish", "es");
        }
    public static String convert(String language){
        return langs.get(language);
    }

}
