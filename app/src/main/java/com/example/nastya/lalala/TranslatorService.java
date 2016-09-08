package com.example.nastya.lalala;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by nastya on 20.05.16.
 */
public class TranslatorService extends IntentService {

    public static final String TAG = "Message";
    private String text;
    private String from;
    private String to;
    public TranslatorService() {
        super("MyIntentService");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {

        Translator translator = new Translator(this);
        String result = "";
        if (translator.isNetworkAvailable()) {
            result = translator.translate(text, from, to);

        }
        else result = "Network isn't available";

        ResultReceiver receiver = workIntent.getParcelableExtra("receiverTag");
        Bundle bundle = new Bundle();

        bundle.putString("result", result);
        receiver.send(0, bundle);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getStringExtra("text");
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }
}
