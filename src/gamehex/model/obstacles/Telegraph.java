package gamehex.model.obstacles;

public final class Telegraph {
    public final float thetaCenterDeg;
    public final float widthDeg;
    public float etaSec;

    public Telegraph(float thetaCenterDeg, float widthDeg, float etaSec) {
        this.thetaCenterDeg = thetaCenterDeg;
        this.widthDeg = widthDeg;
        this.etaSec = etaSec;
    }

}
