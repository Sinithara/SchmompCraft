package de.schmompcraft.schmomp.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SchmompEntity extends TamableAnimal {
    private static final double HEALTH_WILD = 10.0;
    private static final double MOVEMENT_SPEED = 0.3;
    private static final double ATTACK_DAMAGE = 3.0;
    private static final double TEMPT_RANGE = 10.0;
    private static final float LEAP_DISTANCE = 0.4f;
    private static final double MELE_ATTACK_SPEED = 1.0;
    private static final double FOLLOW_SPEED = 1.0;
    private static final float FOLLOW_START_DISTANCE = 10.0f;
    private static final float FOLLOW_STOP_DISTANCE = 2.0f;
    private static final double TEMP_GOAL_SPEED = 1.1;
    private static final double WATER_AVOID_SPEED = 1.0;
    private static final float PLAYER_LOOK_DISTANCE = 8.0f;
    private static final float FOOD_HEAL_FACTOR = 4.0f;

    private static final double HEALTH_TAME = 30.0;

    public SchmompEntity(EntityType<? extends SchmompEntity> entityType, Level level){
        super(entityType, level);
    }

    // Attributes
    public static AttributeSupplier.Builder createAttributes(){
        return  Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, HEALTH_WILD)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.TEMPT_RANGE, TEMPT_RANGE);
    }

    @Override protected void registerGoals(){
        // Goals
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, LEAP_DISTANCE));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, MELE_ATTACK_SPEED, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, FOLLOW_SPEED, FOLLOW_START_DISTANCE, FOLLOW_STOP_DISTANCE));
        this.goalSelector.addGoal(6, new TemptGoal(this, TEMP_GOAL_SPEED, stack -> stack.is(Items.SLIME_BALL), false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, WATER_AVOID_SPEED));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, PLAYER_LOOK_DISTANCE));

        // Target Goals
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());

    }

    @Override public boolean isFood(ItemStack stack){
        return stack.is(Items.SLIME_BALL);
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (this.isTame()){
            if(this.isFood(stack) && this.getHealth() > this.getMaxHealth()){
                if (!this.level().isClientSide()){
                    this.usePlayerItem(player, hand, stack);
                    this.heal(FOOD_HEAL_FACTOR);
                }
                return InteractionResult.SUCCESS;
            }
            if (this.isOwnedBy(player)){
                if (!this.level().isClientSide()){
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }
               return InteractionResult.SUCCESS;
            }
        } else if(this.isFood(stack)){
            if(!this.level().isClientSide()){
                this.usePlayerItem(player, hand, stack);
                this.tryToTame(player);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0){
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
        } else {
            this.level().broadcastEntityEvent(this,EntityEvent.TAMING_FAILED);
        }
    }

    @Override protected void applyTamingSideEffects(){
        if(this.isTame()){
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH_TAME);
            this.setHealth(this.getMaxHealth());
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HEALTH_WILD);
        }
    }

    @Override public boolean wantsToAttack(LivingEntity target, LivingEntity owner){
        if(target instanceof Creeper){
            return false;
        }

        if (target instanceof TamableAnimal pet && pet.isTame() && pet.getOwner() == owner){
            return false;
        }
        return super.wantsToAttack(target, owner);
    }

    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent){
        return null;
    }
}
