package com.example.workcollab;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        readIntent();


        String dest_uri= new StringBuilder(UUID.randomUUID().toString()).append(".png").toString();

        UCrop.Options options = new UCrop.Options();
        options.setContrastEnabled(false);
        options.setBrightnessEnabled(false);
        options.setFreeStyleCropEnabled(false);
        options.setSaturationEnabled(false);
        options.setSharpnessEnabled(false);
        options.setShowCropGrid(false);
        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),dest_uri)))
                .withOptions(options)
                .withAspectRatio(1,1)
                .withMaxResultSize(500,500)
                .start(this);
    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
            System.out.println(fileUri.toString());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            System.out.println(data.toString());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri + "");
            setResult(-1, returnIntent);
            finish();
        }else if(resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
            finish();
        }
    }
}