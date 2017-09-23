package com.whiterabbit.pisabike;


import com.whiterabbit.pisabike.apiclient.HtmlBikeClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class DataUtil {
    HtmlBikeClient mClient;
    String testFile;

    public DataUtil(String filename) {
        this.mClient = new HtmlBikeClient();
        testFile = filename;
    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public String getStringFromFile (String filePath) throws IOException {
        File fl = getFileFromPath(this, filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public List<Station> getTestStations() {
        try {
            String toParse = getStringFromFile(testFile);
            List<Station> s = mClient.fetchStations(toParse);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
