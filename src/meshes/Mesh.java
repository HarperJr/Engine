package meshes;


import org.joml.Vector2f;
import org.joml.Vector3f;
import renderers.IRenderer;
import renderers.MeshRenderer;

@SuppressWarnings("all")
public final class Mesh {

    private final String name;

    private Vector3f[] vertices;
    private Vector2f[] texCoords;
    private Vector3f[] normals;

    private int[] indinces;

    private final IRenderer renderer;

    public Mesh() {
        this("Unnamed");
    }

    public Mesh(String n) {
        name = n;
        renderer = new MeshRenderer(this);
    }

    public String getName() {
        return name;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public Vector3f[] getNormals() {
        return normals;
    }

    public void setNormals(Vector3f[] normals) {
        this.normals = normals;
    }

    public int[] getIndinces() {
        return indinces;
    }

    public void setIndinces(int[] indinces) {
        this.indinces = indinces;
    }

    public int getVerticesCount() { return vertices.length; }

    public int getIndicesCount() { return indinces.length; }

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
