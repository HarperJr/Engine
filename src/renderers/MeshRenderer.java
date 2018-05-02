package renderers;

import meshes.Mesh;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderSurface;
import util.MatrixUtils;
import util.ShaderUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

public class MeshRenderer implements IRenderer {

    private static final int BUFFER_BLOCK_SIZE = 8;

    private static final int BUFFER_POS_OFFSET = 0;
    private static final int BUFFER_TEXCOORD_OFFSET = 3;
    private static final int BUFFER_NORMAL_OFFSET = 5;

    private Deque<Integer> vbos = new ArrayDeque<>();
    private FloatBuffer materialBuffer;

    private final Mesh mesh;
    private Material material;
    private int vao;

    public MeshRenderer(Mesh m) {
        mesh = m;
        material = Material.getStandard();
    }

    @Override
    public void render() {
        ShaderSurface shader = material.getShader();

        ShaderUtils.useShader(shader);
        GL30.glBindVertexArray(vao);

        GL20.glUniformMatrix4fv(shader.getUniform("u_matrix.p_matrix"), false, MatrixUtils.getProjectionMatrixAsBuffer());
        GL20.glUniformMatrix4fv(shader.getUniform("u_matrix.mv_matrix"), false, MatrixUtils.getModelViewMatrixAsBuffer());

        GL20.glUniformMatrix4fv(shader.getUniform("u_material"), false, materialBuffer);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getMapAmbient());
        GL20.glUniform1i(shader.getUniform("u_textures.ambient"), 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getMapDiffuse());
        GL20.glUniform1i(shader.getUniform("u_textures.diffuse"), 1);

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getMapSpecular());
        GL20.glUniform1i(shader.getUniform("u_textures.specular"), 2);

        GL20.glEnableVertexAttribArray(ShaderSurface.POSITION);
        GL20.glEnableVertexAttribArray(ShaderSurface.TEXCOORD);
        GL20.glEnableVertexAttribArray(ShaderSurface.NORMAL);

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(ShaderSurface.POSITION);
        GL20.glDisableVertexAttribArray(ShaderSurface.TEXCOORD);
        GL20.glDisableVertexAttribArray(ShaderSurface.NORMAL);

        GL30.glBindVertexArray(0);
    }

    @Override
    public void dispose() {
        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int vertexBufferObject = createBufferObject();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
        FloatBuffer vertexBuffer = getVertexBuffer();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        vertexBuffer.clear();

        GL20.glVertexAttribPointer(ShaderSurface.POSITION, 3, GL11.GL_FLOAT, false, 32, 0L);
        GL20.glVertexAttribPointer(ShaderSurface.TEXCOORD, 2, GL11.GL_FLOAT, false, 32, 12L);
        GL20.glVertexAttribPointer(ShaderSurface.NORMAL, 3, GL11.GL_FLOAT, false, 32, 20L);

        int elementBufferObject = createBufferObject();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);
        IntBuffer indexBuffer = getIndexBuffer();
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        indexBuffer.clear();

        materialBuffer = material.toBuffer();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void flush() {
        GL30.glDeleteVertexArrays(vao);
        materialBuffer.clear();
        vbos.forEach(GL15::glDeleteBuffers);
    }

    private int createBufferObject() {
        final int bufferObject = GL15.glGenBuffers();
        vbos.add(bufferObject);
        return bufferObject;
    }

    private IntBuffer getIndexBuffer() {
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(mesh.getIndexCount());
        int[] indices = mesh.getIndices().stream().mapToInt(Integer::valueOf).toArray();
        indexBuffer.put(indices);
        return indexBuffer.flip();
    }

    @SuppressWarnings("all")
    private FloatBuffer getVertexBuffer() {
        final float[] vertexArray = new float[mesh.getVertexCount() * BUFFER_BLOCK_SIZE];

        List<Vector3f> vertices = mesh.getVertices();
        List<Vector2f> texCoords = mesh.getTexCoords();
        List<Vector3f> normals = mesh.getNormals();

        Stream.iterate(0, i -> i < mesh.getVertexCount(), i -> i + 1).forEach(i -> {
            Vector3f vertexPos = vertices.get(i);

            vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_POS_OFFSET + 0] = vertexPos.x;
            vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_POS_OFFSET + 1] = vertexPos.y;
            vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_POS_OFFSET + 2] = vertexPos.z;

            if (texCoords.size() > 0) {
                Vector2f texCoord = texCoords.get(i);

                vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_TEXCOORD_OFFSET + 0] = texCoord.x;
                vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_TEXCOORD_OFFSET + 1] = texCoord.y;
            }

            if (normals.size() > 0) {
                Vector3f normal = normals.get(i);

                vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_NORMAL_OFFSET + 0] = normal.x;
                vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_NORMAL_OFFSET + 1] = normal.y;
                vertexArray[i * BUFFER_BLOCK_SIZE + BUFFER_NORMAL_OFFSET + 2] = normal.z;
            }
        });

        final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        return vertexBuffer.flip();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
