package com.whiterabbit.pisabike.screens.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.whiterabbit.pisabike.R;


import butterknife.Bind;
import butterknife.ButterKnife;


public class ProgressView extends LinearLayout {
    private boolean mIsProgress;
    @Bind(R.id.update_view_background)
    ImageView mBackground;

    @Bind(R.id.update_view_icon)
    ImageView mIcon;

    @Bind(R.id.update_view_icon_done)
    ImageView mIconDone;

    @Bind(R.id.update_view_message)
    TextView mMessage;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.update_view_layout, this, true);
        ButterKnife.bind(this);
        setVisibility(GONE);
    }



    public void setUpdating(String message) {
        animate().scaleX(0).scaleY(0).setDuration(0);

        setVisibility(VISIBLE);
        mIsProgress = true;
        mMessage.setText(message);
        mIcon.setVisibility(VISIBLE);
        mIconDone.setVisibility(GONE);
        mIcon.setImageResource(R.drawable.ic_autorenew_white_24px);
        ValueAnimator rotation = ObjectAnimator.ofFloat(mIcon, "rotation", 0, 180);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setDuration(800);
        rotation.start();
        animate().scaleX(1).scaleY(1).setDuration(600);
    }

    public void setDone(String message) {
        mIsProgress = false;
        mIcon.setImageResource(R.drawable.ic_done_white_24px);
        mMessage.setText(message);
        mIcon.setVisibility(GONE);
        mIconDone.setVisibility(VISIBLE);
        animate().scaleX(0).scaleY(0).setDuration(600);
    }

    public void setError() {
        mIsProgress = false;

    }


}
