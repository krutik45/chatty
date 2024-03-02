package chatty.util;

import chatty.util.ProcessManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProcessManager.class)
public class ProcessManagerTest {

    @Mock
    Consumer<String> messageListenerMock;
    @Test
    public void testCommandInvalidInput() {
        // Test with null input
        String resultNull = ProcessManager.command(null, null);
        assertEquals("Invalid input.", resultNull);

        // Test with empty input
        String resultEmpty = ProcessManager.command("", null);
        assertEquals("Invalid input.", resultEmpty);
    }



    @Test
    public void testCommandExec() throws IllegalAccessException, NoSuchFieldException {

        // Mock the behavior of the execute method using reflection
        Field processesField = ProcessManager.class.getDeclaredField("processes");
        processesField.setAccessible(true);
        processesField.set(null, null); // Set processes map to null to avoid NPE

        // Call the command method
        String input = "exec command_name";
        String output = ProcessManager.command(input, null);

        // Validate the output
        assert output.equals("Trying to start process.");
    }
}
