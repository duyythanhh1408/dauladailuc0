package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Hệ thống Công Pháp & Bí Kỹ – Cultivation Manual System
 * Mỗi người chơi có thể luyện tối đa 3 công pháp + 2 bí kỹ.
 * Công pháp giúp tu luyện nhanh hơn, bí kỹ là kỹ năng kích hoạt mạnh tạm thời.
 */

public class CultivationManual {

    // Danh sách công pháp & bí kỹ
    private static final Map<String, ManualData> MANUALS = new HashMap<>();

    static {
        // 🌫️ Quỷ Ảnh Mê Tung – di chuyển né tránh
        MANUALS.put("Quỷ Ảnh Mê Tung", new ManualData("Công pháp", "Di chuyển nhanh, né đòn", 60) {
            @Override
            public void activate(Player player) {
                player.sendSystemMessage(Component.literal("🌫️ Kích hoạt Quỷ Ảnh Mê Tung!"));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 4));
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
            }
        });

        // ✋ Huyền Ngọc Thủ – tăng công tay trần
        MANUALS.put("Huyền Ngọc Thủ", new ManualData("Công pháp", "Cường hóa tay trần, xuyên giáp", 90) {
            @Override
            public void activate(Player player) {
                player.sendSystemMessage(Component.literal("✋ Huyền Ngọc Thủ được kích hoạt!"));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 900, 2));
                player.addEffect(new MobEffectInstance(MobEffects.LIFE_BOOST, 900, 1));
            }
        });

        // 👁️ Tử Cực Ma Đồng – nhìn xuyên ảo ảnh
        MANUALS.put("Tử Cực Ma Đồng", new ManualData("Công pháp", "Nhìn xuyên bóng tối, khói, ảo ảnh", 120) {
            @Override
            public void activate(Player player) {
                player.sendSystemMessage(Component.literal("👁️ Mở Tử Cực Ma Đồng!"));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
            }
        });

        // 💉 Bồ Đề Huyết – bí pháp hy sinh máu để tăng sức mạnh
        MANUALS.put("Bồ Đề Huyết", new ManualData("Bí kỹ", "Hy sinh 30% HP, tăng 100% sát thương", 120) {
            @Override
            public void activate(Player player) {
                player.sendSystemMessage(Component.literal("💉 Bồ Đề Huyết bộc phát!"));
                player.hurt(player.damageSources().magic(), (float)(player.getMaxHealth() * 0.3));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 5));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
            }
        });

        // ⚖️ Quan Âm Hữu Lệ – dồn sức vào một cú đánh duy nhất
        MANUALS.put("Quan Âm Hữu Lệ", new ManualData("Bí kỹ", "Dồn toàn bộ sức mạnh vào một đòn", 180) {
            @Override
            public void activate(Player player) {
                player.sendSystemMessage(Component.literal("⚖️ Thi triển Quan Âm Hữu Lệ!"));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 10));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1000, 1)); // yếu sau đó
            }
        });
    }

    /** Kích hoạt công pháp / bí kỹ */
    public static void activateManual(Player player, String name) {
        ManualData manual = MANUALS.get(name);
        if (manual == null) {
            player.sendSystemMessage(Component.literal("❌ Không tìm thấy công pháp hoặc bí kỹ: " + name));
            return;
        }

        if (manual.isOnCooldown(player)) {
            long remaining = manual.getRemainingCooldown(player);
            player.sendSystemMessage(Component.literal("⌛ Còn " + remaining + "s mới có thể dùng lại."));
        } else {
            manual.triggerCooldown(player);
            manual.activate(player);
        }
    }

    /** Danh sách tất cả công pháp hiện có */
    public static Map<String, ManualData> getManuals() {
        return MANUALS;
    }

    /** Lớp dữ liệu mô tả từng công pháp/bí kỹ */
    public static abstract class ManualData {
        private final String type;
        private final String desc;
        private final int cooldown; // giây
        private final Map<UUID, Long> lastUsed = new HashMap<>();

        public ManualData(String type, String desc, int cooldown) {
            this.type = type;
            this.desc = desc;
            this.cooldown = cooldown;
        }

        public abstract void activate(Player player);

        public boolean isOnCooldown(Player player) {
            UUID id = player.getUUID();
            if (!lastUsed.containsKey(id)) return false;
            long diff = (System.currentTimeMillis() - lastUsed.get(id)) / 1000;
            return diff < cooldown;
        }

        public void triggerCooldown(Player player) {
            lastUsed.put(player.getUUID(), System.currentTimeMillis());
        }

        public long getRemainingCooldown(Player player) {
            UUID id = player.getUUID();
            if (!lastUsed.containsKey(id)) return 0;
            long diff = (System.currentTimeMillis() - lastUsed.get(id)) / 1000;
            return Math.max(0, cooldown - diff);
        }

        public String getType() { return type; }
        public String getDesc() { return desc; }
        public int getCooldown() { return cooldown; }
    }
}
