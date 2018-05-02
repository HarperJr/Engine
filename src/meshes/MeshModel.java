package meshes;

import jdk.jfr.Category;
import org.lwjgl.util.vector.Vector3f;
import renderers.IRenderer;
import renderers.Material;
import renderers.MeshRenderer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Category("data")
@SuppressWarnings("all")
public class MeshModel {
    private final String name;

    private final List<Mesh> meshes;
    private final List<Material> materials;
    private final int vertexCount;

    private final Transform transform;

    public MeshModel(String n, Mesh[] m) {
        name = n;

        meshes = Arrays.asList(m);
        materials = getRenderers().stream().map(x -> ((MeshRenderer)x).getMaterial()).collect(Collectors.toList());
        vertexCount = meshes.stream().mapToInt(Mesh::getVertexCount).sum();
        transform = new Transform();
    }

    public MeshModel(String n, Collection<Mesh> m) {
        this(n, m.stream().toArray(Mesh[]::new));
    }

    public List<IRenderer> getRenderers() {
        return meshes.stream().map(Mesh::getRenderer).collect(Collectors.toList());
    }

    public void dispose() {
        getRenderers().forEach(IRenderer::dispose);
    }

    public String getName() {
        return name;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Transform getTransform() {
        return transform;
    }
}
