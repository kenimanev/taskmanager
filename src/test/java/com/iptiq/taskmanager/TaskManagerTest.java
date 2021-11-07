package com.iptiq.taskmanager;

import com.iptiq.taskmanager.replacement.DefaultReplacementStrategy;
import com.iptiq.taskmanager.replacement.FifoReplacementStrategy;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

import static com.iptiq.taskmanager.ListOrder.*;
import static com.iptiq.taskmanager.ProcessTestFactory.newProcess;
import static com.iptiq.taskmanager.ProcessTestFactory.newStartedProcess;
import static com.iptiq.taskmanager.Selectors.all;
import static com.iptiq.taskmanager.Selectors.byPriority;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private static int CAPACITY = 5;

    @Test
    public void testAddSingleProcess() {
        //given
        var taskManager = newTaskManager();
        var process = newProcess(1);

        //when
        taskManager.addProcess(process);

        // then
        var list = taskManager.listProcesses(BY_PID);
        assertEquals(asList(process), list);
        assertTrue(process.hasStarted());
    }

    @Test
    public void testCannotAddStartedProcess() {
        //given
        var taskManager = newTaskManager();
        var process = newStartedProcess(1, 1, 1);

        //when
        assertThrows(IllegalArgumentException.class, () -> {
           taskManager.addProcess(process);
        });
    }

    @Test
    public void testCannotAddNullProcess() {
        //given
        var taskManager = newTaskManager();

        //when
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addProcess(null);
        });
    }

    @Test
    public void testCapacityFullWithDefaultReplacementStrategy() {
        //given
        var taskManager = newTaskManager();

        //when
        for (int i = 0; i < CAPACITY; i++) {
            taskManager.addProcess(newProcess(i));
        }
        //then
        assertThrows(CapacityFullException.class, () -> {
            taskManager.addProcess(newProcess(100));
        });
    }

    @Test
    public void testCapacityAlwaysAvailableWhenFifo() {
        //given
        var taskManager = newFifoTaskManager();

        //when
        for (int i = 0; i < 100; i++) {
            taskManager.addProcess(newProcess(i));
        }

        //then
        var sorted = taskManager.listProcesses(BY_PID);
        var sortedPids = sorted.stream()
                .map(p -> p.getPid())
                .collect(toList());
        assertEquals(asList(95, 96, 97, 98, 99), sortedPids);
    }

    @Test
    public void testReplacedProcessIsKilled() {
        //given
        var taskManager = newFifoTaskManager();
        var list = rangeClosed(1, CAPACITY + 1)
                .mapToObj(pid -> newProcess(pid))
                .collect(toList());

        //when
        list.forEach(p -> taskManager.addProcess(p));

        //then
        assertTrue(list.get(0).isFinished());
        assertFalse(list.get(1).isFinished());
    }

    @Test
    public void testListByPid() {
        //given
        var taskManager = newTaskManager();
        var list = asList(
                newProcess(3),
                newProcess(10),
                newProcess(1),
                newProcess(2));

        //when
        list.forEach(p -> taskManager.addProcess(p));
        var sorted = taskManager.listProcesses(BY_PID);

        // then
        var sortedPids = sorted.stream()
                .map(p -> p.getPid())
                .collect(toList());
        assertEquals(asList(1,2,3,10), sortedPids);
    }

    @Test
    public void testListByPriority() {
        //given
        var taskManager = newTaskManager();
        var list = asList(
                newProcess(3, 10),
                newProcess(10, 3),
                newProcess(1, 2),
                newProcess(2, 1));

        //when
        list.forEach(p -> taskManager.addProcess(p));
        var sorted = taskManager.listProcesses(BY_PRIORITY);

        // then
        var sortedPids = sorted.stream()
                .map(p -> p.getPid())
                .collect(toList());
        assertEquals(asList(2,1,10,3), sortedPids);
    }

    @Test
    public void testListByTimeStarted() {
        //given
        var taskManager = newTaskManager();
        var list = asList(
                newProcess(3),
                newProcess(10),
                newProcess(1),
                newProcess(2));

        //when
        list.forEach(p -> taskManager.addProcess(p));
        var sorted = taskManager.listProcesses(BY_TIME_STARTED);

        // then
        var sortedPids = sorted.stream()
                .map(p -> p.getPid())
                .collect(toList());
        assertEquals(asList(3, 10, 1, 2), sortedPids);
    }


    @Test
    public void testKillByPid() {
        //given
        var taskManager = newTaskManager();
        var process = newProcess(3);
        var list = asList(
                newProcess(10),
                process,
                newProcess(1),
                newProcess(2));
        list.forEach(p -> taskManager.addProcess(p));

        //when
        var killed = taskManager.kill(Selectors.byPID(3));

        // then
        assertEquals(asList(3), killed);
        assertTrue(process.isFinished());
        assertEquals(list.size() - 1, taskManager.listProcesses(BY_PID).size());
    }

    @Test
    public void testKillAll() {
        //given
        var taskManager = newTaskManager();
        var list = asList(
                newProcess(3),
                newProcess(10),
                newProcess(1),
                newProcess(2));
        list.forEach(p -> taskManager.addProcess(p));

        //when
        var killed = taskManager.kill(all());

        // then
        list.forEach(p -> assertTrue(p.isFinished()));
        Collections.sort(killed);
        assertEquals(asList(1, 2, 3, 10), killed);
        assertEquals(0, taskManager.listProcesses(BY_PID).size());
    }

    @Test
    public void testKillByPriority() {
        //given
        var taskManager = newTaskManager();
        var list = asList(
                newProcess(3, 1),
                newProcess(10, 2),
                newProcess(1, 1),
                newProcess(2, 2));
        list.forEach(p -> taskManager.addProcess(p));

        //when
        var killed = taskManager.kill(byPriority(2));

        // then
        Collections.sort(killed);
        assertEquals(asList(2, 10), killed);
        assertEquals(2, taskManager.listProcesses(BY_PID).size());
    }

    private TaskManager newTaskManager() {
        return new TaskManager(new DefaultReplacementStrategy(), CAPACITY, increasingClock());
    }

    private TaskManager newFifoTaskManager() {
        return new TaskManager(new FifoReplacementStrategy(), CAPACITY, increasingClock());
    }

    /**
     * @return a clock, that is guaranteed to return a different
     *         strictly increasing instant for each call to Clock.getInstant(). This helps us to test sorting
     *         by start time in a reliable manner.
     */
    private Clock increasingClock() {
        return new Clock() {

            private Instant instant = Instant.ofEpochSecond(1);
            @Override
            public ZoneId getZone() {
                return ZoneId.systemDefault();
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                instant = instant.plusSeconds(1);
                return instant;
            }
        };
    }
}
