package util.readers.dds;

import java.nio.ByteBuffer;

import static util.readers.dds.DDSImage.getNext;

@SuppressWarnings("all")
class DDSPixelFormat {

    private static final int DDPF_ALPHAPIXELS = 0x1;
    private static final int DDPF_ALPHA = 0x2;
    private static final int DDPF_FOURCC = 0x4;
    private static final int DDPF_RGB = 0x40;
    private static final int DDPF_YUV = 0x200;
    private static final int DDPF_LUMINANCE = 0x20000;

    private int dwFlags;
    private int dwFourCC;

    private int intRGBBitCount;
    private int[] dwRGBABitMask = new int[4];

    DDSPixelFormat(ByteBuffer pixelFormatBuffer) {

        final int dwSize = getNext(pixelFormatBuffer);
        dwFlags = getNext(pixelFormatBuffer);
        dwFourCC = getNext(pixelFormatBuffer);

        intRGBBitCount = getNext(pixelFormatBuffer);

        dwRGBABitMask[0] = getNext(pixelFormatBuffer);
        dwRGBABitMask[1] = getNext(pixelFormatBuffer);
        dwRGBABitMask[2] = getNext(pixelFormatBuffer);
        dwRGBABitMask[3] = getNext(pixelFormatBuffer);
    }

    public int getDwFourCC() {
        return dwFourCC;
    }

    public int getDwFlags() {
        return dwFlags;
    }

    public int getIntRGBBitCount() {
        return intRGBBitCount;
    }

    public int[] getDwRGBABitMask() {
        return dwRGBABitMask;
    }
}