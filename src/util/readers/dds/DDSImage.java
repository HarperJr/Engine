package util.readers.dds;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static util.readers.dds.DDSImage.getNext;

@SuppressWarnings("all")
public class DDSImage {

    public static final int GL_COMPRESSED_RGBA_S3TC_DXT1_EXT = 0x31545844;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT3_EXT = 0x33545844;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT5_EXT = 0x35545844;

    public static final int D3DFMT_A8B8G8R8 = 0x38281808;
    public static final int D3DFMT_A4B4G4R4 = 0x34241404;

    private static final int MAGIC_NUMBER = 0x20534444;

    private DDSHeader header;
    private byte[] bdata;
    private byte[] bdata2;

    private DDSMipMapLevel[] mipMapLevels;

    private DDSImage(DDSHeader header, byte[] bdata, byte[] bdata2) {
        this.header = header;
        this.bdata = bdata;
        this.bdata2 = bdata2;
    }

    private void setMipMapLevels(DDSMipMapLevel[] mipMapLevels) {
        this.mipMapLevels = mipMapLevels;
    }

    public static DDSImage read(InputStream stream) throws IOException {
        final byte[] magicBytes = new byte[4];

        stream.read(magicBytes); //>>4

        final int magic = getNext(ByteBuffer.wrap(magicBytes));
        if (magic != MAGIC_NUMBER) {
            throw new IOException("Unable to read file: unsupported format");
        }

        final byte[] headerBytes = new byte[124]; //>>124
        stream.read(headerBytes);
        final DDSHeader header = new DDSHeader(ByteBuffer.wrap(headerBytes));

        final int width = header.getDwWidth();
        final int height = header.getDwHeight();

        int blockSize = header.getFourCC() == GL_COMPRESSED_RGBA_S3TC_DXT1_EXT ? 8 : 16;
        int pitch = getPitch(width, height, blockSize);

        final byte[] bdata = new byte[pitch]; //main image buffer (mipMap level 0)
        stream.read(bdata);
        final byte[] bdata2 = stream.readAllBytes();

        final DDSImage image = new DDSImage(header, bdata, bdata2);

        DDSMipMapLevel[] mipMapLevels = new DDSMipMapLevel[header.getDwMipMapCount()];
        int offset = 0;
        int mipMapLevelWidth = width / 2;
        int mipMapLevelHeight = height / 2;

        for (int mipMapLevel = 0; mipMapLevel < header.getDwMipMapCount(); mipMapLevel++) {
            offset += getPitch(mipMapLevelWidth, mipMapLevelHeight, blockSize);
            mipMapLevels[mipMapLevel] = new DDSMipMapLevel(mipMapLevelWidth, mipMapLevelHeight, offset);

            mipMapLevelWidth /= 2;
            mipMapLevelHeight /= 2;
        }
        image.setMipMapLevels(mipMapLevels);

        return image;
    }

    private static int getPitch(int width, int height, int blockSize) {
        return Math.max(1, ((width + 3) / 4) * ((height + 3) / 4)) * blockSize;
    }

    public int getMipMapCount() {
        return mipMapLevels.length;
    }

    public ByteBuffer getMipMapLevel(int level) {
        if (level > 0) {
            level = Math.min(level, mipMapLevels.length);
            ByteBuffer.wrap(bdata2, mipMapLevels[level].getOffset(), mipMapLevels[level].getSize());
        }

        return ByteBuffer.wrap(bdata);
    }

    public int getPixelFormat() {
        int pxf = 0x40404040;
        final DDSPixelFormat pixelFormat = header.getPixelFormat();
        int[] rgbaBitMask = pixelFormat.getDwRGBABitMask();

        pxf = pixelFormat.getIntRGBBitCount() == 0x20
                && rgbaBitMask[0] == 0xff
                && rgbaBitMask[1] == 0xff00
                && rgbaBitMask[2] == 0xff0000
                && rgbaBitMask[3] == 0xff000000 ? D3DFMT_A8B8G8R8 : pxf;

        pxf = pixelFormat.getIntRGBBitCount() == 0x10
                && rgbaBitMask[0] == 0xf
                && rgbaBitMask[1] == 0xf0
                && rgbaBitMask[2] == 0xf00
                && rgbaBitMask[3] == 0xf000 ? D3DFMT_A4B4G4R4 : pxf;

        return pxf;
    }

    public int getDXTFormat() {
        return header.getFourCC();
    }

    public int getWidth() {
        return header.getDwWidth();
    }

    public int getHeight() {
        return header.getDwHeight();
    }

    public boolean isCompressed() {
        return getDXTFormat() != 0;
    }

    static int getNext(ByteBuffer buffer) {
        return (buffer.get() & 0xff) | (buffer.get() & 0xff) << 8 | (buffer.get() & 0xff) << 16 | (buffer.get() & 0xff) << 24;
    }
}

