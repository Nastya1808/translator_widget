package com.example.nastya.translator_widget;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;



/**
 * Created by nastya on 20.05.16.
 */
public class MyResultReceiver  extends ResultReceiver {
    private Receiver receiver;

    public MyResultReceiver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    public interface Receiver {
         void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}
