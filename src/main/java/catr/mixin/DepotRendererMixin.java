package catr.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.slepa01.catr.client.render.ItemGlowRenderer;
import com.slepa01.catr.client.render.ItemInfoWidget;
import com.slepa01.catr.client.util.GlowConditionChecker;
import com.slepa01.catr.items.ModItems;
import com.slepa01.catr.items.ModItemsGen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Добавляет свечение и информационный виджет (только название) для корпусов (corpus_*),
 * которые лежат в Depot из Create. Логика зеркалирует {@link DeployerRendererMixin},
 * но виджет вызывается через {@link ItemInfoWidget#onLookSimple} (без рамок и прочности).
 *
 * <p>Цвет/толщина свечения — белый тонкий ореол, единый для всех корпусов; задаётся
 * в {@link #GLOW_COLORS} и {@link #GLOW_THICKNESS}.
 */
@Mixin(value = DepotRenderer.class, remap = false)
public class DepotRendererMixin {

    /** Цвет свечения для корпусов (0xRRGGBB). Белый по умолчанию. */
    @Unique
    private static final Map<Item, Integer> GLOW_COLORS = new HashMap<>();
    /** Толщина свечения (радиус ореола в мировых единицах). */
    @Unique
    private static final Map<Item, Float> GLOW_THICKNESS = new HashMap<>();
    @Unique
    private static final float DEFAULT_GLOW_THICKNESS = 0.012F;

    // Полуразмеры билборда для попадания мышкой (в блоках).
    @Unique
    private static final Map<Item, Float> GLOW_HIT_HALF_WIDTH = new HashMap<>();
    @Unique
    private static final Map<Item, Float> GLOW_HIT_HALF_HEIGHT = new HashMap<>();
    @Unique
    private static final float DEFAULT_HIT_HALF_WIDTH = 0.22F;
    @Unique
    private static final float DEFAULT_HIT_HALF_HEIGHT = 0.22F;

    /** Смещение центра билборда от центра блока депо (вверх, в блоках). */
    @Unique
    private static final Map<Item, Float> GLOW_HIT_OFFSET_UP = new HashMap<>();
    @Unique
    private static final float DEFAULT_HIT_OFFSET_UP = 0.5F;

    // Состояние текущего блока, чтобы знать BlockPos внутри wrap'нутого ItemRenderer.render.
    // Хранится в статическом поле, потому что renderItem в DepotRenderer — статический метод,
    // и Mixin запрещает инжектить в статический target не-статические callback'и.
    @Unique
    private static BlockPos catr$currentBlockPos = null;

    static {
        // Заполняем настройки для всех корпусов. CORPUS_* — это id вида "corpus_xxx".
        // Цвет — белый, толщина — 0.012, без смещения по Y.
        // Размер билборда — 0.22×0.22 блока, смещение вверх — 0.5 блока от центра депо.
        for (Item item : collectCorpusItems()) {
            GLOW_COLORS.put(item, 0xFFFFFF);
            GLOW_THICKNESS.put(item, 0.012F);
            GLOW_HIT_HALF_WIDTH.put(item, 0.22F);
            GLOW_HIT_HALF_HEIGHT.put(item, 0.22F);
            GLOW_HIT_OFFSET_UP.put(item, 0.5F);
        }
    }

    /**
     * Собирает все предметы, чей реестровый id начинается на {@code corpus_}.
     * Корпуса регистрируются и в {@link ModItems}, и в {@link ModItemsGen}.
     */
    @Unique
    private static Set<Item> collectCorpusItems() {
        Set<Item> result = new HashSet<>();
        // Перебираем все зарегистрированные предметы мода и фильтруем по id.
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            if (key == null) continue;
            String path = key.getPath();
            if (path.startsWith("corpus_")) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Перехватываем вызов {@code ItemRenderer.render} внутри
     * {@link DepotRenderer#renderItem(PoseStack, MultiBufferSource, int, int, ItemStack, int, java.util.Random, Vec3, boolean)}.
     * Параметр {@code center} (Vec3) нужен только для хука {@link #catr$captureBlockData},
     * здесь он недоступен — поэтому сохраняем BlockPos отдельно в {@link #catr$captureBlockData}.
     */
    @Inject(method = "renderItem", at = @At("HEAD"), remap = false)
    private static void catr$captureBlockData(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay,
                                              ItemStack stack, int angle, java.util.Random random, Vec3 center, boolean flat,
                                              CallbackInfo ci) {
        // center — это Vec3.atCenterOf(getBlockPos()) из DepotRenderer.
        catr$currentBlockPos = BlockPos.containing(center);
    }

    @Inject(method = "renderItem", at = @At("RETURN"), remap = false)
    private static void catr$clearBlockData(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay,
                                             ItemStack stack, int angle, java.util.Random random, Vec3 center, boolean flat,
                                             CallbackInfo ci) {
        catr$currentBlockPos = null;
    }

    @WrapOperation(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
            ),
            remap = false
    )
    private static void wrapRender(ItemRenderer renderer, ItemStack stack, ItemDisplayContext displayContext,
                                boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                                int light, int overlay, BakedModel model, Operation<Void> original) {

        Item item = stack.getItem();
        boolean isCorpus = GLOW_COLORS.containsKey(item);
        boolean shouldGlow = false;
        float glowThickness = DEFAULT_GLOW_THICKNESS;
        int glowColor = 0xFFFFFF;

        if (isCorpus && catr$currentBlockPos != null) {
            glowThickness = GLOW_THICKNESS.getOrDefault(item, DEFAULT_GLOW_THICKNESS);
            glowColor = GLOW_COLORS.get(item);

            Vec3 itemPos = calculateBillboardWorldPos(catr$currentBlockPos, item);
            float halfWidth = GLOW_HIT_HALF_WIDTH.getOrDefault(item, DEFAULT_HIT_HALF_WIDTH);
            float halfHeight = GLOW_HIT_HALF_HEIGHT.getOrDefault(item, DEFAULT_HIT_HALF_HEIGHT);
            boolean looking = GlowConditionChecker.isPlayerLookingAtBillboard(itemPos, halfWidth, halfHeight);
            if (looking) {
                shouldGlow = true;
                // Упрощённый виджет: только название, без рамок и без строки прочности.
                ItemInfoWidget.onLookSimple(stack, itemPos);
            }
        }

        if (isCorpus) {
            // 1. Рендер свечения (только при наведении).
            if (shouldGlow) {
                ItemGlowRenderer.renderGlow(
                        renderer, stack, displayContext, leftHand, poseStack, buffer,
                        overlay, model, glowThickness, glowColor
                );
            }

            // 2. Основная текстура предмета.
            original.call(renderer, stack, displayContext, leftHand, poseStack, buffer, light, overlay, model);
        } else {
            original.call(renderer, stack, displayContext, leftHand, poseStack, buffer, light, overlay, model);
        }
    }

    /**
     * Мировая координата центра билборда предмета в депо.
     * Использует позицию блока и вертикальное смещение, заданное для предмета.
     */
    @Unique
    private static Vec3 calculateBillboardWorldPos(BlockPos pos, Item item) {
        float offsetUp = GLOW_HIT_OFFSET_UP.getOrDefault(item, DEFAULT_HIT_OFFSET_UP);
        return Vec3.atCenterOf(pos).add(0.0D, offsetUp, 0.0D);
    }
}