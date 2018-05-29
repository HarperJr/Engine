package lightning;


import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class PointLight extends Light {

    public PointLight(Vector3f position) {
        super(position);
    }

    @Override
    public void load(ByteBuffer buffer) {

    }

    @Override
    public void store(ByteBuffer buffer) {

    }
}
