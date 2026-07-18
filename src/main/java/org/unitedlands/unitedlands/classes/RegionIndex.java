package org.unitedlands.unitedlands.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RegionIndex {

    private static final int MAX_OBJECTS_PER_NODE = 8;
    private static final int MAX_DEPTH = 10;

    private Node root;

    /** Rebuild the whole tree. Call on startup and whenever regions change. */
    public void build(Collection<Region> regions, double worldMinX, double worldMinZ,
            double worldMaxX, double worldMaxZ) {
        root = new Node(worldMinX, worldMinZ, worldMaxX, worldMaxZ, 0);
        for (Region region : regions) {
            root.insert(new Entry(region));
        }
    }

    /** Find the region containing (x, z), or null if the point is in no region. */
    public Region findRegion(double x, double z) {
        if (root == null)
            return null;
        List<Entry> candidates = new ArrayList<>();
        root.collectCandidates(x, z, candidates);
        for (Entry e : candidates) {
            if (e.region.isPointInRegion(x, z)) {
                return e.region;
            }
        }
        return null;
    }

    private class Entry {
        final Region region;
        final double minX, minZ, maxX, maxZ;

        Entry(Region region) {
            this.region = region;
            this.minX = region.getMinX();
            this.minZ = region.getMinZ();
            this.maxX = region.getMaxX();
            this.maxZ = region.getMaxZ();
        }

    }

    private class Node {
        final double minX, minZ, maxX, maxZ, midX, midZ;
        final int depth;
        final List<Entry> entries = new ArrayList<>();
        Node[] children; // 0=SW 1=SE 2=NW 3=NE

        Node(double minX, double minZ, double maxX, double maxZ, int depth) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
            this.midX = (minX + maxX) / 2.0;
            this.midZ = (minZ + maxZ) / 2.0;
            this.depth = depth;
        }

        void insert(Entry e) {
            if (children != null) {
                int idx = childIndexFor(e);
                if (idx != -1) {
                    children[idx].insert(e);
                    return;
                }
            }
            entries.add(e);
            if (children == null && entries.size() > MAX_OBJECTS_PER_NODE && depth < MAX_DEPTH) {
                split();
                entries.removeIf(existing -> {
                    int idx = childIndexFor(existing);
                    if (idx != -1) {
                        children[idx].insert(existing);
                        return true;
                    }
                    return false; // straddles a split, stays here
                });
            }
        }

        void split() {
            children = new Node[] {
                    new Node(minX, minZ, midX, midZ, depth + 1), // SW
                    new Node(midX, minZ, maxX, midZ, depth + 1), // SE
                    new Node(minX, midZ, midX, maxZ, depth + 1), // NW
                    new Node(midX, midZ, maxX, maxZ, depth + 1), // NE
            };
        }

        int childIndexFor(Entry e) {
            boolean west = e.maxX <= midX, east = e.minX >= midX;
            boolean south = e.maxZ <= midZ, north = e.minZ >= midZ;
            if (west && south)
                return 0;
            if (east && south)
                return 1;
            if (west && north)
                return 2;
            if (east && north)
                return 3;
            return -1; // bounding box straddles the split line
        }

        void collectCandidates(double x, double z, List<Entry> out) {
            for (Entry e : entries) {
                if (x >= e.minX && x <= e.maxX && z >= e.minZ && z <= e.maxZ) {
                    out.add(e);
                }
            }
            if (children != null) {
                int idx = (x <= midX)
                        ? (z <= midZ ? 0 : 2)
                        : (z <= midZ ? 1 : 3);
                children[idx].collectCandidates(x, z, out);
            }
        }
    }

}
