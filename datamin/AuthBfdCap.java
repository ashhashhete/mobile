/**
 * 
 */
package com.igenesys.datamin;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author vineet
 *
 */
public interface AuthBfdCap {
	
	void updateImageView(final ImageView imgPreview, final Bitmap previewBitmap, String message, final boolean flagComplete, int captureError);
	//public void getresponse(String res);
	//void setQlyFinger(final int qly,boolean flagComplete);
	
}
