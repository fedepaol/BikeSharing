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

package com.whiterabbit.pisabike.screens.splash;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.whiterabbit.pisabike.screens.choosecity.ChooseNetworkActivity;
import com.whiterabbit.pisabike.screens.main.MainActivity;
import com.whiterabbit.pisabike.storage.PrefsStorage;

public class SplashActivity extends AppCompatActivity {
    PrefsStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = new PrefsStorage(this);

        Intent intent = null;
        if (mStorage.getCurrentNetwork() != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, ChooseNetworkActivity.class);
        }
        startActivity(intent);
        finish();
    }
}