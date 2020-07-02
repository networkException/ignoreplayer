package de.nwex.ignore.command;

import static de.nwex.ignore.util.chat.ChatBuilder.*;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nwex.ignore.util.TextUtil;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ClientCommandManager {

    private static final Set<String> clientSideCommands = new HashSet<>();

    public static void clearClientSideCommands() {
        clientSideCommands.clear();
    }

    public static void addClientSideCommand(String name) {
        clientSideCommands.add(name);
    }

    public static boolean isClientSideCommand(String name) {
        return clientSideCommands.contains(name);
    }

    public static void executeCommand(StringReader reader, String command) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        try {
            if (player != null) {
                player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
            }
        } catch (CommandException e) {
            error(e.getTextMessage().asString());
        } catch (CommandSyntaxException e) {
            error(e.getRawMessage().getString());

            if (e.getInput() != null && e.getCursor() >= 0) {
                int cursor = Math.min(e.getCursor(), e.getInput().length());

                LiteralText text = (LiteralText) new LiteralText("").formatted(Formatting.GRAY)
                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));

                if (cursor > 10) {
                    text.append("...");
                }

                text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));

                if (cursor < e.getInput().length()) {
                    text.append(
                        new LiteralText(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
                }

                text.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));

                error(text.asString());
            }
        } catch (Exception exception) {
            error(
                hover(
                    chain(
                        text("Command"),
                        highlight(exception.getClass().getName()),
                        base(": "),
                        highlight(exception.getMessage())
                    ),
                    error(TextUtil.stackTraceToString(exception))
                )
            );
        }
    }
}
