package com.igenesys.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskedEditText{

    private static final char MASK_CHAR = 'x';

    public static void setupMaskedEditText(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed as text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setMaskedText(EditText editText, String originalText) {
        // Create a masked version of the text
        StringBuilder maskedText = new StringBuilder();
        if(originalText.length()==12){
            for (int i = 0; i < originalText.length()-4; i++) {
                maskedText.append(MASK_CHAR);
            }
            maskedText.append(originalText.substring(originalText.length() - 4));
        }else{
            maskedText.append(originalText);
        }


        // Set the masked text to the EditText
        editText.setText(maskedText.toString());
        // Store the original text as a tag of the EditText
        editText.setTag(originalText);
    }

    public static String getOriginalText(EditText editText) {
        // Retrieve the original text from the tag
        Object tag = editText.getTag();
        return tag != null ? tag.toString() : "";
    }
}
