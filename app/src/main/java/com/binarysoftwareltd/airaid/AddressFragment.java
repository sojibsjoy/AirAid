package com.binarysoftwareltd.airaid;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddressFragment extends Fragment {
    private int len;
    private int[] serialNos = new int[100];
    private String[] names = new String[100];
    private int[] pieces = new int[100];
    private String imageUri;
    private ImageView pImg;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_address,container,false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            len = bundle.getInt("cValue");
            Toast.makeText(getContext(),len+"",Toast.LENGTH_SHORT).show();
            imageUri = bundle.getString("imgUri");
            serialNos = bundle.getIntArray("mSerialNos");
            names = bundle.getStringArray("mNames");
            pieces = bundle.getIntArray("mPieces");
            if(imageUri!=null) {
                pImg = v.findViewById(R.id.pImg);
                pImg.setImageURI(Uri.parse(imageUri));
            }
        }
//        int len = serialNos.length;
//        Toast.makeText(getContext(),len+"",Toast.LENGTH_SHORT).show();
        return v;
    }

}
