package de.keyle.dungeoncraft.dungeon.generator;

import net.minecraft.server.v1_6_R3.*;

import java.util.List;
import java.util.Random;

public class EmptyChunk extends Chunk {
    public EmptyChunk(World paramWorld, int paramInt1, int paramInt2) {
        super(paramWorld, paramInt1, paramInt2);
    }

    public boolean a(int paramInt1, int paramInt2) {
        return (paramInt1 == this.x) && (paramInt2 == this.z);
    }

    public int b(int paramInt1, int paramInt2) {
        return 0;
    }

    public void initLighting() {
    }

    public int getTypeId(int paramInt1, int paramInt2, int paramInt3) {
        return 0;
    }

    public int b(int paramInt1, int paramInt2, int paramInt3) {
        return 255;
    }

    public boolean a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        return true;
    }

    public int getData(int paramInt1, int paramInt2, int paramInt3) {
        return 0;
    }

    public boolean b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        return false;
    }

    public int getBrightness(EnumSkyBlock paramEnumSkyBlock, int paramInt1, int paramInt2, int paramInt3) {
        return 0;
    }

    public void a(EnumSkyBlock paramEnumSkyBlock, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public int c(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        return 0;
    }

    public void a(Entity paramEntity) {
    }

    public void b(Entity paramEntity) {
    }

    public void a(Entity paramEntity, int paramInt) {
    }

    public boolean d(int paramInt1, int paramInt2, int paramInt3) {
        return false;
    }

    public TileEntity e(int paramInt1, int paramInt2, int paramInt3) {
        return null;
    }

    public void a(TileEntity paramTileEntity) {
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, TileEntity paramTileEntity) {
    }

    public void f(int paramInt1, int paramInt2, int paramInt3) {
    }

    public void addEntities() {
    }

    public void removeEntities() {
    }

    public void e() {
    }

    public void a(Entity paramEntity, AxisAlignedBB paramAxisAlignedBB, List paramList, IEntitySelector paramIEntitySelector) {
    }

    public void a(Class paramClass, AxisAlignedBB paramAxisAlignedBB, List paramList, IEntitySelector paramIEntitySelector) {
    }

    public boolean a(boolean paramBoolean) {
        return false;
    }

    public Random a(long paramLong) {
        return new Random(this.world.getSeed() + this.x * this.x * 4987142 + this.x * 5947611 + this.z * this.z * 4392871L + this.z * 389711 ^ paramLong);
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean c(int paramInt1, int paramInt2) {
        return true;
    }
}