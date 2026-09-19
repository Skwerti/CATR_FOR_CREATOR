package com.slepa01.catr.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class GlowConditionChecker {

    /**
     * Проверяет, смотрит ли игрок на плоскую текстуру предмета (билборд), центр которой
     * находится в {@code center}. Текстура считается прямоугольником с полуразмерами
     * {@code halfWidth} и {@code halfHeight}, лежащим в плоскости, перпендикулярной
     * направлению взгляда. Это точнее, чем проверка по сфере/цилиндру: свечение
     * срабатывает только когда луч взгляда действительно пересекает плоскость предмета
     * внутри его видимых границ.
     *
     * @param center     мировая координата центра текстуры предмета
     * @param halfWidth  половина ширины текстуры (в блоках) по горизонтали экрана
     * @param halfHeight половина высоты текстуры (в блоках) по вертикали экрана
     */
    public static boolean isPlayerLookingAtBillboard(Vec3 center, double halfWidth, double halfHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;
        if (player == null || level == null) return false;

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F).normalize();
        double reach = 5.0D;

        // Параметр пересечения луча (start + look*t) с плоскостью билборда,
        // проходящей через center перпендикулярно взгляду.
        Vec3 toCenter = center.subtract(start);
        double t = toCenter.dot(look);
        if (t < 0 || t > reach) return false;

        Vec3 hit = start.add(look.scale(t));

        // Проверка на то, что между глазами игрока и плоскостью предмета нет
        // препятствующих блоков (свечение и подсказка не должны пробиваться сквозь стены).
        if (isOccluded(level, start, hit)) return false;

        // Локальные оси билборда: вправо и вверх относительно камеры.
        Vec3 worldUp = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = look.cross(worldUp);
        if (right.lengthSqr() < 1.0E-6D) {
            // Взгляд строго вертикален — берём другую ортогональную ось.
            right = look.cross(new Vec3(1.0D, 0.0D, 0.0D));
        }
        right = right.normalize();
        Vec3 up = right.cross(look).normalize();

        Vec3 offset = hit.subtract(center);
        double dx = offset.dot(right);
        double dy = offset.dot(up);

        return dx * dx <= halfWidth * halfWidth && dy * dy <= halfHeight * halfHeight;
    }

    /**
     * Проверяет, перекрывает ли какой-либо твёрдый блок прямую видимости между
     * двумя точками. Используется, чтобы свечение и информация о предмете не были
     * видны сквозь стены и другие препятствия.
     */
    private static boolean isOccluded(Level level, Vec3 from, Vec3 to) {
        var context = new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        );
        BlockHitResult result = level.clip(context);
        return result != null && result.getLocation() != null
                && result.getLocation().distanceToSqr(from) < from.distanceToSqr(to) - 1.0E-4D;
    }

    /**
     * Проверяет, смотрит ли игрок на заданную точку мира (с заданным радиусом).
     * Оставлено для совместимости: трактует радиус как полуразмер квадратной текстуры.
     *
     * @param point  точка в мировых координатах
     * @param radius радиус (половина стороны квадратной текстуры) для срабатывания
     */
    public static boolean isPlayerLookingAtPoint(Vec3 point, double radius) {
        return isPlayerLookingAtBillboard(point, radius, radius);
    }
}
