package org.ley.time.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TimeClock {
    void playOnTimeClock(LocalDate date, LocalTime time, int runTick,int globalTick);
}
