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

package com.whiterabbit.pisabike.screens.maincontainer;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.screens.main.MainFragment;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContainerView {
    @Inject
    MainContainerPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PisaBikeApplication app = (PisaBikeApplication) getApplication();

        DaggerMainContainerComponent.builder()
                .applicationComponent(app.getComponent())
                .mainContainerModule(new MainContainerModule(this))
                .build().inject(this);

    }

    @Override
    public void onBackPressed() {
        MainFragment main = (MainFragment) (getSupportFragmentManager().findFragmentById(R.id.main_activity_frame));
        if (main != null && main.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void displayMap() {
        if (getSupportFragmentManager().findFragmentById(R.id.main_activity_frame) == null) {
            Fragment f = MainFragment.createInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame, f)
                    .commit();
        }
    }

    @Override
    public void displayList() {

    }

    @Override
    public void sendBackPressedToMap() {

    }

    @Override
    protected void onResume() {
        mPresenter.onViewAttached(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPresenter.onViewDetached();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPresenter.onRestoreState(savedInstanceState);
    }
}
