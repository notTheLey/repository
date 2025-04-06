package org.ley.menu.tools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.ley.menu.MenuBrowser;
import org.ley.menu.templates.simple.SimpleMenu;

import java.util.*;
import java.util.stream.Collectors;

public class MenuCommand implements CommandExecutor, TabCompleter {

    // Color constants
    private static final TextColor PRIMARY = TextColor.color(85, 170, 255);
    private static final TextColor SECONDARY = TextColor.color(85, 255, 255);
    private static final TextColor ACCENT = TextColor.color(0, 175, 255);
    private static final TextColor ERROR = NamedTextColor.DARK_RED;
    private static final TextColor SUCCESS = NamedTextColor.AQUA;
    private static final TextColor INFO = NamedTextColor.DARK_AQUA;
    private static final TextColor DETAIL = NamedTextColor.GRAY;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open":
                return handleOpenCommand(sender, args);
            case "history":
                return handleHistoryCommand(sender, args);
            case "get":
                return handleGetCommand(sender, args);
            case "clear":
                return handleClearCommand(sender);
            case "list":
                return handleListCommand(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleOpenCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, ERROR, "Usage: /templates open <player> <url> [args...]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, ERROR, "Player not found: " + args[1]);
            return true;
        }

        String url = args[2];
        Map<String, String> params = new HashMap<>();

        for (int i = 3; i < args.length; i++) {
            String[] parts = args[i].split("=", 2);
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            } else {
                sendMessage(sender, INFO, "Ignoring invalid argument: " + args[i]);
            }
        }

        String fullUrl = MenuBrowser.buildURL(url, params);
        MenuBrowser.openForPlayer(target, fullUrl);

        Component message = Component.text("Opened templates '", SUCCESS)
                .append(createUrlComponent(fullUrl))
                .append(Component.text("' for " + target.getName(), SUCCESS));

        sendComponent(sender, message);
        return true;
    }

    private boolean handleHistoryCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, ERROR, "Usage: /templates history <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, ERROR, "Player not found: " + args[1]);
            return true;
        }

        List<String> history = MenuBrowser.getPlayerMenuHistory(target);
        if (history.isEmpty()) {
            sendMessage(sender, INFO, target.getName() + " has no templates history.");
            return true;
        }

        Component header = Component.text("\nMenu History for " + target.getName() + " \n", ACCENT)
                .decorate(TextDecoration.UNDERLINED);

        sendComponent(sender, header);

        for (int i = 0; i < history.size(); i++) {
            String entry = history.get(i);
            String baseUrl = MenuBrowser.getUrl(entry);
            Map<String, String> argsMap = MenuBrowser.getArgs(entry);

            Component entryComponent = Component.text()
                    .append(Component.text("[" + (i + 1) + "] ", PRIMARY))
                    .append(createUrlComponent(baseUrl, argsMap))
                    .hoverEvent(HoverEvent.showText(createHistoryHover(baseUrl, argsMap)))
                    .build();

            sendComponent(sender, entryComponent);
        }

        return true;
    }

    private Component createHistoryHover(String baseUrl, Map<String, String> argsMap) {
        Component hoverText = Component.text("URL: ", SECONDARY)
                .append(Component.text(baseUrl, PRIMARY))
                .append(Component.newline());

        if (!argsMap.isEmpty()) {
            hoverText = hoverText.append(Component.text("Arguments:", SECONDARY));

            for (Map.Entry<String, String> entry : argsMap.entrySet()) {
                hoverText = hoverText.append(Component.newline())
                        .append(Component.text("  " + entry.getKey() + ": ", INFO))
                        .append(Component.text(entry.getValue(), DETAIL));
            }
        } else {
            hoverText = hoverText.append(Component.text("No arguments", DETAIL));
        }

        return hoverText;
    }

    private boolean handleGetCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, ERROR, "Usage: /templates get <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, ERROR, "Player not found: " + args[1]);
            return true;
        }

        String currentMenu = MenuBrowser.getPlayerMenu(target);
        if (currentMenu.equals(MenuBrowser.DEFAULT_NO_MENU)) {
            sendMessage(sender, INFO, target.getName() + " doesn't have any templates open.");
            return true;
        }

        String baseUrl = MenuBrowser.getUrl(currentMenu);
        Map<String, String> argsMap = MenuBrowser.getArgs(currentMenu);

        Component message = Component.text()
                .append(Component.text("Current templates for " + target.getName() + ":", ACCENT))
                .append(Component.newline())
                .append(Component.text("URL: ", DETAIL))
                .append(createUrlComponent(baseUrl, argsMap))
                .hoverEvent(HoverEvent.showText(createHistoryHover(baseUrl, argsMap)))
                .build();

        sendComponent(sender, message);
        return true;
    }

    private boolean handleClearCommand(CommandSender sender) {
        if (!sender.hasPermission("ley.admin.templates")) {
            sendMessage(sender, ERROR, "You don't have permission to do that!");
            return true;
        }

        MenuBrowser.unregisterAllMenus();
        sendMessage(sender, SUCCESS, "All templates have been unregistered and can be re-registered.");
        return true;
    }

    private boolean handleListCommand(CommandSender sender) {
        Map<String, SimpleMenu> menus = MenuBrowser.menuMap;
        if (menus.isEmpty()) {
            sendMessage(sender, INFO, "No templates are currently registered.");
            return true;
        }

        Component header = Component.text("\nRegistered Menus\n", ACCENT)
                .decorate(TextDecoration.UNDERLINED);

        sendComponent(sender, header);

        menus.forEach((url, menu) -> {
            String baseUrl = MenuBrowser.getUrl(url);
            Component menuComponent = Component.text()
                    .append(Component.text("• ", SECONDARY))
                    .append(Component.text(baseUrl, PRIMARY))
                    .append(Component.text(" (" + menu.getClass().getSimpleName() + ")", DETAIL))
                    .hoverEvent(HoverEvent.showText(
                            Component.text("Class: ", SECONDARY)
                                    .append(Component.text(menu.getClass().getName(), DETAIL))
                                    .append(Component.newline())
                                    .append(Component.text("Registered URL: ", SECONDARY))
                                    .append(Component.text(url, DETAIL))
                    ))
                    .build();

            sendComponent(sender, menuComponent);
        });

        return true;
    }

    private void sendHelp(CommandSender sender) {
        Component header = Component.text("\nMenu Command Help\n", ACCENT)
                .decorate(TextDecoration.UNDERLINED);

        sendComponent(sender, header);

        sendComponent(sender, createHelpEntry("/templates open <player> <url> [args...]", "Open a templates"));
        sendComponent(sender, createHelpEntry("/templates history <player>", "Show templates history"));
        sendComponent(sender, createHelpEntry("/templates get <player>", "Get current templates"));

        if (sender.hasPermission("templates.admin")) {
            sendComponent(sender, createHelpEntry("/templates reload", "Reload all templates"));
            sendComponent(sender, createHelpEntry("/templates list", "List registered templates"));
        }
    }

    private Component createHelpEntry(String command, String description) {
        return Component.text()
                .append(Component.text(command, PRIMARY))
                .append(Component.text(" - " + description, DETAIL))
                .build();
    }

    private Component createUrlComponent(String url) {
        return createUrlComponent(url, Collections.emptyMap());
    }

    private Component createUrlComponent(String url, Map<String, String> args) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text(url, PRIMARY));

        if (!args.isEmpty()) {
            builder.hoverEvent(HoverEvent.showText(createHistoryHover(url, args)));
        }

        return builder.build();
    }

    private void sendMessage(CommandSender sender, TextColor color, String message) {
        if (sender instanceof Player) {
            sendComponent(sender, Component.text(message, color));
        } else {
            sender.sendMessage(Component.text(message, color));
        }
    }

    private void sendComponent(CommandSender sender, Component component) {
        if (sender instanceof Player) {
            ((Player) sender).sendMessage(component);
        } else {
            sender.sendMessage(component);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("open", "history", "get");
            if (sender.hasPermission("templates.admin")) {
                options = Arrays.asList("open", "history", "get", "clear", "list");
            }
            return options.stream()
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open") ||
                    args[0].equalsIgnoreCase("history") ||
                    args[0].equalsIgnoreCase("get")) {

                return Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("open")) {
            return MenuBrowser.menuMap.keySet().stream()
                    .map(MenuBrowser::getUrl)
                    .distinct()
                    .filter(url -> url.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}