package com.example.filemanager;

import java.io.Serializable;

public class FolderClass implements Serializable {
    private String name;
    int imageSrc;
    FolderClass(String name, int imageSrc)
    {
        this.name = name;
        this.imageSrc = imageSrc;
    }
    public String getName(){
        return this.name;
    }
    public int getImageSrc(){
        return this.imageSrc;
    }
}
