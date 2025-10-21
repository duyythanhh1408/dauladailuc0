package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * Hệ thống Hồn Hoàn (Spirit Ring System)
 * - Gắn liền với cấp độ hồn lực (1–100)
 * - Xác định khả năng hấp thụ hồn hoàn theo màu và cấp.
 * - Tương thích với song sinh / tam sinh võ hồn.
 */

public class SpiritRingSystem {

    public enum RingColor {
        WHITE, YELLOW, PURPLE, BLACK, RED, GOD
    }

    /** Cấu trúc dữ liệu Hồn Hoàn */
    public static class SpiritRing {
        public final RingColor color;
        public final int age; // tuổi hồn thú
        public final String skillName;
        public final String description;

        public SpiritRing(RingColor color, int age, String skillName, String description) {
            this.color = color;
            this.age = age;
            this.skillName = skillName;
            this.description = description;
        }
    }

    /** Bảng tỉ lệ hấp thụ hồn hoàn theo cấp độ */
    private static final Map<RingColor, int[]> BASE_RATE = new HashMap<>();

    static {
        // Mức hấp thụ cơ bản theo cấp
        BASE_RATE.put(RingColor.WHITE, new int[]{1, 20, 100});
        BASE_RATE.put(RingColor.YELLOW, new int[]{1, 30, 100});
        BASE_RATE.put(RingColor.PURPLE, new int[]{30, 50, 100});
        BASE_RATE.put(RingColor.BLACK, new int[]{50, 60, 100});
        BASE_RATE.put(RingColor.RED, new int[]{70, 90, 80}); // giảm tỉ lệ
        BASE_RATE.put(RingColor.GOD, new int[]{99, 100, 50}); // cực khó
    }

    /** Cấu trúc dữ liệu cho người chơi */
    private static final Map<UUID, List<SpiritRing>> PLAYER_RINGS = new HashMap<>();

    /**
     * Thử hấp thụ hồn hoàn
     * @param player người chơi
     * @param color màu hồn hoàn
     * @param level cấp độ hồn lực
     * @param numSpirit number of spirit (1/2/3)
     */
    public static void absorbRing(Player player, RingColor color, int level, int numSpirit) {
        int baseRate = getBaseRate(color, level);
        double bonus = 0;

        // nếu có 2 hoặc 3 võ hồn → buff hấp thụ hồn hoàn cao cấp
        if (numSpirit == 2 && level >= 90) bonus = 15;
        if (numSpirit == 3 && level >= 90) bonus = 30;

        int finalRate = (int) Math.min(100, baseRate + bonus);
        int roll = new Random().nextInt(100) + 1;

        if (roll <= finalRate) {
            addRing(player, new SpiritRing(color, estimateAge(color), randomSkill(color),
                    "Hấp thụ thành công hồn hoàn " + color + " (" + finalRate + "%)"));
            player.sendSystemMessage(Component.literal("✅ Thành công hấp thụ hồn hoàn " + color + " (" + finalRate + "%)"));
        } else {
            player.sendSystemMessage(Component.literal("❌ Thất bại hấp thụ hồn hoàn (" + roll + "/" + finalRate + "%)"));
        }
    }

    /** Thêm hồn hoàn vào người chơi */
    private static void addRing(Player player, SpiritRing ring) {
        UUID id = player.getUUID();
        PLAYER_RINGS.computeIfAbsent(id, k -> new ArrayList<>()).add(ring);
    }

    /** Tính tỉ lệ cơ bản theo màu và cấp */
    private static int getBaseRate(RingColor color, int level) {
        int[] data = BASE_RATE.get(color);
        if (data == null) return 0;

        if (level < data[0]) return 0;
        if (level >= data[1]) return data[2];

        // tính tỉ lệ tuyến tính theo cấp
        double ratio = (double)(level - data[0]) / (data[1] - data[0]);
        return (int)(ratio * data[2]);
    }

    /** Ước lượng tuổi hồn thú tương ứng màu */
    private static int estimateAge(RingColor color) {
        return switch (color) {
            case WHITE -> 100;
            case YELLOW -> 1000;
            case PURPLE -> 10000;
            case BLACK -> 50000;
            case RED -> 100000;
            case GOD -> 1000000;
        };
    }

    /** Tạo kỹ năng ngẫu nhiên theo màu */
    private static String randomSkill(RingColor color) {
        return switch (color) {
            case WHITE -> "Đánh mạnh +5% sát thương vật lý";
            case YELLOW -> "Tăng tốc độ hồi hồn lực +10%";
            case PURPLE -> "Tạo lớp khiên bảo vệ 10s";
            case BLACK -> "Sát thương bạo kích +20%";
            case RED -> "Võ Hồn Bộc Phát – tăng 50% tất cả chỉ số 10s";
            case GOD -> "Thần kỹ – biến thân trong 30s";
        };
    }

    /** Hiển thị danh sách hồn hoàn người chơi */
    public static void showPlayerRings(Player player) {
        UUID id = player.getUUID();
        List<SpiritRing> rings = PLAYER_RINGS.getOrDefault(id, new ArrayList<>());
        if (rings.isEmpty()) {
            player.sendSystemMessage(Component.literal("⚪ Bạn chưa có hồn hoàn nào."));
            return;
        }

        player.sendSystemMessage(Component.literal("💀 Danh sách hồn hoàn:"));
        for (int i = 0; i < rings.size(); i++) {
            SpiritRing r = rings.get(i);
            player.sendSystemMessage(Component.literal((i+1) + ". " + r.color + " – " + r.skillName + " (" + r.age + " năm)"));
        }
    }
}
