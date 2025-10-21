package com.dauladailuc.system;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import java.util.Random;

/**
 * Hệ thống Hồn Lực (Soul Power System)
 * - Quản lý cấp độ, điểm hồn lực, tỉ lệ hấp thụ hồn hoàn
 * - Liên kết trực tiếp với Võ Hồn và Công Pháp
 */
public class SoulPower {
    private int level;          // cấp hồn lực (1–100)
    private double soulEnergy;  // năng lượng hồn lực hiện tại
    private double regenRate;   // tốc độ hồi phục năng lượng (%)
    private double absorptionRate; // tỉ lệ hấp thụ hồn hoàn (%)

    public SoulPower() {
        this.level = 1;
        this.soulEnergy = 100.0;
        this.regenRate = 1.0;
        this.absorptionRate = 100.0; // mặc định cấp 1-20 hấp thụ trắng/vàng 100%
    }

    /** Cập nhật chỉ số khi tăng cấp **/
    public void levelUp() {
        level++;
        soulEnergy += 50;
        regenRate += 0.2;
        updateAbsorptionRate();
    }

    /** Công thức tính tỉ lệ hấp thụ **/
    private void updateAbsorptionRate() {
        if (level <= 20) absorptionRate = 100;
        else if (level <= 40) absorptionRate = 100; // hấp thụ tím 100%
        else if (level <= 60) absorptionRate = 100; // hấp thụ đen 100%
        else if (level <= 80) absorptionRate = 85;
        else if (level < 90) absorptionRate = 75;
        else absorptionRate = 90 + (level - 90) * 2; // cấp 90+ tăng 2% mỗi cấp
    }

    /** Hấp thụ hồn hoàn (tỉ lệ có thể thay đổi theo võ hồn phụ) **/
    public boolean absorbSoulRing(Player player) {
        Random random = new Random();
        double chance = random.nextDouble() * 100;
        return chance <= absorptionRate;
    }

    /** Hồi phục hồn lực mỗi tick **/
    public void regenerate() {
        soulEnergy = Math.min(soulEnergy + regenRate, getMaxSoulEnergy());
    }

    /** Năng lượng tối đa theo cấp **/
    public double getMaxSoulEnergy() {
        return 100 + (level * 50);
    }

    /** Lưu dữ liệu player **/
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", level);
        tag.putDouble("soulEnergy", soulEnergy);
        tag.putDouble("regenRate", regenRate);
        tag.putDouble("absorptionRate", absorptionRate);
        return tag;
    }

    /** Tải dữ liệu player **/
    public void load(CompoundTag tag) {
        this.level = tag.getInt("level");
        this.soulEnergy = tag.getDouble("soulEnergy");
        this.regenRate = tag.getDouble("regenRate");
        this.absorptionRate = tag.getDouble("absorptionRate");
    }

    // Getters
    public int getLevel() { return level; }
    public double getSoulEnergy() { return soulEnergy; }
    public double getAbsorptionRate() { return absorptionRate; }
}
