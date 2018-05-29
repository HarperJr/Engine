package lightning;

import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class DirectionalLight extends Light {

    public DirectionalLight(Vector3f position) {
        super(position);
    }

    @Override
    public void load(ByteBuffer buffer) {

    }

    @Override
    public void store(ByteBuffer buffer) {

    }


}
