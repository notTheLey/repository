package org.ley.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ley.menu.templates.simple.SimpleMenu;

import java.util.*;
import java.util.stream.Collectors;

public final class MenuBrowser implements Listener {
    public static final String DEFAULT_NO_MENU = "no.templates";
    public static final List<String> DEFAULT_NO_HISTORY = Collections.singletonList("no.history");
    public static final String EMPTY_STRING = "";
    public static boolean ENHANCED_MENUS = false;

    public static PluginManager pm;
    public static JavaPlugin plugin;

    public static Map<String, SimpleMenu> menuMap;
    private static Map<UUID, String> playerMap;
    private static Map<UUID, List<String>> playerHistoryMap;

    static {
        initDefaults();
    }

    private static void initDefaults() {
        menuMap = new HashMap<>();
        playerMap = new HashMap<>();
        playerHistoryMap = new HashMap<>();
    }

    public MenuBrowser(JavaPlugin plugin) {
        MenuBrowser.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        initDefaults();
        pm = Bukkit.getPluginManager();
        pm.registerEvents(this, plugin);
    }

    public MenuBrowser(JavaPlugin plugin, boolean ENHANCED_MENUS) {
        MenuBrowser.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        initDefaults();
        pm = Bukkit.getPluginManager();
        pm.registerEvents(this, plugin);
        MenuBrowser.ENHANCED_MENUS = ENHANCED_MENUS;
    }


    @EventHandler
    public static void onCloseInv(InventoryCloseEvent event){
        if (hasEmptyInventory((Player) event.getPlayer())) {
            playerMap.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event){
        playerMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event){
        playerMap.remove(event.getPlayer().getUniqueId());
    }


    public static String getPlayerMenu(String name) {
        return getPlayerMenuOpt(name).orElse(DEFAULT_NO_MENU);
    }

    public static String getPlayerMenu(Player player) {
        return getPlayerMenuOpt(player).orElse(DEFAULT_NO_MENU);
    }

    public static String getPlayerMenu(UUID uuid) {
        return getPlayerMenuOpt(uuid).orElse(DEFAULT_NO_MENU);
    }

    public static List<String> getPlayerMenuHistory(Player player) {
        return getPlayerMenuHistoryOpt(player).orElse(DEFAULT_NO_HISTORY);
    }

    public static List<String> getPlayerMenuHistory(String name) {
        return getPlayerMenuHistoryOpt(name).orElse(DEFAULT_NO_HISTORY);
    }

    public static List<String> getPlayerMenuHistory(UUID uuid) {
        return getPlayerMenuHistoryOpt(uuid).orElse(DEFAULT_NO_HISTORY);
    }



    public static Optional<String> getPlayerMenuOpt(String name) {
        return Optional.ofNullable(name)
                .map(Bukkit::getPlayer)
                .map(Player::getUniqueId)
                .flatMap(uuid -> Optional.ofNullable(playerMap.get(uuid)));
    }

    public static Optional<String> getPlayerMenuOpt(Player player) {
        return Optional.ofNullable(player)
                .map(Player::getUniqueId)
                .flatMap(uuid -> Optional.ofNullable(playerMap.get(uuid)));
    }

    public static Optional<String> getPlayerMenuOpt(UUID uuid) {
        return Optional.ofNullable(uuid)
                .flatMap(u -> Optional.ofNullable(playerMap.get(u)));
    }


    public static Optional<List<String>> getPlayerMenuHistoryOpt(String name) {
        return Optional.ofNullable(name)
                .map(Bukkit::getPlayer)
                .map(Player::getUniqueId)
                .flatMap(uuid -> Optional.ofNullable(playerHistoryMap.get(uuid)));
    }

    public static Optional<List<String>> getPlayerMenuHistoryOpt(Player player) {
        return Optional.ofNullable(player)
                .map(Player::getUniqueId)
                .flatMap(uuid -> Optional.ofNullable(playerHistoryMap.get(uuid)));
    }

    public static Optional<List<String>> getPlayerMenuHistoryOpt(UUID uuid) {
        return Optional.ofNullable(uuid)
                .flatMap(u -> Optional.ofNullable(playerHistoryMap.get(u)));
    }


    public static synchronized void openForPlayer(Player player, String url) {
        if (player == null || url == null) return;
        String rawUrl = getUrl(url);
        menuMap.get(rawUrl).open(player, getArgs(url));
    }


    public static synchronized void registerOpenMenu(Player player, String url) {
        if (player == null || url == null) return;

        UUID uuid = player.getUniqueId();
        List<String> history = playerHistoryMap.computeIfAbsent(uuid, k -> new ArrayList<>());
        history.add(url);
        playerMap.put(uuid, url);
    }

    public static synchronized void registerMenu(SimpleMenu menu) {
        if (menu == null) return;

        String url = Optional.ofNullable(menu.getUrl()).orElse(DEFAULT_NO_MENU);
        SimpleMenu oldMenu = menuMap.get(url);

        pm.registerEvents(menu, plugin);

        if (oldMenu != null) {
            HandlerList.unregisterAll(oldMenu);
            Bukkit.getLogger().warning(String.format(
                    "Overwriting existing templates '%s' with new templates '%s' for URL: %s",
                    Optional.ofNullable(oldMenu.toString()).orElse("null"),
                    Optional.ofNullable(menu.toString()).orElse("null"),
                    url
            ));
        }

        menuMap.put(url, menu);
    }


    public static String buildURL(String basePath, Map<String, String> args) {
        String safeBasePath = Optional.ofNullable(basePath).orElse(EMPTY_STRING);

        if (args == null || args.isEmpty()) {
            return safeBasePath;
        }

        StringBuilder urlBuilder = new StringBuilder(safeBasePath);
        urlBuilder.append("?");

        String params = args.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));

        urlBuilder.append(params);
        return urlBuilder.toString();
    }

    public static String getUrl(String fullUrl) {
        if (fullUrl == null) return EMPTY_STRING;

        int queryIndex = fullUrl.indexOf('?');
        return queryIndex == -1 ? fullUrl : fullUrl.substring(0, queryIndex);
    }

    public static HashMap<String, String> getArgs(String fullUrl) {
        HashMap<String, String> args = new HashMap<>();

        if (fullUrl == null || !fullUrl.contains("?")) {
            return args;
        }

        String query = fullUrl.substring(fullUrl.indexOf('?') + 1);

        Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(keyValue -> keyValue.length == 2 && keyValue[0] != null && keyValue[1] != null)
                .forEach(keyValue -> args.put(keyValue[0], keyValue[1]));

        return args;
    }

    private static String encode(String s) {
        if (s == null) return EMPTY_STRING;

        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if ((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    c == '-' || c == '_' || c == '.' || c == '!') {
                out.append(c);
            } else {
                out.append('_').append((int) c).append('_');
            }
        }
        return out.toString();
    }

    private static String decode(String s) {
        if (s == null) return EMPTY_STRING;

        StringBuilder out = new StringBuilder();
        int i = 0;

        while (i < s.length()) {
            if (s.charAt(i) == '_' && i + 2 < s.length()) {
                try {
                    int end = s.indexOf('_', i + 1);
                    if (end > 0) {
                        int charCode = Integer.parseInt(s.substring(i + 1, end));
                        out.append((char) charCode);
                        i = end + 1;
                        continue;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            out.append(s.charAt(i++));
        }
        return out.toString();
    }

    public static synchronized void unregisterAllMenus() {
        menuMap.values().forEach(HandlerList::unregisterAll);
        menuMap.clear();
        playerMap.clear();
        playerHistoryMap.clear();
    }

    public static synchronized void unregisterMenu(String url) {
        if (url == null) return;

        Optional.ofNullable(menuMap.remove(url))
                .ifPresent(HandlerList::unregisterAll);
    }


    private static boolean hasEmptyInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {return false;}
        }
        return true;
    }
}