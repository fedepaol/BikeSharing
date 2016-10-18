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

package com.whiterabbit.pisabike;


import com.whiterabbit.pisabike.apiclient.LocalBikeClient;
import com.whiterabbit.pisabike.model.Station;

import org.apache.maven.artifact.ant.shaded.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ParseTest {
    LocalBikeClient mClient;

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    @Before
    public void setup() {
        mClient = new LocalBikeClient();
    }

    @Test
    public void testParsing() {
        File file = getFileFromPath(this, "pisa_to_parse.html");

        try {
            String toParse = FileUtils.fileRead(file);
            List<Station> s = mClient.fetchStations(toParse);
        } catch (IOException e) {

        }
    }
}
