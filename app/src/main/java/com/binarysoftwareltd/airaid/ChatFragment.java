package com.binarysoftwareltd.airaid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {
    private LinearLayout mLayout;
    private EditText mEditText;
    private Button mButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,container,false);

        mLayout = v.findViewById(R.id.ll);
        mEditText = v.findViewById(R.id.editText);
        mButton = v.findViewById(R.id.button);
        mButton.setOnClickListener(onClick());
        TextView textView = new TextView(getContext());
        textView.setText("New text");
        
        return v;
    }

    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.addView(createNewTextView(mEditText.getText().toString()));
            }
        };
    }
    private TextView createNewTextView(String text) {
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(getContext());
        textView.setLayoutParams(lparams);
        textView.setText("New text: " + text);
        return textView;
    }
}
