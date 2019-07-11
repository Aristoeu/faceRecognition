package com.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private ImageView picture;

    private Uri imageUri;

    Bitmap bitmapp;

    String realfile;

    String TAG = "MainActivity";

    FaceBean faces;

    TextView responseText;

    String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        Button save = (Button)findViewById(R.id.save);
        Button sendRequest = (Button)findViewById(R.id.upload);
        picture = (ImageView) findViewById(R.id.picture);
        responseText=(TextView)findViewById(R.id.response_text);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.cameraalbumtest.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBitmapFile(bitmapp);
            }
        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"start successfullyyyy");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("api_key", "f8FQX5Z_LHY9ObTzQG4M_czr4kDQz7fq")
                        .add("api_secret", "0rVTpXFEI2GmD4ZJupdd0mmeZ6fQgLBh")
                        //"http://p1.meituan.net/wedding/ecb5a4f726cc718b97dac68c9886e4cc148515.jpg"
                        .add("image_url", "http://p1.meituan.net/wedding/ecb5a4f726cc718b97dac68c9886e4cc148515.jpg")
                        .add("return_attributes", "gender,age,emotion,ethnicity,beauty")
                        .build();
                final Request request = new Request.Builder()
                        .url("https://api-cn.faceplusplus.com/facepp/v3/detect")
                        .post(body).build();
                Call call = client.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "<<<<e=" + e);
                        //Toast.makeText(MainActivity.this,"connect failed",Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String d = response.body().string();
                            Log.d(TAG, "<<<<d=" + d);
                            faces = getFace(d);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    responseText.setText("女生认为你的颜值是："+getFace(d).getFaces().get(0).getAttributes().getBeauty().getFemale_score()
                                            +"\n男生认为你的颜值是："+getFace(d).getFaces().get(0).getAttributes().getBeauty().getMale_score()
                                            +"\n你的年龄是"+getFace(d).getFaces().get(0).getAttributes().getAge().getValue()
                                            +"\n你的性别是"+getFace(d).getFaces().get(0).getAttributes().getGender().getValue()
                                            +"\n你的人种是"+getFace(d).getFaces().get(0).getAttributes().getEthnicity().getValue()
                                            +"\n经过分析，你这张照片传递出来的各种情绪的概率是：\n愤怒："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getAnger()
                                            +"\t厌恶："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getDisgust()
                                            +"\t恐惧："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getFear()
                                            +"\t高兴："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getHappiness()
                                            +"\n平静："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getNeutral()
                                            +"\t伤心："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getSadness()
                                            +"\t惊讶："+getFace(d).getFaces().get(0).getAttributes().getEmotion().getSurprise());
                                    Log.d(TAG,d);
                                }
                            });
                            //Toast.makeText(MainActivity.this,"connect successfully",Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });


    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmapp=bitmap;
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
    public void saveBitmapFile(Bitmap bitmap)
    {
        realfile = Environment.getExternalStorageDirectory().toString()+"/001.jpg";
        Log.d("result",realfile);
        Toast.makeText(this,realfile,Toast.LENGTH_SHORT).show();
        File file=new File(realfile);//将要保存图片的路径
        try{
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }catch (IOException e){
        e.printStackTrace();
    }
    }
    public static FaceBean getFace(String res){
        Gson gson = new Gson();
        FaceBean faceBean = gson.fromJson(res,FaceBean.class);
        return faceBean;
    }
}