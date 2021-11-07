package com.iptiq.taskmanager;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Represents a managed process.
 */
@Getter
@Builder
@ToString
@Log
public class Process {

    static enum State {
        INIT,
        RUNNING,
        DEAD;
    }

    private final int pid;
    private final int priority;
    private LocalDateTime timeStarted;

    @Getter(value=AccessLevel.NONE)
    @Builder.Default
    private State state = State.INIT;

    public boolean hasStarted() {
        return state != State.INIT;
    }

    public boolean isFinished() {
        return state == State.DEAD;
    }

    public void start(Clock clock) {
        if (hasStarted()) {
            throw new IllegalStateException("Process was already started");
        }

        state = State.RUNNING;
        timeStarted = LocalDateTime.now(clock);
    }

    public void kill() {
        log.info("Killed process: " + toString());
        state = State.DEAD;
    }

}
