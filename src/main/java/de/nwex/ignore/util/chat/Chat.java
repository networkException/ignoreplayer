package de.nwex.ignore.util.chat;

import static de.nwex.ignore.util.chat.ChatBuilder.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;

public class Chat {

    public static void toChat(MutableText... texts) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(ChatBuilder.chain(texts));
    }

    public static void toActionBar(MutableText... texts) {
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(ChatBuilder.chain(texts), false);
    }

    public static void print(String prefix, MutableText... texts) {
        toChat(dark("["), accent("ignorePlayer"), dark("] "), base(prefix), accent(" > "), ChatBuilder.chain(texts));
    }

    public static void print(MutableText... texts) {
        print("Log", texts);
    }

    public static void announce(String prefix, MutableText... texts) {
        toActionBar(dark("["), accent("ignorePlayer"), dark("] "), base(prefix), accent(" > "), ChatBuilder.chain(texts));
    }

    public static void announce(MutableText... texts) {
        announce("Log", texts);
    }

    public static void warn(String prefix, MutableText... texts) {
        toChat(dark("["), accent("ignorePlayer"), dark("] "), ChatBuilder.warn(prefix), accent(" > "),
            ChatBuilder.chain(texts));
    }

    public static void warn(MutableText... texts) {
        warn("Warn", texts);
    }

    public static void error(String prefix, MutableText... texts) {
        toChat(dark("["), accent("ignorePlayer"), dark("] "), ChatBuilder.error(prefix), accent(" > "),
            ChatBuilder.chain(texts));
    }

    public static void error(MutableText... texts) {
        error("Error", texts);
    }

    public static void send(String text) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendChatMessage(text);
        }
    }
}
