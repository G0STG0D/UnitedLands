package org.unitedlands.unitedlands.utils;

import java.util.ArrayList;
import java.util.List;

public class PolygonUtils {

    public static boolean isPointInPolygon(double[] polygon, double px, double py) {

        if (polygon == null)
            return false;

        int n = polygon.length / 2;
        if (n < 3)
            return false;

        boolean inside = false;

        double xj = polygon[2 * (n - 1)];
        double yj = polygon[2 * (n - 1) + 1];

        for (int i = 0; i < n; i++) {
            double xi = polygon[2 * i];
            double yi = polygon[2 * i + 1];

            boolean intersects = ((yi > py) != (yj > py)) &&
                    (px < (xj - xi) * (py - yi) / (yj - yi) + xi);

            if (intersects) {
                inside = !inside;
            }

            xj = xi;
            yj = yi;
        }

        return inside;
    }

    /** Main entry point: guaranteed-inside center */
    public static double[] calculatePolygonCenter(double[] points) {
        double[] centroid = areaCentroid(points);
        if (isPointInPolygon(points, centroid[0], centroid[1])) {
            return centroid;
        }
        // Fallback: centroid of the largest triangle from ear-clipping triangulation
        List<double[]> triangles = triangulate(points);
        double[] best = null;
        double bestArea = -1;
        for (double[] tri : triangles) {
            double area = triangleArea(tri);
            if (area > bestArea) {
                bestArea = area;
                best = tri;
            }
        }
        return triangleCentroid(best);
    }


    /** Area-weighted centroid (correct centroid formula, not just vertex average) */
    private static double[] areaCentroid(double[] points) {
        int n = points.length / 2;
        double cx = 0, cy = 0, area = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double xi = points[2 * i], yi = points[2 * i + 1];
            double xj = points[2 * j], yj = points[2 * j + 1];
            double cross = xi * yj - xj * yi;
            area += cross;
            cx += (xi + xj) * cross;
            cy += (yi + yj) * cross;
        }
        area *= 0.5;
        if (Math.abs(area) < 1e-9) {
            // Degenerate polygon fallback: plain vertex average
            double sx = 0, sy = 0;
            for (int i = 0; i < n; i++) { sx += points[2 * i]; sy += points[2 * i + 1]; }
            return new double[]{ sx / n, sy / n };
        }
        cx /= (6 * area);
        cy /= (6 * area);
        return new double[]{ cx, cy };
    }

    private static double triangleArea(double[] t) {
        return Math.abs((t[2]-t[0])*(t[5]-t[1]) - (t[4]-t[0])*(t[3]-t[1])) / 2.0;
    }

    private static double[] triangleCentroid(double[] t) {
        return new double[]{ (t[0]+t[2]+t[4]) / 3.0, (t[1]+t[3]+t[5]) / 3.0 };
    }

    /** Ear-clipping triangulation. Returns list of triangles, each as [x1,y1,x2,y2,x3,y3]. */
    private static List<double[]> triangulate(double[] points) {
        int n = points.length / 2;
        List<double[]> result = new ArrayList<>();
        if (n < 3) return result;

        // Ensure CCW winding for the standard ear-clip convexity test
        List<Integer> indices = new ArrayList<>();
        double signedArea = signedArea(points);
        if (signedArea < 0) {
            for (int i = n - 1; i >= 0; i--) indices.add(i);
        } else {
            for (int i = 0; i < n; i++) indices.add(i);
        }

        int guard = 0;
        int maxIterations = n * n; // safety valve against infinite loops on bad input

        while (indices.size() > 3 && guard++ < maxIterations) {
            boolean earFound = false;

            for (int i = 0; i < indices.size(); i++) {
                int iPrev = indices.get((i - 1 + indices.size()) % indices.size());
                int iCurr = indices.get(i);
                int iNext = indices.get((i + 1) % indices.size());

                double ax = points[2*iPrev], ay = points[2*iPrev+1];
                double bx = points[2*iCurr], by = points[2*iCurr+1];
                double cx = points[2*iNext], cy = points[2*iNext+1];

                if (!isConvex(ax, ay, bx, by, cx, cy)) continue;

                boolean anyInside = false;
                for (int idx : indices) {
                    if (idx == iPrev || idx == iCurr || idx == iNext) continue;
                    double px = points[2*idx], py = points[2*idx+1];
                    if (pointInTriangle(px, py, ax, ay, bx, by, cx, cy)) {
                        anyInside = true;
                        break;
                    }
                }

                if (!anyInside) {
                    result.add(new double[]{ ax, ay, bx, by, cx, cy });
                    indices.remove(i);
                    earFound = true;
                    break;
                }
            }

            if (!earFound) break; // malformed/self-intersecting polygon; stop gracefully
        }

        if (indices.size() == 3) {
            int a = indices.get(0), b = indices.get(1), c = indices.get(2);
            result.add(new double[]{ points[2*a], points[2*a+1],
                                      points[2*b], points[2*b+1],
                                      points[2*c], points[2*c+1] });
        }

        return result;
    }

    private static boolean isConvex(double ax, double ay, double bx, double by, double cx, double cy) {
        return ((bx - ax) * (cy - ay) - (by - ay) * (cx - ax)) > 0;
    }

    private static boolean pointInTriangle(double px, double py,
                                           double ax, double ay, double bx, double by, double cx, double cy) {
        double d1 = sign(px, py, ax, ay, bx, by);
        double d2 = sign(px, py, bx, by, cx, cy);
        double d3 = sign(px, py, cx, cy, ax, ay);
        boolean hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        return !(hasNeg && hasPos);
    }

    private static double sign(double px, double py, double ax, double ay, double bx, double by) {
        return (px - bx) * (ay - by) - (ax - bx) * (py - by);
    }

    public static double[] offsetPolygon(double[] points, double distance, double miterLimit) {
        int n = points.length / 2;
        if (n < 3)
            return points.clone();

        double signedArea = signedArea(points);
        // Positive signedArea = counterclockwise in standard math coords.
        // SVG's y-axis points down, so CCW in math-space appears CW on screen.
        // We just need consistent "outward" direction, so use the sign to flip if
        // needed.
        double orientation = signedArea >= 0 ? 1.0 : -1.0;

        double[] result = new double[points.length];

        for (int i = 0; i < n; i++) {
            int prevIdx = (i - 1 + n) % n;
            int nextIdx = (i + 1) % n;

            double x = points[2 * i], y = points[2 * i + 1];
            double px = points[2 * prevIdx], py = points[2 * prevIdx + 1];
            double nx = points[2 * nextIdx], ny = points[2 * nextIdx + 1];

            // Edge vectors
            double e1x = x - px, e1y = y - py; // prev -> current
            double e2x = nx - x, e2y = ny - y; // current -> next

            // Normalize edge vectors
            double len1 = Math.hypot(e1x, e1y);
            double len2 = Math.hypot(e2x, e2y);
            if (len1 == 0)
                len1 = 1e-9;
            if (len2 == 0)
                len2 = 1e-9;
            e1x /= len1;
            e1y /= len1;
            e2x /= len2;
            e2y /= len2;

            // Perpendicular (outward) normals of each edge.
            // Rotate edge vector -90deg: (x,y) -> (y, -x) gives outward normal
            // for CCW polygons in standard math space; multiply by orientation
            // to handle CW polygons too.
            double n1x = orientation * e1y, n1y = -orientation * e1x;
            double n2x = orientation * e2y, n2y = -orientation * e2x;

            // Average normal (bisector direction)
            double bisectorX = n1x + n2x;
            double bisectorY = n1y + n2y;
            double bisectorLen = Math.hypot(bisectorX, bisectorY);

            double offsetX, offsetY;

            if (bisectorLen < 1e-9) {
                // Edges point in opposite directions (180-degree fold) — just use one normal
                offsetX = n1x * distance;
                offsetY = n1y * distance;
            } else {
                bisectorX /= bisectorLen;
                bisectorY /= bisectorLen;

                // cos(theta/2) via dot product of normal and bisector
                double cosHalfAngle = n1x * bisectorX + n1y * bisectorY;
                cosHalfAngle = Math.max(cosHalfAngle, 1e-4); // avoid divide-by-near-zero

                double miterLength = 1.0 / cosHalfAngle;
                miterLength = Math.min(miterLength, miterLimit); // clamp sharp spikes

                offsetX = bisectorX * distance * miterLength;
                offsetY = bisectorY * distance * miterLength;
            }

            result[2 * i] = x + offsetX;
            result[2 * i + 1] = y + offsetY;
        }

        return result;
    }

    /**
     * Shoelace formula: positive = CCW, negative = CW (in standard math
     * coordinates)
     */
    private static double signedArea(double[] points) {
        int n = points.length / 2;
        double area = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += points[2 * i] * points[2 * j + 1];
            area -= points[2 * j] * points[2 * i + 1];
        }
        return area / 2.0;
    }

}
