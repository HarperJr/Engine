package util.readers;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public final class TargaReader {

    private final static int CHUNK = 512;
    private final static int UNCOMPRESSED_TRUE_COLOR = 2;

    public static BufferedImage read(InputStream stream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);

        ArrayList<byte[]> data = new ArrayList<>();
        boolean reading = true;
        int cumulative = 0;

        while (reading) {
            byte[] chunk = new byte[CHUNK];
            int read = bufferedInputStream.read(chunk, 0, CHUNK);

            if (read < CHUNK) {
                reading = false;
            }
            if (read > 0) {
                data.add(chunk);
                cumulative += read;
            }
        }
        bufferedInputStream.close();

        ByteStack stack = new ByteStack(data, cumulative, CHUNK);

        int footer = 26;
        int offset = 18;

        int x_ll = (stack.getUnsigned(9) << 8) + stack.getUnsigned(8);
        int y_ll = (stack.getUnsigned(11) << 8) + stack.getUnsigned(10);

        //image dimensions
        int width = (stack.getUnsigned(13) << 8) + stack.getUnsigned(12);
        int height = (stack.getUnsigned(15) << 8) + stack.getUnsigned(14);

        int byte_per_pixel = stack.getUnsigned(16) / 8;

        //part of the file spec. The end of the file should say TRUEVISION-XFILE.
        char[] signature = new char[17];
        int start = cumulative - footer + 8;

        for (int i = 0; i < 17; i++) {
            signature[i] = (char) stack.getUnsigned(start + i);
        }

        int type = stack.get(2);

        if (type != UNCOMPRESSED_TRUE_COLOR) {
            throw new IOException("The image type provided is not valid. Only uncompressed" +
                    "true type tga files accepted. Type provided: " + type);
        }


        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        //comes in bgra.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int dex = (j * width + i) * byte_per_pixel + offset;
                int b = stack.getUnsigned(dex);
                int g = stack.getUnsigned(dex + 1);
                int r = stack.getUnsigned(dex + 2);
                int a = stack.getUnsigned(dex + 3);

                int y = j;

                if (y_ll == 0) {
                    y = height - j - 1;
                }

                bufferedImage.setRGB(i, y, (a << 24) + (r << 16) + (g << 8) + b);
            }
        }
        return bufferedImage;
    }
}

class ByteStack {
    private ArrayList<byte[]> stack;
    private int length;
    private int chunk;

    public ByteStack(ArrayList<byte[]> s, int length, int chunk) {
        this.length = length;
        this.stack = s;
        this.chunk = chunk;
    }

    public byte get(int dex) {

        int row = dex / chunk;
        int column = dex % chunk;

        return stack.get(row)[column];

    }

    public int getUnsigned(int dex) {
        int row = dex / chunk;
        int column = dex % chunk;

        int b = stack.get(row)[column];
        return b < 0 ? 256 + b : b;
    }
}

