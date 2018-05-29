package util;

import main.Config;
import main.Resources;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import util.readers.DDSReader;
import util.readers.TargaReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class TextureUtils {

    public static final int STANDARD;
    private final static HashMap<String, Integer> textures = new HashMap<>();

    public static int getTexture(String path) {
        if (textures.containsKey(path)) {
            return textures.get(path);
        }

        try {
            if (path.contains("\\u005C")) {
                path.replace("\\u005C", "\\u002F");
            }

            BufferedImage image = getTextureBuffered(path.trim());
            int i = GL11.glGenTextures();
            loadTexture(image, i);
            textures.put(path, i);

            return i;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return STANDARD;
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

    private static void generateMiMaps(BufferedImage image) {

    }

    private static BufferedImage getTextureBuffered(String path) throws IOException {
        try (InputStream stream = Resources.getResource(Config.getAttribute("src-tex") + path)) {
            if (stream == null) throw new IOException("Unable to find texture " + path);

            if (path.endsWith(".tga")) {
                return TargaReader.read(stream);
            }
            if (path.endsWith(".dds")) {
                return DDSReader.read(stream);
            }

            return ImageIO.read(stream);
        }
    }

    static {
        final BufferedImage standard = new BufferedImage(8, 8, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics g = standard.getGraphics();
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, standard.getWidth(), standard.getHeight());
        g.dispose();

        STANDARD = GL11.glGenTextures();
        loadTexture(standard, STANDARD);
    }
}
