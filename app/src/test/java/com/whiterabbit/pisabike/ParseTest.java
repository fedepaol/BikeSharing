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
