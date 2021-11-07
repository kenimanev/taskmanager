package com.iptiq.taskmanager;

/**
 * Exception thrown when the {@link TaskManager} refuses to accept a new process.
 *
 * Please, note that the this happens when both of the following conditions are met
 * <ul>
 *     <li>1) The capacity is full</li>
 *     <li>2) The configured {@link ReplacementStrategy} cannot find any process that can be killed.</li>
 * </ul>
 */
public class CapacityFullException extends RuntimeException {

    CapacityFullException(String message) {
        super(message);
    }
}
