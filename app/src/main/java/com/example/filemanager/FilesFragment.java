package com.example.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FilesFragment extends Fragment {
    ArrayList<Object> objectList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        objectList.clear();
        if (getArguments() != null) {
            objectList.addAll((List<Object>) getArguments().getSerializable("folderData"));
            if (objectList.size() > 0) {
                RecyclerView fileRecyclerView = (RecyclerView) container.findViewById(R.id.files_recycler_view);
                fileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                fileRecyclerView.setAdapter(new FileRecyclerViewAdaptor(objectList, getArguments().getString("path")));
            }
        }
        return inflater.inflate(R.layout.files_fragment_layout, container, false);
    }
}
