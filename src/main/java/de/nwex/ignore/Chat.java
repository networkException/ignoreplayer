package de.nwex.ignore;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class Chat
{
    public static void print(Text prefix, Text message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(IgnorePlayer.DARK))
            .append(new LiteralText("ignorePlayer").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText("] ").formatted(IgnorePlayer.DARK))
            .append(prefix.formatted(IgnorePlayer.BASE))
            .append(new LiteralText(" > ").formatted(IgnorePlayer.ACCENT))
            .append(message.formatted(IgnorePlayer.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void print(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(IgnorePlayer.DARK))
            .append(new LiteralText("ignorePlayer").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText("] ").formatted(IgnorePlayer.DARK))
            .append(new LiteralText(prefix).formatted(IgnorePlayer.BASE))
            .append(new LiteralText(" > ").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText(message).formatted(IgnorePlayer.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void print(String message)
    {
        print("Log", message);
    }

    public static void warn(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(IgnorePlayer.DARK))
            .append(new LiteralText("ignorePlayer").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText("] ").formatted(IgnorePlayer.DARK))
            .append(new LiteralText(prefix).formatted(IgnorePlayer.WARN))
            .append(new LiteralText(" > ").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText(message).formatted(IgnorePlayer.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void warn(String message)
    {
        print("Warn", message);
    }

    public static void error(Text prefix, Text message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(IgnorePlayer.DARK))
            .append(new LiteralText("ignorePlayer").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText("] ").formatted(IgnorePlayer.DARK))
            .append(prefix.formatted(IgnorePlayer.ERROR))
            .append(new LiteralText(" > ").formatted(IgnorePlayer.ACCENT))
            .append(message.formatted(IgnorePlayer.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void error(String prefix, String message)
    {
        Text chat = new LiteralText("")
            .append(new LiteralText("[").formatted(IgnorePlayer.DARK))
            .append(new LiteralText("ignorePlayer").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText("] ").formatted(IgnorePlayer.DARK))
            .append(new LiteralText(prefix).formatted(IgnorePlayer.ERROR))
            .append(new LiteralText(" > ").formatted(IgnorePlayer.ACCENT))
            .append(new LiteralText(message).formatted(IgnorePlayer.BASE));

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chat);
    }

    public static void error(String message)
    {
        print("Error", message);
    }
}
