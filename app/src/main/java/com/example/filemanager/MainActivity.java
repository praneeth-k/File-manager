package com.example.filemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    ArrayList<File> fileList = new ArrayList<>();
    ArrayList<Object> objectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int readPermission = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int writePermission = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        if(readPermission!= PackageManager.PERMISSION_GRANTED || writePermission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 1);
        }
        if(readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED)
        {
            File directory = android.os.Environment.getExternalStorageDirectory();
            displayFiles(directory);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermissionsGranted = true;
        File directory = android.os.Environment.getExternalStorageDirectory();
        for (int res: grantResults){
            if(res != PackageManager.PERMISSION_GRANTED){
                allPermissionsGranted = false;
                break;
            }
        }
        if(allPermissionsGranted)
            displayFiles(directory);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        TextView filePathTextView = findViewById(R.id.filePath);
        String curPath = filePathTextView.getText().toString();
        String newPath;
        if(curPath.equals("/storage/emulated/0"))
            super.onBackPressed();
        else {
            newPath = curPath.substring(0, curPath.lastIndexOf("/"));
            displayFiles(new File(newPath));
        }
    }

    public void switchContent(Bundle bundleArgs, String path)
    {
        TextView filePathTextView = findViewById(R.id.filePath);
        filePathTextView.setText(path);
        FilesFragment filesFragment = new FilesFragment();
        filesFragment.setArguments(bundleArgs);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, filesFragment);
        transaction.commit();
    }
    @SuppressLint("DefaultLocale")
    public void appendFileObject(File file){
        float fileSize = Integer.parseInt(String.valueOf(file.length()));
        String unit = "B";
        if(fileSize>1000){
            unit = "KB";
            fileSize = fileSize/1024;
            if(fileSize>1000) {
                unit = "MB";
                fileSize = fileSize / 1024;
                if (fileSize > 1000) {
                    unit = "GB";
                    fileSize = fileSize / 1024;
                    if (fileSize > 1000) {
                        unit = "Tb";
                        fileSize = fileSize / 1024;
                    }
                }
            }
        }
        objectList.add(new FileClass(file.getName(),String.format("%.2f", fileSize)+unit));
    }
    public void appendFolderObject(String folderName, boolean isEmpty){
        if(isEmpty)
            objectList.add(new FolderClass(folderName, 0));
        else
            objectList.add(new FolderClass(folderName, R.drawable.outline_folder_black_24dp));
    }
    public void displayFiles(File directory)
    {
        if(directory.isDirectory()) {
            if (directory.list() != null && directory.list().length!=0) {
                fileList.clear();
                objectList.clear();
                fileList.addAll(Arrays.asList(directory.listFiles()));
                for(File file: fileList)
                {
                    if(file.isFile())
                        appendFileObject(file);
                    else
                        appendFolderObject(file.getName(),false);
                }
                Bundle args = new Bundle();
                args.putSerializable("folderData", objectList);
                args.putString("path", directory.toString());
                switchContent(args, directory.toString());
            } else {
                fileList.clear();
                objectList.clear();
                appendFolderObject("No Files Found", true);
                Bundle args = new Bundle();
                args.putSerializable("folderData", objectList);
                args.putString("path", directory.toString());
                switchContent(args, directory.toString());
            }
        }
    }
}