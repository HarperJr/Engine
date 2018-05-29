package util.readers.dds;

@SuppressWarnings("all")
class DDSMipMapLevel {

    private final int offset;

    private final int width;
    private final int height;

    DDSMipMapLevel(int width, int height, int offset) {
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return width * height * 4;
    }
}