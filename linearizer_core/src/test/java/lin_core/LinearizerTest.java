package lin_core;

import org.junit.Test;

import static lin_core.Linearizer.generateSettings;
import lin_core.Linearizer.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LinearizerTest {
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
}
