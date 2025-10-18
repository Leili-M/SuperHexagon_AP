package gamehex.model;

import gamehex.model.obstacles.HexWall;
import gamehex.model.obstacles.Telegraph;

import java.util.List;

public record Snapshot(
        int    sectors,
        float  bgAngleDeg,
        float  playerThetaDeg,
        float  playerRadius,
        float  playerSizePx,
        float  omegaDegPerSec,
        boolean gameOver,
        boolean paused,
        List<HexWall> hexWalls,
        Telegraph telegraph
) {
    public static Snapshot from(GameState s){
        return new Snapshot(
                s.world().sectors(),
                s.bgAngleDeg(),
                s.player().thetaDeg(),
                s.player().radius(),
                s.playerSizePx(),
                s.player().omega(),
                s.gameOver(),
                s.paused(),
                List.copyOf(s.obstacles().walls()),
                null
        );
    }
}
