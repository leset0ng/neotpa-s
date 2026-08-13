package ihh.neotpa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves lang keys on the server side so clients do not need the mod's
 * language files at all. The language is chosen via the server config
 * (NeoTPAConfig.LANGUAGE), falling back to the server's JVM locale, with
 * en_us as the final fallback for missing keys.
 */
public class Localization {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeoTPA.MODID);
    private static final Gson GSON = new Gson();
    private static final String DEFAULT_LANGUAGE = "en_us";
    /** Matches "%s" and positional "%1$s" style specifiers. */
    private static final Pattern FORMAT_SPECIFIER = Pattern.compile("%(\\d+\\$)?s");
    private static final Map<String, Map<String, String>> LANGUAGE_CACHE = new HashMap<>();

    private Localization() {
    }

    /**
     * Translates {@code key} with the server-side language and colors the
     * surrounding text with {@code color}, while keeping the styles of the
     * argument components (e.g. green player names, red command hints).
     */
    public static MutableComponent translate(String key, ChatFormatting color, Component... args) {
        String pattern = getOrDefault(key);
        MutableComponent result = Component.empty();
        Matcher matcher = FORMAT_SPECIFIER.matcher(pattern);
        int lastEnd = 0;
        int sequentialIndex = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.append(Component.literal(pattern.substring(lastEnd, matcher.start())).withStyle(color));
            }
            int argIndex = matcher.group(1) != null
                    ? Integer.parseInt(matcher.group(1).substring(0, matcher.group(1).length() - 1)) - 1
                    : sequentialIndex++;
            if (argIndex >= 0 && argIndex < args.length) {
                result.append(args[argIndex]);
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < pattern.length()) {
            result.append(Component.literal(pattern.substring(lastEnd)).withStyle(color));
        }
        return result;
    }

    /**
     * Resolves {@code key} to a plain unstyled component, for use in e.g. hover text.
     */
    public static Component raw(String key) {
        return Component.literal(getOrDefault(key));
    }

    private static String getOrDefault(String key) {
        String code = languageCode();
        String value = language(code).get(key);
        if (value == null && !DEFAULT_LANGUAGE.equals(code)) {
            value = language(DEFAULT_LANGUAGE).get(key);
        }
        return value != null ? value : key;
    }

    private static String languageCode() {
        String configured = NeoTPAConfig.LANGUAGE.get();
        if (configured != null && !configured.isBlank()) {
            return configured.toLowerCase(Locale.ROOT);
        }
        Locale locale = Locale.getDefault();
        String code = locale.getLanguage();
        if (!locale.getCountry().isEmpty()) {
            code += "_" + locale.getCountry();
        }
        return code.toLowerCase(Locale.ROOT);
    }

    private static Map<String, String> language(String code) {
        return LANGUAGE_CACHE.computeIfAbsent(code, Localization::loadLanguage);
    }

    private static Map<String, String> loadLanguage(String code) {
        Map<String, String> result = new HashMap<>();
        String path = "/assets/" + NeoTPA.MODID + "/lang/" + code + ".json";
        try (InputStream in = Localization.class.getResourceAsStream(path)) {
            if (in == null) {
                LOGGER.warn("Language file {} not found, falling back to en_us", path);
                return result;
            }
            try (Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                Map<String, String> parsed = GSON.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
                if (parsed != null) {
                    result.putAll(parsed);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load language file {}", path, e);
        }
        return result;
    }
}
