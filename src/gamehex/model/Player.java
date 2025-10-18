package gamehex.model;



public final class Player {

    private float thetaDeg;
    private final float radius;
    private float omega;

    private float omegaMax, alpha, beta;

    public Player(float initialThetaDeg, float radius,
                  float omegaMax, float alpha, float beta) {
        this.thetaDeg = Polar.wrapDeg(initialThetaDeg);
        this.radius   = radius;
        this.omegaMax = omegaMax;
        this.alpha    = alpha;
        this.beta     = beta;
        this.omega    = 0f;
    }

    public void applyDifficulty(Difficulty d){
        this.omegaMax = d.omegaMaxDegPerSec;
        this.alpha    = d.alphaDegPerSec2;
        this.beta     = d.betaDegPerSec2;
    }

    public void setThetaDeg(float deg){
        this.thetaDeg = ((deg % 360f) + 360f) % 360f;
    }

    public void rotateBy(float deltaDeg){
        this.thetaDeg = Polar.wrapDeg(this.thetaDeg + deltaDeg);
    }

    public void control(boolean left, boolean right, float dtSec){
        if (left == right) {
            if (omega != 0f) {
                float sign = omega > 0 ? -1f : 1f;
                float dw = sign * beta * dtSec;
                if (Math.signum(omega + dw) != Math.signum(omega)) omega = 0f;
                else omega += dw;
            }
        } else if (left) {
            omega -= alpha * dtSec;
        } else if (right) {
            omega += alpha * dtSec;
        }

        if (omega > 0) omega = Math.min(omega, omegaMax);
        else           omega = Math.max(omega, -omegaMax);

        thetaDeg = Polar.wrapDeg(thetaDeg + omega * dtSec);
    }

    public float thetaDeg(){ return thetaDeg; }
    public float radius(){ return radius; }
    public float omega(){ return omega; }
    final class Polar {
        static float wrapDeg(float d){
            float r = d % 360f;
            return r < 0f ? r + 360f : r;
        }
    }
}
