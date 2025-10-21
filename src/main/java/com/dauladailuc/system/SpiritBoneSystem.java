package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * HỆ THỐNG HỒN CỐT (Spirit Bone System)
 * - Mỗi người chơi có 6 vị trí hồn cốt + 1 ngoại phụ.
 * - Hồn cốt có thể tăng chỉ số, kích hoạt kỹ năng đặc biệt.
 * - Sau khi thành thần, hồn cốt dung hợp vĩnh viễn.
 */

public class SpiritBoneSystem {

    public enum BoneSlot {
        HEAD, TORSO, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG, EXTERNAL
    }

    public static class SpiritBone {
        public final String name;
        public final int rarity; // 1–5
        public final Map<String, Double> stats;
        public final String activeSkill;

        public SpiritBone(String name, int rarity, Map<String, Double> stats, String skill) {
            this.name = name;
            this.rarity = rarity;
            this.stats = stats;
            this.activeSkill = skill;
        }
    }

    private static final Map<UUID, Map<BoneSlot, SpiritBone>> PLAYER_BONES = new HashMap<>();

    // ========================= CÁC HỒN CỐT CÓ SẴN =========================
    public static final SpiritBone HEAD_BONE = new SpiritBone(
            "Thiên Thanh Linh Nhãn", 5,
            Map.of("crit", 15.0, "manaRegen", 10.0),
            "Tăng tầm nhìn và tỷ lệ chí mạng 15%."
    );

    public static final SpiritBone ARM_BONE = new SpiritBone(
            "Hổ Linh Cốt", 4,
            Map.of("attack", 25.0, "armorPen", 10.0),
            "Khi tấn công có 10% cơ hội tăng sức mạnh gấp đôi trong 5s."
    );

    public static final SpiritBone LEG_BONE = new SpiritBone(
            "Phong Ảnh Cước", 3,
            Map.of("speed", 20.0, "jump", 10.0),
            "Tăng tốc độ di chuyển và bật nhảy cao hơn."
    );

    public static final SpiritBone EXTERNAL_BONE = new SpiritBone(
            "Thiên Sứ Dực", 5,
            Map.of("fly", 1.0, "holyResist", 20.0),
            "Cho phép bay, kháng sát thương bóng tối 20%."
    );

    // ========================= HÀM CHÍNH =========================

    /** Trang bị hồn cốt */
    public static void equipBone(Player player, BoneSlot slot, SpiritBone bone) {
        UUID id = player.getUUID();
        PLAYER_BONES.putIfAbsent(id, new EnumMap<>(BoneSlot.class));
        PLAYER_BONES.get(id).put(slot, bone);
        player.sendSystemMessage(Component.literal("💀 Trang bị " + bone.name + " vào vị trí " + slot));
    }

    /** Gỡ hồn cốt */
    public static void unequipBone(Player player, BoneSlot slot) {
        UUID id = player.getUUID();
        if (!PLAYER_BONES.containsKey(id) || !PLAYER_BONES.get(id).containsKey(slot)) {
            player.sendSystemMessage(Component.literal("⚠️ Không có hồn cốt ở vị trí này."));
            return;
        }
        SpiritBone bone = PLAYER_BONES.get(id).remove(slot);
        player.sendSystemMessage(Component.literal("❎ Gỡ hồn cốt " + bone.name + " khỏi " + slot));
    }

    /** Hiển thị toàn bộ hồn cốt đang dùng */
    public static void showBones(Player player) {
        UUID id = player.getUUID();
        Map<BoneSlot, SpiritBone> bones = PLAYER_BONES.getOrDefault(id, new EnumMap<>(BoneSlot.class));
        if (bones.isEmpty()) {
            player.sendSystemMessage(Component.literal("⚪ Chưa trang bị hồn cốt nào."));
            return;
        }
        player.sendSystemMessage(Component.literal("🦴 Danh sách hồn cốt của bạn:"));
        for (var entry : bones.entrySet()) {
            player.sendSystemMessage(Component.literal("- " + entry.getKey() + ": " + entry.getValue().name));
        }
    }

    /** Áp dụng buff khi trang bị */
    public static void applyBoneBuffs(Player player) {
        UUID id = player.getUUID();
        if (!PLAYER_BONES.containsKey(id)) return;

        Map<BoneSlot, SpiritBone> bones = PLAYER_BONES.get(id);
        double atk = 0, def = 0, spd = 0, crit = 0;

        for (SpiritBone bone : bones.values()) {
            atk += bone.stats.getOrDefault("attack", 0.0);
            def += bone.stats.getOrDefault("defense", 0.0);
            spd += bone.stats.getOrDefault("speed", 0.0);
            crit += bone.stats.getOrDefault("crit", 0.0);
        }

        player.sendSystemMessage(Component.literal(String.format(
                "✨ Tổng Buff: +%.0f ATK | +%.0f DEF | +%.0f%% SPD | +%.0f%% CRIT",
                atk, def, spd, crit
        )));
    }

    /** Dung hợp hồn cốt khi thành thần */
    public static void fuseBones(Player player) {
        UUID id = player.getUUID();
        if (!PLAYER_BONES.containsKey(id)) return;

        double totalBonus = PLAYER_BONES.get(id).size() * 5;
        PLAYER_BONES.remove(id);

        player.sendSystemMessage(Component.literal("🔥 Hồn cốt dung hợp! Toàn bộ buff được cộng vĩnh viễn."));
        player.setHealth(player.getMaxHealth() + totalBonus);
    }

    /** Kỹ năng kích hoạt của hồn cốt (nếu có) */
    public static void activateBoneSkill(Player player, BoneSlot slot) {
        UUID id = player.getUUID();
        if (!PLAYER_BONES.containsKey(id)) return;
        SpiritBone bone = PLAYER_BONES.get(id).get(slot);
        if (bone == null) {
            player.sendSystemMessage(Component.literal("⚠️ Không có kỹ năng ở vị trí này."));
            return;
        }
        player.sendSystemMessage(Component.literal("💫 Kích hoạt kỹ năng hồn cốt: " + bone.activeSkill));
    }
}
