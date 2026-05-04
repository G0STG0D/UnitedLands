package org.unitedlands.unitedlands.classes.interfaces;

import org.unitedlands.unitedlands.classes.Coordinates;

public interface CoordinateHolder {
    Coordinates getCoordinates();

    Coordinates getWorldCoords();
    Coordinates getCenter();
    Coordinates getLowerLeft();
    Coordinates getLowerRight();
    Coordinates getUpperLeft();
    Coordinates getUpperRight();
}
