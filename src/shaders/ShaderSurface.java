package shaders;

public class ShaderSurface extends Shader {

    public static final int POSITION = 0;
    public static final int TEXCOORD = 1;
    public static final int NORMAL = 2;

    @Override
    protected void bindAttributes() {
    }

    @Override
    protected void loadUniforms() {
        loadUniform("u_textures.ambient");
        loadUniform("u_textures.diffuse");
        loadUniform("u_textures.specular");
        loadUniform("u_material");
    }

    @Override
    public String getName() {
        return "ShaderSurface";
    }

}
