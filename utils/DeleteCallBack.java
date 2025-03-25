package com.igenesys.utils;

import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.model.AttachmentListImageDetails;

public interface DeleteCallBack {

    void onDelete(String itemUrl, MediaInfoDataModel model,int position);
}
