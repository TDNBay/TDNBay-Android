package com.tcn.tcnbay.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tcn.tcnbay.R;
import com.tcn.tcnbay.background.tasks.service.VideoUploadService;

public class UploadVideoActivity extends AppCompatActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        Toolbar t = findViewById(R.id.uploadVideoToolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editText = findViewById(R.id.txtVideoTitle);
        Button b = findViewById(R.id.btnSelectFile);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate())
                    return;
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("video/*");
                photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(photoPickerIntent,getString(R.string.complete_action_using)), 1);
            }
        });
    }

    private boolean validate() {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.invalid_video_name_error));
            return false;
        }
        editText.setError(null);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri media = data.getData();
            if (media == null)
                return;
            Intent intent = new Intent(this, VideoUploadService.class);
            intent.putExtra("uri", media);
            intent.putExtra("videoTitle", editText.getText().toString());
            startService(intent);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
