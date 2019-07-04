package nju.androidchat.client.component;

import android.graphics.Bitmap;

import java.util.UUID;

public interface OnImageMessageToShow {
    void addImageMessage(Bitmap bitmap, UUID messageId, boolean isSender, int index);
}
