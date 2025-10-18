package gamehex.model.obstacles;

import gamehex.model.physics.HexMath;
import static gamehex.model.physics.HexMath.K_R_FROM_NORMAL;

import java.util.*;

public final class ObstacleSystem {

    private final Random rnd = new Random();

    private float spawnSpacingPx = 230f;

    private float spawnExtraPx = 250f;

    private float distAccR = 0f;
    private float currentSpeedR = 0f;

    private final int sectors;
    private final float outerRadius;
    private final float playerRadius;


    private float wallSpeedPxPerSec = 40f;
    private float wallThicknessPx   = 24f;

    private float spawnRatePerSec   = 1.0f;
    private float minGapDeg         = 40f;
    private float telegraphLeadSec  = 0.6f;

    private final List<HexWall> walls = new ArrayList<>();
    public List<HexWall> walls(){ return walls; }

    private enum Pattern { ONE_GAP, ALT_PAIRS, RANDOM_SAFE }
    private Pattern nextPattern = Pattern.ONE_GAP;

    public ObstacleSystem(int sectors, float outerRadius, float playerRadius){
        if (sectors < 3) throw new IllegalArgumentException("sectors >= 3");
        this.sectors = sectors;
        this.outerRadius = outerRadius;
        this.playerRadius = playerRadius;
    }


    public void setDifficultyParams(float v, float gDeg, float lambda, float thicknessBase, float tSafe){
        this.wallSpeedPxPerSec = v;
        this.minGapDeg         = gDeg;
        this.spawnRatePerSec   = lambda;
        this.wallThicknessPx   = thicknessBase;
        this.telegraphLeadSec  = tSafe;

        this.currentSpeedR = v * K_R_FROM_NORMAL;
    }

    public void update(float dtSec){
        final float thicknessR = wallThicknessPx * K_R_FROM_NORMAL;
        final float spacingR = spawnSpacingPx * K_R_FROM_NORMAL;
        distAccR += currentSpeedR * dtSec;
        while (distAccR >= spacingR) {
            distAccR -= spacingR;
            int k = rnd.nextInt(3);
            nextPattern = (k==0)? Pattern.ONE_GAP : (k==1)? Pattern.ALT_PAIRS : Pattern.RANDOM_SAFE;
            spawnWave(nextPattern, thicknessR, currentSpeedR);
        }

        for (Iterator<HexWall> it = walls.iterator(); it.hasNext(); ){
            HexWall w = it.next();
            w.update(dtSec);
            if (w.expired()) it.remove();
        }
    }

    private void spawnWave(Pattern p, float thicknessR, float speedR){
        final float extraR   = spawnExtraPx * K_R_FROM_NORMAL;
        final float spawnR   = outerRadius + extraR;
        final float u0 = 0f, u1 = 1f;

        switch (p){
            case ONE_GAP -> {
                int gap = rnd.nextInt(sectors);
                for (int side=0; side<sectors; side++){
                    if (side == gap) continue;
                    walls.add(new HexWall(side, u0, u1, spawnR, thicknessR, speedR));
                }
            }
            case ALT_PAIRS -> {
                int parity = rnd.nextBoolean() ? 0 : 1;
                for (int side=0; side<sectors; side++){
                    if ((side & 1) == parity){
                        walls.add(new HexWall(side, u0, u1, spawnR, thicknessR, speedR));
                    }
                }
            }
            case RANDOM_SAFE -> {
                List<Integer> idx = new ArrayList<>(sectors);
                for (int i=0;i<sectors;i++) idx.add(i);
                Collections.shuffle(idx, rnd);
                int minBars = Math.max(2, sectors/2);
                int maxBars = sectors - 1;
                int count   = rnd.nextInt(maxBars - minBars + 1) + minBars;
                for (int i=0;i<count;i++){
                    int side = idx.get(i);
                    walls.add(new HexWall(side, u0, u1, spawnR, thicknessR, speedR));
                }
            }
        }
    }

    public boolean triangleHitsWallsHexSimple(float playerThetaDeg, float ringR, float arenaAngleDeg){
        final float sideLen = gamehex.model.physics.Phys.SHIP_SIDE_PX <= 0f
                ? 12f : gamehex.model.physics.Phys.SHIP_SIDE_PX;
        final float rC   = ringR * gamehex.model.physics.Phys.SHIP_CENTER_R_F;
        final double th  = Math.toRadians(playerThetaDeg);
        final double d   = sideLen / Math.sqrt(3.0);

        final double mx = rC * Math.cos(th);
        final double my = rC * Math.sin(th);

        double a0 = th, a1 = th + 2.0*Math.PI/3.0, a2 = th - 2.0*Math.PI/3.0;
        float[] T0 = new float[]{ (float)(mx + d*Math.cos(a0)), (float)(my + d*Math.sin(a0)) };
        float[] T1 = new float[]{ (float)(mx + d*Math.cos(a1)), (float)(my + d*Math.sin(a1)) };
        float[] T2 = new float[]{ (float)(mx + d*Math.cos(a2)), (float)(my + d*Math.sin(a2)) };
        float[][] tri = new float[][]{ T0, T1, T2 };

        final double phase = Math.toRadians(arenaAngleDeg);

        for (HexWall w : walls){
            float[] O0 = HexMath.pointOnSide(w.rOuter, w.side, w.u0, phase);
            float[] O1 = HexMath.pointOnSide(w.rOuter, w.side, w.u1, phase);
            float[] I1 = HexMath.pointOnSide(w.rInner, w.side, w.u1, phase);
            float[] I0 = HexMath.pointOnSide(w.rInner, w.side, w.u0, phase);
            float[][] quad = new float[][]{ O0, O1, I1, I0 };

            if (pointInConvexQuad(T0, quad) || pointInConvexQuad(T1, quad) || pointInConvexQuad(T2, quad)) return true;
            if (pointInTriangle(O0, tri) || pointInTriangle(O1, tri) || pointInTriangle(I1, tri) || pointInTriangle(I0, tri)) return true;

            int[][] triEdges  = new int[][]{ {0,1},{1,2},{2,0} };
            int[][] quadEdges = new int[][]{ {0,1},{1,2},{2,3},{3,0} };
            for (int[] te : triEdges){
                float[] A = tri[te[0]], B = tri[te[1]];
                for (int[] qe : quadEdges){
                    float[] C = quad[qe[0]], D = quad[qe[1]];
                    if (segmentsIntersect(A[0],A[1], B[0],B[1], C[0],C[1], D[0],D[1])) return true;
                }
            }
        }
        return false;
    }


    private static boolean pointInTriangle(float[] P, float[][] T){
        float[] A=T[0], B=T[1], C=T[2];
        float v0x=C[0]-A[0], v0y=C[1]-A[1];
        float v1x=B[0]-A[0], v1y=B[1]-A[1];
        float v2x=P[0]-A[0], v2y=P[1]-A[1];
        float dot00 = v0x*v0x + v0y*v0y;
        float dot01 = v0x*v1x + v0y*v1y;
        float dot02 = v0x*v2x + v0y*v2y;
        float dot11 = v1x*v1x + v1y*v1y;
        float dot12 = v1x*v2x + v1y*v2y;
        float invDen = 1f / (dot00*dot11 - dot01*dot01);
        float u = (dot11*dot02 - dot01*dot12) * invDen;
        float v = (dot00*dot12 - dot01*dot02) * invDen;
        return (u >= 0f && v >= 0f && (u+v) <= 1f);
    }
    private static boolean pointInConvexQuad(float[] P, float[][] Q){
        return pointInTriangle(P, new float[][]{Q[0],Q[1],Q[2]})
                || pointInTriangle(P, new float[][]{Q[0],Q[2],Q[3]});
    }
    private static boolean segmentsIntersect(float x1,float y1,float x2,float y2,
                                             float x3,float y3,float x4,float y4){
        float d1 = direction(x3,y3,x4,y4,x1,y1);
        float d2 = direction(x3,y3,x4,y4,x2,y2);
        float d3 = direction(x1,y1,x2,y2,x3,y3);
        float d4 = direction(x1,y1,x2,y2,x4,y4);
        if (((d1>0 && d2<0)||(d1<0 && d2>0)) && ((d3>0 && d4<0)||(d3<0 && d4>0))) return true;
        if (d1==0 && onSegment(x3,y3,x4,y4,x1,y1)) return true;
        if (d2==0 && onSegment(x3,y3,x4,y4,x2,y2)) return true;
        if (d3==0 && onSegment(x1,y1,x2,y2,x3,y3)) return true;
        if (d4==0 && onSegment(x1,y1,x2,y2,x4,y4)) return true;
        return false;
    }
    private static float direction(float ax,float ay,float bx,float by,float px,float py){
        return (bx-ax)*(py-ay) - (by-ay)*(px-ax);
    }
    private static boolean onSegment(float ax,float ay,float bx,float by,float px,float py){
        return px >= Math.min(ax,bx)-1e-4 && px <= Math.max(ax,bx)+1e-4
                && py >= Math.min(ay,by)-1e-4 && py <= Math.max(ay,by)+1e-4;
    }
}
