package util;

import main.Config;
import main.ResourceLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class TextureUtils {

    private static final String STANDARD = "Standard";
    private final static HashMap<String, Integer> textures = new HashMap<>();

    public static int getStandard() {
        return getTexture(STANDARD);
    }

    public static int getTexture(String path) {
        if (textures.containsKey(path)) return textures.get(path);
        try {
            loadTexture(path);
            return textures.get(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return getStandard();
    }

    public static void loadTexture(String path) throws IOException {
        if (textures.containsKey(path)) return;

        BufferedImage image = getTextureBuffered(path);
        int i = GL11.glGenTextures();
        loadTexture(image, i);

        textures.put(path, i);
    }

    private static void loadTexture(BufferedImage image, int textureId) {

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int[] pixels = new int[imageWidth * imageHeight];
        image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
        byte[] pixelArray = new byte[pixels.length * 4];

        for (int i = 0; i < pixels.length; i++) {
            pixelArray[i * 4 + 0] = (byte) (pixels[i] & 0xff); //r channel
            pixelArray[i * 4 + 1] = (byte) (pixels[i] >> 8 & 0xff); //g channel
            pixelArray[i * 4 + 2] = (byte) (pixels[i] >> 16 & 0xff); //b channel
            pixelArray[i * 4 + 3] = (byte) (pixels[i] >> 24 & 0xff); //a channel
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        ByteBuffer textureBuffer = BufferUtils.createByteBuffer(pixelArray.length);
        textureBuffer.put(pixelArray).flip();

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, imageWidth, imageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
        textureBuffer.clear();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    private static BufferedImage getTextureBuffered(String path) throws IOException {

        if (path.equals(STANDARD)) {
            BufferedImage standard = new BufferedImage(8, 8, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = standard.getGraphics();
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, standard.getWidth(), standard.getHeight());
            g.dispose();
            return standard;
        }

        InputStream stream = ResourceLoader.getResource(Config.getAttribute("src-tex") + path);
        if (stream == null) throw new IOException("Unable to find texture " + path);

        if (path.endsWith(".tga")) {
            return TargaReader.read(stream);
        }else if (path.endsWith(".dds")) {
            return DDSReader.read(stream);
        }

        return ImageIO.read(stream);
    }
}
