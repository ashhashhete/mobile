package com.igenesys.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.LayerContent;
import com.esri.arcgisruntime.layers.LegendInfo;
import com.esri.arcgisruntime.symbology.Symbol;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.databinding.ViewShowTocBinding;
import com.igenesys.model.ChildTocModel;
import com.igenesys.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class ShowTocAdapter extends RecyclerView.Adapter<ShowTocAdapter.RowHolder> {
    private final List<Layer> layers;
    private final float density;
    private final Activity activity;
    private final String intentType;

    public ShowTocAdapter(Activity activity, List<Layer> layers, String intentType) {
        this.layers = layers;
        this.activity = activity;
        this.intentType = intentType;
        this.density = activity.getResources().getDisplayMetrics().density;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_show_toc, viewGroup, false);
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        Layer layer = layers.get(position);
        if (layer.getName().contains(Constants.workAreaLayerName))
            holder.binding.layerNameTv.setText(Constants.workAreaLayerNameProper);
        else if (layer.getName().contains(Constants.structureInfoLayerName))
            holder.binding.layerNameTv.setText(Constants.structureInfoLayerNameProper);
        else
            holder.binding.layerNameTv.setText(String.format("%s", layer.getName().replaceAll("_"," " ).trim()));

        holder.binding.layerCb.setChecked(layer.isVisible());

//        holder.binding.ivColor.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.bg_button));

//        if (layer.getName().equalsIgnoreCase(Constants.BOUNDARY_ULB))
//            holder.binding.ivColor.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.bg_button));
//
//        if (layer.getName().equalsIgnoreCase(Constants.BOUNDARY_WARD))
//            holder.binding.ivColor.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.txt_blue));
//
//        if (layer.getName().equalsIgnoreCase(Constants.BOUNDARY_DISTRICT))
//            holder.binding.ivColor.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.colorRed));

//        if (!layer.getName().equalsIgnoreCase("ULBBoundary")
//                &&
//                !layer.getName().equalsIgnoreCase("WardBoundary")
//                &&
//                !layer.getName().equalsIgnoreCase("District_layer")) {
//            BitmapDrawable bitmap = AppMethods.getBitmapDrawableFacility(activity, intentType);
//            holder.binding.ivColor.setImageDrawable(bitmap);
//        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        holder.binding.rvLegendInfo.setLayoutManager(layoutManager);
        holder.binding.rvLegendInfo.setHasFixedSize(true);

        ArrayList<ChildTocModel> childTocModels = new ArrayList<>();


        try {
            // Try get each layer legend

            holder.binding.lblLegendInfo.setVisibility(View.GONE);
            holder.binding.ivColor.setVisibility(View.VISIBLE);
            holder.binding.rvLegendInfo.setVisibility(View.GONE);

            if (layer.getSubLayerContents().size() > 0) {
                for (LayerContent subLayerContent : layer.getSubLayerContents()) {
                    final ListenableFuture<List<LegendInfo>> layerLegend = subLayerContent.fetchLegendInfosAsync();

                    layerLegend.addDoneListener(() -> {
                        try {
                            // Get each layer legend
                            List<LegendInfo> legendInfo = layerLegend.get();
//                            holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_up_24));
                            holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_down));
//                            holder.binding.lblLegendInfo.setText("Legend Info");
                            holder.binding.lblLegendInfo.setText("Click to expand the list.");
                            holder.binding.lblLegendInfo.setVisibility(View.VISIBLE);

                            Symbol legendSymbol = legendInfo.get(0).getSymbol();
                            ListenableFuture<Bitmap> symbolSwatch = legendSymbol.createSwatchAsync(activity, Color.TRANSPARENT);

                            // Set each layer legend
                            Bitmap symbolBitmap = symbolSwatch.get();
                            String name = subLayerContent.getName();
                            childTocModels.add(new ChildTocModel(name, symbolBitmap));

                            ShowChildTocAdapter showChildTocAdapter = new ShowChildTocAdapter(activity, childTocModels);
                            holder.binding.rvLegendInfo.setAdapter(showChildTocAdapter);
//                            holder.binding.rvLegendInfo.setVisibility(View.VISIBLE);

                        } catch (InterruptedException e) {
                            //showMessage("Animation interrupted");
                        } catch (ExecutionException e) {
                            // Deal with exception during animation...
                        }
                    });
                }
            } else {
                final ListenableFuture<List<LegendInfo>> layerLegend = layer.fetchLegendInfosAsync();

                layerLegend.addDoneListener(() -> {
                    try {
                        // Get each layer legend
                        List<LegendInfo> legendInfo = layerLegend.get();
                        if (legendInfo.size() == 1) {
                            Symbol legendSymbol = legendInfo.get(0).getSymbol();
                            ListenableFuture<Bitmap> symbolSwatch = legendSymbol.createSwatchAsync(activity, Color.TRANSPARENT);

                            // Set each layer legend
                            Bitmap symbolBitmap = symbolSwatch.get();
                            holder.binding.ivColor.setImageBitmap(symbolBitmap);
                        } else {
                            //holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_up_24));
                            holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_down));
                            holder.binding.lblLegendInfo.setText("Click to expand the list.");
                            holder.binding.lblLegendInfo.setVisibility(View.VISIBLE);
//                            holder.binding.lblLegendInfo.setText("Legend Info");

                            for (LegendInfo info : legendInfo) {
                                Symbol legendSymbol = info.getSymbol();
                                ListenableFuture<Bitmap> symbolSwatch = legendSymbol.createSwatchAsync(activity, Color.TRANSPARENT);

                                // Set each layer legend
                                Bitmap symbolBitmap = symbolSwatch.get();
                                String name = info.getName();
                                childTocModels.add(new ChildTocModel(name, symbolBitmap));
                            }
                            ShowChildTocAdapter showChildTocAdapter = new ShowChildTocAdapter(activity, childTocModels);
                            holder.binding.rvLegendInfo.setAdapter(showChildTocAdapter);
//                            holder.binding.rvLegendInfo.setVisibility(View.VISIBLE);
                        }

                    } catch (InterruptedException e) {
                        //showMessage("Animation interrupted");
                    } catch (ExecutionException e) {
                        // Deal with exception during animation...
                    }
                });
            }


        } catch (Exception e) {
            // Ignore changing layer legend
        }

        holder.binding.itemLay.setOnClickListener(v -> {
            if (childTocModels.size() > 0) {
                if (holder.binding.rvLegendInfo.getVisibility() == View.VISIBLE) {
                    holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_down));
                    holder.binding.rvLegendInfo.setVisibility(View.GONE);
                    holder.binding.lblLegendInfo.setVisibility(View.VISIBLE);
                    holder.binding.lblLegendInfo.setText("Click to expand the list.");
                } else {
                    holder.binding.ivColor.setImageDrawable(activity.getResources().getDrawable(R.drawable.black_arrow_up_24));
                    holder.binding.rvLegendInfo.setVisibility(View.VISIBLE);
                    holder.binding.lblLegendInfo.setVisibility(View.VISIBLE);
                    holder.binding.lblLegendInfo.setText("Legend Info");
                }
            }
        });


        holder.binding.layerCb.setOnCheckedChangeListener((compoundButton, b) -> {
            layer.setVisible(b);
            App.getSharedPreferencesHandler().putBoolean(layer.getName(), b);
        });

        holder.binding.ivSetting.setOnClickListener(v -> {
            if (holder.binding.rlSeekbar.getVisibility() == View.GONE) {
                holder.binding.rlSeekbar.setVisibility(View.VISIBLE);
                holder.binding.lblTransparent.setVisibility(View.VISIBLE);
            } else {
                holder.binding.rlSeekbar.setVisibility(View.GONE);
                holder.binding.lblTransparent.setVisibility(View.GONE);
            }
        });

        int progressValue = (int) (layer.getOpacity() * 100);
        holder.binding.seekBar.setProgress(progressValue);

        holder.binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.binding.tvSeekEnd.setText(String.format("%s", (float) progress / 100));
                layer.setOpacity((float) progress / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != layers ? layers.size() : 0);
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ViewShowTocBinding binding;

        RowHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}