package com.example.filemanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;
import static java.security.AccessController.getContext;

public class FileRecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int TYPE_FOLDER = 1;
    private static int TYPE_FILE = 2;
    private PackageManager packageManager = this.packageManager;
    private Context context;
    private List<Object> objectList = new ArrayList<>();
    private ArrayList<Object> newObjectList = new ArrayList<>();
    String currentPath, newPath;
    public FileRecyclerViewAdaptor(List<Object> objectList, String path)
    {
        this.objectList.addAll(objectList);
        currentPath = path;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        final ConstraintLayout constraintLayout;
        if(viewType == TYPE_FOLDER) {
            constraintLayout = (ConstraintLayout) LayoutInflater.from(context)
                    .inflate(R.layout.folder_item_layout, parent, false);
            return new FolderRecyclerViewHolder(constraintLayout);
        }
        else {
            constraintLayout = (ConstraintLayout) LayoutInflater.from(context)
                    .inflate(R.layout.file_item_layout, parent, false);
            return new FileRecyclerViewHolder(constraintLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(objectList.get(position) instanceof FolderClass)
            return TYPE_FOLDER;
        else if(objectList.get(position) instanceof FileClass)
            return TYPE_FILE;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == TYPE_FOLDER){
            FolderClass folderObj = (FolderClass) objectList.get(position);
            ((FolderRecyclerViewHolder) holder).setFolderDetails(folderObj.getName(), folderObj.getImageSrc());
            setFolderOnclickListener((FolderRecyclerViewHolder)holder);
        }
        else
        {
            FileClass fileObj = (FileClass)objectList.get(position);
            ((FileRecyclerViewHolder) holder).setFileDetails(fileObj.getName(), fileObj.getSize(),fileObj.getImageSrc());
            setFileOnclickListener((FileRecyclerViewHolder) holder);
        }
    }
    public void setFolderOnclickListener(final FolderRecyclerViewHolder holder)
    {
        holder.folderItemConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.folderNameTextView.getText().equals("No files found"))
                {
                    newPath = currentPath + "/" + holder.folderNameTextView.getText();
                    File newDir = new File(newPath);
                    fragmentJump(newDir);
                }
            }
        });
    }
    public void setFileOnclickListener(final FileRecyclerViewHolder holder)
    {
        holder.fileItemConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = ((TextView)view.findViewById(R.id.fileName)).getText().toString();
                //String filePath = ((TextView)view.findViewById(R.id.filePath)).getText().toString();
                File curFile = new File(currentPath+"/"+fileName);
                ContentResolver cR = context.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(cR.getType(Uri.fromFile(curFile)));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = getUriForFile(context, "com.example.fileprovider", curFile);
                intent.setDataAndType(uri,"application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Intent intentChooser = Intent.createChooser(intent, "open File");
//               intent.addCategory(Intent.CATEGORY_APP_GALLERY);
                try{
                    context.startActivity(intent);
                }
                catch (ActivityNotFoundException e)
                {
                    Log.e("intent", Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }
    public void fragmentJump(File dir){
        MainActivity mainActivity = (MainActivity)context;
        mainActivity.displayFiles(dir);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public static class FileRecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView fileNameTextView, fileSizeTextView;
        public ImageView fileImageView;
        public ConstraintLayout fileItemConstraintLayout;
        public FileRecyclerViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileName);
            fileSizeTextView = itemView.findViewById(R.id.fileSize);
            fileImageView = itemView.findViewById(R.id.fileImageView);
            fileItemConstraintLayout = itemView.findViewById(R.id.fileItemConstraintLayout);
        }
        private void setFileDetails(String fileName, String fileSize, int imageSrc){
            fileNameTextView.setText(fileName);
            fileSizeTextView.setText(fileSize);
            fileImageView.setImageResource(imageSrc);
        }
    }

    public static class FolderRecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView folderNameTextView;
        public ConstraintLayout folderItemConstraintLayout;
        public ImageView folderImageView;
        public FolderRecyclerViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            folderNameTextView = itemView.findViewById(R.id.folderName);
            folderImageView = itemView.findViewById(R.id.folderImageView);
            folderItemConstraintLayout = itemView.findViewById(R.id.folderItemConstraintLayout);
        }
        private void setFolderDetails(String folderName, int imageSrc){
            folderNameTextView.setText(folderName);
            folderImageView.setImageResource(imageSrc);
        }
    }
}
