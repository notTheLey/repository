package org.ley.time;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ley.time.interfaces.RunTimer;
import org.ley.time.interfaces.TickTimer;
import org.ley.time.interfaces.TimeClock;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class TimeMatrix {

    private static final int TICKS_PER_SECOND = 20;
    private static final HashMap<LocalTime, List<TimeClock>> clockMap = new HashMap<>();
    private static final HashMap<Integer, List<TickTimer>> tickTimerMap = new HashMap<>();
    private static final HashMap<Integer, List<RunTimer>> runTimerMap = new HashMap<>();

    private int globalTick = 0;
    private int runTick = 0;
    private final Plugin plugin;
    private final File dataFile;
    private final List<Long> timeStamps = new ArrayList<>();
    private BukkitTask task;

    public TimeMatrix(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.dataFile = new File(plugin.getDataFolder(), "time.yml");
        loadData();
        start();
    }

    public void start() {
        if (task != null && !task.isCancelled()) {
            plugin.getLogger().warning("TimeMatrix is already running!");
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();

                checkTimeClock(currentDate, currentTime);
                checkTickTimer(currentDate, currentTime);
                checkRunTimer(currentDate, currentTime);

                globalTick++;
                runTick++;

                if (globalTick % 6000 == 0) {
                    saveData();
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error in TimeMatrix task: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        saveData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            this.globalTick = config.getInt("global_tick", 0);
            this.timeStamps.addAll(config.getLongList("time_stamps"));
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load TimeMatrix data: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("global_tick", globalTick);
            timeStamps.add(System.currentTimeMillis());
            config.set("time_stamps", timeStamps);

            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }

            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save time data: " + e.getMessage());
        }
    }

    // TimeClock methods
    public static void registerTimeClock(TimeClock clock, LocalTime time) {
        registerTimeClock(clock, List.of(time));
    }

    public static void registerTimeClock(TimeClock clock, List<LocalTime> times) {
        Objects.requireNonNull(clock, "TimeClock cannot be null");
        Objects.requireNonNull(times, "Times list cannot be null");

        times.forEach(time -> {
            LocalTime roundedTime = time.withNano(0);
            clockMap.computeIfAbsent(roundedTime, k -> new ArrayList<>()).add(clock);
        });
    }

    public static void unregisterTimeClock(TimeClock clock) {
        clockMap.values().forEach(list -> list.remove(clock));
    }

    public static void unregisterTimeClock(TimeClock clock, LocalTime time) {
        List<TimeClock> clocks = clockMap.get(time.withNano(0));
        if (clocks != null) {
            clocks.remove(clock);
        }
    }

    // TickTimer methods
    public static void registerTickTimer(TickTimer timer, TimeUnit timeType, int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        registerTickTimer(timer, convertToTicks(duration, timeType));
    }

    public static void registerTickTimer(TickTimer timer, TimeUnit timeType, List<Integer> durations) {
        Objects.requireNonNull(durations, "Durations list cannot be null");
        durations.forEach(duration -> registerTickTimer(timer, timeType, duration));
    }

    public static void registerTickTimer(TickTimer timer, int tickDuration) {
        if (tickDuration <= 0) {
            throw new IllegalArgumentException("Tick duration must be positive");
        }
        registerTickTimer(timer, List.of(tickDuration));
    }

    public static void registerTickTimer(TickTimer timer, List<Integer> tickDurations) {
        Objects.requireNonNull(timer, "TickTimer cannot be null");
        Objects.requireNonNull(tickDurations, "Tick durations list cannot be null");

        tickDurations.forEach(ticks -> {
            if (ticks <= 0) {
                throw new IllegalArgumentException("All tick durations must be positive");
            }
            tickTimerMap.computeIfAbsent(ticks, k -> new ArrayList<>()).add(timer);
        });
    }

    public static void unregisterTickTimer(TickTimer timer) {
        tickTimerMap.values().forEach(list -> list.remove(timer));
    }

    public static void unregisterTickTimer(TickTimer timer, int tickDuration) {
        List<TickTimer> timers = tickTimerMap.get(tickDuration);
        if (timers != null) {
            timers.remove(timer);
        }
    }

    // RunTimer methods
    public static void registerRunTimer(RunTimer timer, TimeUnit timeType, int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Time must be positive");
        }
        registerRunTimer(timer, convertToTicks(time, timeType));
    }

    public static void registerRunTimer(RunTimer timer, TimeUnit timeType, List<Integer> times) {
        Objects.requireNonNull(times, "Times list cannot be null");
        times.forEach(time -> registerRunTimer(timer, timeType, time));
    }

    public static void registerRunTimer(RunTimer timer, int ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Ticks must be positive");
        }
        registerRunTimer(timer, List.of(ticks));
    }

    public static void registerRunTimer(RunTimer timer, List<Integer> ticks) {
        Objects.requireNonNull(timer, "RunTimer cannot be null");
        Objects.requireNonNull(ticks, "Ticks list cannot be null");

        ticks.forEach(tick -> {
            if (tick <= 0) {
                throw new IllegalArgumentException("All ticks must be positive");
            }
            runTimerMap.computeIfAbsent(tick, k -> new ArrayList<>()).add(timer);
        });
    }

    public static void unregisterRunTimer(RunTimer timer) {
        runTimerMap.values().forEach(list -> list.remove(timer));
    }

    public static void unregisterRunTimer(RunTimer timer, int ticks) {
        List<RunTimer> timers = runTimerMap.get(ticks);
        if (timers != null) {
            timers.remove(timer);
        }
    }

    // Utility methods
    private static int convertToTicks(int duration, TimeUnit timeType) {
        Objects.requireNonNull(timeType, "TimeUnit cannot be null");
        return (int) (timeType.toSeconds(duration) * TICKS_PER_SECOND);
    }

    public int getGlobalTick() {
        return globalTick;
    }

    public int getRunTick() {
        return runTick;
    }

    public List<Long> getTimeStamps() {
        return Collections.unmodifiableList(timeStamps);
    }

    // Internal check methods
    private void checkTimeClock(LocalDate date, LocalTime time) {
        LocalTime roundedTime = time.withNano(0);
        List<TimeClock> clocks = clockMap.get(roundedTime);
        if (clocks != null) {
            clocks.forEach(clock -> {
                try {
                    clock.playOnTimeClock(date, time, runTick, globalTick);
                } catch (Exception e) {
                    plugin.getLogger().severe("Error in TimeClock: " + e.getMessage());
                }
            });
        }
    }

    private void checkTickTimer(LocalDate date, LocalTime time) {
        tickTimerMap.forEach((tickInterval, timers) -> {
            if (tickInterval > 0 && globalTick % tickInterval == 0) {
                timers.forEach(timer -> {
                    try {
                        timer.playEveryTick(tickInterval, date, time, runTick, globalTick);
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error in TickTimer: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void checkRunTimer(LocalDate date, LocalTime time) {
        runTimerMap.forEach((tickInterval, timers) -> {
            if (tickInterval > 0 && runTick % tickInterval == 0) {
                timers.forEach(timer -> {
                    try {
                        timer.runOnRunTicks(date, time, runTick, globalTick);
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error in RunTimer: " + e.getMessage());
                    }
                });
            }
        });
    }
}