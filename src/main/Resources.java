package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("all")
public final class Resources {

    private static final String SOURCE_FOLDER = "src/resources/";

    public static String getSourceFolder() { return SOURCE_FOLDER; }

    public static InputStream getResource(String path) throws IOException {
        return getResource(Paths.get(SOURCE_FOLDER + path));
    }

    public static InputStream getResourceSafety(String path) throws IOException {
        return getResourceSafety(Paths.get(SOURCE_FOLDER + path));
    }

    public static void createResource(String path, String source, boolean rewrite) {
        createResource(Paths.get(SOURCE_FOLDER + path), source, rewrite);
    }

    @Nullable
    private static InputStream getResource(Path path) throws IOException {
        final File file = new File(path.toUri());
        InputStream stream = new FileInputStream(file);
        return stream;
    }

    private static File create(Path path) {
        File file = new File(path.toUri());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return file;
    }

    @NotNull
    private static InputStream getResourceSafety(Path path) throws IOException {
        InputStream stream = getResource(path);
        if (stream == null) {
            final File file = create(path);
            stream = new FileInputStream(file);
        }
        return stream;
    }

    @NotNull
    private static void createResource(Path path, String source, boolean rewrite) {
        File file = new File(path.toUri());
        if (!file.exists()) {
            file = create(path);
        }

        try (OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(file, rewrite))) {
            streamWriter.write(source);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
