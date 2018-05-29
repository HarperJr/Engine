package physics;


import org.joml.Quaternionf;
import org.joml.Vector3f;

@SuppressWarnings("all")
public class Transform {

    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }
}
