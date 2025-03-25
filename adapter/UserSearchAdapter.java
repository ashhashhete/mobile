package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.igenesys.R;
import com.igenesys.model.StructureInfoDataModel;

import java.util.ArrayList;

public class UserSearchAdapter extends ArrayAdapter<StructureInfoDataModel> {

    int count = 0;
    private Activity context;
    private LayoutInflater vi;
    private ArrayList<StructureInfoDataModel> mItems;


    public UserSearchAdapter(Activity context, ArrayList<StructureInfoDataModel> items) {
        super(context, 0, items);
        this.context = context;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;

    }


    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(ArrayList<StructureInfoDataModel> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final StructureInfoDataModel i = mItems.get(position);
        if (i != null) {

        }
        v = vi.inflate(R.layout.list_item, null);
        final TextView title = (TextView) v
                .findViewById(R.id.txtview);
        if (title != null)
            title.setText(i.getTenement_number());

        return v;
    }

}