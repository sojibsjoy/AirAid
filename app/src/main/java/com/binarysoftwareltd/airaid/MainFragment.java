package com.binarysoftwareltd.airaid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.otto.Subscribe;

import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {
    private static final String STATE_USER = "user";
    private String mUser;
    private View fragmentView;
    private char[] bengaliNum = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
    private boolean bengaliFlag = false;
    private ImageView img;
    private Uri imgUri;
    private boolean imgUploadedFlag = false;
    private boolean nameFlag = false;
    private boolean orderFlag = false;
    private int[] serialNos = new int[100];
    private String[] names = new String[100];
    private int[] pieces = new int[100];
    private TextView warningTV;
    private TextView[] serials = new TextView[100];
    private EditText[] mNameField = new EditText[100];
    private EditText[] mPieceField = new EditText[100];
    private int index = 6, counter = 5;
    private LinearLayout fLayout;
    private LinearLayout iLayout;
    private CardView uploadCV;
    private CardView addCV;
    private CardView nextCV;
    private String appLang;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mUser = savedInstanceState.getString(STATE_USER);
        } else {
            // Probably initialize members with default values for a new instance
            mUser = "NewUser";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_USER, mUser);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //this vw is the main view object to find all elements in main fragment
        if(fragmentView != null) {
            return fragmentView;
        }
        View vw = inflater.inflate(R.layout.fragment_main, container, false);
        appLang = getArguments().getString("language");
        if (appLang.equals("bn"))
            bengaliFlag = true;
        ///initializing all variables by using a method
        initializeAll(vw);
        //this is the cardView click method to upload an image
        uploadCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(intent, 1);
            }
        });
        //this is the cardView click method to add more input field for medicine details
        addCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 100) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //this vw is the main view object to find all elements in new added view
                    View vw = inflater.inflate(R.layout.medicine_field_layout, null);
                    fLayout.addView(vw, params);
                    serials[counter] = vw.findViewById(R.id.serialNum);
                    mNameField[counter] = vw.findViewById(R.id.mNameField);
                    mPieceField[counter] = vw.findViewById(R.id.mPieceField);
                    if (bengaliFlag) {
                        setBengaliDigits();
                        index++;
                    } else {
                        serials[counter++].setText(index + ".");
                        index++;
                    }
                } else
                    Toast.makeText(getContext(), "No more fields to create!", Toast.LENGTH_SHORT).show();
            }
        });
        nextCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataCollection();
                checkNames();
                if (imgUploadedFlag) {
                    valueCheck();
                    if (orderFlag) {
                        proceed();
                    } else {
                        Toast.makeText(getContext(), R.string.piece_empty_warning, Toast.LENGTH_SHORT).show();
                        mPieceField[0].requestFocus();
                    }
                } else if (nameFlag) {
                    valueCheck();
                    if (orderFlag) {
                        proceed();
                    } else {
                        Toast.makeText(getContext(), R.string.piece_empty_warning, Toast.LENGTH_SHORT).show();
                        mPieceField[0].requestFocus();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.fields_empty_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragmentView = vw;
        return vw;
    }

    private void proceed() {
        AddressFragment addressFragment = new AddressFragment();
        Bundle bundle = new Bundle();
        if (imgUploadedFlag)
            bundle.putString("imgUri", imgUri.toString());
        bundle.putInt("cValue", counter);
        bundle.putIntArray("mSerialNos", serialNos);
        bundle.putStringArray("mNames", names);
        bundle.putIntArray("mPieces", pieces);
        addressFragment.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().add(R.id.fragment_container, addressFragment,"AddressFragment").addToBackStack("key").commit();
    }

    private void checkNames() {
        int i;
        for (i = 0; i < counter; i++) {
            if (!names[i].equals("")) {
                nameFlag = true;
                break;
            }
        }
    }

    private void setBengaliDigits() {
        String newIndex = "";
        int[] d = new int[3];
        int num, i = 0, j;
        num = index;
        while (num > 0) {
            d[i] = num % 10;
            num /= 10;
            i++;
        }
        for (j = i - 1; j >= 0; j--) {
            char indx = bengaliNum[d[j]];
            newIndex += String.valueOf(indx);
        }
        serials[counter++].setText(newIndex + "।");
    }

    private void valueCheck() {
        int i;
        for (i = 0; i < counter; i++) {
            if (pieces[i] > 0) {
                orderFlag = true;
                break;
            }
        }
    }

    private void dataCollection() {
        int i;
        String sl;
        for (i = 0; i < counter; i++) {
            sl = serials[i].getText().toString();
            sl = sl.substring(0, sl.length() - 1);
            serialNos[i] = Integer.parseInt(sl);
            names[i] = mNameField[i].getText().toString();
            sl = mPieceField[i].getText().toString();
            if (sl.equals(""))
                sl = "0";
            pieces[i] = Integer.parseInt(sl);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    private void initializeAll(View v) {
        uploadCV = v.findViewById(R.id.uploadCV);
        warningTV = v.findViewById(R.id.warningTV);
        serials[0] = v.findViewById(R.id.serialNumI);
        serials[1] = v.findViewById(R.id.serialNumII);
        serials[2] = v.findViewById(R.id.serialNumIII);
        serials[3] = v.findViewById(R.id.serialNumIV);
        serials[4] = v.findViewById(R.id.serialNumV);
        mNameField[0] = v.findViewById(R.id.medicineNameFieldI);
        mNameField[1] = v.findViewById(R.id.medicineNameFieldII);
        mNameField[2] = v.findViewById(R.id.medicineNameFieldIII);
        mNameField[3] = v.findViewById(R.id.medicineNameFieldIV);
        mNameField[4] = v.findViewById(R.id.medicineNameFieldV);
        mPieceField[0] = v.findViewById(R.id.medicinePieceFieldI);
        mPieceField[1] = v.findViewById(R.id.medicinePieceFieldII);
        mPieceField[2] = v.findViewById(R.id.medicinePieceFieldIII);
        mPieceField[3] = v.findViewById(R.id.medicinePieceFieldIV);
        mPieceField[4] = v.findViewById(R.id.medicinePieceFieldV);
        fLayout = v.findViewById(R.id.fLayout);
        iLayout = v.findViewById(R.id.imageLayout);
        addCV = v.findViewById(R.id.addCV);
        nextCV = v.findViewById(R.id.nextCV);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Don't forget to check requestCode before continuing your job
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Do your job
            imgUri = data.getData();
            setImageLayout();
            img.setImageURI(imgUri);
            warningTV.setText(R.string.serial_warning);
            warningTV.setTextColor(getResources().getColor(R.color.img_warning_blue));
            imgUploadedFlag = true;
        }
    }

    private void setImageLayout() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View v = inflater.inflate(R.layout.image_layout, null);
        if (imgUploadedFlag)
            iLayout.removeAllViews();
        iLayout.addView(v, params);
        img = v.findViewById(R.id.pImage);
    }
}
