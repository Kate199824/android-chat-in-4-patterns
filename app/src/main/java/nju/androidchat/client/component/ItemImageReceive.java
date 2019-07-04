package nju.androidchat.client.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.util.UUID;

import nju.androidchat.client.R;

public class ItemImageReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;


    public ItemImageReceive(Context context,Bitmap bitmap, UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_text_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        setImage(bitmap);
    }

    public void setImage(Bitmap bm) {
        imageView.setImageBitmap(bm);
    }
    public void init(Context context) {

    }

}
