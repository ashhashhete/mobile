package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.databinding.RvListItemSummaryBinding;
import com.igenesys.model.AttachmentListImageDetails;
import com.igenesys.adapter.CustomExpandableListAdapter;
import com.igenesys.model.SummaryItemModel;
import com.igenesys.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private final String attachmentType;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<SummaryItemModel> summaryItemModels;
    private CustomExpandableListAdapter customExpandableListAdapter;
    private ArrayList<HohInfoDataModel> expandableListTitle;
    private ExpandableListAdapter expandableListAdapter;
    private HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail;

    private OnItemClickListner onItemClickListner;

    public SummaryListAdapter(Activity activity, ArrayList<SummaryItemModel> summaryItemModels, String attachmentType, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.summaryItemModels = summaryItemModels;
        this.attachmentType = attachmentType;
        this.onItemClickListner = onItemClickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_summary, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SummaryItemModel summaryItemModel = summaryItemModels.get(position);
        ((SummaryChildVH) holder).binding.commonSummaryCardHeader.txtHeader.setText(summaryItemModel.getHeader());
        if (position==0){
            ((SummaryChildVH) holder).binding.laySummaryChild.setVisibility(View.VISIBLE);
        }
        ((SummaryChildVH) holder).binding.commonSummaryCardHeader.imgCircularUpDown.setOnClickListener(view -> {
            if (((SummaryChildVH) holder).binding.laySummaryChild
                    .getVisibility() == View.VISIBLE) {
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.imgCircularUpDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_circular_down_svg));
                ((SummaryChildVH) holder).binding.laySummaryChild.setVisibility(View.GONE);
            } else {
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.imgCircularUpDown.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_circular_up_svg));
                ((SummaryChildVH) holder).binding.laySummaryChild.setVisibility(View.VISIBLE);
            }
        });

        String status = "N/A";
        String structStatus = summaryItemModel.getUnitStatus();
        int imgInt = R.color.main_color;

        if (structStatus.equals(Constants.InProgress_statusLayer)) {
            status = Constants.In_Progress;
//            imgInt = R.color.inProgressBoarderColor;
            imgInt = R.color.status_dark_blue;
        } else if (structStatus.equals(Constants.OnHold_statusLayer)) {
            status = Constants.On_Hold;
            imgInt = R.color.onHoldBoarderColor;
        } else if (structStatus.equals(Constants.NotStarted_statusLayer)) {
            status = Constants.Not_Started;
            imgInt = R.color.notStartedBoarderColor;
        } else if (structStatus.equals(Constants.completed_statusLayer)) {
            status = Constants.completed;
            imgInt = R.color.completeBoarderColor;
        }else if (structStatus.equals(Constants.completed_dispute)) {
            status = Constants.dispute;
            imgInt = R.color.completeBoarderColor;
        }


//        if (summaryItemModel.isUnitDetails) {
//            ((SummaryChildVH) holder).binding.commonSummaryCardHeader.txtHeaderStatus.setText(status);
//        } else {
//            ((SummaryChildVH) holder).binding.commonSummaryCardHeader.txtHeaderStatus.setText("");
//        }
//        ((SummaryChildVH) holder).binding.commonSummaryCardHeader.txtHeaderStatus.setTextColor(ContextCompat.getColorStateList(activity, imgInt));

        switch (summaryItemModel.getUnitStatus()) {
            case "Completed":
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusComplete.setVisibility(View.VISIBLE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusInProgress.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusOnHold.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusDispute.setVisibility(View.GONE);
                break;
            case "In Progress":
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusComplete.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusInProgress.setVisibility(View.VISIBLE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusOnHold.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusDispute.setVisibility(View.GONE);
                break;
            case "On Hold":
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusComplete.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusInProgress.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusOnHold.setVisibility(View.VISIBLE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusDispute.setVisibility(View.GONE);
                break;
            case "Dispute":
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusComplete.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusInProgress.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusOnHold.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusDispute.setVisibility(View.VISIBLE);
                break;
            default:
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusComplete.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusInProgress.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusOnHold.setVisibility(View.GONE);
                ((SummaryChildVH) holder).binding.commonSummaryCardHeader.unitStatusDispute.setVisibility(View.GONE);
        }
// Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
//        layoutManager.setInitialPrefetchItemCount(summaryItemModel.getSummaryChildItemModels().size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        SummaryChildItemAdapter childItemAdapter = new SummaryChildItemAdapter(activity, summaryItemModel.getSummaryChildItemModels());
        ((SummaryChildVH) holder).binding.rvSummaryChild.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        ((SummaryChildVH) holder).binding.rvSummaryChild.setAdapter(childItemAdapter);


        if ( summaryItemModel.isMemberDetails) {// vidnyan,commenting member check summaryItemModel.isMemberDetails
            ArrayList<HohInfoDataModel> expandableListTitle = new ArrayList<>();
            HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail = new HashMap<>();


            if (summaryItemModel.getHohInfoDataModelArrayList() != null && summaryItemModel.getHohInfoDataModelArrayList().size() > 0) {
                expandableListTitle = new ArrayList<>(summaryItemModel.getHohInfoDataModelArrayList());
                ((SummaryChildVH) holder).binding.cvMemberDetails.setVisibility(View.VISIBLE);
            } else ((SummaryChildVH) holder).binding.cvMemberDetails.setVisibility(View.GONE);

            if (summaryItemModel.getMemberInfoList() != null && summaryItemModel.getMemberInfoList().size() > 0) {
                expandableListDetail.putAll(summaryItemModel.getMemberInfoList());
            }

            customExpandableListAdapter = new CustomExpandableListAdapter(activity, expandableListTitle, expandableListDetail);
            expandableListAdapter = customExpandableListAdapter;
            ((SummaryChildVH) holder).binding.expandableListView.setVerticalScrollBarEnabled(false);
            ((SummaryChildVH) holder).binding.expandableListView.setAdapter(expandableListAdapter);

            for (int i = 0; i < expandableListTitle.size(); i++) {
                if (expandableListTitle.size()==1 || i== expandableListDetail.size()-1){
                    ((SummaryChildVH) holder).binding.expandableListView.expandGroup(i,true);
                }else {
                    ((SummaryChildVH) holder).binding.expandableListView.expandGroup(i);
                }
            }

        }else{
            ((SummaryChildVH) holder).binding.cvMemberDetails.setVisibility(View.GONE);
        }


        ((SummaryChildVH) holder).binding.commonSummaryCardHeader.imgEdit.setOnClickListener(view -> {
            if (summaryItemModel.isUnitDetails) {
                onItemClickListner.onUnitEditBtnClicked(position - 1);
            } else if (summaryItemModel.isMemberDetails) {

            } else
                onItemClickListner.onStructureEditBtnClicked();

        });


            //((SummaryChildVH) holder).binding.cvMemberDetails.setVisibility(View.VISIBLE);


    }

    private void addToList(ArrayList<HohInfoDataModel> hohInfoDataModelArrayList, HashMap<String,
            ArrayList<MemberInfoDataModel>> memberInfoList) {


        expandableListTitle.addAll(hohInfoDataModelArrayList);
        expandableListDetail.putAll(memberInfoList);

    }

    @Override
    public int getItemCount() {
        return summaryItemModels == null ? 0 : summaryItemModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSummaryItemModels(ArrayList<SummaryItemModel> summaryItemModels) {
        this.summaryItemModels = summaryItemModels;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSummaryItemModels(ArrayList<SummaryItemModel> summaryItemModels) {
        this.summaryItemModels.addAll(summaryItemModels);
        notifyDataSetChanged();
    }

    public interface OnAttachmentItemClickListner {
        void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails);

        void onAttachmentCancelBtnClicked(String attachmentType, AttachmentListImageDetails attachmentListImageDetails);
    }

    public interface OnItemClickListner {
        void onStructureEditBtnClicked();

        void onUnitEditBtnClicked(int position);
    }

    static class SummaryChildVH extends RecyclerView.ViewHolder {

        RvListItemSummaryBinding binding;

        public SummaryChildVH(@NonNull View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }

}
