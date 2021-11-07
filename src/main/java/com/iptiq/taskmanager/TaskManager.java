package com.iptiq.taskmanager;

import com.iptiq.taskmanager.replacement.DefaultReplacementStrategy;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * TaskManager manages running processes, by allowing adding, killing or listing.
 *
 * The class has minimal public API - one public method for each operation. This helps
 * the users to understand how the underlying implementation functions, reduces clutter,
 * and allows easier evolution.
 *
 * For more on the characteristics of a good API, please see https://youtu.be/aAb7hSCtvGw?t=367
 */
@Slf4j
public class TaskManager {

    private static final int DEFAULT_CAPACITY = 10;

    private final ReplacementStrategy replacementStrategy;
    private final Map<Integer, Process> processes;
    private final Clock clock;
    private final int capacity;

    public TaskManager() {
        this(new DefaultReplacementStrategy());
    }

    public TaskManager(ReplacementStrategy replacementStrategy) {
        this(replacementStrategy, DEFAULT_CAPACITY, Clock.systemDefaultZone());
    }

    /**
     * For testing purposes only. As per specification, the {@link #capacity} parameter
     * will be defined at build time via the {@link #DEFAULT_CAPACITY} constant.
     */
    TaskManager(ReplacementStrategy replacementStrategy, int capacity, Clock clock) {
        validate(replacementStrategy == null, "replacementStrategy cannot be null");
        validate(capacity <= 0, "capacity must be a positive number");

        this.replacementStrategy = replacementStrategy;
        this.capacity = capacity;
        this.clock = clock;
        this.processes = new HashMap<>(capacity);
    }

    public synchronized void addProcess(Process process) throws CapacityFullException {
        validate(process == null, "Process cannot be null");
        validate(process.hasStarted(), "Cannot add an already started process");
        validate(processes.containsKey(process.getPid()), "Process with the same pid is already active");

        ensureCapacity(process);

        startInternal(process);
        processes.put(process.getPid(), process);
    }

    private void ensureCapacity(Process newProcess) {
        if (capacity == processes.size()) {
            var replacedOptional = replacementStrategy.choose(processes.values(), newProcess);
            if (replacedOptional.isPresent()) {
                var replacedProcess = replacedOptional.get();
                processes.remove(replacedProcess.getPid());
                killInternal(replacedProcess);
            } else {
                throw new CapacityFullException("No free capacity to accept new processes.");
            }
        }
    }

    public List<Process> listProcesses(ListOrder listOrder) {
        validate(listOrder == null, "listOrder cannot be null");

        return processes.values().stream()
                .sorted(listOrder.getComparator())
                .collect(toList());
    }

    /**
     * Kills all processes that match a specified selector.
     * @param selector a predicate that selects which processes to kill. Please, see {@link Selectors} for possible
     * @return a list of pid-s for all killed processes
     */
    public synchronized List<Integer> kill(Predicate<Process> selector) {
        validate(selector == null, "selector cannot be null");

        var iterator = processes.entrySet().iterator();

        var killedPids = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var process = entry.getValue();
            if (selector.test(process)) {
                iterator.remove();
                killInternal(process);
                killedPids.add(process.getPid());
            }
        }

        return killedPids;
    }

    /**
     * Starting a process is an operation that will potentially
     * need error handling (boilerplate code), and may be tricky to handle.
     *
     * This is the reason we decide to place it in a single private method.
     */
    private void startInternal(Process process) {
        log.info("About to start process " + process);
        process.start(clock);
    }

    /**
     * Killing a process is an operation that will potentially
     * need error handling (boilerplate code), and may be tricky to handle.
     *
     * This is the reason we decide to place it in a single private method.
     */
    private void killInternal(Process process) {
        log.info("About to kill process " + process);
        process.kill();
    }

    /**
     * A simple function that helps with validating input parameters.
     * This kind of logic often comes from the frameworks in a larger code base
     * e.g. Spring, Apache Commons etc.
     *
     * Here, I felt we cannot justify the inclusion of a large library for
     * something so trivial as condition checking.
     */
    private static void validate(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
