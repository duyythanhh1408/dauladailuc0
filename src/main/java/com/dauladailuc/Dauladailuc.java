package com.dauladailuc;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Dauladailuc.MOD_ID)
public class Dauladailuc {
    public static final String MOD_ID = "dauladailuc";
    private static final Logger LOGGER = LoggerFactory.getLogger(Dauladailuc.class);

    public Dauladailuc() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // TODO: đăng ký item, block, effect tại đây
        modEventBus.addListener(this::setup);

        LOGGER.info("Khởi tạo Đấu La Đại Lục Mod!");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Đấu La Đại Lục: thiết lập hệ thống hồn lực và võ hồn...");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Máy chủ Minecraft đã bắt đầu cùng mod Đấu La Đại Lục!");
    }
}
