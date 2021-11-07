package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.iptiq.taskmanager.ProcessTestFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioBasedReplacementStrategyTest {

    private ReplacementStrategy strategy = new PrioBasedReplacementStrategy();

    @ParameterizedTest
    @MethodSource("provideFoundParams")
    public void testProcessToKillFound(List<Process> list, Process newProcess, int expectedPidToKill) {
        //when
        var result = strategy.choose(list, newProcess);

        //then
        assertTrue(result.isPresent());
        assertEquals(expectedPidToKill, result.get().getPid());
    }

    @ParameterizedTest
    @MethodSource("provideNotFoundParams")
    public void testNoProcessToKill(List<Process> list, Process newProcess) {
        //when
        var result = strategy.choose(list, newProcess);

        //then
        assertTrue(result.isEmpty());
    }


    private static Stream<Arguments> provideFoundParams() {
        var testList1 = Arrays.asList(
                newStartedProcess(1, 2, 1000),
                newStartedProcess(2, 5, 1000),
                newStartedProcess(3, 5, 999),
                newStartedProcess(4, 2, 999),
                newStartedProcess(5, 5, 2000)
        );
        var testList2 = Arrays.asList(
                newStartedProcess(1, 11, 1000),
                newStartedProcess(2, 11, 2000),
                newStartedProcess(3, 11, 500),
                newStartedProcess(4, 11, 700),
                newStartedProcess(5, 11, 600)
        );

        return Stream.of(
                Arguments.of(testList1, newProcess(3), 4),
                Arguments.of(testList1, newProcess(10), 4),
                Arguments.of(testList2, newProcess(15), 3),
                Arguments.of(testList2, newProcess(12), 3)
        );
    }

    private static Stream<Arguments> provideNotFoundParams() {
        return Stream.of(
                Arguments.of(startedProcesses(2, 10, 11), newProcess(-1)),
                Arguments.of(startedProcesses(7), newProcess(6)),
                Arguments.of(startedProcesses(5, 5, 12, 7), newProcess(3))
        );
    }

}
