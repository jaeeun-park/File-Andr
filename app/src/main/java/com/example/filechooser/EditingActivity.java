package com.example.filechooser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditingActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText editName;
    private EditText editContent;
    private ImageView goBackBtn;
    private File data;
    private String title;
    private String content;
    private String path;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        initView();
    }

    private void initView(){
        Intent recieveIntent = getIntent();
        isEditMode = recieveIntent.getBooleanExtra("TYPE", false);
        //edit 일 때만 데이터 설정한다.
        if(isEditMode){
            data = (File)recieveIntent.getExtras().get("DATA");
            title = data.getName().replace(".txt", "");
            content = readFile(data);
        } else{
            path = recieveIntent.getStringExtra("PATH");
        }

        saveBtn = findViewById(R.id.editing_save_file);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                content = editContent.getText().toString();
                if(!isEditMode){
                    title = editName.getText().toString();
                    data = new File(path, title+".txt");
                }
                if(title.length() > 0){

                    BufferedOutputStream bs = null;
                    try {
                        bs = new BufferedOutputStream(new FileOutputStream(data));
                        bs.write(content.getBytes());
//                        bs.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(EditingActivity.this, MainActivity.class);
                    intent.putExtra("DATA", data);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        editName = findViewById(R.id.editing_input_name);
        if(isEditMode){
            editName.setEnabled(false);
        }
        editContent = findViewById(R.id.editing_input_content);

        editName.setText(title);
        editContent.setText(content);

        goBackBtn = findViewById(R.id.editing_goBack);
        goBackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String readFile(File data){
        FileInputStream fs = null;
        byte[] buffer = null;
        try {
            fs = new FileInputStream(data);
            buffer = new byte[fs.available()];
            while(fs.read(buffer) != -1){}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(buffer);
    }

}
