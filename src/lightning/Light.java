package lightning;

import org.joml.Vector3f;

import java.nio.ByteBuffer;

public abstract class Light {
    private Vector3f position;
    private Vector3f color;
    private float intensity;

    public Light(Vector3f position) {
        this.position = position;
    }

    public abstract void load(ByteBuffer buffer);

    public abstract void store(ByteBuffer buffer);

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
