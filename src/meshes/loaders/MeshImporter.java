package meshes.loaders;

import main.Config;
import main.ResourceLoader;
import meshes.Mesh;
import meshes.MeshModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderers.Material;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public abstract class MeshImporter {

    protected final LinkedList<Mesh> loadedMeshes = new LinkedList<>();
    protected final LinkedList<Material> loadedMaterials = new LinkedList<>();

    protected abstract void processMeshSubData(Mesh mesh, String sub) throws IllegalArgumentException;

    protected abstract void processMaterialSubData(Material material, String sub) throws IllegalArgumentException;

    public final MeshModel loadMeshModel(String path) {
        InputStream stream = ResourceLoader.getResource(Config.getAttribute("src-obj") + path);
        if (stream == null) throw new IllegalStateException("Unable to get object file " + path);
        final String name = Paths.get(path).getFileName().toString();

        loadedMeshes.add(new Mesh(name));

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processMeshSubData(loadedMeshes.getLast(), line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(String.format("Unable to load mesh %s.", name));
        }
        List<Mesh> meshes = loadedMeshes.stream().filter(m -> m.getVertexCount() > 0).collect(Collectors.toList());
        MeshModel mesh = new MeshModel(name, meshes);
        clear();

        return mesh;
    }

    protected final void loadMaterial(String path) {
        InputStream stream = ResourceLoader.getResource(Config.getAttribute("src-obj") + path);
        if (stream == null) throw new IllegalStateException("Unable to get material file " + path);

        final String name = Paths.get(path).getFileName().toString();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Material processingMaterial = null;
                if (!loadedMaterials.isEmpty()) {
                    processingMaterial = loadedMaterials.getLast();
                }
                processMaterialSubData(processingMaterial, line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(String.format("Unable to process material %s. %s", name, ex));
        }
    }

    protected Material findMaterial(String matName) {
        var finded = loadedMaterials.stream().filter(x -> x.getName().equals(matName)).findFirst();
        if (finded.isPresent()) return finded.get();
        throw new IllegalStateException("Unable to find material " + matName);
    }

    protected void process(String line, String prefix, Consumer<String> action) throws IllegalArgumentException {
        String s = line.trim();
        if (s.startsWith(prefix)) {
            action.accept(s.substring(prefix.length()));
        }
    }

    protected Vector3f toVector3f(String arg) {
        double[] args = Arrays.stream(arg.split(" ")).mapToDouble(Double::parseDouble).toArray();
        if (args.length == 3) {
            return new Vector3f((float) args[0], (float) args[1], (float) args[2]);
        }
        return new Vector3f(0f, 0f, 0f);
    }

    protected Vector2f toVector2f(String arg) {
        double[] args = Arrays.stream(arg.split(" ")).mapToDouble(Double::parseDouble).toArray();
        if (args.length == 2) {
            return new Vector2f((float) args[0], (float) args[1]);
        }
        return new Vector2f(0f, 0f);
    }

    protected void clear() {
        loadedMeshes.clear();
        loadedMaterials.clear();
    }

}

