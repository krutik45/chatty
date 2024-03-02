package chatty.util;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
public class TimedCounterTest {
    @Test
    public void testCounter() {
        // Create a TimedCounter instance with a 2-minute interval (120000 milliseconds)
        TimedCounter timedCounterInstance = new TimedCounter(120000);
        // Increase the count a few times
        timedCounterInstance.increase();
        timedCounterInstance.increase();
        timedCounterInstance.increase();
        // Check the count without removing old elements
        int countWithoutRemove = timedCounterInstance.getCount(false);
        // The count should be 3 as we increased it 3 times without removing old elements
        assertEquals(3, countWithoutRemove);
        // Check the count with removing old elements
        int countWithRemove = timedCounterInstance.getCount(true);
        // Since we haven't changed the interval, all elements added within 2 minutes should be counted
        // We added 3 elements, but all should still be within the 2-minute window
        assertEquals(3, countWithRemove);
    }

    @Test
    public void testIncrease() {
        // Create a TimedCounter instance with a 2-minute interval (120000 milliseconds)
        TimedCounter timedCounterInstance = new TimedCounter(120000);
        // Initially, the count should be zero
        int initialCount = timedCounterInstance.getCount();
        // Call increase() method to increase the count
        timedCounterInstance.increase();
        // After calling increase(), the count should be one
        int countAfterIncrease = timedCounterInstance.getCount();
        // Assert that the count increased by one
        assertEquals(initialCount + 1, countAfterIncrease);
    }
}
