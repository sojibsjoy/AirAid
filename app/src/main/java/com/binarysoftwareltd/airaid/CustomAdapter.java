package com.binarysoftwareltd.airaid;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<DataTemplate> {

    private Activity context;
    private List<DataTemplate> dtList;

    public CustomAdapter(Activity context, List<DataTemplate> dtList) {
        super(context, R.layout.orders_sample_layout, dtList);
        this.context = context;
        this.dtList = dtList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View vw = layoutInflater.inflate(R.layout.orders_sample_layout, null, true);
        DataTemplate dtObject = dtList.get(position);
        TextView mobileNo, totalOrdersNo;
        mobileNo = vw.findViewById(R.id.mobileNo);
        totalOrdersNo = vw.findViewById(R.id.totalOrdersNo);
        mobileNo.setText(new StringBuilder().append(R.string.mobile_no_field).append(": ").append(dtObject.getName()).toString());
        totalOrdersNo.setText(R.string.total_order + dtObject.getPiece());
        return vw;
    }
}
