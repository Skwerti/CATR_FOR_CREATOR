package catr.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerRenderer;
import com.slepa01.catr.client.render.ItemGlowRenderer;
import com.slepa01.catr.client.render.ItemInfoWidget;
import com.slepa01.catr.client.util.DeployerLookState;
import com.slepa01.catr.client.util.GlowConditionChecker;
import com.slepa01.catr.items.ModItems;
import com.slepa01.catr.items.ModItemsGen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = DeployerRenderer.class, remap = false)
public class DeployerRendererMixin {

    private static final Map<Item, Integer> GLOW_COLORS = new HashMap<>();
    // Толщина свечения (относительное расширение модели) для каждого предмета.
    // Чем больше значение, тем толще ореол. Можно задавать индивидуально под предмет.
    private static final Map<Item, Float> GLOW_THICKNESS = new HashMap<>();
    private static final float DEFAULT_GLOW_THICKNESS = 0.12F;

    // Итоговый масштаб текстуры предмета в руке deployer.
    // Применяется в мировом пространстве (с учётом родительского scale в DeployerRenderer),
    // поэтому значение 0.7 действительно даёт размер 0.7 от базового, а не 0.7 * 0.5.
    // 1.0 — без изменений; <1 — меньше; >1 — больше.
    private static final Map<Item, Float> GLOW_ITEM_SCALE = new HashMap<>();
    private static final float DEFAULT_ITEM_SCALE = 1.0F;

    // Вертикальное смещение текстуры предмета в руке deployer (в мировых блоках).
    // Применяется в мировом пространстве — работает одинаково при любом направлении deployer
    // и не зависит от поворотов/масштабов в стеке.
    // >0 — вверх, <0 — вниз.
    private static final Map<Item, Float> GLOW_ITEM_Y_OFFSET = new HashMap<>();
    private static final float DEFAULT_ITEM_Y_OFFSET = 0.0F;

    // Размер области наведения (половина ширины/высоты текстуры в блоках).
    // Задаётся индивидуально для каждого предмета; при отсутствии берётся значение по умолчанию.
    private static final Map<Item, Float> GLOW_HIT_HALF_WIDTH = new HashMap<>();
    private static final Map<Item, Float> GLOW_HIT_HALF_HEIGHT = new HashMap<>();
    private static final float DEFAULT_HIT_HALF_WIDTH = 0.18F;  // половина ширины (по горизонтали экрана)
    private static final float DEFAULT_HIT_HALF_HEIGHT = 0.22F; // половина высоты (по вертикали экрана)

    // Смещение центра области наведения от центра блока (в блоках), индивидуально для каждого предмета.
    // UP   — смещение вверх/вниз. FORWARD — смещение вперёд по направлению deployer
    //        (применяется только для горизонтальных направлений).
    private static final Map<Item, Float> GLOW_HIT_OFFSET_UP = new HashMap<>();
    private static final Map<Item, Float> GLOW_HIT_OFFSET_FORWARD = new HashMap<>();
    private static final float DEFAULT_HIT_OFFSET_UP = -0.8F;       // смещение вверх от центра блока
    private static final float DEFAULT_HIT_OFFSET_FORWARD = 0.5F;   // смещение вперёд по направлению deployer

    // Цвет линий рамки виджета информации (формат 0xRRGGBB, с альфа-каналом 0xFF).
    // Задаётся индивидуально для каждого предмета; иначе используется значение по умолчанию.
    private static final Map<Item, Integer> GLOW_INFO_BORDER_COLOR = new HashMap<>();
    private static final int DEFAULT_INFO_BORDER_COLOR = 0xFF444444;

    // Прозрачность чёрного фона виджета информации (0 — прозрачный, 255 — непрозрачный),
    // индивидуально для каждого предмета; иначе используется значение по умолчанию.
    private static final Map<Item, Integer> GLOW_INFO_BG_ALPHA = new HashMap<>();
    private static final int DEFAULT_INFO_BG_ALPHA = 255;

    // Поля для хранения текущего состояния
    @Unique
    private BlockPos catr$currentBlockPos = null;
    @Unique
    private Direction catr$currentFacing = null;

    static {
        GLOW_COLORS.put(ModItems.IRON_DRILL_BIT.get(), 0xFFFFFF);
        GLOW_COLORS.put(ModItemsGen.DIAMOND_DRILL_BIT.get(), 0x00FFFF);
        GLOW_COLORS.put(ModItemsGen.STEEL_DRILL_BIT.get(), 0x574A4A);

// Толщина свечения под каждый предмет (при необходимости поправьте под размер модели)
        GLOW_THICKNESS.put(ModItems.IRON_DRILL_BIT.get(), 0.011F);
        GLOW_THICKNESS.put(ModItemsGen.DIAMOND_DRILL_BIT.get(), 0.011F);
        GLOW_THICKNESS.put(ModItemsGen.STEEL_DRILL_BIT.get(), 0.011F);

        // Итоговый размер текстуры предмета в мире (для трёх буров: уменьшаем до 0.7).
        GLOW_ITEM_SCALE.put(ModItems.IRON_DRILL_BIT.get(), 0.6F);
        GLOW_ITEM_SCALE.put(ModItemsGen.DIAMOND_DRILL_BIT.get(), 0.6F);
        GLOW_ITEM_SCALE.put(ModItemsGen.STEEL_DRILL_BIT.get(), 0.6F);

        // Смещение текстуры по вертикали в мировых блоках (>0 — вверх, <0 — вниз).
        GLOW_ITEM_Y_OFFSET.put(ModItems.IRON_DRILL_BIT.get(), 0.2F);
        GLOW_ITEM_Y_OFFSET.put(ModItemsGen.DIAMOND_DRILL_BIT.get(), 0.2F);
        GLOW_ITEM_Y_OFFSET.put(ModItemsGen.STEEL_DRILL_BIT.get(), 0.2F);

        // --- Настройки области наведения (размер и позиция) для каждого предмета ---
        // Формат: <предмет>, <значение>. При отсутствии предмета в карте используются
        // значения по умолчанию (DEFAULT_HIT_HALF_WIDTH/HEIGHT и DEFAULT_HIT_OFFSET_UP/FORWARD).
        Item iron = ModItems.IRON_DRILL_BIT.get();
        Item diamond = ModItemsGen.DIAMOND_DRILL_BIT.get();
        Item steel = ModItemsGen.STEEL_DRILL_BIT.get();

        // половина ширины / высоты области наведения (блоки)
        GLOW_HIT_HALF_WIDTH.put(iron, 0.05F);
        GLOW_HIT_HALF_WIDTH.put(diamond, 0.05F);
        GLOW_HIT_HALF_WIDTH.put(steel, 0.05F);

        GLOW_HIT_HALF_HEIGHT.put(iron, 0.35F);
        GLOW_HIT_HALF_HEIGHT.put(diamond, 0.35F);
        GLOW_HIT_HALF_HEIGHT.put(steel, 0.35F);

        // смещение центра области наведения (блоки): вверх и вперёд
        GLOW_HIT_OFFSET_UP.put(iron, -0.9F);
        GLOW_HIT_OFFSET_UP.put(diamond, -0.9F);
        GLOW_HIT_OFFSET_UP.put(steel, -0.9F);

        GLOW_HIT_OFFSET_FORWARD.put(iron, 0.5F);
        GLOW_HIT_OFFSET_FORWARD.put(diamond, 0.5F);
        GLOW_HIT_OFFSET_FORWARD.put(steel, 0.5F);

        // Цвет линий рамки виджета (0xRRGGBB с альфой 0xFF) для каждого предмета
        GLOW_INFO_BORDER_COLOR.put(iron, 0xFFFFFFFF);    // белый — для железного бура
        GLOW_INFO_BORDER_COLOR.put(diamond, 0xFF00FFFF); // голубой — для алмазного бура
        GLOW_INFO_BORDER_COLOR.put(steel, 0xFF7C6F6F);   // тёмно-красный — для стального бура

        // Прозрачность чёрного фона виджета (0..255) для каждого предмета
        GLOW_INFO_BG_ALPHA.put(iron, 100);
        GLOW_INFO_BG_ALPHA.put(diamond, 100);
        GLOW_INFO_BG_ALPHA.put(steel, 100);
    }

    // Захватываем позицию и направление блока в начале рендера
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void catr$captureBlockData(DeployerBlockEntity be, float partialTicks, PoseStack ms,
                                       MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (be != null) {
            catr$currentBlockPos = be.getBlockPos();
            catr$currentFacing = be.getBlockState().getValue(DeployerBlock.FACING);

            // Фиксируем область взаимодействия (ПКМ) даже для ПУСТОГО деплоера,
            // чтобы в него можно было положить бур. У пустого деплоера предмет не
            // рендерится, поэтому wrapRender не отработает и DeployerLookState не
            // установится — делаем это здесь, пробуя позицию по умолчанию.
            Item held = ((DeployerBlockEntityAccessor) be).getHeldItem().getItem();
            Item probe = held != null ? held : ModItems.IRON_DRILL_BIT.get();
            Vec3 itemPos = calculateItemWorldPos(catr$currentBlockPos, catr$currentFacing, probe);
            float halfWidth = GLOW_HIT_HALF_WIDTH.getOrDefault(probe, DEFAULT_HIT_HALF_WIDTH);
            float halfHeight = GLOW_HIT_HALF_HEIGHT.getOrDefault(probe, DEFAULT_HIT_HALF_HEIGHT);
            if (GlowConditionChecker.isPlayerLookingAtBillboard(itemPos, halfWidth, halfHeight)) {
                DeployerLookState.set(catr$currentBlockPos, probe);
            }
        } else {
            catr$currentBlockPos = null;
            catr$currentFacing = null;
        }
    }

    // Очищаем данные после рендера
    @Inject(method = "renderItem", at = @At("RETURN"))
    private void catr$clearBlockData(DeployerBlockEntity be, float partialTicks, PoseStack ms,
                                     MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        catr$currentBlockPos = null;
        catr$currentFacing = null;
    }

    @WrapOperation(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
            ),
            remap = false
    )
    private void wrapRender(ItemRenderer renderer, ItemStack stack, ItemDisplayContext displayContext,
                            boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                            int light, int overlay, BakedModel model, Operation<Void> original) {

        // Определяем, нужно ли свечение: смотрим, наведён ли взгляд на точку предмета
        Item item = stack.getItem();
        boolean isOurItem = GLOW_COLORS.containsKey(item);
        boolean shouldGlow = false;
        int glowColor = GLOW_COLORS.getOrDefault(item, 0xFFFFFF);
        float glowThickness = GLOW_THICKNESS.getOrDefault(item, DEFAULT_GLOW_THICKNESS);
        float itemScale = GLOW_ITEM_SCALE.getOrDefault(item, DEFAULT_ITEM_SCALE);
        float itemYOffset = GLOW_ITEM_Y_OFFSET.getOrDefault(item, DEFAULT_ITEM_Y_OFFSET);
        if (catr$currentBlockPos != null && catr$currentFacing != null) {
            Vec3 itemPos = calculateItemWorldPos(catr$currentBlockPos, catr$currentFacing, item);
            float halfWidth = GLOW_HIT_HALF_WIDTH.getOrDefault(item, DEFAULT_HIT_HALF_WIDTH);
            float halfHeight = GLOW_HIT_HALF_HEIGHT.getOrDefault(item, DEFAULT_HIT_HALF_HEIGHT);
            // Область наведения растёт/сжимается вместе с предметом — иначе при scale<1
            // свечение будет срабатывать только при наведении в центр уменьшенной текстуры.
            halfWidth *= itemScale;
            halfHeight *= itemScale;
            boolean looking = GlowConditionChecker.isPlayerLookingAtBillboard(itemPos, halfWidth, halfHeight);
            if (looking) {
                // Область взаимодействия (ПКМ) — фиксируем всегда, чтобы бур можно было
                // и положить обратно. Это нужно независимо от того, наш это предмет или нет.
                DeployerLookState.set(catr$currentBlockPos, item);
            }
            if (looking && isOurItem) {
                shouldGlow = true;
                int borderColor = GLOW_INFO_BORDER_COLOR.getOrDefault(item, DEFAULT_INFO_BORDER_COLOR);
                int bgAlpha = GLOW_INFO_BG_ALPHA.getOrDefault(item, DEFAULT_INFO_BG_ALPHA);
                ItemInfoWidget.onLook(stack, itemPos, borderColor, bgAlpha);
            }
        }

        if (isOurItem) {
            // Базовое преобразование применяем ТОЛЬКО к нашим предметам (бурам), иначе
            // обычные предметы деплоера оказываются перевёрнуты лишним поворотом.
            // Единое преобразование для предмета и его свечения, чтобы контур не съезжал.
            // Сюда же входят индивидуальный размер (scale) и смещение по вертикали (yOffset),
            // оба применяются в мировом пространстве с учётом родительского scale.
            poseStack.pushPose();
            ItemGlowRenderer.applyBaseTransform(poseStack, itemScale, itemYOffset);

            // 1. Рендер свечения (только при наведении).
            if (shouldGlow) {
                ItemGlowRenderer.renderGlow(
                        renderer, stack, displayContext, leftHand, poseStack, buffer,
                        overlay, model, glowThickness, glowColor
                );
            }

            // 2. Основная текстура предмета — рендерим в той же системе координат.
            original.call(renderer, stack, displayContext, leftHand, poseStack, buffer, light, overlay, model);
            poseStack.popPose();
        } else {
            // Для всех остальных предметов рендерим как есть, без лишнего поворота.
            original.call(renderer, stack, displayContext, leftHand, poseStack, buffer, light, overlay, model);
        }
    }

    /**
     * Вычисляет мировую координату центра области наведения предмета на основе позиции
     * блока, его ориентации и настраиваемых для предмета смещений
     * ( {@link #GLOW_HIT_OFFSET_UP} / {@link #GLOW_HIT_OFFSET_FORWARD} ).
     */
    @Unique
    private Vec3 calculateItemWorldPos(BlockPos pos, Direction facing, Item item) {
        Vec3 center = Vec3.atCenterOf(pos);

        float offsetUp = GLOW_HIT_OFFSET_UP.getOrDefault(item, DEFAULT_HIT_OFFSET_UP);
        float offsetForward = GLOW_HIT_OFFSET_FORWARD.getOrDefault(item, DEFAULT_HIT_OFFSET_FORWARD);

        // Вертикальное смещение (вверх)
        Vec3 upOffset = new Vec3(0, offsetUp, 0);

        // Горизонтальное смещение вперёд, если deployer направлен горизонтально
        Vec3 forwardOffset = Vec3.ZERO;
        if (facing.getAxis() != Direction.Axis.Y) {
            Vec3 forward = new Vec3(facing.getStepX(), 0, facing.getStepZ()).normalize();
            forwardOffset = forward.scale(offsetForward);
        }
        // Если deployer направлен вертикально, предмет, скорее всего, просто сверху (смещение вверх уже учтено)

        return center.add(upOffset).add(forwardOffset);
    }
}
