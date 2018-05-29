package ui;

import meshes.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderers.IRenderer;
import renderers.MeshRenderer;
import renderers.UIRenderer;
import shaders.Shader;
import util.ShaderUtils;
import util.TextureUtils;

import java.util.ArrayList;
import java.util.List;

public class UI {

    private final List<Mesh> uiComponents = new ArrayList<>();
    private final Shader shaderUI = ShaderUtils.getShader("ShaderUI");

    public UI() {
        //uiRenderer = new UIRenderer(this);
    }

    public void initUI() {
        addRect(new Vector2f(0f, 100f), new Vector2f(100f, 0f), TextureUtils.getTexture("DepthTexture"));
    }

    public void addRect(Vector2f upperLeft, Vector2f bottomRight, int texture) {
        final Mesh rect = new Mesh();

        Vector3f upperLeftVec = new Vector3f(upperLeft.x, upperLeft.y, 0f);
        Vector3f upperRightVec = new Vector3f(bottomRight.x, upperLeft.y, 0f);
        Vector3f bottomRightVec = new Vector3f(bottomRight.x, bottomRight.y, 0f);
        Vector3f bottomLeftVec = new Vector3f(upperLeft.x, bottomRight.y, 0f);

        rect.setVertices(new Vector3f[]{upperLeftVec, upperRightVec, bottomRightVec, bottomLeftVec});
        rect.setIndinces(new int[]{0, 1, 2, 0, 2, 3});

        MeshRenderer renderer = (MeshRenderer)rect.getRenderer();
        renderer.getMaterial().setMapAmbient(texture);
        uiComponents.add(rect);
    }

    public List<Mesh> getUiComponents() {
        return uiComponents;
    }

    public Shader getShaderUI() {
        return shaderUI;
    }


}
