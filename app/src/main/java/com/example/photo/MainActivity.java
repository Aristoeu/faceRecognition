package com.example.photo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photo.Bean.FaceBean;
import com.example.photo.Model.OnFaceListener;
import com.example.photo.Model.PostFace;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    private Bitmap bitmapp=null;
    Button takePhoto,chooseFromAlbum,save,preference;
    String realfile,emoti,genders,ethic,realethic,dd;
    String TAG = "MainActivity",log=null;
    FaceBean faces;
    TextView responseText,yanzhi,age,ethnicity,gender,emotion,praise;
    File file=null;
    SharedPreferences.Editor editor;
    PostFace postFace;
    OnFaceListener onFaceListener;
    ProgressBar progressBar;
    double disgust,fear,happiness,neutral,sadness,surprise,anger;

    public void initView(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
        takePhoto = (Button) findViewById(R.id.take_photo);
        chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        save = (Button)findViewById(R.id.save);
        preference = (Button)findViewById(R.id.preference);
        picture = (ImageView) findViewById(R.id.picture);
        responseText=(TextView)findViewById(R.id.response_text);
        emotion=(TextView)findViewById(R.id.fra_emotion);
        age=(TextView)findViewById(R.id.fra_age);
        gender=(TextView)findViewById(R.id.fra_gender);
        ethnicity=(TextView)findViewById(R.id.fra_ethnicity);
        yanzhi=(TextView)findViewById(R.id.fra_yanzhi);
        praise=(TextView)findViewById(R.id.praise);
        postFace= new PostFace();
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        onFaceListener= new OnFaceListener() {
            @Override
            public void onSuccess(String s) {
                dd=s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dd.length()<=500){
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this,"你传的这张照片上没有脸！",Toast.LENGTH_LONG).show();}
                        else setView(dd);
                    }});
            }
            @Override
            public void onError() {
                Toast.makeText(MainActivity.this,"你手机没联网吧！",Toast.LENGTH_SHORT).show();
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"请确认你给了拍照和相册存储的权限！",Toast.LENGTH_LONG).show();
        initView();
        init();
    }
//butterknife
    private void init() {
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.CAMERA }, 2);
                }else
                takephoto();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapp==null)
                    Toast.makeText(MainActivity.this,"请先选照片！",Toast.LENGTH_LONG).show();
                else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        praise.setVisibility(View.INVISIBLE);
                    }
                });
                saveBitmapFile(bitmapp);
                postFace.post_face(file,onFaceListener);
            }}
        });
        preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (log==null)
                    Toast.makeText(MainActivity.this,"你还没有测试过哦！",Toast.LENGTH_LONG).show();
                else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                        log =pref.getString("log","");
                        Toast.makeText(MainActivity.this,"上次的测试结果为：\n"+log,Toast.LENGTH_LONG).show();
                    }
                });}
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

    private void takephoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg"); // 创建File对象，用于存储拍照后的图片
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
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");// 启动相机程序
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
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
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takephoto();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
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
            file=new File(imagePath);
            if(file.length()<=900000)
                bitmapp=BitmapFactory.decodeFile(imagePath);
            else
            bitmapp=BitmapFactory.decodeFile(imagePath,getBitmapOption(6));
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBitmapFile(Bitmap bitmap) {
        realfile = Environment.getExternalStorageDirectory().toString()+"/001.jpg";
        Log.d("result",realfile);
        //Toast.makeText(this,realfile,Toast.LENGTH_SHORT).show();
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

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public void setView(String ee){
        anger = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getAnger();
        disgust = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getDisgust();
        fear = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getFear();
        happiness = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getHappiness();
        neutral = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getNeutral();
        sadness = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getSadness();
        surprise = getFace(ee).getFaces().get(0).getAttributes().getEmotion().getSurprise();

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

        String realgender=getFace(ee).getFaces().get(0).getAttributes().getGender().getValue();
        if (realgender.equals("Male"))
            genders="男";
        else genders="女";
        realethic=getFace(ee).getFaces().get(0).getAttributes().getEthnicity().getValue();
        if (realethic.equals("Asian")||realethic.equals("ASIAN"))
            ethic="亚洲人";
        else if (realethic.equals("White")||realethic.equals("WHITE"))
            ethic="白人";
        else if (realethic.equals("India")||realethic.equals("Indian")||realethic.equals("INDIA")||realethic.equals("INDIAN"))
            ethic="印度人";
        else ethic="黑人";
                yanzhi.setText("♂"+getFace(ee).getFaces().get(0).getAttributes().getBeauty().getMale_score()+" ♀"+getFace(ee).getFaces().get(0).getAttributes().getBeauty().getFemale_score());
                age.setText(getFace(ee).getFaces().get(0).getAttributes().getAge().getValue()+"");
                emotion.setText(emoti);
                gender.setText(genders);
                ethnicity.setText(ethic);
                Log.d(TAG,ee);
                editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("log","颜值："+yanzhi.getText().toString()+" 年龄:"+age.getText().toString()
                        +"\n" +"性别："+gender.getText().toString()+" 人种："+ethnicity.getText().toString());editor.apply();
                        log="ok";
                praise.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
    }
}