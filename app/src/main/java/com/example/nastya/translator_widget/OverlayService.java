package com.example.nastya.translator_widget;

/**
 * Created by nastya on 08.05.16.
 */
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OverlayService extends Service implements MyResultReceiver.Receiver {

    private WindowManager windowManager;

    private RelativeLayout rootLayout;
    private LinearLayout contentContainerLayout;

    private TextView translation;
    private View bubble;

    MyResultReceiver receiver;

    private String from;
    private String to;

    private boolean isrootLayout = false;

    public static final String TAG = "Message";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        bubble = new ImageView(this);
       ((ImageView)bubble).setImageResource(R.drawable.bubble);


        final WindowManager.LayoutParams bubbleParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,                    //These windows are normally placed above all applications, but behind the status bar. In multiuser systems shows on all users' windows.
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,            // Don't let it grab the input focus
                PixelFormat.TRANSLUCENT                                   // Make the underlying application window visible through any transparent parts
        );

        bubbleParams.gravity = Gravity.TOP | Gravity.LEFT;
        bubbleParams.x = 0;
        bubbleParams.y = 100;
        windowManager.addView(bubble, bubbleParams);


        rootLayout = (RelativeLayout)LayoutInflater.from(this).
                inflate(R.layout.widget_layout, null);
        contentContainerLayout = (LinearLayout) rootLayout.findViewById(R.id.container_layout);
        translation = (TextView)contentContainerLayout.findViewById(R.id.hello_tv);

        final WindowManager.LayoutParams rootParams = new WindowManager.LayoutParams(
                900, 170, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        rootParams.gravity = Gravity.TOP | Gravity.LEFT;
        rootParams.x = 100;

        receiver = new MyResultReceiver(new Handler());
        receiver.setReceiver(this);

        try {
            bubble.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = bubbleParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private boolean isClick;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            isClick = true;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (isClick) {
                                if (!isrootLayout) {
                                    windowManager.addView(rootLayout, rootParams);
                                    isrootLayout = true;
                                    getTranslation();
                                } else {
                                    if (rootLayout != null) windowManager.removeView(rootLayout);
                                    isrootLayout = false;
                                }
                            }
                            isClick = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            isClick = false;
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(bubble, paramsF);
                            break;
                    }
                    return false;
                }
            });


            rootLayout.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = rootParams;
                private int initialY;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            initialY = paramsF.y;
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(rootLayout, paramsF);
                            break;
                    }
                    return false;
                }
            });


        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubble != null) windowManager.removeView(bubble);
        if (rootLayout != null && rootLayout.getWindowToken() != null) windowManager.removeView(rootLayout);
    }

    private void getTranslation() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String paste = "";

        if ((clipboard.hasPrimaryClip())) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            paste = item.getText().toString();
            callIntentService(paste);
        }
    }

    private void callIntentService(String text){
        Intent serviceIntent = new Intent(this, TranslatorService.class);
        serviceIntent.putExtra("text", text);
        serviceIntent.putExtra("receiverTag", receiver);
        serviceIntent.putExtra("from", from);
        serviceIntent.putExtra("to", to);
        startService(serviceIntent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        translation.setText(resultData.getString("result"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null){
            from = intent.getStringExtra("from");
            to = intent.getStringExtra("to");
        }
        return START_NOT_STICKY;
    }
}
