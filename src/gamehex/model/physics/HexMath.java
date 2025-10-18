package gamehex.model.physics;

public final class HexMath {
    private HexMath(){}

    public static final float K_R_FROM_NORMAL = (2f / (float)Math.sqrt(3.0));

    public static float[] pointOnSide(float R, int sideIndex, float u, double phaseRad){
        int i0 = ((sideIndex % 6) + 6) % 6;
        int i1 = (i0 + 1) % 6;

        double a0 = (-Math.PI/2) + phaseRad + i0 * (Math.PI/3);
        double a1 = (-Math.PI/2) + phaseRad + i1 * (Math.PI/3);

        float x0 = (float)(R * Math.cos(a0)), y0 = (float)(R * Math.sin(a0));
        float x1 = (float)(R * Math.cos(a1)), y1 = (float)(R * Math.sin(a1));
        return new float[]{ x0 + u*(x1-x0), y0 + u*(y1-y0) };
    }

}
