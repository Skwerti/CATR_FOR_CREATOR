package com.slepa01.catr.client.render;

import com.slepa01.catr.CatrMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Vector3f;

/**
 * Небольшой HUD-виджет, показывающий название предмета и его текущую прочность
 * (в процентах). Отображается справа от центра текстуры предмета в мире.
 *
 * <p>Появляется, только если игрок смотрит на предмет дольше {@link #HOLD_TIME_MS}
 * миллисекунд, и исчезает сразу после того, как взгляд уходит с предмета.
 *
 * <p>Состояние «смотрит ли игрок на предмет» обновляется из {@code DeployerRendererMixin}
 * через {@link #onLook(ItemStack, Vec3)} каждый кадр, пока предмет в фокусе.
 */
@EventBusSubscriber(modid = CatrMod.MOD_ID, value = Dist.CLIENT)
public class ItemInfoWidget {

    // Время удержания взгляда перед появлением виджета (мс)
    private static final long HOLD_TIME_MS = 500L;
    // Небольшая задержка «исчезновения» после того, как взгляд ушёл (мс)
    private static final long AWAY_GRACE_MS = 120L;
    // Коэффициент горизонтального смещения виджета от предмета. Смещение задаётся в
    // долях от расстояния игрока до предмета (offset = FACTOR * distance), поэтому
    // визуальный зазор между предметом и виджетом остаётся примерно постоянным при
    // приближении/отдалении — так же, как масштаб самого виджета.
    private static final double WIDGET_OFFSET_RIGHT_FACTOR = 0.12D;
    private static final double WIDGET_OFFSET_RIGHT_MIN = 0.08D; // минимальный отступ (блоки)
    private static final double WIDGET_OFFSET_RIGHT_MAX = 1.0D;  // максимальный отступ (блоки)
    private static final int PADDING = 4;

    // Коэффициент сглаживания положения/масштаба виджета между кадрами (0..1).
    // Убирает дрожание (тряску) при движении игрока, вызванное микро-рассогласованием
    // проекции и расстояния.
    private static final double SMOOTHING = 0.35D;
    private static double smoothX = Double.NaN;
    private static double smoothY = Double.NaN;
    private static double smoothScale = Double.NaN;

    // Плавное появление/исчезновение: текущая прозрачность виджета (0..1) и скорость
    // её изменения между кадрами.
    private static final float FADE_SPEED = 0.15F;
    private static float displayAlpha = 0.0F;

    // Масштаб виджета в зависимости от расстояния игрока до предмета.
    // Размер меняется пропорционально 1/distance (так же, как перспективный размер
    // самой текстуры предмета), поэтому виджет синхронно «приближается/удаляется»
    // вместе с предметом. Ограничения лишь страхуют от вырожденных значений.
    private static final double INFO_BASE_DISTANCE = 3.0D; // расстояние (блоки), при котором scale = 1.0
    private static final float INFO_MIN_SCALE = 0.25F;
    private static final float INFO_MAX_SCALE = 1.0F;

    // Цвет линий рамки по умолчанию (если не задан для предмета)
    public static final int DEFAULT_BORDER_COLOR = 0xFF444444;
    // Прозрачность чёрного фона по умолчанию (0 — полностью прозрачный, 255 — непрозрачный)
    public static final int DEFAULT_BG_ALPHA = 255;

    private static ItemStack hoveredStack = ItemStack.EMPTY;
    private static Vec3 hoveredWorldPos = null;
    private static int hoveredBorderColor = DEFAULT_BORDER_COLOR;
    private static int hoveredBgAlpha = DEFAULT_BG_ALPHA;
    // Упрощённый режим: показывать только название предмета (без рамок и строки прочности).
    private static boolean hoveredSimple = false;
    private static long lookStartTime = -1L;   // когда начали непрерывно смотреть на текущий предмет
    private static long lastLookTime = -1L;    // время последнего кадра, когда смотрели на предмет

    /**
     * Полная версия: вызывается каждый кадр, пока игрок смотрит на предмет
     * (из миксина рендера deployer). Обновляет текущее состояние наведения,
     * цвет рамки, прозрачность фона и таймер.
     *
     * @param borderColor цвет линий рамки виджета для данного предмета (формат 0xRRGGBB, с альфа).
     * @param bgAlpha     прозрачность чёрного фона (0 — прозрачный, 255 — непрозрачный).
     */
    public static void onLook(ItemStack stack, Vec3 worldPos, int borderColor, int bgAlpha) {
        if (stack == null || stack.isEmpty() || worldPos == null) return;

        long now = System.currentTimeMillis();

        boolean sameTarget = !hoveredStack.isEmpty()
                && ItemStack.isSameItemSameComponents(hoveredStack, stack)
                && hoveredWorldPos != null
                && hoveredWorldPos.distanceToSqr(worldPos) < 0.0025D; // ~0.05 блока

        if (!sameTarget) {
            // Новый предмет / новая позиция — сбрасываем таймер удержания.
            hoveredStack = stack.copy();
            hoveredWorldPos = worldPos;
            hoveredBorderColor = borderColor;
            hoveredBgAlpha = bgAlpha;
            hoveredSimple = false;
            lookStartTime = now;
        } else {
            // Тот же предмет — обновляем настройки на случай смены конфигурации.
            hoveredBorderColor = borderColor;
            hoveredBgAlpha = bgAlpha;
            hoveredSimple = false;
        }

        lastLookTime = now;
    }

    /**
     * Упрощённая версия: показывает только название предмета (без линий рамки
     * сверху/снизу и без строки прочности). Используется для предметов без durability
     * (например, корпусов в депо).
     */
    public static void onLookSimple(ItemStack stack, Vec3 worldPos) {
        if (stack == null || stack.isEmpty() || worldPos == null) return;

        long now = System.currentTimeMillis();

        boolean sameTarget = !hoveredStack.isEmpty()
                && ItemStack.isSameItemSameComponents(hoveredStack, stack)
                && hoveredWorldPos != null
                && hoveredWorldPos.distanceToSqr(worldPos) < 0.0025D;

        if (!sameTarget) {
            hoveredStack = stack.copy();
            hoveredWorldPos = worldPos;
            hoveredBorderColor = DEFAULT_BORDER_COLOR;
            hoveredBgAlpha = DEFAULT_BG_ALPHA;
            hoveredSimple = true;
            lookStartTime = now;
        } else {
            hoveredSimple = true;
        }

        lastLookTime = now;
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (hoveredStack.isEmpty() || hoveredWorldPos == null) return;

        long now = System.currentTimeMillis();

        // Цель прозрачности: 1, если смотрим на предмет достаточно долго и взгляд ещё «свежий»,
        // иначе 0 (плавное исчезновение).
        boolean stillLooking = (now - lastLookTime) <= AWAY_GRACE_MS;
        boolean heldLongEnough = (now - lookStartTime) >= HOLD_TIME_MS;
        float targetAlpha = (stillLooking && heldLongEnough) ? 1.0F : 0.0F;

        // Плавно приближаем текущую прозрачность к цели.
        displayAlpha += (targetAlpha - displayAlpha) * FADE_SPEED;
        if (displayAlpha < 0.003F) displayAlpha = 0.0F;

        // Полностью исчезло И взгляд уже отведён — сбрасываем состояние (и сглаживание),
        // чтобы при новом наведении виджет появился «с нуля». Важно: во время начального
        // удержания (до 0.5 с) targetAlpha = 0, но взгляд ещё «свежий» (stillLooking),
        // поэтому сбрасывать нельзя — иначе не накопится таймер появления.
        if (displayAlpha <= 0.0F && !stillLooking) {
            reset();
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();

        // Единый источник для проекции и расстояния — позиция камеры (а не глаз игрока),
        // чтобы избежать рассинхрона и дрожания при движении/бобинге камеры.
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        Vector3f leftV = camera.getLeftVector();
        Vec3 camRight = new Vec3(-leftV.x(), -leftV.y(), -leftV.z()).normalize();

        double distance = camPos.distanceTo(hoveredWorldPos);
        double offset = WIDGET_OFFSET_RIGHT_FACTOR * distance;
        offset = Math.max(WIDGET_OFFSET_RIGHT_MIN, Math.min(WIDGET_OFFSET_RIGHT_MAX, offset));

        Vec3 anchor = hoveredWorldPos.add(camRight.scale(offset));

        double[] screen = projectToScreen(mc, anchor);
        // Если точка позади камеры — мягко гасим, не обрывая резко.
        if (screen == null) {
            displayAlpha += (0.0F - displayAlpha) * FADE_SPEED;
            return;
        }

        // Сглаживание по кадрам, чтобы убрать тряску при движении.
        float targetScale = computeScale(mc, camPos);
        if (Double.isNaN(smoothX)) {
            smoothX = screen[0];
            smoothY = screen[1];
            smoothScale = targetScale;
        } else {
            smoothX += (screen[0] - smoothX) * SMOOTHING;
            smoothY += (screen[1] - smoothY) * SMOOTHING;
            smoothScale += (targetScale - smoothScale) * SMOOTHING;
        }

        drawWidget(gui, mc, smoothX, smoothY, (float) smoothScale, displayAlpha);
    }

    private static int withAlpha(int rgb, float alpha) {
        int a = (int) (alpha * 255.0F) & 0xFF;
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    /**
     * Проецирует мировую координату в экранные пиксели GUI.
     * Возвращает {@code null}, если точка находится позади камеры.
     */
    private static double[] projectToScreen(Minecraft mc, Vec3 worldPos) {
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        Vector3f leftV = camera.getLeftVector();
        Vector3f upV = camera.getUpVector();
        Vector3f fwdV = camera.getLookVector();

        Vec3 relative = worldPos.subtract(camPos);

        // Правая ось экрана = -вектор «влево» камеры.
        double csX = -relative.dot(new Vec3(leftV.x(), leftV.y(), leftV.z()));
        double csY = relative.dot(new Vec3(upV.x(), upV.y(), upV.z()));
        double csZ = relative.dot(new Vec3(fwdV.x(), fwdV.y(), fwdV.z()));

        if (csZ <= 0.01D) return null; // позади камеры

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        double fovDeg = mc.options.fov().get();
        double tanHalfFov = Math.tan(Math.toRadians(fovDeg) / 2.0D);
        double aspect = (double) width / (double) height;

        double ndcX = (csX / csZ) / (tanHalfFov * aspect);
        double ndcY = (csY / csZ) / tanHalfFov;

        double screenX = (ndcX * 0.5D + 0.5D) * width;
        double screenY = (0.5D - ndcY * 0.5D) * height;
        return new double[]{screenX, screenY};
    }

    private static void drawWidget(GuiGraphics gui, Minecraft mc, double centerX, double centerY, float scale, float alpha) {
        Component name = hoveredStack.getHoverName();

        int lineH = mc.font.lineHeight;
        int nameW = mc.font.width(name);
        int boxW;
        int boxH;
        if (hoveredSimple) {
            // Упрощённый режим: только название, без рамок и без строки прочности.
            boxW = nameW + PADDING * 2;
            boxH = lineH + PADDING * 2;
        } else {
            int maxDamage = hoveredStack.getMaxDamage();
            int damage = hoveredStack.getDamageValue();
            int percent = maxDamage > 0
                    ? (int) Math.round((maxDamage - damage) * 100.0D / maxDamage)
                    : 100;
            Component durabilityText = Component.translatable("catr.item_info.durability", percent + "%");
            int durW = mc.font.width(durabilityText);
            boxW = Math.max(nameW, durW) + PADDING * 2;
            boxH = lineH * 2 + PADDING * 2 + 2;
        }

        // Масштаб (уже сглаженный) зависит от расстояния игрока до предмета.
        int scaledW = (int) Math.ceil(boxW * scale);
        int scaledH = (int) Math.ceil(boxH * scale);

        int x = (int) centerX;
        int y = (int) (centerY - scaledH / 2.0D);

        // Не выходим за границы экрана (с учётом масштаба).
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();
        int maxX = screenW - scaledW;
        int maxY = screenH - scaledH;
        if (x > maxX) x = maxX;
        if (x < 0) x = 0;
        if (y > maxY) y = maxY;
        if (y < 0) y = 0;

        gui.pose().pushPose();
        gui.pose().translate(x, y, 0.0D);
        gui.pose().scale(scale, scale, 1.0F);

        // Чёрный фон с настраиваемой прозрачностью, умноженной на общую прозрачность виджета.
        int bgA = (int) ((hoveredBgAlpha & 0xFF) * alpha) & 0xFF;
        int bgColor = bgA << 24;
        gui.fill(0, 0, boxW, boxH, bgColor);

        // Линии рамки (цвет задаётся для каждого предмета) — только в полном режиме,
        // в упрощённом режиме виджет без рамок сверху/снизу.
        if (!hoveredSimple) {
            int borderColor = withAlpha(hoveredBorderColor, alpha);
            gui.fill(0, 0, boxW, 1, borderColor);
            gui.fill(0, boxH - 1, boxW, boxH, borderColor);
        }

        int nameColor = withAlpha(0xFFFFFF, alpha);
        gui.drawString(mc.font, name, PADDING, PADDING, nameColor);

        if (!hoveredSimple) {
            // Строка прочности — только в полном режиме.
            int maxDamage = hoveredStack.getMaxDamage();
            int damage = hoveredStack.getDamageValue();
            int percent = maxDamage > 0
                    ? (int) Math.round((maxDamage - damage) * 100.0D / maxDamage)
                    : 100;
            Component durabilityText = Component.translatable("catr.item_info.durability", percent + "%");
            int durColor = withAlpha(0xAAAAAA, alpha);
            gui.drawString(mc.font, durabilityText, PADDING, PADDING + lineH + 2, durColor);
        }

        gui.pose().popPose();
    }

    /**
     * Вычисляет масштаб виджета по расстоянию от игрока до предмета:
     * ближе — больше, дальше — меньше. Ограничен {@link #INFO_MIN_SCALE}..{@link #INFO_MAX_SCALE}.
     */
    private static float computeScale(Minecraft mc, Vec3 camPos) {
        if (hoveredWorldPos == null) return 1.0F;
        double dist = camPos.distanceTo(hoveredWorldPos);
        if (dist < 0.1D) dist = 0.1D;
        double s = INFO_BASE_DISTANCE / dist;
        return (float) Math.max(INFO_MIN_SCALE, Math.min(INFO_MAX_SCALE, s));
    }

    private static void reset() {
        hoveredStack = ItemStack.EMPTY;
        hoveredWorldPos = null;
        hoveredBorderColor = DEFAULT_BORDER_COLOR;
        hoveredBgAlpha = DEFAULT_BG_ALPHA;
        hoveredSimple = false;
        lookStartTime = -1L;
        lastLookTime = -1L;
        smoothX = Double.NaN;
        smoothY = Double.NaN;
        smoothScale = Double.NaN;
        displayAlpha = 0.0F;
    }
}
