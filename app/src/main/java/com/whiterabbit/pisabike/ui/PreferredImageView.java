package com.whiterabbit.pisabike.ui;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;

import com.whiterabbit.pisabike.R;

public class PreferredImageView extends android.support.v7.widget.AppCompatImageView {
    private boolean mPreferred;

    public PreferredImageView(Context context) {
        super(context);
    }

    public PreferredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPreferred(boolean preferred) {
        mPreferred = preferred;
        setPreferredIcon();
    }

    private void setPreferredIcon() {
        if (mPreferred) {
            setImageResource(R.drawable.star_selected);
        } else {
            setImageResource(R.drawable.star_not_selected);
        }
    }

    public boolean getPreferred() {
        return mPreferred;
    }

    public void togglePreferred(boolean preferred) {
        /*
        mPreferred = preferred;
        int animationTime = getResources().getInteger( android.R.integer.config_shortAnimTime);
        animate().setDuration(animationTime).rotation(360).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setRotation(0);
                setPreferredIcon();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });*/
    }
}
