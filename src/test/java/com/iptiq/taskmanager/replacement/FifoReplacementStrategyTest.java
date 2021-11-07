package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.iptiq.taskmanager.ProcessTestFactory.newProcess;
import static com.iptiq.taskmanager.ProcessTestFactory.startedProcesses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FifoReplacementStrategyTest {

    private ReplacementStrategy strategy = new FifoReplacementStrategy();

    @ParameterizedTest
    @MethodSource("provideTestParams")
    public void testResultIsAlwaysMinStartTime(List<Process> list, int expectedPidToKill) {
        //when
        var result = strategy.choose(list, newProcess(0));

        //then
        assertTrue(result.isPresent());
        assertEquals(expectedPidToKill, result.get().getPid());
    }

    private static Stream<Arguments> provideTestParams() {
        return Stream.of(
                Arguments.of(startedProcesses(1, 2, 3), 1),
                Arguments.of(startedProcesses(7, 12, 10, 19, 3), 3),
                Arguments.of(startedProcesses(100), 100)
        );
    }

}
