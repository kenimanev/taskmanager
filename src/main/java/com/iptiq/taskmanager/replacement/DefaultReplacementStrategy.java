package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;

import java.util.Collection;
import java.util.Optional;

/**
 * Default replacement strategy - new processes cannot substitute currently running processes.
 */
public class DefaultReplacementStrategy implements ReplacementStrategy {

    @Override
    public Optional<Process> choose(Collection<Process> processes, Process newProcess) {
        return Optional.empty();
    }
}
