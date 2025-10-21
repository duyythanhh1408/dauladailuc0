package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * Hệ thống Lĩnh Vực & Thành Thần (Domain & Godhood System)
 * - Mở khóa lĩnh vực khi người chơi đạt cấp 70+
 * - Khảo hạch Thần Cấp 9 (God Trial)
 * - Biến thân thành Thần, nhận buff vĩnh viễn
 */

public class DomainAndGodhood {

    public enum DomainType {
        ICE, FIRE, LIGHT, DARK, TIME, SPACE, NATURE, DEATH, SEA, ANGEL
    }

    /** Dữ liệu người chơi */
    private static final Map<UUID, DomainType> PLAYER_DOMAIN = new HashMap<>();
    private static final Map<UUID, Integer> GOD_TRIAL_LEVEL = new HashMap<>();
    private static final Set<UUID> GOD_PLAYERS = new HashSet<>();

    /** Mở khóa lĩnh vực */
    public static void unlockDomain(Player player, DomainType domain, int spiritLevel) {
        if (spiritLevel < 70) {
            player.sendSystemMessage(Component.literal("⚠️ Cần đạt cấp 70 hồn lực để lĩnh ngộ lĩnh vực."));
            return;
        }
        UUID id = player.getUUID();
        PLAYER_DOMAIN.put(id, domain);
        player.sendSystemMessage(Component.literal("✨ Bạn đã lĩnh ngộ lĩnh vực: " + domain + "!"));
    }

    /** Bắt đầu khảo hạch thần cấp */
    public static void startGodTrial(Player player) {
        UUID id = player.getUUID();
        if (!PLAYER_DOMAIN.containsKey(id)) {
            player.sendSystemMessage(Component.literal("❌ Bạn cần lĩnh vực trước khi khảo hạch thần."));
            return;
        }
        GOD_TRIAL_LEVEL.put(id, 1);
        player.sendSystemMessage(Component.literal("🌟 Khảo hạch Thần Cấp 1 đã bắt đầu!"));
    }

    /** Nâng cấp khảo hạch */
    public static void progressGodTrial(Player player, boolean success) {
        UUID id = player.getUUID();
        if (!GOD_TRIAL_LEVEL.containsKey(id)) {
            player.sendSystemMessage(Component.literal("⚠️ Bạn chưa bắt đầu khảo hạch."));
            return;
        }

        int current = GOD_TRIAL_LEVEL.get(id);
        if (!success) {
            player.sendSystemMessage(Component.literal("❌ Thất bại khảo hạch cấp " + current + "."));
            return;
        }

        current++;
        if (current <= 9) {
            GOD_TRIAL_LEVEL.put(id, current);
            player.sendSystemMessage(Component.literal("✅ Hoàn thành khảo hạch cấp " + (current - 1) + "!"));
            if (current == 9) {
                player.sendSystemMessage(Component.literal("🔥 Chuẩn bị khảo hạch cuối cùng – Cấp 9!"));
            }
        } else {
            becomeGod(player);
        }
    }

    /** Trở thành Thần */
    private static void becomeGod(Player player) {
        UUID id = player.getUUID();
        GOD_PLAYERS.add(id);
        player.sendSystemMessage(Component.literal("👑 Bạn đã trở thành Thần!"));
        applyGodBuff(player);
    }

    /** Áp dụng buff thần cấp */
    private static void applyGodBuff(Player player) {
        player.sendSystemMessage(Component.literal("🌈 Sức mạnh thần cấp tràn ngập trong bạn."));
        player.setHealth(player.getMaxHealth() + 20); // +20 HP
        player.giveExperiencePoints(5000); // thêm exp
    }

    /** Kích hoạt lĩnh vực */
    public static void activateDomain(Player player) {
        UUID id = player.getUUID();
        DomainType domain = PLAYER_DOMAIN.get(id);
        if (domain == null) {
            player.sendSystemMessage(Component.literal("⚠️ Bạn chưa có lĩnh vực."));
            return;
        }

        String effect = switch (domain) {
            case ICE -> "Đóng băng mọi sinh vật xung quanh trong 5s.";
            case FIRE -> "Thiêu đốt kẻ thù trong phạm vi 10 ô.";
            case LIGHT -> "Tăng tốc độ và hồi máu cho đồng minh.";
            case DARK -> "Ẩn thân và hút sinh lực đối thủ.";
            case TIME -> "Làm chậm mọi sinh vật xung quanh.";
            case SPACE -> "Dịch chuyển ngẫu nhiên quanh chiến trường.";
            case NATURE -> "Hồi phục sinh lực và tăng phòng thủ.";
            case DEATH -> "Tạo sương tử khí gây sát thương liên tục.";
            case SEA -> "Triệu hồi sóng thần quét sạch kẻ địch.";
            case ANGEL -> "Tạo vùng ánh sáng hồi sinh và miễn nhiễm sát thương.";
        };

        player.sendSystemMessage(Component.literal("🌪️ Kích hoạt lĩnh vực: " + domain + " → " + effect));
    }

    /** Kiểm tra trạng thái thần cấp */
    public static boolean isGod(Player player) {
        return GOD_PLAYERS.contains(player.getUUID());
    }

    /** Hiển thị thông tin khảo hạch */
    public static void showStatus(Player player) {
        UUID id = player.getUUID();
        String domain = PLAYER_DOMAIN.containsKey(id) ? PLAYER_DOMAIN.get(id).name() : "Chưa có";
        int trial = GOD_TRIAL_LEVEL.getOrDefault(id, 0);
        boolean isGod = GOD_PLAYERS.contains(id);

        player.sendSystemMessage(Component.literal("💫 Lĩnh vực: " + domain));
        player.sendSystemMessage(Component.literal("⚔️ Cấp khảo hạch: " + trial + "/9"));
        player.sendSystemMessage(Component.literal(isGod ? "👑 Trạng thái: ĐÃ THÀNH THẦN" : "🌀 Trạng thái: Phàm nhân"));
    }
}
