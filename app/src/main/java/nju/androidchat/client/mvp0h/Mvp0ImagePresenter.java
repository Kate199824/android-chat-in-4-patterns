package nju.androidchat.client.mvp0h;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import lombok.AllArgsConstructor;
import nju.androidchat.client.component.OnImageMessageToShow;


public class Mvp0ImagePresenter implements BasePresenter {

    private OnImageMessageToShow iMvp0TalkView;

    public Mvp0ImagePresenter(OnImageMessageToShow iMvp0TalkView)
    {
        this.iMvp0TalkView = iMvp0TalkView;
    }

    public void getBitMap(String url, UUID messageId, boolean isSender, int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                URL myFileUrl = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream is = null;
                    if (url.startsWith("http")) {
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        is = conn.getInputStream();
                    }
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                iMvp0TalkView.addImageMessage(bitmap, messageId, isSender, index);
            }
        }).run();


    }

    @Override
    public void start() {

    }
}
