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

package com.whiterabbit.pisabike.apiclient;


import com.whiterabbit.pisabike.model.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

public class HtmlBikeClient {
    private static final String URL = "http://www.ciclopi.eu/frmLeStazioni.aspx";
    private OkHttpClient mClient;

    public HtmlBikeClient() {
        mClient = new OkHttpClient();
    }

    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i=0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    List<Station> parseStations(String s) {
        String[] values = s.split("','");
        String[] latitudes = values[3].split("\\|");
        String[] longitudes = values[4].split("\\|");
        String[] names = values[5].split("\\|");
        String[] bikes = values[6].split("\\|");
        String[] addresses = values[7].split("\\|");
        ArrayList<Station> res = new ArrayList<>(latitudes.length);

        for (int i = 0; i < names.length; i++) {
            long available = countOccurrences(bikes[i], '4');
            long free = countOccurrences(bikes[i], '0');
            long broken = countOccurrences(bikes[i], '5');
            if (names[i].endsWith(":")) {
                names[i] = names[i].substring(0, names[i].length() - 1);
            }
            if (addresses[i].endsWith(":")) {
                addresses[i] = addresses[i].substring(0, addresses[i].length() - 1);
            }
            Station station = new Station(names[i], "PISA", Double.valueOf(latitudes[i]),
                    Double.valueOf(longitudes[i]),
                    addresses[i],
                    available,
                    free,
                    broken,
                    false);
            res.add(station);
        }
        return res;
    }

    public List<Station> fetchStations(String page) throws IOException {
        final String CALL = "{RefreshMap(";
        Scanner s = new Scanner(page);
        while (s.hasNextLine()) {
            String line = s.nextLine();
            int callPosition = line.indexOf(CALL);
            if (callPosition == -1) {
                continue;
            }
            callPosition += CALL.length();
            int endPosition = line.indexOf("}", callPosition);

            String stationsToParse = line.substring(callPosition, endPosition);
            s.close();
            return parseStations(stationsToParse);
        }
        s.close();
        return null;
    }

    private List<Station> fetchStations() throws IOException {
        String toParse = run(URL);
        return fetchStations(toParse);
    }

    public Observable<List<Station>> getStations() {
        return Observable.fromCallable(this::fetchStations);
    }
}
