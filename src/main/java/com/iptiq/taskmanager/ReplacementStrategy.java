package com.iptiq.taskmanager;

import java.util.Collection;
import java.util.Optional;

/**
 * When its buffer capacity is reached, {@link TaskManager#addProcess(Process)} delegates
 * the decision what process to replace (n.b. by killing it) to implementations of this interface.
 */
public interface ReplacementStrategy {

    /**
     * @param processes a collection of all currently running processes
     * @param newProcess the new process, which we are looking to add
     * @return a process that can be killed, so that the new process can take its place. The new process will be
     *         rejected if the return value is an empty Optional.
     */
    Optional<Process> choose(Collection<Process> processes, Process newProcess);

}
