package com.whiterabbit.pisabike.screens.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @Bind(R.id.update_view_icon_error)
    ImageView mIconError;

    private final Handler mHandler;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.update_view_layout, this, true);
        ButterKnife.bind(this);
        setVisibility(GONE);
        mHandler = new Handler();
    }



    public void setUpdating(String message) {
        animate().scaleX(0).scaleY(0).setDuration(0);

        setVisibility(VISIBLE);
        mIsProgress = true;
        mMessage.setText(message);
        mIcon.setVisibility(VISIBLE);
        mIconDone.setVisibility(GONE);
        mIconError.setVisibility(GONE);
        ValueAnimator rotation = ObjectAnimator.ofFloat(mIcon, "rotation", 0, 180);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setDuration(800);
        rotation.start();
        animate().scaleX(1).scaleY(1).setDuration(600);
    }

    private void setProgressFinished(String message, boolean success) {
        mHandler.postDelayed(() -> {
            mIsProgress = false;
            mMessage.setText(message);
            mIcon.setVisibility(GONE);
            if (success) {
                mIconDone.setVisibility(VISIBLE);
            } else {
                mIconError.setVisibility(VISIBLE);
            }
            new Handler().postDelayed( () -> {
                animate().scaleX(0).scaleY(0).setDuration(600);}, 1000);
        }, 1000);
    }

    public void setDone(String message) {
        setProgressFinished(message, true);
    }

    public void setError(String error) {
        setProgressFinished(error, false);
    }
}
