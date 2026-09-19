package com.slepa01.catr.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Утилита для рендера «свечения-контура» предмета.
 *
 * <p>Главная проблема предыдущей реализации — свечение рисовалось с собственным
 * преобразованием (другой поворот/смещение), из-за чего контур «съезжал» относительно
 * предмета. Здесь базовое преобразование положения/ориентации предмета
 * ({@link #applyBaseTransform}) используется ОДИНАКОВО и для основного рендера, и для
 * свечения. Толщина свечения задаётся равномерным расширением модели
 * (параметр {@code thickness}), поэтому контур всегда «облегает» предмет и не смещается.
 */
public class ItemGlowRenderer {

    public static final RenderType GLOW_RENDER_TYPE = createGlowRenderType();

    private static RenderType createGlowRenderType() {
        return RenderType.create(
                "catr_glow_item",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                        .createCompositeState(false)
        );
    }

    /**
     * Базовое преобразование, задающее положение и ориентацию предмета.
     * Должно применяться идентично как для обычного рендера, так и для свечения,
     * иначе контур свечения будет съезжать относительно предмета.
     */
    public static void applyBaseTransform(PoseStack poseStack) {
        applyBaseTransform(poseStack, 1.0F, 0.0F);
    }

    /**
     * Базовое преобразование с заданным масштабом и вертикальным смещением предмета.
     *
     * <p>К моменту вызова {@code DeployerRenderer} уже применил к {@code poseStack}
     * свои повороты и финальный изотропный {@code scale(s,s,s)} (s ≈ 0.5..0.75).
     * Поэтому:
     * <ul>
     *     <li>Наивный {@code poseStack.scale(0.7)} дал бы 0.7 × 0.5 = 0.35 — слишком мелко.</li>
     *     <li>Наивный {@code poseStack.translate(0, yOffset, 0)} сдвинул бы вбок,
     *         потому что после поворотов Deployer локальный +Y уже не совпадает
     *         с мировым +Y.</li>
     * </ul>
     *
     * <p>Чтобы оба параметра давали предсказуемый визуальный эффект:
     * <ol>
     *     <li>Извлекаем эффективный scale Deployer (длина первого столбца матрицы).</li>
     *     <li>Отменяем его прямой правкой {@code m00, m10, m20} — это «распаковывает»
     *         матрицу до состояния «повороты без постороннего масштаба».</li>
     *     <li>Применяем «внутренние» повороты спрайта (XP + ZP) и свой {@code scale}.</li>
     *     <li>Возвращаем scale Deployer обратно — диагональ × {@code sParent}.</li>
     *     <li>{@code yOffset} прибавляем напрямую к {@code m13} (Y-компонента
     *         мировой трансляции). Трансляция в матрице не зависит от поворотов и
     *         масштабов в верхней 3×3 части, поэтому это всегда даёт ровно
     *         {@code yOffset} блоков смещения по мировому Y — вверх или вниз.</li>
     * </ol>
     *
     * @param scale   итоговый масштаб предмета в мире (1.0 — без изменений).
     * @param yOffset смещение по вертикали в мировых блоках (>0 — вверх, <0 — вниз).
     */
    public static void applyBaseTransform(PoseStack poseStack, float scale, float yOffset) {
        Matrix4f m = poseStack.last().pose();

        // 1. Эффективный scale DeployerRenderer = длина первого столбца матрицы
        //    (Deployer применяет изотропный scale последним).
        float sParent = (float) Math.sqrt(m.m00() * m.m00() + m.m10() * m.m10() + m.m20() * m.m20());

        // 2. Отменяем родительский scale через прямую правку первого столбца матрицы.
        if (sParent > 1.0E-6F) {
            float inv = 1.0F / sParent;
            m.m00(m.m00() * inv);
            m.m10(m.m10() * inv);
            m.m20(m.m20() * inv);
        }

        // 3. «Внутренние» повороты спрайта и свой масштаб.
        poseStack.translate(0.0D, 0.0D, -0.3D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0F));
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }

        // 4. Возвращаем родительский scale обратно.
        if (sParent > 1.0E-6F) {
            m.m00(m.m00() * sParent);
            m.m10(m.m10() * sParent);
            m.m20(m.m20() * sParent);
        }

        // 5. Вертикальное смещение в МИРОВОМ пространстве через прямую правку m13.
        //    m13 — Y-компонента столбца трансляции; он не зависит от поворотов и
        //    масштабов в верхней 3×3 части, поэтому любое значение прибавляется к
        //    итоговой мировой Y-координате «как есть» — yOffset блоков вверх/вниз.
        if (yOffset != 0.0F) {
            m.m13(m.m13() + yOffset);
        }
    }

    /**
     * Рисует равномерный ореол вокруг плоского спрайта предмета.
     *
     * @param thickness толщина свечения в мировых единицах (постоянный радиус ореола,
     *                  одинаковый во все стороны — без «раздувания» на концах).
     * @param color     цвет свечения в формате 0xRRGGBB.
     */
    public static void renderGlow(ItemRenderer renderer, ItemStack stack, ItemDisplayContext displayContext,
                                  boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                                  int overlay, BakedModel model, float thickness, int color) {

        VertexConsumer glowConsumer = buffer.getBuffer(GLOW_RENDER_TYPE);

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        // Яркость одного образца. Смещённые копии накладываются аддитивно, поэтому
        // держим её низкой, чтобы ореол не пересвечивался.
        float sampleIntensity = 0.1F;

        // Заполняем диск радиуса thickness «золотым углом» — равномерно, без полос.
        // Каждая копия — сама иконка (с её альфой), поэтому ореол точно повторяет
        // силуэт предмета, а не прямоугольник квада. Смещения — в плоскости спрайта
        // (локальные X/Y), чтобы ореол был равномерным вокруг контура.
        final int SAMPLES = 28;
        final float GOLDEN_ANGLE = 2.399963F;
        for (int i = 0; i < SAMPLES; i++) {
            float t = (i + 0.5F) / SAMPLES;
            float radius = thickness * (float) Math.sqrt(t);
            float angle = i * GOLDEN_ANGLE;
            float ox = (float) Math.cos(angle) * radius;
            float oy = (float) Math.sin(angle) * radius;

            poseStack.pushPose();
            poseStack.translate(ox, oy, 0.0F);
            RenderSystem.setShaderColor(r, g, b, sampleIntensity);
            renderer.render(
                    stack,
                    displayContext,
                    leftHand,
                    poseStack,
                    new MultiBufferSource() {
                        @Override
                        public VertexConsumer getBuffer(RenderType renderType) {
                            return glowConsumer;
                        }
                    },
                    LightTexture.FULL_BRIGHT,
                    overlay,
                    model
            );
            poseStack.popPose();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
