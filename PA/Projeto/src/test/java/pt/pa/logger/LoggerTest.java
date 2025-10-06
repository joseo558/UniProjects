package pt.pa.logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * In order to preserve Logger class integrity,
 * these tests only work when the logs.txt file is not yet created.
 */
class LoggerTest {
    static Logger logger;
    static List<String> commandList;

    @BeforeAll
    static void setUp() {
        LoggerTest.logger = new Logger();

        // To create the file:
        LoggerTest.logger.update(null, "Command 0;");
        logger.clearFile();
        LoggerTest.commandList = logger.readFromFile();
    }

    @Test
    void getCommandListSize() {
        assertEquals(0, commandList.size());
        assertNotNull(logger);
        assertNotNull(commandList);
    }

    @Test
    void updateCommands() {
        logger.update(null, "Command 1;");
        logger.update(null, "Command 2;");
        logger.update(null, "Command 3;");
        LoggerTest.commandList = logger.readFromFile();

        assertFalse(commandList.isEmpty());
        assertEquals(3, commandList.size());
        assertEquals("Command 1;", commandList.get(0));
        assertEquals("Command 3;", commandList.get(2));
        assertTrue(commandList.contains("Command 2;"));
    }

    @Test
    void updateWhenCommandIsNull() {
        logger.update(null, null);

        assertEquals(0, commandList.size());
    }

    @Test
    void updateWhenCommandIsNotString() {
        Object obj = new Object();
        logger.update(null, obj);

        logger.update(null, 123456);
        logger.update(null, 1.005);
        logger.update(null, false);

        System.out.println(commandList);
        assertEquals(0, commandList.size());
    }
}