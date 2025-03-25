package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.RvListFilterViewBinding;

import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import com.igenesys.model.DahboardListModel;
import com.igenesys.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardRvListAdapter extends RecyclerView.Adapter implements Filterable {

    List<DahboardListModel> dahboardListModels, originalList, filteredList;
    private Activity activity;
    private Filter fRecords;

    public DashboardRvListAdapter(List<DahboardListModel> dahboardListModels, Activity activity) {
        this.dahboardListModels = dahboardListModels;
        this.originalList = dahboardListModels;
        this.activity = activity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<DahboardListModel> dahboardListModels) {
        this.originalList = dahboardListModels;
        this.dahboardListModels = dahboardListModels;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void dahboardListModels(List<DahboardListModel> dahboardListModels) {
        this.dahboardListModels = dahboardListModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_filter_view, parent, false);
        return new DashbaordVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        DahboardListModel dahboardListModel = dahboardListModels.get(position);

        if (dahboardListModel.getInputType().equalsIgnoreCase(Constants.clusterList)) {
            ((DashbaordVh) holder).viewBinding.txtClusterHeader.setText("Work Area:");
        } else {
//            ((DashbaordVh) holder).viewBinding.txtClusterHeader.setText("Tenement ID:");
            ((DashbaordVh) holder).viewBinding.txtClusterHeader.setText("Hut ID:");
        }
        ((DashbaordVh) holder).viewBinding.txtClusterName.setText(dahboardListModel.getClusterName());
        ((DashbaordVh) holder).viewBinding.txtClusterDate.setText(dahboardListModel.getDate());
        switch (dahboardListModel.getStatusTxt()) {
            case "In progress":
                ((DashbaordVh) holder).viewBinding.cvStatus.setCardBackgroundColor(activity.getColor(R.color.status_dark_blue));
                break;
            case "Not Started":
                ((DashbaordVh) holder).viewBinding.cvStatus.setCardBackgroundColor(activity.getColor(R.color.notStartedGridColor));
                break;
            case "On Hold":
                ((DashbaordVh) holder).viewBinding.cvStatus.setCardBackgroundColor(activity.getColor(R.color.onHoldGridColor));
                break;
            case "Completed":
                ((DashbaordVh) holder).viewBinding.cvStatus.setCardBackgroundColor(activity.getColor(R.color.status_dark_green));
                break;
        }
        ((DashbaordVh) holder).viewBinding.txtStatusLable.setText(dahboardListModel.getStatusTxt());

    }

    @Override
    public int getItemCount() {
        return dahboardListModels == null ? 0 : dahboardListModels.size();
    }

    //return the filter class object
    @Override
    public Filter getFilter() {
        if (fRecords == null) {
            fRecords = new RecordFilter();
        }
        return fRecords;
    }

    static class DashbaordVh extends RecyclerView.ViewHolder {
        RvListFilterViewBinding viewBinding;

        public DashbaordVh(View itemView) {
            super(itemView);
            this.viewBinding = DataBindingUtil.bind(itemView);
        }
    }

    //filter class
    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            //Implement filter logic
            // if edittext is null return the actual list
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = originalList;
                results.count = originalList.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<DahboardListModel> fRecords = new ArrayList<>();

                for (int l = 0; l < originalList.size(); l++) {
                    String serviceName = originalList.get(l).getStatusTxt().toLowerCase();
                    if (Utils.isNullOrEmpty(constraint.toString())) {
                        fRecords.add(originalList.get(l));
                    } else if (serviceName.toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                        fRecords.add(originalList.get(l));
                    }
                }
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            //it set the data from filter to adapter list and refresh the recyclerview adapter
            dahboardListModels = (ArrayList<DahboardListModel>) results.values;
            dahboardListModels(dahboardListModels);
            if (dahboardListModels.size() == 0)
                Utils.showMessagePopup("No record found.", activity);
//            notifyDataSetChanged();
        }
    }
}
