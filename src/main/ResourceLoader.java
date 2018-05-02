package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceLoader {

    private static final String SOURCE_FOLDER = "/resources/";

    public static InputStream getResource(String path) {
        return getResource(Paths.get(SOURCE_FOLDER + path));
    }

    public static InputStream getResourceSafety(String path) throws IOException{
        return getResourceSafety(Paths.get(SOURCE_FOLDER + path));
    }

    @Nullable
    private static InputStream getResource(Path path) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(path.toString());
    }

    @NotNull
    private static InputStream getResourceSafety(Path path) throws IOException {
        InputStream stream = getResource(path);
        if (stream == null) {
            File file = new File("src" + path);
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            file.createNewFile();
            stream = new FileInputStream(file);
        }
        return stream;
    }
}
