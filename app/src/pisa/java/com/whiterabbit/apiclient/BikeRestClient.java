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

package com.whiterabbit.apiclient;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiterabbit.apiclient.*;
import com.whiterabbit.pisabike.model.Station;


import java.util.List;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class BikeRestClient {

    private com.whiterabbit.apiclient.BikeService mClient;

    public static Gson getGSon() {
        GsonBuilder builder = new GsonBuilder();
        return builder.create();
    }

    public BikeRestClient() {
        OkHttpClient okClient = new OkHttpClient.Builder().build();


        mClient = new Retrofit.Builder()
                              .baseUrl("https://api.citybik.es/v2/networks/ciclopi/")
                              .client(okClient)
                              .addConverterFactory(GsonConverterFactory.create(getGSon()))
                              .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                              .build()
                              .create(com.whiterabbit.apiclient.BikeService.class);
    }

    public Observable<List<Station>> getStations() {
        return mClient.listStations();
    }

}
