package com.iptiq.taskmanager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;

public class ProcessTestFactory {

    public static Process newProcess(int pid) {
        return newProcess(pid, pid);
    }

    public static Process newProcess(int pid, int priority) {
        return Process.builder()
                .pid(pid)
                .priority(priority)
                .build();
    }

    public static List<Process> newNumberOfStartedProcesses(int numItems) {
        int[] pids = rangeClosed(1, numItems).toArray();
        return startedProcesses(pids);
    }

    public static List<Process> startedProcesses(int... pids) {
        var list = new ArrayList<Process>();

        for (int i = 0; i < pids.length; i++) {
            list.add(newStartedProcess(pids[i], pids[i], pids[i]));
        }

        Collections.shuffle(list);
        return list;
    }

    public static Process newStartedProcess(int pid, int priority, int startTime) {
        return Process.builder()
                .pid(pid)
                .priority(priority)
                .timeStarted(time(startTime))
                .state(Process.State.RUNNING)
                .build();
    }

    private static LocalDateTime time(int time) {
        return Instant
                .ofEpochSecond(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


}
