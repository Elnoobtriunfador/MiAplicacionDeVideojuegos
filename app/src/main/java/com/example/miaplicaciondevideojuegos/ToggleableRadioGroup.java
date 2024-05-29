package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ToggleableRadioGroup extends RadioGroup {

    private RadioButton mSelectedRadioButton;

    public ToggleableRadioGroup(Context context) {
        super(context);
    }

    public ToggleableRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof RadioButton) {
            final RadioButton radioButton = (RadioButton) child;
            radioButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedRadioButton != null) {
                        mSelectedRadioButton.setChecked(false);
                        mSelectedRadioButton = null;
                    }
                    if (radioButton.isChecked()) {
                        mSelectedRadioButton = radioButton;
                    }
                }
            });
        }
    }
}
