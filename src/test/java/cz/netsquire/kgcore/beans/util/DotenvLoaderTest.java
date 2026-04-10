package cz.netsquire.kgcore.beans.util;

import cz.netsquire.kgcore.util.DotenvLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DotenvLoaderTest {

    @AfterEach
    void cleanup() {
        // clear any system properties we set during tests
        System.clearProperty("TEST_KEY");
        System.clearProperty("EMPTY_VAL");
        System.clearProperty("QUOTED_VAL");
        System.clearProperty("ESCAPED_VAL");
        System.clearProperty("EXPORTED");
    }

    @Test
    void loadsSimpleKeyValuePairs() throws Exception {
        File tmp = File.createTempFile("testenv", ".env");
        try (FileWriter w = new FileWriter(tmp, StandardCharsets.UTF_8)) {
            w.write("# comment line\n");
            w.write("TEST_KEY=hello world\n");
            w.write("EMPTY_VAL=\n");
            w.write("QUOTED_VAL=\"quoted value\"\n");
            w.write("ESCAPED_VAL=\"line1\\nline2\\tend\"\n");
            w.write("export EXPORTED=exportedValue\n");
        }

        // ensure environment doesn't have these keys
        assertNull(System.getProperty("TEST_KEY"));
        assertNull(System.getenv("TEST_KEY"));

        DotenvLoader.load(tmp);

        assertEquals("hello world", System.getProperty("TEST_KEY"));
        assertEquals("", System.getProperty("EMPTY_VAL"));
        assertEquals("quoted value", System.getProperty("QUOTED_VAL"));
        assertEquals("line1\nline2\tend", System.getProperty("ESCAPED_VAL"));
        assertEquals("exportedValue", System.getProperty("EXPORTED"));

        // cleanup temp file
        tmp.delete();
    }

    @Test
    void doesNotOverrideExistingSystemProperty() throws Exception {
        System.setProperty("TEST_KEY", "preexisting");
        File tmp = File.createTempFile("testenv", ".env");
        try (FileWriter w = new FileWriter(tmp, StandardCharsets.UTF_8)) {
            w.write("TEST_KEY=fromenv\n");
        }

        DotenvLoader.load(tmp);

        assertEquals("preexisting", System.getProperty("TEST_KEY"));
        tmp.delete();
    }
}
