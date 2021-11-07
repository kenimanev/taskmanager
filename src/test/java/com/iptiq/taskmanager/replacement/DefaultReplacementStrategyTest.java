package com.iptiq.taskmanager.replacement;

import com.iptiq.taskmanager.Process;
import com.iptiq.taskmanager.ReplacementStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.iptiq.taskmanager.ProcessTestFactory.newNumberOfStartedProcesses;
import static com.iptiq.taskmanager.ProcessTestFactory.newProcess;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultReplacementStrategyTest {

    private ReplacementStrategy strategy = new DefaultReplacementStrategy();

    @ParameterizedTest
    @MethodSource("provideTestParams")
    public void testResultIsAlwaysEmpty(List<Process> list) {
        //when
        var result = strategy.choose(list, newProcess(0));

        //then
        assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> provideTestParams() {
        return Stream.of(
                Arguments.of(newNumberOfStartedProcesses(0)),
                Arguments.of(newNumberOfStartedProcesses(1)),
                Arguments.of(newNumberOfStartedProcesses(2)),
                Arguments.of(newNumberOfStartedProcesses(10))
        );
    }

}
