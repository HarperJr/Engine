package renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.Shader;
import shadows.ShadowMap;
import util.ShaderUtils;
import util.TextureUtils;

import java.nio.ByteBuffer;

public class ShadowMapRenderer implements IRenderer {

    private final Shader shadowMapShader = ShaderUtils.getShader("ShaderShadows");

    private final ShadowMap shadowMap;
    private int fbo;

    private int depthTexture;

    public ShadowMapRenderer(ShadowMap shadowMap) {
        this.shadowMap = shadowMap;
    }

    @Override
    public void render() {
        ShaderUtils.useShader(shadowMapShader);

        
    }

    @Override
    public void dispose() {
        fbo = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GL11.glDrawBuffer(GL11.GL_NONE);

        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        final int dimension = shadowMap.getDimension();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, dimension, dimension, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void updateFrameBuffer() {

    }

    @Override
    public void flush() {
        GL30.glDeleteFramebuffers(fbo);
    }
}
