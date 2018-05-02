package shaders;

import main.Config;
import main.ResourceLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public abstract class Shader {

    private static final int INFO_LOG = 512;

    private final HashMap<String, Integer> uniforms = new HashMap<>();
    private final String name;

    private int shader;

    private int vertexShader;
    private int fragmentShader;

    public Shader(String n) {
        name = n;
    }

    protected abstract void bindAttributes();

    protected abstract void loadUniforms();

    public void initShader() {

        vertexShader = createShader(GL20.GL_VERTEX_SHADER, getShaderSourceFromFile(String.format("%s.vert", name)));
        fragmentShader = createShader(GL20.GL_FRAGMENT_SHADER, getShaderSourceFromFile(String.format("%s.frag", name)));

        shader = GL20.glCreateProgram();

        GL20.glAttachShader(shader, vertexShader);
        GL20.glAttachShader(shader, fragmentShader);
        bindAttributes();

        linkProgram(shader);

        loadUniform("u_matrix.p_matrix");
        loadUniform("u_matrix.mv_matrix");

        loadUniforms();

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    public int getUniform(String uniform) {
        if (uniforms.containsKey(uniform)) return uniforms.get(uniform);
        throw  new IllegalArgumentException("Unable to get uniform" + uniform);
    }

    protected void bindAttribute(int location, String attribute) {
        GL20.glBindAttribLocation(shader, location, attribute);
    }

    protected void loadUniform(String uniform) {
        if (uniforms.containsKey(uniform)) return;
        uniforms.put(uniform, GL20.glGetUniformLocation(shader, uniform));
    }

    private void linkProgram(int program) {
        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String info = GL20.glGetProgramInfoLog(program, INFO_LOG);
            GL20.glDeleteProgram(program);
            GL20.glDeleteShader(vertexShader);
            GL20.glDeleteShader(fragmentShader);
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

    private String getShaderSourceFromFile(String path) {

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream stream = ResourceLoader.getResourceSafety(Config.getAttribute("src-shaders") + path);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(line);
                }

            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public int getShader() {
        return shader;
    }
}
