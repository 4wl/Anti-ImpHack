package mod.imphack.module.modules.combat;


import mod.imphack.module.Category;
import mod.imphack.module.Module;
import mod.imphack.setting.settings.BooleanSetting;
import mod.imphack.setting.settings.IntSetting;
import mod.imphack.util.BlockUtil;
import mod.imphack.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static mod.imphack.util.BlockUtil.faceVectorPacketInstant;

public class Surround extends  Module{
	public Surround() {
		super("Surround", "Places Obsidian Around You", Category.COMBAT);

		addSetting(triggerSurround);
		addSetting(shiftOnly);
		addSetting(rotate);
		addSetting(disableOnJump);
		addSetting(centerPlayer);
		addSetting(tickDelay);
		addSetting(timeOutTicks);
		addSetting(blocksPerTick);
	}

	public final BooleanSetting triggerSurround = new BooleanSetting("trigger", this, false);
	public final BooleanSetting shiftOnly = new BooleanSetting("onShift", this, false);
	public final BooleanSetting rotate = new BooleanSetting("rotate", this, true);
	public final BooleanSetting disableOnJump = new BooleanSetting("offJump", this, false);
	public final BooleanSetting centerPlayer = new BooleanSetting("autoCenter", this, true);
	public final IntSetting tickDelay = new IntSetting("tickDelay", this, 5);
	public final IntSetting timeOutTicks = new IntSetting("timeOutTicks", this, 100);
	public final IntSetting blocksPerTick = new IntSetting("blocksPerTick", this, 4);

	private boolean noObby = false;
    private boolean isSneaking = false;
    private boolean firstRun = false;

    private int oldSlot = -1;

    private int runTimeTicks = 0;
    private int delayTimeTicks = 0;
    private int offsetSteps = 0;
    

    private Vec3d centeredBlock = Vec3d.ZERO;
    
    
    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, ticks));
    }
    
    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }
   
    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            onDisable();
            return;
        }

        if (centerPlayer.isEnabled() && mc.player.onGround) {
        	
           PlayerUtil.centerPlayer(centeredBlock);
        }

        centeredBlock = getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posY);

        oldSlot = mc.player.inventory.currentItem;

        if (findObsidianSlot() != -1) {
            mc.player.inventory.currentItem = findObsidianSlot();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (isSneaking){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        centeredBlock = Vec3d.ZERO;

        noObby = false;
        firstRun = true;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            onDisable();
            return;
        }

        if (mc.player.posY <= 0) {
            return;
        }

        if (firstRun){
            firstRun = false;
            if (findObsidianSlot() == -1 ) {
                noObby = true;
                onDisable();
            }
        }
        else {
            if (delayTimeTicks < tickDelay.getValue()) {
                delayTimeTicks++;
                return;
            }
            else {
                delayTimeTicks = 0;
            }
        }
        
        if (shiftOnly.isEnabled() && !mc.player.isSneaking()) {
            return;
        }

        if (disableOnJump.isEnabled() && !(mc.player.onGround)) {
            return;
        }

        if (centerPlayer.isEnabled() && centeredBlock != Vec3d.ZERO && mc.player.onGround) {

           PlayerUtil.centerPlayer(centeredBlock);
        }

        if (triggerSurround.isEnabled() && runTimeTicks >= timeOutTicks.getValue()) {
            runTimeTicks = 0;
            onDisable();
            return;
        }

        int blocksPlaced = 0;

        while (blocksPlaced <= blocksPerTick.getValue()) {
            Vec3d[] offsetPattern;
            offsetPattern = Surround.Offsets.SURROUND;
            int maxSteps = Surround.Offsets.SURROUND.length;

            if (offsetSteps >= maxSteps){
                offsetSteps = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetSteps]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            boolean tryPlacing = mc.world.getBlockState(targetPos).getMaterial().isReplaceable();

            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityPlayer) {
                    tryPlacing = false;
                    break;
                }
            }

            if (tryPlacing && placeBlock(targetPos)) {
                blocksPlaced++;
            }

            offsetSteps++;

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
        runTimeTicks++;
    }

    private int findObsidianSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++){
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian){
                slot = i;
                break;
            }
        }
        return slot;
    }

    private boolean placeBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        EnumFacing side = BlockUtil.getPlaceableSide(pos);

        if (side == null){
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (BlockUtil.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obsidianSlot = findObsidianSlot();

        if (mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != -1) {

            mc.player.inventory.currentItem = obsidianSlot;
        }

        if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (obsidianSlot == -1) {
            noObby = true;
            return false;
        }

        if (rotate.isEnabled()) {
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }


    private Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {

        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;

        return new Vec3d(newX, newY, newZ);
    }

    private static class Offsets {
        private static final Vec3d[] SURROUND = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1)
        };
    }
}
