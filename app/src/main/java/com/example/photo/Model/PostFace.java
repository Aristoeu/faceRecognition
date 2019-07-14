package com.example.photo.Model;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostFace implements FaceModel {
    String ee;
    public void post_face(File file, final OnFaceListener mlistener){
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
                    ee = response.body().string();
                    mlistener.onSuccess(ee);
                } else {
                    mlistener.onError();
                }
            }
        });

    }
}
