package util.readers;

import gr.zdimensions.jsquish.Squish;
import util.readers.dds.DDSImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@SuppressWarnings("all")
/**
 * This code isn't written by myself
 * But you could see a lot of my own improvements
 */
public final class DDSReader {

    public static BufferedImage read(InputStream stream) throws IOException {
        return loadBufferedImage(DDSImage.read(stream));
    }

    private static BufferedImage loadBufferedImage(final DDSImage image) throws IOException{
        if (image.isCompressed()) {
            return decompressTexture(image);
        }
        return loadBufferedImageFromByteBuffer(image);
    }

    private static BufferedImage loadBufferedImageFromByteBuffer(DDSImage image) throws IOException{
        return new ByteBufferedImage(image.getWidth(), image.getHeight(), image.getMipMapLevel(0));
    }

    private static BufferedImage decompressTexture(DDSImage image) {
        return new DXTBufferDecompressor(image.getMipMapLevel(0), image.getWidth(), image.getHeight(), findCompressionFormat(image)).getImage();
    }

    private static Squish.CompressionType findCompressionFormat(DDSImage image) {
        int dxtFormat = image.getDXTFormat();
        Squish.CompressionType compressionType = null;
        try {
            compressionType = getSquishCompressionFormat(dxtFormat);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return compressionType;
    }

    private static Squish.CompressionType getSquishCompressionFormat(int pixelFormat) throws Exception {
        switch (pixelFormat) {
            case DDSImage.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT:
                return Squish.CompressionType.DXT1;
            case DDSImage.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT:
                return Squish.CompressionType.DXT3;
            case DDSImage.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT:
                return Squish.CompressionType.DXT5;
            default:
                throw new Exception("Unable to identify file compression format "  + pixelFormat);
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

    protected ByteBuffer decompressedBuffer;
    protected Dimension dimension;

    protected abstract ByteBuffer squishDecompress(byte[] compressedData, int width, int height, Squish.CompressionType type) throws OutOfMemoryError;

    public BufferedImage getImage() {
        BufferedImage image = new ByteBufferedImage(dimension.width, dimension.height, decompressedBuffer);
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
        decompressedBuffer = squishDecompressBuffer(compressedBuffer, dimension.width, dimension.height, type);
        this.dimension = dimension;
        this.compressionType = type;
    }

    protected ByteBuffer squishDecompress(byte[] compressedData, int width, int height, Squish.CompressionType type) throws OutOfMemoryError {
        if (type != null) {
            final byte[] decompressedData = Squish.decompressImage(null, width, height, compressedData, type);
            return ByteBuffer.wrap(decompressedData);
        }
        return ByteBuffer.wrap(compressedData);
    }

    private ByteBuffer squishDecompressBuffer(ByteBuffer byteBuffer, int width, int height, Squish.CompressionType type) throws OutOfMemoryError {
        byte[] data = new byte[byteBuffer.capacity()];
        byteBuffer.get(data);

        return squishDecompress(data, width, height, type);
    }
}
