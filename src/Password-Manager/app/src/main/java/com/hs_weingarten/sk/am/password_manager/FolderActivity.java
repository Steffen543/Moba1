package com.hs_weingarten.sk.am.password_manager;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FolderActivity extends Fragment {

    ListView folderList;
    String[] folders;
    int[] image = {R.drawable.ic_folder};
    public FolderActivity(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View folderView = inflater.inflate(R.layout.list_view, container, false);
        folderList = (ListView) folderView.findViewById(R.id.listView);
        // here needs to be the call of the folders from the Database
        // for example:
        // folders = mydatabasehandler.getFolders();
        MyFolderAdapter folderAdapter = new MyFolderAdapter(getActivity(), folders, image);
        folderList.setAdapter(folderAdapter);

        return folderView;
    }

    private class MyFolderAdapter extends ArrayAdapter {
        int[] imageArray;
        String[] foldersArray;

        MyFolderAdapter(Context context, String[] folderNames, int[] folderImage) {
            super(context, R.layout.folder_view, R.id.folderName, folderNames);
            this.imageArray = folderImage;
            this.foldersArray = folderNames;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.folder_view, parent, false);

            ImageView folderImage = (ImageView) row.findViewById(R.id.folderImage);
            TextView folderName = (TextView) row.findViewById(R.id.folderName);

            folderImage.setImageResource(imageArray[position]);
            folderName.setText(foldersArray[position]);

            return row;
        }
    }
}
