package org.ley.time.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TickTimer {
    void playEveryTick(int tickFrequency, LocalDate date, LocalTime time, int runTick,int globalTick);
}
