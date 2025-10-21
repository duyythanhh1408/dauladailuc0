package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * HỆ THỐNG ÁM KHÍ ĐƯỜNG MÔN (Tang Sect Weapon System)
 * - Cho phép chế tạo, sử dụng và nâng cấp ám khí.
 * - Gồm 3 loại: Thủ công, Tự động, Đặc chế.
 * - Có thể khảm nạp hồn lực để tăng sức mạnh.
 * - Mỗi ám khí có độ bền, sát thương, kỹ năng riêng.
 */

public class TangSectWeaponSystem {

    public static class HiddenWeapon {
        public final String name;
        public final int tier;
        public double baseDamage;
        public int durability;
        public String type;
        public String abilityDescription;
        public double cooldown;

        public HiddenWeapon(String name, int tier, double damage, int durability, String type, String ability, double cooldown) {
            this.name = name;
            this.tier = tier;
            this.baseDamage = damage;
            this.durability = durability;
            this.type = type;
            this.abilityDescription = ability;
            this.cooldown = cooldown;
        }
    }

    // ========================= DANH SÁCH ÁM KHÍ =========================
    public static final HiddenWeapon TANGLOTUS = new HiddenWeapon(
            "Phật Nộ Đường Liên", 5,
            500.0, 20, "Đặc chế",
            "Gây nổ diện rộng, sát thương cực cao. Dùng 1 lần.",
            60.0
    );

    public static final HiddenWeapon PURPLE_NEEDLE = new HiddenWeapon(
            "Tử Cực Châm", 4,
            200.0, 50, "Tự động",
            "Bắn 10 kim độc liên tiếp, gây hiệu ứng trúng độc.",
            15.0
    );

    public static final HiddenWeapon SILVER_DAGGER = new HiddenWeapon(
            "Lưu Ngân Phiêu", 3,
            150.0, 100, "Thủ công",
            "Dao phi chính xác cao, xuyên giáp nhẹ, hồi 1 viên sau 30s.",
            10.0
    );

    public static final HiddenWeapon PEACOCK_FEATHER = new HiddenWeapon(
            "Khổng Tước Linh Vũ", 4,
            350.0, 30, "Đặc chế",
            "Tạo hiệu ứng lan, mỗi mục tiêu trúng giảm giáp 10%.",
            25.0
    );

    public static final HiddenWeapon DRAGON_RING = new HiddenWeapon(
            "Long Tước Hoàn", 5,
            400.0, 40, "Tự động",
            "Bắn 5 vòng năng lượng, hút kẻ địch lại gần trung tâm.",
            40.0
    );

    // ========================= DANH SÁCH NGƯỜI CHƠI & ÁM KHÍ =========================
    private static final Map<UUID, List<HiddenWeapon>> PLAYER_WEAPONS = new HashMap<>();
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

    /** Nhận ám khí */
    public static void obtainWeapon(Player player, HiddenWeapon weapon) {
        UUID id = player.getUUID();
        PLAYER_WEAPONS.putIfAbsent(id, new ArrayList<>());
        PLAYER_WEAPONS.get(id).add(weapon);
        player.sendSystemMessage(Component.literal("🌀 Nhận được Ám Khí: " + weapon.name + " [" + weapon.type + "]"));
    }

    /** Hiển thị túi ám khí */
    public static void showWeapons(Player player) {
        UUID id = player.getUUID();
        List<HiddenWeapon> list = PLAYER_WEAPONS.getOrDefault(id, new ArrayList<>());
        if (list.isEmpty()) {
            player.sendSystemMessage(Component.literal("⚪ Bạn chưa có ám khí nào."));
            return;
        }

        player.sendSystemMessage(Component.literal("🎯 Danh sách Ám Khí của bạn:"));
        for (HiddenWeapon w : list) {
            player.sendSystemMessage(Component.literal("- " + w.name + " | Damage: " + w.baseDamage + " | Loại: " + w.type));
        }
    }

    /** Kích hoạt ám khí */
    public static void useWeapon(Player player, String weaponName) {
        UUID id = player.getUUID();

        // Cooldown check
        if (COOLDOWNS.containsKey(id) && System.currentTimeMillis() < COOLDOWNS.get(id)) {
            player.sendSystemMessage(Component.literal("⏳ Ám khí đang hồi chiêu!"));
            return;
        }

        List<HiddenWeapon> list = PLAYER_WEAPONS.getOrDefault(id, new ArrayList<>());
        HiddenWeapon weapon = list.stream().filter(w -> w.name.equalsIgnoreCase(weaponName)).findFirst().orElse(null);

        if (weapon == null) {
            player.sendSystemMessage(Component.literal("⚠️ Không tìm thấy ám khí " + weaponName));
            return;
        }

        // Giảm độ bền
        weapon.durability -= 1;
        if (weapon.durability <= 0) {
            player.sendSystemMessage(Component.literal("💥 " + weapon.name + " đã hỏng và biến mất!"));
            list.remove(weapon);
            return;
        }

        // Kích hoạt kỹ năng
        player.sendSystemMessage(Component.literal("💫 Kích hoạt " + weapon.name + ": " + weapon.abilityDescription));
        player.sendSystemMessage(Component.literal("💥 Gây " + weapon.baseDamage + " damage diện rộng!"));

        // Set cooldown
        COOLDOWNS.put(id, System.currentTimeMillis() + (long) (weapon.cooldown * 1000));
    }

    /** Nâng cấp ám khí */
    public static void upgradeWeapon(Player player, String weaponName) {
        UUID id = player.getUUID();
        List<HiddenWeapon> list = PLAYER_WEAPONS.getOrDefault(id, new ArrayList<>());
        HiddenWeapon weapon = list.stream().filter(w -> w.name.equalsIgnoreCase(weaponName)).findFirst().orElse(null);

        if (weapon == null) {
            player.sendSystemMessage(Component.literal("⚠️ Không tìm thấy ám khí để nâng cấp."));
            return;
        }

        weapon.baseDamage *= 1.25;
        weapon.durability += 10;
        player.sendSystemMessage(Component.literal("🔧 Nâng cấp " + weapon.name + "! Damage tăng 25%, độ bền +10."));
    }

    /** Tạo ngẫu nhiên ám khí khi rèn luyện */
    public static HiddenWeapon craftRandomWeapon() {
        List<HiddenWeapon> pool = List.of(TANGLOTUS, PURPLE_NEEDLE, SILVER_DAGGER, PEACOCK_FEATHER, DRAGON_RING);
        return pool.get(new Random().nextInt(pool.size()));
    }
}
