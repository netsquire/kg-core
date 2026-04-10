package cz.netsquire.kgcore.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Small, dependency-free .env loader used for local development.
 * <p>
 * Features:
 * - Supports lines starting with `export `
 * - Ignores blank lines and full-line comments starting with `#`
 * - Keeps values that contain `=` (splits on first `=` only)
 * - Supports quoted values (single or double quotes) and unescapes common escapes (\n, \t, \", \\)
 * - Ignores inline comments after an unquoted value (text after a space followed by `#`)
 * - Respects existing system properties and environment variables (does not override)
 */
public final class DotenvLoader {

    private DotenvLoader() {
    }

    public static void load(File file) {
        if (file == null || !file.exists() || !file.isFile()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // support lines starting with `export `
                if (line.startsWith("export ")) {
                    line = line.substring("export ".length()).trim();
                }

                int eq = line.indexOf('=');
                if (eq <= 0) continue; // invalid line

                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();

                if (val.isEmpty()) {
                    // allow empty values
                    setIfAbsent(key, "");
                    continue;
                }

                // If value is quoted (single or double), strip quotes and unescape common sequences
                if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                    val = val.substring(1, val.length() - 1);
                    val = unescape(val);
                } else {
                    // Unquoted value: remove inline comments (a space then #comment)
                    int commentIdx = val.indexOf(" #");
                    if (commentIdx >= 0) {
                        val = val.substring(0, commentIdx).trim();
                    }
                    // also trim trailing comments starting with # immediately (e.g., VAL=abc#comment)
                    int sharpIdx = val.indexOf('#');
                    if (sharpIdx == 0) {
                        val = ""; // value was empty before comment
                    } else if (sharpIdx > 0) {
                        // only remove if there is no space before (already handled space case), keep it conservative
                        val = val.substring(0, sharpIdx).trim();
                    }
                }

                setIfAbsent(key, val);
            }
        } catch (IOException ignore) {
            // ignore and continue
        }
    }

    private static void setIfAbsent(String key, String value) {
        if (key == null || key.isEmpty()) return;
        if (System.getProperty(key) == null && System.getenv(key) == null) {
            System.setProperty(key, value == null ? "" : value);
        }
    }

    private static String unescape(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                switch (n) {
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case '"': sb.append('"'); i++; break;
                    case '\'': sb.append('\''); i++; break;
                    default:
                        // unknown escape, keep as-is (drop backslash)
                        sb.append(n); i++; break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
