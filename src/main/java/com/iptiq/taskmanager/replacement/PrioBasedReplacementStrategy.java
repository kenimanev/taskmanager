package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Comparator.comparing;

/**
 * A strategy that would result in killing the lowest priority process which runs
 * for the longest time, if and only if the new process has a higher priority.
 *
 * If there are no processes with lower prio running, the strategy will return an
 * empty result, and the new process will not be accepted/scheduled.
 */
public class PrioBasedReplacementStrategy implements ReplacementStrategy {

    @Override
    public Optional<Process> choose(Collection<Process> processes, Process newProcess) {
        return processes
                .stream()
                .min(prioBasedComparator())
                .filter(p -> p.getPriority() < newProcess.getPriority());
    }

    private static Comparator<Process> prioBasedComparator() {
        return comparing(Process::getPriority)
                .thenComparing(Process::getTimeStarted);
    }
}
