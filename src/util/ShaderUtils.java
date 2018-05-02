package util;

import org.lwjgl.opengl.GL20;
import shaders.Shader;

import java.util.HashMap;

public final class ShaderUtils {

    private static final HashMap<String, Shader> shaderMap = new HashMap<>();

    public static void initShader(Shader shader) {
        if (shaderMap.containsKey(shader.getName())) return;

        shader.initShader();
        shaderMap.put(shader.getName(), shader);
    }

    public static void useShader(Shader shader) {
        GL20.glUseProgram(shader.getShader());
    }

    public static void deleteShaders() {
        shaderMap.forEach((shaderName, shader) -> GL20.glDeleteProgram(shader.getShader()));
    }

    public static Shader getShader(String shader) {
        if (shaderMap.containsKey(shader)) return shaderMap.get(shader);
        throw new IllegalArgumentException("Unable to find shader " + shader);
    }
}
