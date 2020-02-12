package com.example.filechooser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private File here;
    private Map<Integer,File> deleteTargetFiles = new HashMap<>();
    private boolean[] isChecked;

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private RecyclerView.LayoutManager gridLayoutManager;
    private RecyclerView.LayoutManager layoutManager;
    private TextView pointer;
    private ImageView toggle;
    private CheckBox checkBox;
    private Group editBtnGroup;
    private ImageView addFileBtn;
    private ImageView deleteFileBtn;
    private boolean isEditMode = ItemType.EDIT_MODE_OFF;
    private String root;
    public static final int REQUEST_CODE_ADD_FILE = 1000;
    public static final int REQUEST_CODE_EDIT_FILE = 2000;
    public static final boolean FILE_EDIT = true;
    public static final boolean FILE_MAKE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // File[]를 통으로 넘겨주는게 좋다.
        // 굳이 처리하여 제한된 정보만 넘겨주는 것보다 통으로 넘기는게 좋음.

        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 3);
        layoutManager = linearLayoutManager;

        initView();
//        root = Environment.getRootDirectory().toString();
        root = getFilesDir().getAbsolutePath();
        setRecyclerData(root);

    }

    private void initView() {
        toggle = findViewById(R.id.main_layout_change);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutManager == linearLayoutManager) {
                    toggle.setActivated(true);
                    layoutManager = gridLayoutManager;
                    mAdapter.setLayoutType(ItemType.LAYOUT_GRID);
                } else {
                    toggle.setActivated(false);
                    layoutManager = linearLayoutManager;
                    mAdapter.setLayoutType(ItemType.LAYOUT_LINEAR);
                }
                recyclerView.setLayoutManager(layoutManager);

            }
        });

        //상단 백버튼
        findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        //탐색 위치 나타내는 textView
        pointer = findViewById(R.id.pointer);

        //편집 모드에서의 하단 바
        editBtnGroup = findViewById(R.id.main_edit_btn_group);

        //파일 생성 키 클릭
        addFileBtn = findViewById(R.id.main_layout_add_file);
        addFileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditingActivity.class);
                intent.putExtra("PATH", here.getAbsolutePath());
                intent.putExtra("TYPE", FILE_MAKE);
                startActivityForResult(intent, REQUEST_CODE_ADD_FILE);
            }
        });

        //파일 삭제 키 클릭
        deleteFileBtn = findViewById(R.id.main_delete);
        deleteFileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 클릭을 하면 checkbox 체크 된 것을 확인해서 삭제할건지 확인하고 ok하면 삭제해주기
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("경고")
                        .setMessage("정말 삭제 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("DeleteFile-MainActivity", "onClick: "+deleteTargetFiles.size());
                                for(int key: deleteTargetFiles.keySet()){
                                    File target = deleteTargetFiles.get(key);
                                    mAdapter.deleteData(target, key);
                                    target.delete();
                                }
                                goBack();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.show();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if(isEditMode){
                    CheckBox target = view.findViewById(R.id.layout_file_select);
                    int index = mAdapter.getPosition(view);
                    target.setChecked(isChecked[index]);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if(isEditMode){
                    CheckBox target = view.findViewById(R.id.layout_file_select);
                    target.setChecked(false);
                }
            }
        });
    }

    // 리사이클러 뷰 데이터 설정
    private void setRecyclerData(String filePath) {
        here = new File(filePath);
        ArrayList<File> files = new ArrayList<>(Arrays.asList(here.listFiles()));
        mAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setDataList(files);
        mAdapter.setOnItemClickListener(itemClickListener);
        mAdapter.setOnItemLongClickListener(itemLongClickListener);
    }


    private void goBack() {
        if(isEditMode){
            isEditMode = ItemType.EDIT_MODE_OFF;
            deleteTargetFiles.clear();
            mAdapter.setEditMode(ItemType.EDIT_MODE_OFF);
            editBtnGroup.setVisibility(View.GONE);
            addFileBtn.setEnabled(true);
        } else{
//            if (here.getAbsolutePath().equals(Environment.getRootDirectory().toString())) {
            if (root.equals(here.getAbsolutePath())) {
                finish();
            } else {
                File parentFile = here.getParentFile();
                String pointerTextNow = pointer.getText().toString();
                pointer.setText(pointerTextNow.substring(0, pointerTextNow.length() - (here.getName().length() + 3)));
                here = parentFile;
                mAdapter.setDataList(new ArrayList<File>(Arrays.asList(parentFile.listFiles())));
            }
        }
    }


    private RecyclerAdapter.OnItemClickListener itemClickListener
            = new RecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(File data) {
            if (data.isDirectory()) {
                ArrayList<File> files = new ArrayList<>(Arrays.asList(data.listFiles()));
                here = data;
                pointer.append(" > " + here.getName());
                mAdapter.setDataList(files);
            } else if (data.isFile()) {

                Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.filechooser.fileprovider", data);
                String type = URLConnection.guessContentTypeFromName(data.getName());

                if("text/plain".equals(type)){
                    // 파일 읽어와서 값을 넘겨주기
                    Intent intent = new Intent(MainActivity.this, EditingActivity.class);
                    intent.putExtra("DATA", data);
                    intent.putExtra("TYPE", FILE_EDIT);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_FILE);
                } else{
                    // 파일 mimeType을 읽어와서 적절한 앱에 연결해주기
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    try {
                        if (type == null) {
                            type = "application/*";
                        }
                        intent.setDataAndType(uri, type);
                        startActivity(intent);
                    } catch (Exception e) {
                        type = type.split("/")[0] + "/*";
                        intent.setDataAndType(uri, type);
                        startActivity(intent);
                    }
                }

            }
        }

        @Override
        public void onItemClick(View view, File file, int index) {
            // 클릭이 발생하면 체크박스 체크하기
            checkBox = view.findViewById(R.id.layout_file_select);
            checkBox.setChecked(!checkBox.isChecked());
            if(checkBox.isChecked()){
                //체크 되면 어레이 리스트에 파일 추가
                deleteTargetFiles.put(index, file);
                isChecked[index] = true;
            } else{
                deleteTargetFiles.remove(index);
                isChecked[index] = false;
            }
        }
    };

    private RecyclerAdapter.OnItemLongClickListener itemLongClickListener
            = new RecyclerAdapter.OnItemLongClickListener() {
        @Override
        public void onItemLongClick() {
            isEditMode = ItemType.EDIT_MODE_ON;
            mAdapter.setEditMode(isEditMode);
            editBtnGroup.setVisibility(View.VISIBLE);
            addFileBtn.setEnabled(false);
            isChecked = new boolean[mAdapter.getItemCount()];
        }
    };

    //기존의 백버튼
    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_FILE && resultCode == Activity.RESULT_OK){
            mAdapter.addData((File)data.getExtras().get("DATA"));
        }
    }

}
