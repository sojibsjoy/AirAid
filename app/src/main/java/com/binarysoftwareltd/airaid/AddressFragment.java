package com.binarysoftwareltd.airaid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddressFragment extends Fragment {
    private int orderNo = 1;
    private int orderSerial = 0;
    private DatabaseReference dbReference, dbr;
    private static final String STATE_USER = "user";
    private String mUser;
    private View oldView;
    private TextView numWarning;
    private EditText nameField, phoneField, areaField, addressField, detailsField;
    private CardView confirmCV;
    private int len;
    private String nameOfPerson, phoneNumber, areaName, addressOfOrder, detailsOfOrder;
    private int[] serialNos = new int[100];
    private String[] names = new String[100];
    private int[] pieces = new int[100];
    private String imageUri;

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
            if (phoneNumber.length() > 10) {
                if (!addressOfOrder.equals("")) {
                    if (addressField.length() >= 5) {
                        checkOrderSerial();
                    } else {
                        addressField.requestFocus();
                        Toast.makeText(getContext(), R.string.address_valid_warning, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    addressField.requestFocus();
                    Toast.makeText(getContext(), R.string.address_warning, Toast.LENGTH_SHORT).show();
                }
            } else {
                phoneField.requestFocus();
                Toast.makeText(getContext(), R.string.number_valid_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            phoneField.requestFocus();
            Toast.makeText(getContext(), R.string.number_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkOrderSerial() {
        dbr = FirebaseDatabase.getInstance().getReference(phoneNumber).child("currentOrderSerial");
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value != null)
                    orderSerial = value;
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
//        dbr = FirebaseDatabase.getInstance().getReference(phoneNumber);
//        if(orderNo <= orderSerial) {
//            orderNo = orderSerial;
//            orderNo += 1;
//        }
//        Map<String, Integer> orderSerialMap = new HashMap<>();
//        orderSerialMap.put("currentOrderSerial",orderNo);

        dbr = FirebaseDatabase.getInstance().getReference();
        if (orderNo <= orderSerial) {
            orderNo = orderSerial;
            orderNo += 1;
        }
        OrderSerial osObject = new OrderSerial(orderNo);
        Map<String, Object> orderSerialMap = new HashMap<>();
        orderSerialMap.put(phoneNumber, osObject);
        dbr.updateChildren(orderSerialMap);
//        dbr.setValue(osObject);
        uploadAllData();
    }

    private void uploadAllData() {
        dbReference = FirebaseDatabase.getInstance().getReference(phoneNumber).child("orderNo: " + orderNo);
        DataTemplate dtObject;
        Map<String, Object> orderMap = new HashMap<>();
        int i;
        for (i = 0; i < len; i++) {
            if (pieces[i] != 0) {
                dtObject = new DataTemplate(names[i], pieces[i]);
                orderMap.put("serialNo: " + serialNos[i], dtObject);
                dbr.updateChildren(orderMap);
                //dbReference.child("serialNo: "+serialNos[i]).setValue(dtObject);
            }
        }
    }

    private void collectAllData() {
        nameOfPerson = nameField.getText().toString();
        phoneNumber = phoneField.getText().toString();
        areaName = areaField.getText().toString();
        addressOfOrder = addressField.getText().toString();
        detailsOfOrder = detailsField.getText().toString();
    }

    private void initializeAll(View v) {
        numWarning = v.findViewById(R.id.numWarning);
        nameField = v.findViewById(R.id.nameField);
        phoneField = v.findViewById(R.id.phoneField);
        areaField = v.findViewById(R.id.areaField);
        addressField = v.findViewById(R.id.addressField);
        detailsField = v.findViewById(R.id.detailsField);
        confirmCV = v.findViewById(R.id.confirmCV);
    }
}
