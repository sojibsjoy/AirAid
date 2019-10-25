package com.binarysoftwareltd.airaid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MessageFragment extends Fragment {
    String name,piece;
    private TextView txtview;
    private EditText[] mNameField = new EditText[10];
    private EditText[] mPieceField = new EditText[10];
    private int mNameFieldId, mPieceFieldId, index=6,counter=0;
    private LinearLayout fLayout;
    private CardView addCV;
    private CardView nextCV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vw = inflater.inflate(R.layout.fragment_main, container, false);
        initializeAll(vw);
        addCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                View vw = inflater.inflate(R.layout.medicine_field_layout, null);
                fLayout.addView(vw, params);
                mNameField[counter++] = vw.findViewById(R.id.mNameField);
                txtview = vw.findViewById(R.id.serialNum);
                txtview.setText(index+".");
                index++;
//                pieceField = vw.findViewById(R.id.mPieceField);
//                mNameFieldId++;
//                // Add the new row before the add field button.

            }
        });
        nextCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
                if(counter==2) {
                    name = mNameField[1].getText().toString();
                    Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return vw;
    }

    private void initializeAll(View v) {
        fLayout = v.findViewById(R.id.fLayout);
        addCV = v.findViewById(R.id.addCV);
        nextCV = v.findViewById(R.id.nextCV);
    }
}
