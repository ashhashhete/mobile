package com.igenesys.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.igenesys.R;
import com.igenesys.model.IdentifyItemListModel;
import com.igenesys.model.IndentifyFeatureListModel;
import com.igenesys.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FeatureListAdapter extends PagerAdapter {
    Context context;
    ArrayList<IndentifyFeatureListModel> indentifyFeatureListModel;
    ArrayList<IdentifyItemListModel> identifyItemListModel;
    String mode;

    OnSelectedItemClickListner onRecordItemClickListner;

    public FeatureListAdapter(Context context, ArrayList<IndentifyFeatureListModel> indentifyFeatureListModel, OnSelectedItemClickListner onRecordItemClickListner, List<IdentifyItemListModel> indentifyItemListModel) {
        this.context = context;
        this.indentifyFeatureListModel = indentifyFeatureListModel;
        this.onRecordItemClickListner = onRecordItemClickListner;
        this.identifyItemListModel = (ArrayList<IdentifyItemListModel>) indentifyItemListModel;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public int getCount() {
        return indentifyFeatureListModel.size();
    }

    @NotNull
    public Object instantiateItem(@NotNull ViewGroup viewGroup, final int i) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item__maplist, viewGroup, false);
        ((TextView) inflate.findViewById(R.id.txtLayerName)).setText(indentifyFeatureListModel.get(i).getLayerName());
        ((TextView) inflate.findViewById(R.id.txtHead1)).setText(indentifyFeatureListModel.get(i).getHeader3());

        ((TextView) inflate.findViewById(R.id.txtHead2)).setText(indentifyFeatureListModel.get(i).getHeader2());
//        ((TextView) inflate.findViewById(R.id.txtHead3)).setText(indentifyFeatureListModel.get(i).getHeader3());

        if (Utils.isNullOrEmpty(indentifyFeatureListModel.get(i).getGlobalId())) {
            inflate.findViewById(R.id.ic_imagesLayCheck).setVisibility(View.GONE);
            inflate.findViewById(R.id.ic_imagesLayView).setVisibility(View.VISIBLE);
            inflate.findViewById(R.id.ic_imagesLayView).setOnClickListener(view ->
                    onRecordItemClickListner.onItemClick(indentifyFeatureListModel.get(i)));
        } else {
            inflate.findViewById(R.id.ic_imagesLayView).setVisibility(View.GONE);
            inflate.findViewById(R.id.ic_imagesLayCheck).setVisibility(View.VISIBLE);
            inflate.findViewById(R.id.ic_imagesLayCheck).setOnClickListener(view ->
                    onRecordItemClickListner.onItemClick(indentifyFeatureListModel.get(i)));
        }

        inflate.setOnClickListener(view -> {

        });

        viewGroup.addView(inflate);
        return inflate;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((RelativeLayout) obj);
    }

    public interface OnSelectedItemClickListner {
        void onItemClick(IndentifyFeatureListModel indentifyFeatureListModel);
    }
}