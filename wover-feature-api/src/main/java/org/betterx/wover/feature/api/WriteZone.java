package org.betterx.wover.feature.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.joml.Vector3f;

/**
 * Horizontal write area available to a world-generation feature.
 * On the 26.1 NeoForge mappings the generating step is not exposed, so the
 * vanilla FEATURES contract (the current chunk plus one chunk in each
 * direction) is represented directly here.
 */
public record WriteZone(int minX, int minZ, int maxX, int maxZ) {
    public static final WriteZone UNBOUNDED = new WriteZone(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2,
            Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);

    public static WriteZone of(LevelAccessor level) {
        if (!(level instanceof WorldGenRegion region)) return UNBOUNDED;
        int cx = region.getCenter().x();
        int cz = region.getCenter().z();
        return new WriteZone(SectionPos.sectionToBlockCoord(cx - 1), SectionPos.sectionToBlockCoord(cz - 1),
                SectionPos.sectionToBlockCoord(cx + 1) + 15, SectionPos.sectionToBlockCoord(cz + 1) + 15);
    }

    public boolean isUnbounded() { return equals(UNBOUNDED); }
    public boolean contains(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
    public boolean contains(BlockPos pos) { return contains(pos.getX(), pos.getZ()); }

    public int clampX(int x) { return Math.min(Math.max(x, minX), maxX); }
    public int clampZ(int z) { return Math.min(Math.max(z, minZ), maxZ); }

    public boolean isDisjoint(int boxMinX, int boxMinZ, int boxMaxX, int boxMaxZ) {
        return boxMaxX < minX || boxMinX > maxX || boxMaxZ < minZ || boxMinZ > maxZ;
    }

    public BoundingBox clip(BoundingBox box) {
        return new BoundingBox(clampX(box.minX()), box.minY(), clampZ(box.minZ()),
                clampX(box.maxX()), box.maxY(), clampZ(box.maxZ()));
    }

    public BoundingBox toBoundingBox() {
        return new BoundingBox(minX, Integer.MIN_VALUE / 2, minZ,
                maxX, Integer.MAX_VALUE / 2, maxZ);
    }

    public BoundingBox toBoundingBox(int minY, int maxY) { return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ); }
    public float headroom(int cx, int cz) {
        if (isUnbounded()) return Float.MAX_VALUE / 2;
        return Math.max(0.0F, Math.min(Math.min((float) cx - minX, (float) maxX - cx),
                Math.min((float) cz - minZ, (float) maxZ - cz)));
    }
    public float fitRadius(int cx, int cz, float radius, float minimum) {
        float fitted = Math.min(radius, headroom(cx, cz));
        return fitted < minimum ? -1.0F : fitted;
    }

    public Vector3f fitSegment(Vector3f start, Vector3f end, BlockPos origin, float radius) {
        if (isUnbounded()) return new Vector3f(end);
        final float loX = minX + radius, hiX = maxX - radius;
        final float loZ = minZ + radius, hiZ = maxZ - radius;
        final float sx = origin.getX() + start.x(), sz = origin.getZ() + start.z();
        if (sx < loX || sx > hiX || sz < loZ || sz > hiZ) return new Vector3f(start);
        final float ex = origin.getX() + end.x(), ez = origin.getZ() + end.z();
        final float t = Math.min(1.0F, Math.min(axisLimit(sx, ex, loX, hiX), axisLimit(sz, ez, loZ, hiZ)));
        if (t >= 1.0F) return new Vector3f(end);
        if (t <= 0.0F) return new Vector3f(start);
        return new Vector3f(start.x() + (end.x() - start.x()) * t,
                start.y() + (end.y() - start.y()) * t,
                start.z() + (end.z() - start.z()) * t);
    }

    private static float axisLimit(float s, float e, float lo, float hi) {
        final float d = e - s;
        if (d > 0.0F) return (hi - s) / d;
        if (d < 0.0F) return (lo - s) / d;
        return 1.0F;
    }
}
