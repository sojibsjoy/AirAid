package com.binarysoftwareltd.airaid;

import android.app.Person;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersFragment extends Fragment {
    String mobileNum;
    String totalOrdersNo;
    private View oView;
    private static final String STATE_USER = "user";
    private String mUser;
    private String deviceID;
    private ListView myOrdersList;
    private DatabaseReference dbR, dtbsR;
    private List<DataTemplate> dtList;
    private CustomAdapter customAdapter;
    Button loadOrders;

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
        if (oView != null) {
            return oView;
        }
        View vw = inflater.inflate(R.layout.fragment_my_orders, container, false);
        deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        initializeAll(vw);
        loadOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadData();
                Toast.makeText(getContext(),mobileNum,Toast.LENGTH_SHORT).show();
            }
        });
        oView = vw;
        return vw;
    }

    private void loadData() {

    }

    private void initializeAll(View v) {
        myOrdersList = v.findViewById(R.id.myOrdersList);
        dbR = FirebaseDatabase.getInstance().getReference(deviceID);
        dtbsR = FirebaseDatabase.getInstance().getReference(deviceID + " sub");
//        dtList = new ArrayList<>();
//        customAdapter = new CustomAdapter(getActivity(),dtList);
        loadOrders = v.findViewById(R.id.loadOrders);
    }

    @Override
    public void onStart() {
//        dbR.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds: dataSnapshot.getChildren()) {
//                    mobileNum = ds.getKey();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        dtbsR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dtS: dataSnapshot.getChildren()) {
                    totalOrdersNo = dtS.getKey();
                    DatabaseReference db = dtbsR.child(totalOrdersNo).child("currentOrderSerial");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mobileNum = String.valueOf(dataSnapshot.getValue(Integer.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //mobileNum = dtS.child(totalOrdersNo).child("currentOrderSerial").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
}
