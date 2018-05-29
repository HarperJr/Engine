package util.readers.dds;

import java.nio.ByteBuffer;
import static util.readers.dds.DDSImage.getNext;

@SuppressWarnings("all")
class DDSHeader {

    private static final int DDSD_CAPS = 0x1;
    private static final int DDSD_HEIGHT = 0x2;
    private static final int DDSD_WIDTH = 0x4;
    private static final int DDSD_PITCH = 0x8;
    private static final int DDSD_PIXELFORMAT = 0x1000;
    private static final int DDSD_MIPMAPCOUNT = 0x20000;
    private static final int DDSD_LINEARSIZE = 0x80000;
    private static final int DDSD_DEPTH = 0x800000;

    private int dwFlags;
    private int dwWidth;
    private int dwHeight;

    private int dwDepth;
    private int dwMipMapCount;

    private DDSPixelFormat pixelFormat;

    DDSHeader(ByteBuffer headerBuffer) {

        final int dwSize = getNext(headerBuffer);
        dwFlags = getNext(headerBuffer);

        if ((dwFlags & DDSD_CAPS) == 0 && (dwFlags & DDSD_HEIGHT) == 0 && (dwFlags & DDSD_WIDTH) == 0 && (dwFlags & DDSD_PIXELFORMAT) == 0) {
            throw new IllegalStateException("Unable to read dds: not all flags are settled");
        }

        dwHeight = getNext(headerBuffer);
        dwWidth = getNext(headerBuffer);

        final int dwPithOnLinearSize = getNext(headerBuffer);
        dwDepth = getNext(headerBuffer);
        dwMipMapCount = getNext(headerBuffer);

        final int[] reserved = new int[11];
        for (int i = 0; i < reserved.length; i++) {
            reserved[i] = getNext(headerBuffer);
        }

        final byte[] pixelFormatBytes = new byte[32];
        headerBuffer.get(pixelFormatBytes);
        pixelFormat = new DDSPixelFormat(ByteBuffer.wrap(pixelFormatBytes));
    }

    public int getDwWidth() {
        return dwWidth;
    }

    public int getDwHeight() {
        return dwHeight;
    }

    public int getDwMipMapCount() {
        return dwMipMapCount;
    }

    public int getDwFlags() {
        return dwFlags;
    }

    public int getDwDepth() {
        return dwDepth;
    }

    public int getFourCC() {
        return pixelFormat.getDwFourCC();
    }

    public DDSPixelFormat getPixelFormat() {
        return pixelFormat;
    }
}
