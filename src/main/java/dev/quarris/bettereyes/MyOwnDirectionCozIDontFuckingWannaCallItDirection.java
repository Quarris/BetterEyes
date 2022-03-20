package dev.quarris.bettereyes;

public enum MyOwnDirectionCozIDontFuckingWannaCallItDirection {
    N, NNE, NE, ENE, E, ESE, SE, SSE, S, SSW, SW, WSW, W, WNW, NW, NNW;

    public static MyOwnDirectionCozIDontFuckingWannaCallItDirection getDirectionFromAngle(double angle) {
        double anglePerDir = 360.0 / MyOwnDirectionCozIDontFuckingWannaCallItDirection.values().length;
        int index = (int) (((angle + anglePerDir / 2) % 360) / anglePerDir);
        return MyOwnDirectionCozIDontFuckingWannaCallItDirection.values()[index];
    }
}

