package com.example.photo.Model;

import android.content.Context;

import java.io.File;

public interface FaceModel {
    void post_face(File file, OnFaceListener mlistener);
}
