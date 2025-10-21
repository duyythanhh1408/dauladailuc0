package com.dauladailuc.system;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import java.util.*;

/**
 * Hệ thống Võ Hồn — Spirit Weapon System
 * Gồm 4 nhánh chính: Thú, Khí, Thực Vật, Biến Dị
 * - Tác động trực tiếp vào chỉ số hồn lực, tấn công, phòng ngự, tốc độ, hấp thụ
 */

public class SpiritWeapon {
    public enum Type { THU, KHI, THUC_VAT, BIEN_DI }
    private Type type;
    private String name;
    private int rarity; // 1-5 sao
    private boolean awakened;
    private List<String> abilities; // Danh sách kỹ năng (hồn kỹ)

    // Các chỉ số cơ bản
    private double atkBonus;
    private double defBonus;
    private double speedBonus;
    private double soulBoost; // tăng tốc độ hồi hồn lực %

    public SpiritWeapon(Type type, String name, int rarity) {
        this.type = type;
        this.name = name;
        this.rarity = rarity;
        this.awakened = false;
        this.abilities = new ArrayList<>();
        initBaseStats();
    }

    /** Khởi tạo chỉ số cơ bản theo loại Võ Hồn **/
    private void initBaseStats() {
        switch (type) {
            case THU -> { // Thú Võ Hồn – tăng toàn diện, thiên về sát thương
                atkBonus = 15 + rarity * 5;
                defBonus = 10 + rarity * 3;
                speedBonus = 5;
                soulBoost = 5;
            }
            case KHI -> { // Khí Võ Hồn – thiên công, sát thương cực mạnh
                atkBonus = 25 + rarity * 10;
                defBonus = 5;
                speedBonus = 3;
                soulBoost = 2;
            }
            case THUC_VAT -> { // Thực Vật – hồi phục và buff đồng đội
                atkBonus = 5;
                defBonus = 15 + rarity * 5;
                speedBonus = 2;
                soulBoost = 10 + rarity * 5;
            }
            case BIEN_DI -> { // Biến Dị – ngẫu nhiên mạnh hoặc yếu
                Random r = new Random();
                atkBonus = 10 + r.nextInt(30);
                defBonus = 10 + r.nextInt(20);
                speedBonus = r.nextInt(10);
                soulBoost = r.nextInt(10);
            }
        }
    }

    /** Thức tỉnh Võ Hồn – mở khóa sức mạnh & kỹ năng **/
    public void awaken(Player player) {
        if (!awakened) {
            awakened = true;
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("⚡ Võ Hồn " + name + " đã thức tỉnh!"));
            applyBuff(player);
        }
    }

    /** Áp dụng buff **/
    public void applyBuff(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, (int)(atkBonus / 10)));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, (int)(defBonus / 10)));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, (int)(speedBonus / 10)));
    }

    /** Kích hoạt kỹ năng đặc biệt theo Võ Hồn **/
    public void activateAbility(Player player, String ability) {
        switch (ability.toLowerCase()) {
            case "hồn kỹ 7":
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("🔥 Kích hoạt Võ Hồn Chân Thân!"));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 5));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 5));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 2));
                break;

            case "loạn phi phong chùy pháp":
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("💥 Thi triển Loạn Phi Phong Chùy Pháp!"));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 3));
                break;

            case "đại tu di chuy tạc hoàn":
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("💢 Đại Tu Di Chùy Tạc Hoàn – Cường hóa tạm thời!"));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 6));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1)); // bị yếu sau đó
                break;

            default:
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Không thể kích hoạt kỹ năng này."));
        }
    }

    /** Lưu dữ liệu **/
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", type.name());
        tag.putString("name", name);
        tag.putInt("rarity", rarity);
        tag.putBoolean("awakened", awakened);
        return tag;
    }

    /** Tải dữ liệu **/
    public void load(CompoundTag tag) {
        this.type = Type.valueOf(tag.getString("type"));
        this.name = tag.getString("name");
        this.rarity = tag.getInt("rarity");
        this.awakened = tag.getBoolean("awakened");
    }

    public Type getType() { return type; }
    public String getName() { return name; }
    public boolean isAwakened() { return awakened; }
    public List<String> getAbilities() { return abilities; }
}
