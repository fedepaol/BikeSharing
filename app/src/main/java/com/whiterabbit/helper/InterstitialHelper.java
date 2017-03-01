/*
 * Copyright (c) 2016 Federico Paolinelli.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.whiterabbit.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class InterstitialHelper {
    private Context mContext;
    private static final String INTERSTITIAL_PREF = "com.whiterabbit.interst";
    InterstitialAd mInterstitialAd;
    private int mBase;
    private int mInterval;

    public InterstitialHelper(Context c,
                              int base,
                              int interval,
                              String unitId,
                              boolean showNow) {
        mContext = c;

        mInterstitialAd = new InterstitialAd(c);
        mInterstitialAd.setAdUnitId(unitId);
        mBase = base;
        mInterval = interval;

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                if (!showNow)
                    return;

                long count = getCounter();
                if (count > mBase && count % mInterval == 0) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    private long getCounter() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        long counter = prefs.getLong(INTERSTITIAL_PREF, 0) + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(INTERSTITIAL_PREF, counter);
        editor.apply();
        return counter;
    }

    public void checkAndShowInterstitial() {
        long count = getCounter();
        if (count > mBase && (count % mInterval) == 0) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }


}
