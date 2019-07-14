package com.example.photo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    private ImageView picture;

    private Uri imageUri;

    Bitmap bitmapp;

    String realfile;

    String TAG = "MainActivity";

    FaceBean faces;

    TextView responseText,yanzhi,age,ethnicity,gender,emotion,praise;

    String responseData,emotions;

    File file;

    String dd;

    Bitmap bitmapsmall;

    SharedPreferences.Editor editor;

    double disgust,fear,happiness,neutral,sadness,surprise,anger;

    String emoti,genders,ethic,realethic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        Button save = (Button)findViewById(R.id.save);
       // Button sendRequest = (Button)findViewById(R.id.upload);
        Button preference = (Button)findViewById(R.id.preference);
        picture = (ImageView) findViewById(R.id.picture);
        responseText=(TextView)findViewById(R.id.response_text);
        emotion=(TextView)findViewById(R.id.fra_emotion);
        age=(TextView)findViewById(R.id.fra_age);
        gender=(TextView)findViewById(R.id.fra_gender);
        ethnicity=(TextView)findViewById(R.id.fra_ethnicity);
        yanzhi=(TextView)findViewById(R.id.fra_yanzhi);
        praise=(TextView)findViewById(R.id.praise);

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
                post_file(file);
            }
        });
        preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                        String log =pref.getString("log","");
                        Toast.makeText(MainActivity.this,log,Toast.LENGTH_SHORT).show();
                        responseText.setText(log);
                    }
                });
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
                        bitmapp=BitmapFactory.decodeFile(getExternalCacheDir()+"/output_image.jpg",getBitmapOption(6));
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
           // bitmapp=bitmap;
            bitmapp=BitmapFactory.decodeFile(imagePath,getBitmapOption(6));
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
        file=new File(realfile);//将要保存图片的路径
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
    protected void post_file(File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/jpg"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("image_file", file.getName(), body);
            requestBody.addFormDataPart("api_key", "f8FQX5Z_LHY9ObTzQG4M_czr4kDQz7fq");
            requestBody.addFormDataPart("api_secret", "0rVTpXFEI2GmD4ZJupdd0mmeZ6fQgLBh");
            requestBody.addFormDataPart("return_attributes", "gender,age,emotion,ethnicity,beauty");
        }

        Request request = new Request.Builder().url("https://api-cn.faceplusplus.com/facepp/v3/detect")
                .post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lfq" ,"onFailure");
                //Toast.makeText(MainActivity.this,"upload error",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    dd = response.body().string();
                    Log.i("lfq", response.message() + " , body " + dd);
                    //Toast.makeText(MainActivity.this,realfile,Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "<<<<d=" + dd);
                    faces = getFace(dd);

                    anger = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getAnger();
                    disgust = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getDisgust();
                    fear = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getFear();
                    happiness = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getHappiness();
                    neutral = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getNeutral();
                    sadness = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getSadness();
                    surprise = getFace(dd).getFaces().get(0).getAttributes().getEmotion().getSurprise();

                    double[] emo = {disgust, fear, happiness, neutral, sadness, surprise, anger};
                    double max = disgust;
                    int j=0;
                    for (int i = 0; i < 7; i++) {
                        if (emo[i] >= max) {
                            max = emo[i];
                            j = i;
                        }
                    }
                    switch (j) {
                        case 0:
                            emoti = "厌恶";
                            break;
                        case 1:
                            emoti = "恐惧";
                            break;
                        case 2:
                            emoti = "高兴";
                            break;
                        case 3:
                            emoti = "平静";
                            break;
                        case 4:
                            emoti = "伤心";
                            break;
                        case 5:
                            emoti = "惊讶";
                            break;
                        case 6:
                            emoti = "愤怒";
                            break;
                    }

                    String realgender=getFace(dd).getFaces().get(0).getAttributes().getGender().getValue();
                    if (realgender.equals("Male"))
                        genders="男";
                    else genders="女";

                    realethic=getFace(dd).getFaces().get(0).getAttributes().getEthnicity().getValue();
                    if (realethic.equals("Asian")||realethic.equals("ASIAN"))
                        ethic="亚洲人";
                    else if (realethic.equals("White")||realethic.equals("WHITE"))
                        ethic="白人";
                    else ethic="黑人";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            yanzhi.setText("♂"+getFace(dd).getFaces().get(0).getAttributes().getBeauty().getMale_score()+" ♀"+getFace(dd).getFaces().get(0).getAttributes().getBeauty().getFemale_score());
                            age.setText(getFace(dd).getFaces().get(0).getAttributes().getAge().getValue()+"");
                            emotion.setText(emoti);
                            gender.setText(genders);
                            ethnicity.setText(ethic);
                            Log.d(TAG,dd);
                            editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("log",responseText.getText().toString());editor.apply();
                            praise.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    Log.i("lfq" ,response.message() + " error : body " + response.body().string());
                    //Toast.makeText(MainActivity.this,"return error",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private BitmapFactory.Options getBitmapOption(int inSampleSize)

    {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }
}