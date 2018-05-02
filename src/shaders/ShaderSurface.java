package shaders;

public class ShaderSurface extends Shader {

    public static final int POSITION = 0;
    public static final int TEXCOORD = 1;
    public static final int NORMAL = 2;

    public ShaderSurface(String n) {
        super(n);
    }

    @Override
    protected void bindAttributes() {

        bindAttribute(POSITION, "position");
        bindAttribute(TEXCOORD, "texcoord");
        bindAttribute(NORMAL, "normal");
    }

    @Override
    protected void loadUniforms() {

        loadUniform("u_textures.ambient");
        loadUniform("u_textures.diffuse");
        loadUniform("u_textures.specular");
        loadUniform("u_material");
    }

}
