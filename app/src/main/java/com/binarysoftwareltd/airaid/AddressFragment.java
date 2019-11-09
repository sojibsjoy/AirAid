package com.binarysoftwareltd.airaid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class AddressFragment extends Fragment {
    private String deviceID;
    private DatabaseReference dbReference;
    private static final String STATE_USER = "user";
    private String mUser;
    private View oldView;
    private EditText nameField, mobileNoField, areaField, houseNoField, wardNoField, roadNoField, colonyField, othersField;
    private CardView confirmCV;
    private int len;
    private String nameOfPerson, mobileNumber, areaName, houseNo, wardNo, roadNo, colony, othersOfAddress;
    private int[] serialNos = new int[100];
    private String[] names = new String[100];
    private int[] pieces = new int[100];
    private String imageUri;
    private String cTime;
    private String cDate;
    private Uri imgUri;
    private String imgUrl;
    private StorageReference stR;
    private FirebaseDatabase fD;
    private AlertDialog.Builder alb;
    private AlertDialog ald;
    private int count;
    private GifImageView gifImageView;



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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (oldView != null) {
            return oldView;
        }
        View v = inflater.inflate(R.layout.fragment_address, container, false);
        initializeAll(v);
        loadFromPhone();
        deviceID = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
        Bundle bundle = getArguments();
        if (bundle != null) {
            len = bundle.getInt("cValue");
            imageUri = bundle.getString("imgUri");
            serialNos = bundle.getIntArray("mSerialNos");
            names = bundle.getStringArray("mNames");
            pieces = bundle.getIntArray("mPieces");
        }
        confirmCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectAllData();
                checkDetails();
                saveToPhone();
            }
        });
        oldView = v;
        return v;
    }

    private void initializeAll(@NonNull View v) {
        fD = FirebaseDatabase.getInstance();
        nameField = v.findViewById(R.id.nameField);
        mobileNoField = v.findViewById(R.id.mobileNoField);
        areaField = v.findViewById(R.id.areaField);
        houseNoField = v.findViewById(R.id.houseNoField);
        wardNoField = v.findViewById(R.id.wardNoField);
        roadNoField = v.findViewById(R.id.roadNoField);
        colonyField = v.findViewById(R.id.colonyField);
        othersField = v.findViewById(R.id.othersField);
        confirmCV = v.findViewById(R.id.confirmCV);
    }

    private void loadFromPhone() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Addresses", Activity.MODE_PRIVATE);
        nameOfPerson = prefs.getString("name", "");
        mobileNumber = prefs.getString("mobileNo", "");
        areaName = prefs.getString("area", "");
        houseNo = prefs.getString("houseNo", "");
        wardNo = prefs.getString("wardNo", "");
        roadNo = prefs.getString("roadNo", "");
        colony = prefs.getString("colony", "");
        othersOfAddress = prefs.getString("others", "");
        if (mobileNumber != null) {
            setAllData();
        }
    }

    private void setAllData() {
        nameField.setText(nameOfPerson);
        mobileNoField.setText(mobileNumber);
        areaField.setText(areaName);
        houseNoField.setText(houseNo);
        wardNoField.setText(wardNo);
        roadNoField.setText(roadNo);
        colonyField.setText(colony);
        othersField.setText(othersOfAddress);
    }

    private void collectAllData() {
        nameOfPerson = nameField.getText().toString();
        mobileNumber = mobileNoField.getText().toString();
        areaName = areaField.getText().toString();
        houseNo = houseNoField.getText().toString();
        wardNo = wardNoField.getText().toString();
        roadNo = roadNoField.getText().toString();
        colony = colonyField.getText().toString();
        othersOfAddress = othersField.getText().toString();
    }

    private void checkDetails() {
        if (!mobileNumber.equals("")) {
            //this condition must be equals to 11
            if (mobileNumber.length() == 11) {
                showConfirmDialog();
            } else {
                mobileNoField.requestFocus();
                Toast.makeText(getContext(), R.string.number_valid_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            mobileNoField.requestFocus();
            Toast.makeText(getContext(), R.string.number_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfirmDialog() {
        AlertDialog.Builder alb = new AlertDialog.Builder(getContext());
        alb.setIcon(R.drawable.question);
        alb.setTitle(R.string.confirm_title);
        alb.setMessage(R.string.confirm_dialog_message);
        alb.setPositiveButton(R.string.exit_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alb.setNegativeButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setAddress();
            }
        });
        AlertDialog ald = alb.create();
        ald.show();
    }

    private void setAddress() {
        showGifDialog();
        if(haveNetworkConnection()) {
            AddressOfOrder addressObject = new AddressOfOrder(nameOfPerson, mobileNumber, areaName, houseNo, wardNo, roadNo, colony, othersOfAddress);
            DatabaseReference dtR = fD.getReference("Address");
            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put(mobileNumber, addressObject);
            dtR.updateChildren(addressMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        uploadAllData();
                    } else {
                        Toast.makeText(getContext(), "Failed to Submit your Order!", Toast.LENGTH_SHORT).show();
                        ald.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Network Error!", Toast.LENGTH_SHORT).show();
            ald.dismiss();
        }
    }


    private void uploadAllData() {
        getDateAndTime();
        dbReference = FirebaseDatabase.getInstance().getReference("Queue").child(deviceID).child(mobileNumber).child("Time: " + cTime + " Date: " + cDate);
        DataTemplate dtObject;
        Map<String, Object> orderMap = new HashMap<>();
        int i;
        for (i = 0; i < len; i++) {
            if (pieces[i] != 0) {
                dtObject = new DataTemplate(names[i], pieces[i]);
                orderMap.put("serialNo: " + serialNos[i], dtObject);
                dbReference.updateChildren(orderMap);
            }
        }
        ImageUploader();
    }

    private void getDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dF = new SimpleDateFormat("dd-MM-yyyy");
        cTime = df.format(c.getTime());
        cDate = dF.format(c.getTime());
    }

    private void ImageUploader() {
        if (imageUri != null) {
            imgUri = Uri.parse(imageUri);
            stR = FirebaseStorage.getInstance().getReference(deviceID);
            StorageReference dtR = stR.child(mobileNumber);
            StorageReference ref = dtR.child("Time: " + cTime + " Date: " + cDate + "." + getExtension(imgUri));
            ref.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                            DatabaseReference dRef = fD.getReference("Image").child(deviceID);
                            DatabaseReference imgRef = dRef.child(mobileNumber).child("Time: " + cTime + " Date: " + cDate);
                            imgRef.setValue(imgUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(),"Image Upload Failed!",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        ald.dismiss();
        setCheckGif();
    }

    private void showGifDialog() {
        alb = new AlertDialog.Builder(getContext());
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.confirm_layout, null);
        gifImageView = view.findViewById(R.id.gifView);
        gifImageView.setImageResource(R.drawable.loading);
        alb.setTitle("Please Wait...");
        alb.setView(view);
        ald = alb.create();
        ald.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
//                ald.dismiss();
            }
        });
        thread.start();
    }

    private void setCheckGif() {
        AlertDialog.Builder aldb = new AlertDialog.Builder(getContext());
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.confirm_layout, null);
        gifImageView = view.findViewById(R.id.gifView);
        gifImageView.setImageResource(R.drawable.check);
        aldb.setTitle("Order Submitted!");
        aldb.setView(view);
        aldb.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int something) {
                dlg.dismiss();
            }
        });
        AlertDialog altd = aldb.create();
        altd.show();
    }

    private void doWork() {
        for(count=1; count<=6; count++) {
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    private void saveToPhone() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Addresses", getActivity().MODE_PRIVATE).edit();
        editor.putString("name", nameOfPerson);
        editor.putString("mobileNo", mobileNumber);
        editor.putString("area", areaName);
        editor.putString("houseNo", houseNo);
        editor.putString("wardNo", wardNo);
        editor.putString("roadNo", roadNo);
        editor.putString("colony", colony);
        editor.putString("others", othersOfAddress);
        editor.apply();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}