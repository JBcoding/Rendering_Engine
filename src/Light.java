import sun.security.x509.X509CertInfo;

import java.awt.*;

/**
 * Created by madsbjoern on 10/12/2016.
 */
public class Light {
    public static final int LIGHT_FADING_LINEAR = 1;
    public static final int LIGHT_FADING_SQUARE = 2;
    public static final int LIGHT_FADING_SQUAREROOT = 3;

    private int lightFading;
    private Vector3D position;
    private double intensity;
    private String name;

    public Light(Vector3D position, int lightFading, double intensity) {
        this.position = position;
        this.lightFading = lightFading;
        this.intensity = intensity;
    }

    public double getLightInfluence(Vector3D hitPoint) {
        double distance = hitPoint.sub(position).getMagnitude();
        switch (lightFading) {
            case LIGHT_FADING_LINEAR:
                break;
            case LIGHT_FADING_SQUARE:
                distance *= distance;
                break;
            case LIGHT_FADING_SQUAREROOT:
                distance = Math.sqrt(distance);
                break;
        }
        if (distance == 0) {
            return 1;
        }
        return Math.min(intensity / distance, 1);
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLightFading(int lightFading) {
        this.lightFading = lightFading;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
}
