package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.igenesys.R;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.igenesys.view.FormPageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAttachAdapter extends RecyclerView.Adapter<ViewAttachAdapter.HorizontalViewHolder> {

    List<MediaInfoDataModel> newMediaInfoDataModels;
    int flag=0;
    boolean isImageFitToScreen;
    private final Activity activity;
    private OnViewClickListner onViewClickListner;
    private String unit_relative_path="";
    private String unitUniqueId="";
    List<MediaInfoDataModel> listt=new ArrayList<>();
    List<MediaInfoDataModel> listtNN=new ArrayList<>();
    LocalSurveyDbViewModel localSurveyDbViewModel;
    HorizontalAdapter horizontalAdapter;
    ViewListAdapter viewListAdapter;
    HorizontalAdapterDelete horizontalAdapterDelete;
    ViewDeleteAdapterDelete viewDeleteAdapterDelete;

    List<AttachmentItemList> ltt=new ArrayList<>();
    public ViewAttachAdapter(List<MediaInfoDataModel> newMediaInfoDataModels, int flag, Activity activity, String unit_relative_path, String unitUniqueId, OnViewClickListner onViewClickListner, LocalSurveyDbViewModel localSurveyDbViewModel) {
        this.newMediaInfoDataModels = newMediaInfoDataModels;
        this.flag = flag;
        this.activity = activity;
        this.onViewClickListner = onViewClickListner;
        this.unit_relative_path = unit_relative_path;
        this.unitUniqueId = unitUniqueId;
        this.localSurveyDbViewModel = localSurveyDbViewModel;

    }

@Override
public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_layout_view_attach, parent, false);
    return new HorizontalViewHolder(view);
}

@Override
public void onBindViewHolder(HorizontalViewHolder holder, int position) {

    if (newMediaInfoDataModels != null && !newMediaInfoDataModels.isEmpty() && newMediaInfoDataModels.get(position) != null) {
        // holder.edtypeProof.setText(newMediaInfoDataModels.get(position).getDocument_category());
        holder.edtypeProof.setText(FormPageViewModel.AttName);
        holder.docType.setText(newMediaInfoDataModels.get(position).getDocument_type());
        holder.remarks.setText(newMediaInfoDataModels.get(position).getDocument_remarks());

        ltt = new ArrayList<>();
        for (int i = 0; i < newMediaInfoDataModels.get(position).getAttachmentItemLists().size(); i++) {
            if (!newMediaInfoDataModels.get(position).getAttachmentItemLists().get(i).isDeleted) {
                ltt.add(newMediaInfoDataModels.get(position).getAttachmentItemLists().get(i));
            }
        }
        if (newMediaInfoDataModels.get(position).getDocument_type().equals(Constants.UnitDistometerVideoType)) {
            // if(ltt.get(position).getItem_url().contains("http")){
            //     holder.btn_edit.setVisibility(View.INVISIBLE);
            // }else{
            //     holder.btn_edit.setVisibility(View.VISIBLE);
            // }
            viewListAdapter = new ViewListAdapter(newMediaInfoDataModels.get(position), ltt, 1, activity, Constants.video);
            holder.imageRecycler.setAdapter(viewListAdapter);
        } else {
            holder.btn_edit.setVisibility(View.VISIBLE);
            viewListAdapter = new ViewListAdapter(newMediaInfoDataModels.get(position), ltt, 1, activity, "");
            holder.imageRecycler.setAdapter(viewListAdapter);
        }
    }



    holder.btn_edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            holder.viewLayout.setVisibility(View.GONE);
            holder.deleteLayout.setVisibility(View.VISIBLE);
            holder.btn_delete.setVisibility(View.GONE);
            holder.imageRecyclerDelete.setVisibility(View.VISIBLE);
//            holder.catDoc.setText(newMediaInfoDataModels.get(position).getDocument_category());
            holder.catDoc.setText(FormPageViewModel.AttName);
            holder.typeDoc.setText(newMediaInfoDataModels.get(position).getDocument_type());
            holder.remarksDoc.setText(newMediaInfoDataModels.get(position).getDocument_remarks());

            ltt=new ArrayList<>();
            for(int i=0;i<newMediaInfoDataModels.get(position).getAttachmentItemLists().size();i++){
                if(!newMediaInfoDataModels.get(position).getAttachmentItemLists().get(i).isDeleted){
                    ltt.add(newMediaInfoDataModels.get(position).getAttachmentItemLists().get(i));
                }
            }
            viewDeleteAdapterDelete=new ViewDeleteAdapterDelete(newMediaInfoDataModels.get(position),ltt,2,activity,onViewClickListner,newMediaInfoDataModels,position);
            holder.imageRecyclerDelete.setAdapter(viewDeleteAdapterDelete);
        }
    });

    holder.btnBrowseEdit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onViewClickListner.onAttachmentBrowseClicked(newMediaInfoDataModels.get(position),newMediaInfoDataModels.get(position).getDocument_type(), unit_relative_path, newMediaInfoDataModels.get(position).getDocument_type() + "_" + Utils.getEpochDateStamp());
        }
    });

    holder.updateBtnMedia.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newMediaInfoDataModels.get(position).setDocument_remarks(holder.remarksDoc.getText().toString());
            onViewClickListner.onAttachmentUpdateClicked(newMediaInfoDataModels.get(position));
        }
    });

    holder.btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onViewClickListner.onAttachmentUpdateClicked(null);
        }
    });

    holder.btn_delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete the record?",
                    "Delete", "Cancel", false,2,position);
        }
    });

}

@Override
public int getItemCount() {
    return newMediaInfoDataModels.size();
}

public class HorizontalViewHolder extends RecyclerView.ViewHolder {
    TextView edtypeProof,docType,remarks,catDoc,typeDoc,remarksDoc;
    RecyclerView imageRecycler,imageRecyclerDelete;

    LinearLayout viewLayout,deleteLayout,btnBrowseEdit,updateBtnMedia;
    AppCompatButton btn_edit,btnCancel,btn_delete;

    public HorizontalViewHolder(View itemView) {
        super(itemView);
        edtypeProof = itemView.findViewById(R.id.edtypeProof);
        docType = itemView.findViewById(R.id.docType);
        remarks = itemView.findViewById(R.id.remarks);

        imageRecycler = itemView.findViewById(R.id.imageRecycler);
        imageRecyclerDelete = itemView.findViewById(R.id.imageRecyclerDelete);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity.getApplicationContext(),3);
        imageRecycler.setLayoutManager(gridLayoutManager);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(activity.getApplicationContext(),3);
        imageRecyclerDelete.setLayoutManager(gridLayoutManager1);

        viewLayout = itemView.findViewById(R.id.viewLayout);
        deleteLayout = itemView.findViewById(R.id.deleteLayout);
        btn_edit = itemView.findViewById(R.id.btn_edit);
        catDoc = itemView.findViewById(R.id.catDoc);
        typeDoc = itemView.findViewById(R.id.typeDoc);
        remarksDoc = itemView.findViewById(R.id.remarksDoc);
        btnBrowseEdit = itemView.findViewById(R.id.btnBrowseEdit);
        updateBtnMedia = itemView.findViewById(R.id.updateMediaBtn);
        btnCancel = itemView.findViewById(R.id.btnCancel);
        btn_delete = itemView.findViewById(R.id.btn_delete);

   } }


    public interface OnViewClickListner {
//        void onAttachmentImageDeleteClicked(AttachmentListImageDetails attachmentListImageDetails);
        void onAttachmentUpdateClicked(MediaInfoDataModel newMediaInfoDataModels);
        void onAttachmentDeletedClicked(List<MediaInfoDataModel> deleteTotalMediaList, int flag, int pos, List<AttachmentItemList> attachmentItemLists, String itemUrl);

        void onAttachmentBrowseClicked(MediaInfoDataModel mediaInfoDataModel, String documentType, String unitRelativePath, String s);

//        void onAttachmentDeleteClicked(String attachmentType, AttachmentListImageDetails attachmentListImageDetails);
    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod,int flag,int pos) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_mssage.setText(mssage);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);

        txt_yes.setText(btnYes);
        txt_no.setText(btnNo);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        ImageView img_close = customLayout.findViewById(R.id.img_close);
        btn_yes.getBackground().setColorFilter(activity.getColor(R.color.lighter_red), PorterDuff.Mode.SRC_ATOP);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            onViewClickListner.onAttachmentDeletedClicked(newMediaInfoDataModels,2,pos, null, "");
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedImages(List<AttachmentItemList> attachmentListImageDetails) {
        if (viewDeleteAdapterDelete != null)
            viewDeleteAdapterDelete.setUpdatedImages(attachmentListImageDetails);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUpdatedList(List<AttachmentItemList> attachmentListImageDetails) {
        if (viewDeleteAdapterDelete != null)
            viewDeleteAdapterDelete.setUpdatedList(attachmentListImageDetails);
    }
}