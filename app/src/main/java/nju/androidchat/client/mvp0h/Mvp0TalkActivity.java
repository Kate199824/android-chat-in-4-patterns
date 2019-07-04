package nju.androidchat.client.mvp0h;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemImageReceive;
import nju.androidchat.client.component.ItemImageSend;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnImageMessageToShow;
import nju.androidchat.client.component.OnRecallMessageRequested;
import nju.androidchat.client.mvp0h.Mvp0ImagePresenter;

@Log
public class Mvp0TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested, OnImageMessageToShow {
    private Mvp0Contract.Presenter presenter;
    private Mvp0ImagePresenter imagePresenter;
    private List<View> messageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);

        this.imagePresenter = new Mvp0ImagePresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);
                    // 删除所有已有的ItemText
                    content.removeAllViews();
                    messageItems = new ArrayList<>();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        //如果是图片消息
                        Pattern i =  Pattern.compile("(?<=!\\[\\]\\()[^\\)]+");
                        Matcher m = i.matcher(text);
                        boolean isSender = message.getSenderUsername().equals(this.presenter.getUsername());
                        UUID messageId = message.getMessageId();
                        int index = messages.indexOf(message);
                        if(m.find()){
                            String v = m.group();
                            AsyncTask.execute(() -> {
                                this.imagePresenter.getBitMap(v, messageId, isSender, index);
                            });
                            if (isSender) {
                                messageItems.add(new ItemImageSend(this, null, messageId, this));
                            } else {
                                messageItems.add(new ItemImageReceive(this, null, messageId));
                            }
                        }
                        else {
                                // 如果是自己发的，增加ItemTextSend
                                if (isSender) {
                                    messageItems.add(new ItemTextSend(this, text, messageId, this));
                                } else {
                                    messageItems.add(new ItemTextReceive(this, text, messageId));
                                }
                        }
                    }
                    for(View view : messageItems)
                    content.addView(view);
                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }

    @Override
    public void addImageMessage(Bitmap bitmap, UUID messageId, boolean isSender, int index){
        runOnUiThread(() -> {
            LinearLayout content = findViewById(R.id.chat_content);
            View target;
            if (isSender) {
                target = new ItemImageSend(this, bitmap, messageId, this);
            } else {
                target =  new ItemImageReceive(this, bitmap, messageId);
            }
            content.removeViewAt(index);
            content.addView(target, index);

        });
    }
}

