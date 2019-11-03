package com.binarysoftwareltd.airaid;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddressFragment extends Fragment {
    private String deviceID;
    private int orderNo;
    private int orderSerial;
    private DatabaseReference dbr, dbReference;
    private static final String STATE_USER = "user";
    private String mUser;
    private View oldView;
    private TextView numWarning;
    private EditText nameField, mobileNoField, areaField, houseNoField, wardNoField, roadNoField, colonyField, othersField;
    private CardView confirmCV;
    private int len;
    private String nameOfPerson, phoneNumber, areaName, houseNo, wardNo, roadNo, colony, othersOfAddress;
    private int[] serialNos = new int[100];
    private String[] names = new String[100];
    private int[] pieces = new int[100];
    private String imageUri;
    private Uri imgUri;
    private StorageReference stR;

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
            }
        });
        oldView = v;
        return v;
    }

    private void checkDetails() {
        if (!phoneNumber.equals("")) {
            //this condition must be equals to 11
            if (phoneNumber.length() == 11) {
                checkOrderSerial();
            } else {
                mobileNoField.requestFocus();
                Toast.makeText(getContext(), R.string.number_valid_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            mobileNoField.requestFocus();
            Toast.makeText(getContext(), R.string.number_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkOrderSerial() {
        orderSerial = 0;
        dbr = FirebaseDatabase.getInstance().getReference().child(deviceID + " sub");
        DatabaseReference newDR = dbr.child(phoneNumber);
        DatabaseReference newR = newDR.child("currentOrderSerial");
        newR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value != null) {
                    orderSerial = value;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        showConfirmDialog();
    }

    private void showConfirmDialog() {
        AlertDialog.Builder alb = new AlertDialog.Builder(getContext());
        alb.setIcon(R.drawable.question);
        alb.setTitle("Confirm");
        alb.setMessage("Are you sure want to order?");
        alb.setPositiveButton(R.string.exit_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alb.setNegativeButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setOrderSerial();
            }
        });
        AlertDialog ald = alb.create();
        ald.show();
    }

    private void setOrderSerial() {
        orderNo = orderSerial;
        orderNo += 1;
        dbr = FirebaseDatabase.getInstance().getReference(deviceID + " sub");
        OrderSerial osObject = new OrderSerial(orderNo);
        Map<String, Object> orderSerialMap = new HashMap<>();
        orderSerialMap.put(phoneNumber, osObject);
        dbr.updateChildren(orderSerialMap);
        AddressOfOrder addressObject = new AddressOfOrder(nameOfPerson,phoneNumber,areaName,houseNo,wardNo,roadNo,colony,othersOfAddress);
        DatabaseReference dtbsRfnc = dbr.child(phoneNumber);
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("Address", addressObject);
        dtbsRfnc.updateChildren(addressMap);
        uploadAllData();
    }

    private void uploadAllData() {
        dbReference = FirebaseDatabase.getInstance().getReference(deviceID).child(phoneNumber).child("orderNo: " + orderNo);
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

    private String getExtension(Uri uri) {
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        return mtm.getExtensionFromMimeType(cr.getType(uri));
    }

    private void ImageUploader() {
        if(imageUri!=null) {
            imgUri = Uri.parse(imageUri);
            stR = FirebaseStorage.getInstance().getReference(deviceID);
            StorageReference dtR = stR.child(phoneNumber);
            StorageReference ref = dtR.child("orderNo: " + orderNo+ "." + getExtension(imgUri));
            ref.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
    }

    private void collectAllData() {
        nameOfPerson = nameField.getText().toString();
        phoneNumber = mobileNoField.getText().toString();
        areaName = areaField.getText().toString();
        houseNo = houseNoField.getText().toString();
        wardNo = wardNoField.getText().toString();
        roadNo = roadNoField.getText().toString();
        colony = colonyField.getText().toString();
        othersOfAddress = othersField.getText().toString();
    }

    private void initializeAll(@NonNull View v) {
        numWarning = v.findViewById(R.id.numWarning);
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
}