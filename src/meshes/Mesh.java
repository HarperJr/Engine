package meshes;

import jdk.jfr.Category;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderers.IRenderer;
import renderers.MeshRenderer;
import util.DynamicList;

import java.util.List;

@Category("Data")
@SuppressWarnings("all")
public final class Mesh {

    private final String name;

    private List<Vector3f> vertices = new DynamicList<>();
    private List<Vector2f> texCoords = new DynamicList<>();
    private List<Vector3f> normals = new DynamicList<>();

    private List<Integer> indices = new DynamicList<>();

    private final IRenderer renderer;

    public Mesh(String n) {
        name = n;
        renderer = new MeshRenderer(this);
    }

    public String getName() {
        return name;
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public void setVertices(DynamicList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public List<Vector2f> getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(DynamicList<Vector2f> texCoords) {
        this.texCoords = texCoords;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(DynamicList<Vector3f> normals) {
        this.normals = normals;
    }

    public List<Integer> getIndices() {
        return indices;
    }

    public void setIndices(DynamicList<Integer> indices) {
        this.indices = indices;
    }

    public int getIndexCount() {

        return indices.size();
    }

    public int getVertexCount() {

        return vertices.size();
    }

    public IRenderer getRenderer() {

        return renderer;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Mesh) return name.equals(((Mesh) o).name);
        return false;
    }

    @Override
    public String toString() {

        return name;
    }
}
