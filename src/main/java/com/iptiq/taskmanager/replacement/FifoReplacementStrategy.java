package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/**
 * Picks the oldest process to kill.
 */
public class FifoReplacementStrategy implements ReplacementStrategy {

    @Override
    public Optional<Process> choose(Collection<Process> processes, Process newProcess) {
            return processes
                    .stream()
                    .min(Comparator.comparing(Process::getTimeStarted));
    }
}
