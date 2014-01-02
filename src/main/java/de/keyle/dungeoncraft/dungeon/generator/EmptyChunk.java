package de.keyle.dungeoncraft.dungeon.generator;

import net.minecraft.server.v1_7_R1.*;

import java.util.List;
import java.util.Random;

public class EmptyChunk extends Chunk {
    public EmptyChunk(World world, int posX, int posZ) {
        super(world, posX, posZ);
    }

    public boolean a(int posX, int posZ) {
        return (posX == this.locX) && (posZ == this.locZ);
    }

    public int b(int posX, int posZ) {
        return 0;
    }

    public void initLighting() {
    }

    public Block getType(int blockX, int blockY, int blockZ) {
        return Blocks.AIR;
    }

    public int b(int blockX, int blockY, int blockZ) {
        return 255;
    }

    public boolean a(int blockX, int blockY, int blockZ, Block block, int paramInt4) {
        return true;
    }

    public int getData(int blockX, int blockY, int blockZ) {
        return 0;
    }

    public boolean a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        return false;
    }

    public int getBrightness(EnumSkyBlock skyBlock, int blockX, int blockY, int blockZ) {
        return 0;
    }

    public void a(EnumSkyBlock skyBlock, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public int b(int blockX, int blockY, int blockZ, int paramInt4) {
        return 0;
    }

    public void a(Entity entity) {
    }

    public void b(Entity entity) {
    }

    public void a(Entity entity, int slice) {
    }

    public boolean d(int blockX, int blockY, int blockZ) {
        return false;
    }

    public TileEntity e(int blockX, int blockY, int blockZ) {
        return null;
    }

    public void a(TileEntity tileEntity) {
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, TileEntity tileEntity) {
    }

    public void f(int paramInt1, int paramInt2, int paramInt3) {
    }

    public void addEntities() {
    }

    public void removeEntities() {
    }

    public void e() {
    }

    public void a(Entity entity, AxisAlignedBB alignedBB, List list, IEntitySelector entitySelector) {
    }

    public void a(Class clazz, AxisAlignedBB alignedBB, List list, IEntitySelector entitySelector) {
    }

    public boolean a(boolean paramBoolean) {
        return false;
    }

    public Random a(long seed) {
        return new Random(this.world.getSeed() + this.locX * this.locX * 4987142 + this.locX * 5947611 + this.locZ * this.locZ * 4392871L + this.locZ * 389711 ^ seed);
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean c(int posX, int posZ) {
        return true;
    }
}