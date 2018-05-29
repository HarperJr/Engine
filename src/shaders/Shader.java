package shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;

public abstract class Shader {

    public static final int POSITION_ATTRIBUTE = 0x0;
    public static final int TEXCOORD_ATTRIBUTE = 0x1;
    public static final int NORMAL_ATTRIBUTE = 0x2;

    private static final int INFO_LOG = 512;
    private final HashMap<String, Integer> uniforms = new HashMap<>();

    private int shader;

    protected abstract void bindAttributes();

    protected abstract void loadUniforms();

    public abstract String getName();

    public void initShader(String vertexShaderSource, String fragmentShaderSource) {
        final int vertexShader = createShader(GL20.GL_VERTEX_SHADER, vertexShaderSource);
        final int fragmentShader = createShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderSource);

        shader = GL20.glCreateProgram();

        GL20.glAttachShader(shader, vertexShader);
        GL20.glAttachShader(shader, fragmentShader);

        GL20.glBindAttribLocation(shader, POSITION_ATTRIBUTE, "position");
        GL20.glBindAttribLocation(shader, TEXCOORD_ATTRIBUTE, "texcoord");
        GL20.glBindAttribLocation(shader, NORMAL_ATTRIBUTE, "normal");

        bindAttributes();

        try {
            linkProgram(shader);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }finally {
            GL20.glDeleteShader(vertexShader);
            GL20.glDeleteShader(fragmentShader);
        }

        loadUniform("u_matrix.p_matrix");
        loadUniform("u_matrix.mv_matrix");

        loadUniforms();
    }

    public int getUniform(String uniform) {
        if (uniforms.containsKey(uniform)) return uniforms.get(uniform);
        throw  new IllegalArgumentException("Unable to get uniform" + uniform);
    }

    protected void loadUniform(String uniform) {
        if (uniforms.containsKey(uniform)) return;
        uniforms.put(uniform, GL20.glGetUniformLocation(shader, uniform));
    }

    private void linkProgram(int program) throws RuntimeException{
        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String info = GL20.glGetProgramInfoLog(program, INFO_LOG);
            GL20.glDeleteProgram(program);
            throw new RuntimeException("Unable to link program " + info);
        }
    }

    private int createShader(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String info = GL20.glGetShaderInfoLog(shader, INFO_LOG);
            GL20.glDeleteShader(shader);
            throw new RuntimeException("Unable to compile shader " + info);
        }

        return shader;
    }


    public int getShader() {
        return shader;
    }
}
