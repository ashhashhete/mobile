package com.igenesys.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.ItemSearchListBinding;
import com.igenesys.model.SearchItemModel;
import com.igenesys.utils.AppLog;


import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchChildVH> {
    public Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<SearchItemModel> mValues;
    Activity activity;

    private OnItemClickListner onItemClickListner;

    public SearchAdapter(Context context, ArrayList<SearchItemModel> arrayList, OnItemClickListner onItemClickListner) {
        this.mValues = arrayList;
        this.mContext = context;
        this.onItemClickListner = onItemClickListner;
    }

    public void setDataList(ArrayList<SearchItemModel> arrayList) {
        mValues = arrayList;
        notifyDataSetChanged();
    }

    public void setNewDataList(ArrayList<SearchItemModel> arrayList) {
        mValues = arrayList;
        notifyDataSetChanged();
    }

    public SearchChildVH onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SearchChildVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_list, viewGroup, false));
    }

    public void onBindViewHolder(SearchChildVH viewHolder, int i) {

        try {
            SearchItemModel searchItemModel = mValues.get(i);

            SearchChildItemAdapter childItemAdapter = new SearchChildItemAdapter(activity, searchItemModel.getSearchChildItemModels());
            viewHolder.binding.rvSearchChild.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            viewHolder.binding.rvSearchChild.setAdapter(childItemAdapter);

//            viewHolder.binding.txtHeader.setText(searchItemModel.getHeader());

            viewHolder.binding.imgvProceed.setOnClickListener(view -> {
                onItemClickListner.onCardClicked(searchItemModel);
            });
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }


    public interface OnItemClickListner {
        void onCardClicked(SearchItemModel searchItemModel);
    }

    static class SearchChildVH extends RecyclerView.ViewHolder {

        ItemSearchListBinding binding;

        public SearchChildVH(@NonNull View itemView) {
            super(itemView);
            try {
                this.binding = DataBindingUtil.bind(itemView);
            } catch (Exception exception) {
                AppLog.e(exception.getMessage());
            }
        }
    }
}
