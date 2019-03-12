package com.example.asuss410u.appfacebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.facebook.share.Share;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Options extends AppCompatActivity {
    EditText edtTitle,edtDesc,edtURL;
    Button btnShareLink, btnShareImg, btnShareVideo, btnPickVideo;
    ImageView imgShare;
    VideoView videoView;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    public static int Select_Image = 1;
    public static int Pick_Video = 2;
    Bitmap bitmap;
    Uri selectVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Anhxa();
        shareDialog = new ShareDialog(Options.this);
        btnShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareLinkContent = new ShareLinkContent.Builder().setContentTitle(edtTitle.getText().toString())
                            .setContentDescription(edtDesc.getText().toString())
                            .setContentUrl(Uri.parse(edtURL.getText().toString())).build();
                    // Gọi lại shareDialog cho nó show ra
                    shareDialog.show(shareLinkContent);
                }
            }
        });
        // Lấy ảnh
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // Lấy kết quả
                startActivityForResult(intent,Select_Image);

            }
        });
        // Chia sẻ ảnh sau khi lấy được (bitmap)
        btnShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        });
        btnPickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent,Pick_Video);
            }
        });
        btnShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShareDialog.canShow(ShareVideoContent.class)) {
                    ShareVideo shareVideo = new ShareVideo.Builder()
                            .setLocalUrl(selectVideo)
                            .build();
                    ShareVideoContent content = new ShareVideoContent.Builder()
                            .setVideo(shareVideo)
                            .build();
                    shareDialog.show(content);
                    videoView.stopPlayback();
                }
            }
        });

    }

    private void Anhxa() {
        edtTitle = (EditText)findViewById(R.id.edtTitile);
        edtDesc = findViewById(R.id.edtDesc);
        edtURL = findViewById(R.id.edtURL);
        btnShareImg = findViewById(R.id.btnShareImg);
        btnShareLink = findViewById(R.id.btnShareLink);
        btnShareVideo = findViewById(R.id.btnShareVideo);
        btnPickVideo = findViewById(R.id.btnPickVideo);
        imgShare = findViewById(R.id.imgShow);
        videoView = findViewById(R.id.videoView);
    }

    //Lấy ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Select_Image && resultCode == RESULT_OK){
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                 bitmap = BitmapFactory.decodeStream(inputStream);
                 imgShare.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == Pick_Video && resultCode == RESULT_OK){
            selectVideo = data.getData();
            videoView.setVideoURI(selectVideo);
            videoView.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
