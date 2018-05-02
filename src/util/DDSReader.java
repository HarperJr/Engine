package util;

import com.jogamp.opengl.util.texture.spi.DDSImage;
import gr.zdimensions.jsquish.Squish;
import org.lwjgl.BufferUtils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@SuppressWarnings("all")
public final class DDSReader {

    public static BufferedImage read(InputStream stream) throws IOException {
        final byte[] bytes = stream.readAllBytes();
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes).flip();
        return loadBufferedImage(DDSImage.read(buffer));
    }

    private static BufferedImage loadBufferedImage(final DDSImage image) {
        if (image.isCompressed())
            return decompressTexture(image.getMipMap(0).getData(), image.getWidth(), image.getHeight(), findCompressionFormat(image));
        else
            return loadBufferedImageFromByteBuffer(image.getMipMap(0).getData(), image.getWidth(), image.getHeight(), image);
    }

    private static BufferedImage loadBufferedImageFromByteBuffer(ByteBuffer data, int width, int height, DDSImage image) {
        if (image.getPixelFormat() == DDSImage.D3DFMT_A8R8G8B8) {
            return new ByteBufferedImage(width, height, data);
        }
        return null;
    }

    private static BufferedImage decompressTexture(ByteBuffer textureBuffer, int width, int height, Squish.CompressionType compressionType) {
        return new DXTBufferDecompressor(textureBuffer, width, height, compressionType).getImage();
    }

    private static Squish.CompressionType findCompressionFormat(DDSImage image) {
        int pixelFormat = image.getPixelFormat();
        Squish.CompressionType compressionType = null;
        try {
            compressionType = getSquishCompressionFormat(pixelFormat);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return compressionType;
    }

    private static Squish.CompressionType getSquishCompressionFormat(int pixelFormat) throws Exception {
        switch (pixelFormat) {
            case DDSImage.D3DFMT_DXT1:
                return Squish.CompressionType.DXT1;
            case DDSImage.D3DFMT_DXT3:
                return Squish.CompressionType.DXT3;
            case DDSImage.D3DFMT_DXT5:
                return Squish.CompressionType.DXT5;
            default:
                throw new Exception("Unable to identify file format");
        }
    }
}

@SuppressWarnings("all")
class ByteBufferedImage extends BufferedImage {

    public ByteBufferedImage(int w, int h, ByteBuffer buffer) {

        super(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        WritableRaster raster = super.getRaster();
        byte[] argb = new byte[buffer.capacity()];
        buffer.get(argb);
        raster.setDataElements(0, 0, w, h, argb);
    }
}

@SuppressWarnings("all")
abstract class BufferDecompressor {

    protected ByteBuffer uncompressedBuffer;
    protected Dimension dimension;

    public BufferedImage getImage() {

        BufferedImage image = new ByteBufferedImage(dimension.width, dimension.height, uncompressedBuffer);
        return image;
    }
}

@SuppressWarnings("all")
class DXTBufferDecompressor extends BufferDecompressor {

    protected Squish.CompressionType compressionType;

    public DXTBufferDecompressor(ByteBuffer compressedBuffer, int width, int height, Squish.CompressionType type) {
        this(compressedBuffer, new Dimension(width, height), type);
    }

    private DXTBufferDecompressor(ByteBuffer compressedBuffer, Dimension dimension, Squish.CompressionType type) {
        uncompressedBuffer = squishDecompressBuffer(compressedBuffer, dimension.width, dimension.height, type);
        this.dimension = dimension;
        this.compressionType = type;
    }

    private static byte[] squishDecompressToArray(byte[] compressedData, int width, int height, Squish.CompressionType type) throws OutOfMemoryError {
        if (type != null) {
            byte[] decompressedData = Squish.decompressImage(null, width, height, compressedData, type);
            return decompressedData;
        }
        return compressedData;
    }

    private static ByteBuffer squishDecompress(byte[] compressedData, int width, int height, Squish.CompressionType type) throws OutOfMemoryError {
        return ByteBuffer.wrap(squishDecompressToArray(compressedData, width, height, type));
    }

    private static ByteBuffer squishDecompressBuffer(ByteBuffer byteBuffer, int width, int height, Squish.CompressionType type) throws OutOfMemoryError {
        byte[] data = new byte[byteBuffer.capacity()];
        byteBuffer.get(data);

        return squishDecompress(data, width, height, type);
    }
}
