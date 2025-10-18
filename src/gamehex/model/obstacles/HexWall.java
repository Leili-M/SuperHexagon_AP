package gamehex.model.obstacles;

public final class HexWall {
    public final int side;
    public final float u0,u1;
    public float rOuter;
    public float rInner;
    public final float speedR;

    public HexWall(int side, float u0, float u1, float rOuter, float thicknessR, float speedR){
        this.side   = side;
        this.u0     = Math.min(u0,u1);
        this.u1     = Math.max(u0,u1);
        this.rOuter = rOuter;
        this.rInner = Math.max(0f, rOuter - thicknessR);
        this.speedR = speedR;
    }

    public void update(float dt){
        rOuter -= speedR * dt;
        if (rOuter < 0f) rOuter = 0f;
        rInner = Math.max(0f, rInner - speedR * dt);
    }

    public boolean expired(){
        return rInner <= 0f;
    }
}
