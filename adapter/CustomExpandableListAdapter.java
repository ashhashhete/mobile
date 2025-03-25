package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.utils.AppLog;

import java.util.ArrayList;
import java.util.HashMap;

/*
* This adapter is used to display hoh & it's member in a list.
* On screen Login > choose work area > choose one point > Summary screen > scroll & see.
*
* */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HohInfoDataModel> expandableListTitle;
    private HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, ArrayList<HohInfoDataModel> expandableListTitle,
                                       HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<HohInfoDataModel> expandableListTitle,
                        HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail) {
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        try {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).getHoh_id()).get(expandedListPosition);
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }
        return null;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final MemberInfoDataModel expandedListText = (MemberInfoDataModel) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandablelist_item, null);
        }

        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(expandedListText.getMember_name());

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        try {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition).getHoh_id()).size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        HohInfoDataModel listTitle = (HohInfoDataModel) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandablelist_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle.getHoh_name());

//        ExpandableListView mExpandableListView = (ExpandableListView) parent;
//        mExpandableListView.expandGroup(listPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}