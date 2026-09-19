package com.slepa01.catr.network;

import com.slepa01.catr.CatrMod;
import com.slepa01.catr.items.DrillBits;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.lang.reflect.Field;

public class DeployerSwapPacket implements CustomPacketPayload {

    public static final Type<DeployerSwapPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CatrMod.MOD_ID, "deployer_swap"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeployerSwapPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> pkt.write(buf),
                    buf -> new DeployerSwapPacket(buf)
            );

    private final BlockPos pos;

    public DeployerSwapPacket(BlockPos pos) {
        this.pos = pos;
    }

    public DeployerSwapPacket(RegistryFriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    @Override
    public Type<DeployerSwapPacket> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static void handle(DeployerSwapPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (!(player instanceof ServerPlayer sp)) return;

            ServerLevel level = sp.serverLevel();
            BlockEntity be = level.getBlockEntity(packet.pos);
            if (!(be instanceof DeployerBlockEntity dbe)) return;

            // Текущий предмет деплоера хранится либо в поле heldItem (в покое), либо в руке
            // фейкового игрока (во время обработки). Считаем текущим непустой вариант,
            // отдавая приоритет heldItem (именно его рисует рендерер).
            ItemStack heldField = getDeployerHeldItem(dbe);
            ItemStack fakeHand = dbe.getPlayer() != null ? dbe.getPlayer().getMainHandItem() : ItemStack.EMPTY;
            ItemStack currentItem = !heldField.isEmpty() ? heldField : fakeHand;

            ItemStack hand = sp.getMainHandItem().copy();
            boolean currentDrill = DrillBits.isDrill(currentItem.getItem());
            boolean handDrill = DrillBits.isDrill(hand.getItem());

            // Работаем только с бурами из миксина.
            if (!currentDrill && !handDrill) return;

            ItemStack newDeployer;
            if (handDrill) {
                // Не перезаписываем посторонний (не бур) предмет в деплоере — иначе он
                // пропадёт. Меняем местами только бур на бур или кладём в пустой деплоер.
                if (!currentItem.isEmpty() && !currentDrill) return;
                // Кладём бур из руки в деплоер, а старый (если был) забираем к себе.
                sp.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                newDeployer = hand.copy();
                if (currentDrill) {
                    giveToPlayer(sp, currentItem.copy());
                }
            } else if (hand.isEmpty() && currentDrill) {
                // Забираем бур из деплоера в свободную руку.
                newDeployer = ItemStack.EMPTY;
                sp.setItemInHand(InteractionHand.MAIN_HAND, currentItem.copy());
            } else {
                // В руке посторонний предмет — ничего не делаем, чтобы не «украсть» бур.
                return;
            }

            setDeployerHeldItem(dbe, newDeployer);
            dbe.setChanged();
            dbe.sendData();
        });
    }

    // Устанавливает предмет деплоера напрямую в поле heldItem (через reflection, т.к. Mixin-сеттер
    // для интерфейсного аксессора не записывает поле) и синхронизирует руку фейкового игрока,
    // чтобы отображение и последующая обработка использовали один и тот же предмет.
    private static final Field HELD_ITEM_FIELD;

    static {
        Field f = null;
        try {
            f = DeployerBlockEntity.class.getDeclaredField("heldItem");
            f.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
        HELD_ITEM_FIELD = f;
    }

    private static void setDeployerHeldItem(DeployerBlockEntity be, ItemStack stack) {
        if (HELD_ITEM_FIELD != null) {
            try {
                HELD_ITEM_FIELD.set(be, stack);
            } catch (IllegalAccessException ignored) {
            }
        }
        if (be.getPlayer() != null) {
            be.getPlayer().setItemInHand(InteractionHand.MAIN_HAND, stack);
        }
    }

    private static ItemStack getDeployerHeldItem(DeployerBlockEntity be) {
        if (HELD_ITEM_FIELD != null) {
            try {
                return (ItemStack) HELD_ITEM_FIELD.get(be);
            } catch (IllegalAccessException ignored) {
            }
        }
        return be.getPlayer() != null ? be.getPlayer().getMainHandItem() : ItemStack.EMPTY;
    }

    private static void giveToPlayer(ServerPlayer sp, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (sp.getMainHandItem().isEmpty()) {
            sp.setItemInHand(InteractionHand.MAIN_HAND, stack);
        } else if (!sp.getInventory().add(stack)) {
            sp.drop(stack, false);
        }
    }
}
