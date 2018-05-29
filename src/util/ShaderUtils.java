package util;

import main.Config;
import main.Resources;
import org.lwjgl.opengl.GL20;
import shaders.Shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public final class ShaderUtils {

    private static final String VERTEX_SHADER_TR;
    private static final String FRAGMENT_SHADER_TR;

    private static final HashMap<String, Shader> shaderMap = new HashMap<>();

    public static void initShader(Shader shader) {
        if (shaderMap.containsKey(shader.getName())) return;

        final String vertexShaderSource = getShaderSourceFromPath(String.format("%s.vert", shader.getName()));
        final String fragmentShaderSource = getShaderSourceFromPath(String.format("%s.frag", shader.getName()));

        shader.initShader(vertexShaderSource, fragmentShaderSource);

        shaderMap.put(shader.getName(), shader);
    }

    public static Shader getShader(String shader) {
        if (shaderMap.containsKey(shader)) return shaderMap.get(shader);
        throw new IllegalArgumentException("Unable to find shader " + shader);
    }

    public static void useShader(Shader shader) {
        GL20.glUseProgram(shader.getShader());
    }

    public static void deleteShaders() {
        shaderMap.forEach((shaderName, shader) -> GL20.glDeleteProgram(shader.getShader()));
    }

    private static String getShaderSourceFromPath(String path) {
        final String absolutePath = Config.getAttribute("src-shaders") + path;
        final StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Resources.getResource(absolutePath)))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException ex) {
            if (path.endsWith(".vert")) {
                Resources.createResource(absolutePath, VERTEX_SHADER_TR, true);
                stringBuilder.append(VERTEX_SHADER_TR);
            } else if (path.endsWith(".frag")) {
                Resources.createResource(absolutePath, FRAGMENT_SHADER_TR, true);
                stringBuilder.append(FRAGMENT_SHADER_TR);
            }
        }

        return stringBuilder.toString();
    }

    static {
        VERTEX_SHADER_TR = "#version 330\n\n" +
        "struct Matrix {\n" +
        "    mat4 mv_matrix;\n" +
        "    mat4 p_matrix;\n" +
        "}\n\n" +
        "layout(location = 0) in vec3 position;\n" +
        "layout(location = 1) in vec2 texcoord;\n" +
        "layout(location = 2) in vec3 normal;\n\n" +
        "uniform Matrix u_matrix;\n\n" +
        "void main(void) {\n" +
        "    gl_Position = u_matrix.p_matrix * u_matrix.mv_matrix * vec4(position, 1.0);\n" +
        "}";

        FRAGMENT_SHADER_TR = "#version 330\n\n" +
        "out vec4 frag_color;\n\n" +
        "void main(void) {\n" +
        "    frag_color = vec4(1.0);\n" +
        "}";
    }
}
