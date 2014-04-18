/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2013-2014 Keyle & xXLupoXx
 * DungeonCraft is licensed under the GNU Lesser General Public License.
 *
 * DungeonCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.keyle.dungeoncraft.entity.types;

import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.entity.ai.AIGoalSelector;
import de.keyle.dungeoncraft.entity.ai.movement.LookAtPlayer;
import de.keyle.dungeoncraft.entity.ai.movement.RandomLookaround;
import de.keyle.dungeoncraft.entity.ai.navigation.AbstractNavigation;
import de.keyle.dungeoncraft.entity.ai.navigation.VanillaNavigation;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import net.minecraft.server.v1_7_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Field;
import java.util.*;

public abstract class EntityDungeonCraft extends EntityCreature implements IMonster {
    public AIGoalSelector petPathfinderSelector, petTargetSelector;
    public EntityLiving goalTarget = null;
    protected double walkSpeed = 0.3F;
    protected boolean hasRider = false;
    protected int idleSoundTimer = 0;
    public AbstractNavigation petNavigation;
    private Map<Float, org.bukkit.inventory.ItemStack> lootTable = new HashMap<Float, org.bukkit.inventory.ItemStack>();
    public List<EntityTemplateComponent> components = new ArrayList<EntityTemplateComponent>();
    private int maxDrops;
    private int lootIterations;

    private Field jump = null;
    private int armor = 0;

    public EntityDungeonCraft(World world) {
        super(world);

        try {
            setSize();

            this.petPathfinderSelector = new AIGoalSelector();
            this.petTargetSelector = new AIGoalSelector();

            this.walkSpeed = 0.30D;
            getAttributeInstance(GenericAttributes.d).setValue(walkSpeed);

            petNavigation = new VanillaNavigation(this);

            this.setPathfinder();

            try {
                jump = EntityLiving.class.getDeclaredField("bc");
                jump.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPathfinder() {
        petPathfinderSelector.addGoal("Float", new de.keyle.dungeoncraft.entity.ai.movement.Float(this));
        petPathfinderSelector.addGoal("LookAtPlayer", new LookAtPlayer(this, 8.0F));
        petPathfinderSelector.addGoal("RandomLockaround", new RandomLookaround(this));
    }

    public void setSize() {
        setSize(0F);
    }

    public void setSize(float extra) {
        EntityInfo es = this.getClass().getAnnotation(EntityInfo.class);
        if (es != null) {
            this.a(es.width(), es.height() + extra);
        }
    }

    public void setArmor(int armor) {
        this.armor = Math.max(0, armor);
    }

    public boolean hasRider() {
        return passenger != null;
    }

    public void setLocation(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    public boolean canMove() {
        return true;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public boolean playIdleSound() {
        if (idleSoundTimer-- <= 0) {
            idleSoundTimer = 5;
            return true;
        }
        return false;
    }

    public void setLootTable(Map<Float, org.bukkit.inventory.ItemStack> lootTable) {
        if (lootTable != null) {
            this.lootTable = lootTable;
        }
    }

    public Map<Float, org.bukkit.inventory.ItemStack> getLootTable() {
        return lootTable;
    }

    public int getMaxDrops() {
        return maxDrops;
    }

    public void setMaxDrops(int maxDrops) {
        this.maxDrops = maxDrops;
    }

    public int getLootIterations() {
        return lootIterations;
    }

    public void setLootIterations(int lootIterations) {
        this.lootIterations = lootIterations;
    }

    /**
     * Is called when a Entity attemps to do damge to another entity
     */
    public boolean attack(Entity entity) {
        boolean damageEntity = false;
        try {
            double damage = 1;
            damageEntity = entity.damageEntity(DamageSource.mobAttack(this), (float) damage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return damageEntity;
    }

    public ItemStack getEquipment(int i) {
        if (Thread.currentThread().getStackTrace()[2].getClassName().equals("net.minecraft.server.v1_7_R3.EntityTrackerEntry")) {
            DungeonCraftLogger.write("get EQ: " + Thread.currentThread().getStackTrace()[2].getClassName());
            DebugLogger.printStackTrace(Thread.currentThread().getStackTrace());
        }
        return super.getEquipment(i);
    }

    public boolean damageEntity(DamageSource damagesource, float damage) {

        if (isInvulnerable()) {
            return false;
        }

        this.aU = 0;
        if (getHealth() <= 0.0F) {
            return false;
        }
        if ((damagesource.o()) && (hasEffect(MobEffectList.FIRE_RESISTANCE))) {
            return false;
        }
        if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && getEquipment(4) != null) {
            getEquipment(4).damage((int) (damage * 4.0F + this.random.nextFloat() * damage * 2.0F), this);
            damage *= 0.75F;
        }

        this.aF = 1.5F;
        boolean flag = true;

        EntityDamageEvent event = CraftEventFactory.handleEntityDamageEvent(this, damagesource, damage);
        if (event != null) {
            if (event.isCancelled()) {
                return false;
            }
            damage = (float) event.getDamage();
        }

        if (this.noDamageTicks > this.maxNoDamageTicks / 2.0F) {
            if (damage <= this.lastDamage) {
                return false;
            }

            d(damagesource, damage - this.lastDamage);
            this.lastDamage = damage;
            flag = false;
        } else {
            this.lastDamage = damage;
            this.aw = getHealth();
            this.noDamageTicks = this.maxNoDamageTicks;
            d(damagesource, damage);
            this.hurtTicks = (this.ay = 10);
        }

        this.az = 0.0F;
        Entity entity = damagesource.getEntity();

        if (entity != null) {
            if (entity instanceof EntityLiving) {
                b((EntityLiving) entity);
            }

            if (entity instanceof EntityHuman) {
                this.lastDamageByPlayerTime = 100;
                this.killer = ((EntityHuman) entity);
            } else if (entity instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entity;

                if (entitywolf.isTamed()) {
                    this.lastDamageByPlayerTime = 100;
                    this.killer = null;
                }
            }
        }

        if (flag) {
            this.world.broadcastEntityEffect(this, (byte) 2);
            if (damagesource != DamageSource.DROWN) {
                P();
            }

            if (entity != null) {
                double deltaX = entity.locX - this.locX;
                double deltaZ = entity.locZ - this.locZ;

                for (; deltaX * deltaX + deltaZ * deltaZ < 0.0001D; deltaZ = (Math.random() - Math.random()) * 0.01D) {
                    deltaX = (Math.random() - Math.random()) * 0.01D;
                }

                this.az = ((float) (Math.atan2(deltaZ, deltaX) * 180.0D / 3.141592741012573D) - this.yaw);
                a(entity, damage, deltaX, deltaZ);
            } else {
                this.az = ((int) (Math.random() * 2.0D) * 180);
            }

        }

        if (getHealth() <= 0.0F) {
            String deathSound = getDeathSound();
            if ((flag) && (deathSound != null)) {
                makeSound(deathSound, be(), getSoundSpeed());
            }
            die(damagesource);
        } else {
            String hurtSound = getHurtSound();
            if (flag && hurtSound != null) {
                makeSound(hurtSound, be(), getSoundSpeed());
            }
        }
        return true;
    }

    public void dropEquipment(boolean b, int i) {
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public CraftDungeonCraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new CraftDungeonCraftEntity(this.world.getServer(), this);
        }
        return (CraftDungeonCraftEntity) this.bukkitEntity;
    }

    // Obfuscated Method handler ------------------------------------------------------------------------------------

    public int getArmor() {
        return armor;
    }

    /**
     * Is called when player rightclicks this Entity
     * return:
     * true: there was a reaction on rightclick
     * false: no reaction on rightclick
     */
    public boolean handlePlayerInteraction(EntityHuman entityhuman) {
        ItemStack itemStack = entityhuman.inventory.getItemInHand();

        return false;
    }

    public void onLivingUpdate() {
    }

    protected void initDatawatcher() {
    }

    public Random getRandom() {
        return this.random;
    }

    /**
     * Returns the speed of played sounds
     * The faster the higher the sound will be
     */
    public float getSoundSpeed() {
        float pitchAddition = 0;
        if (isBaby()) {
            pitchAddition += 0.5F;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1 + pitchAddition;
    }

    /**
     * Returns the default sound of the Entity
     */
    protected abstract String getLivingSound();

    /**
     * Returns the sound that is played when the Entity get hurt
     */
    protected abstract String getHurtSound();

    /**
     * Returns the sound that is played when the Entity dies
     */
    protected abstract String getDeathSound();

    public void playStepSound() {
    }

    public void playStepSound(int i, int j, int k, Block block) {
        playStepSound();
    }

    // Obfuscated Methods -------------------------------------------------------------------------------------------

    /**
     * -> initDatawatcher()
     */
    protected void c() {
        super.c();
        try {
            initDatawatcher();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
    }

    /**
     * Is called when player rightclicks this Entity
     * return:
     * true: there was a reaction on rightclick
     * false: no reaction on rightclick
     * -> handlePlayerInteraction(EntityHuman)
     */
    protected boolean a(EntityHuman entityhuman) {
        try {
            return handlePlayerInteraction(entityhuman);
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return false;
    }

    /**
     * -> playStepSound()
     */
    protected void a(int i, int j, int k, Block block) {
        try {
            playStepSound(i, j, k, block);
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
    }

    /**
     * Returns the sound that is played when the Entity get hurt
     * -> getHurtSound()
     */
    protected String aS() {
        try {
            return getHurtSound();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return null;
    }

    /**
     * Returns the sound that is played when the Entity dies
     * -> getDeathSound()
     */
    protected String aT() {
        try {
            return getDeathSound();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return null;
    }

    /**
     * Returns basic armor value of the entity
     * -> getArmor()
     */
    public int aU() {
        try {
            return super.aU() + getArmor();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return super.aU();
    }

    /**
     * Returns the speed of played sounds
     */
    protected float bf() {
        try {
            return getSoundSpeed();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return super.bf();
    }

    /**
     * Set weather the "new" AI is used
     */
    public boolean bj() {
        return true;
    }

    /**
     * Entity AI tick method
     * -> updateAITasks()
     */
    @Override
    protected void bm() {
        try {
            aU += 1; // entityAge

            if (isAlive()) {
                getEntitySenses().a(); // sensing

                petTargetSelector.tick(); // target selector
                petPathfinderSelector.tick(); // pathfinder selector
                petNavigation.tick(); // navigation
            }

            bo(); // "mob tick"

            // controls
            getControllerMove().c(); // move
            getControllerLook().a(); // look
            getControllerJump().b(); // jump

            for (EntityTemplateComponent component : components) {
                component.tick();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    public void e(float motionSideways, float motionForward) {
        if (!hasRider || this.passenger == null) {
            super.e(motionSideways, motionForward);
            return;
        }

        //apply pitch & yaw
        this.lastYaw = (this.yaw = this.passenger.yaw);
        this.pitch = this.passenger.pitch * 0.5F;
        b(this.yaw, this.pitch);
        this.aP = (this.aN = this.yaw);

        // get motion from passenger (player)
        motionSideways = ((EntityLiving) this.passenger).bd * 0.5F;
        motionForward = ((EntityLiving) this.passenger).be;

        // backwards is slower
        if (motionForward <= 0.0F) {
            motionForward *= 0.25F;
        }
        // sideways is slower too
        motionSideways *= 0.85F;

        float speed = 0.22222F;
        double jumpHeight = 0.3D;
        i(speed); // set ride speed
        super.e(motionSideways, motionForward); // apply motion

        // jump when the player jumps
        if (jump != null && onGround) {
            try {
                if (jump.getBoolean(this.passenger)) {
                    this.motY = Math.sqrt(jumpHeight);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    public void h() {
        super.h();
        try {
            onLivingUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
    }


    /**
     * Returns the default sound of the Entity
     * -> getLivingSound()
     */
    protected String t() {
        try {
            return playIdleSound() ? getLivingSound() : null;
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
        return null;
    }
}