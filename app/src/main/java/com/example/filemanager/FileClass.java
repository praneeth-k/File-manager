package com.example.filemanager;

import java.io.Serializable;

public class FileClass implements Serializable {
    private String name, size;
    private int imageSrc;
    FileClass(String name, String size)
    {
        this.name = name;
        this.size = size;
        imageSrc = R.drawable.outline_file_black_18dp;
    }
    public String getName(){
        return this.name;
    }
    public String getSize(){
        return this.size;
    }
    public int getImageSrc(){
        return this.imageSrc;
    }
}
