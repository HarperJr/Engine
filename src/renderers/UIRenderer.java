package renderers;

import meshes.Material;
import meshes.Mesh;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.Shader;
import shaders.ShaderUI;
import util.MatrixUtils;
import util.ShaderUtils;

public class UIRenderer implements IRenderer {

    private final Mesh uiMesh;
    private final Material material;
    private int vao;

    public UIRenderer(Mesh mesh) {
        this.uiMesh = mesh;
        this.material = Material.getStandard();
    }

    @Override
    public void render() {
      /*  ShaderUtils.useShader(uiShader);

        GL30.glBindVertexArray(vao);

        GL20.glUniformMatrix4fv(uiShader.getUniform("u_matrix.p_matrix"), false, MatrixUtils.getProjectionMatrixAsBuffer());
        GL20.glUniformMatrix4fv(uiShader.getUniform("u_matrix.mv_matrix"), false, MatrixUtils.getModelViewMatrixAsBuffer());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, );
        GL20.glUniform1i(uiShader.getUniform("u_textures.uiTexture"), 0);

        GL20.glEnableVertexAttribArray(ShaderUI.POSITION_ATTRIBUTE);
        GL20.glEnableVertexAttribArray(ShaderUI.TEXCOORD_ATTRIBUTE);

        GL11.glDrawElements(GL11.GL_TRIANGLES, uiMesh.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(ShaderUI.POSITION_ATTRIBUTE);
        GL20.glDisableVertexAttribArray(ShaderUI.TEXCOORD_ATTRIBUTE);

        GL30.glBindVertexArray(0); */
    }

    @Override
    public void dispose() {

    }

    @Override
    public void flush() {

    }

}
