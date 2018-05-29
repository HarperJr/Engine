package main;

import java.io.*;
import java.util.HashMap;

public class Config {
    private static final String CONFIG_SOURCE = "config.ini";
    private static final HashMap<String, String> configs = new HashMap<>();

    public static void initConfig() {
        try {
            InputStream stream = Resources.getResourceSafety(CONFIG_SOURCE);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    var configPar = line.trim().split(" ");
                    if (configPar.length <= 1) continue;

                    configs.put(configPar[0].trim(), configPar[1].trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int getInt(String cfg) {
        return Integer.parseInt(getAttribute(cfg));
    }

    public static boolean getBool(String cfg) {
        return Boolean.parseBoolean(getAttribute(cfg));
    }

    public static Float getFloat(String cfg) {
        return Float.parseFloat(getAttribute(cfg));
    }

    public static Double getDouble(String cfg) {
        return Double.parseDouble(getAttribute(cfg));
    }

    public static String getAttribute(String cfg) {
        if (configs.containsKey(cfg)) return configs.get(cfg);
        throw new IllegalArgumentException("Unable to get config argument " + cfg);
    }

}
