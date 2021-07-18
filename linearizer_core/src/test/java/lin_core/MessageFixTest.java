package lin_core;

import org.junit.Test;

import static lin_core.Linearizer.generateSettings;
import lin_core.Linearizer.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;

public class MessageFixTest {
    static String[] emptyParams = new String[0];

    @Test
    public void justAnExample() {
        assert(1+2 == 3);
    }

    @Test
    public void settings() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("strip", emptyParams);
            put("fixCase", emptyParams);
        }};
        s = generateSettings(settingsMap);
        assert(s.strip);
        assert(s.fixCase);
        assert(s.badStarts == null);

        settingsMap = new HashMap<>() {{
            put("badStarts", new String[]{"one", "two", "three"});
            put("fixBig", emptyParams);
        }};
        s = generateSettings(settingsMap);
        assert(s.strip == false);
        assert(s.fixCase == false);
        assert(Arrays.equals(s.badStarts, new String[]{"one", "two", "three"}));
        assert(s.fixBig);
    }

    @Test
    public void badStarts() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("badStarts", new String[] {"*", "+"});
        }};

        s = generateSettings(settingsMap);
        String original = "*Commit message";
        String result = "Commit message";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void fixBig() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("fixBig", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "This is long commit message";
        String result = "This is long commit\n\nmessage";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void fixCase() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("fixCase", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "cOMMIT MESSAGE";
        String result = "Commit message";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void strip() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("strip", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "         Commit message             ";
        String result = "Commit message";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void stripAndFixCase() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("strip", emptyParams);
            put("fixCase", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "         commit message             ";
        String result = "Commit message";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void badStartsAndFixBig() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("badStarts", new String[] {"*", "+"});
            put("fixBig", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "***+++This is long commit message";
        String result = "This is long commit\n\nmessage";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }

    @Test
    public void fixAll() throws Exception {
        Map<String, String[]> settingsMap;
        Settings s;

        settingsMap = new HashMap<>() {{
            put("badStarts", new String[] {"*", "+"});
            put("fixBig", emptyParams);
            put("strip", emptyParams);
            put("fixCase", emptyParams);
        }};

        s = generateSettings(settingsMap);
        String original = "          ***+++        this is long commit message                ";
        String result = "This is long commit\n\nmessage";
        original = Linearizer.fixString(original, s);
        Assertions.assertEquals(original, result);
    }
}
