/*
 * Copyright (C) 2015 Federico Paolinelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.whiterabbit.pisabike.apiclient;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whiterabbit.pisabike.model.Station;


import java.util.List;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class BikeRestClient {

    private BikeService mClient;

    public static Gson getGSon() {
        GsonBuilder builder = new GsonBuilder();

        return builder.create();
    }

    public BikeRestClient() {
        OkHttpClient okClient = new OkHttpClient.Builder().build();


        mClient = new Retrofit.Builder()
                              .baseUrl("https://api.trello.com/")
                              .client(okClient)
                              .addConverterFactory(GsonConverterFactory.create(getGSon()))
                              .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                              .build()
                              .create(BikeService.class);
    }

    public Observable<List<Station>> getStations() {
        return mClient.listStations();
    }

}
