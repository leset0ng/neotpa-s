package ihh.neotpa;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoTPAConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> LANGUAGE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        LANGUAGE = builder
                .comment("The language used for messages sent by this mod, e.g. \"zh_cn\" or \"en_us\".",
                        "An empty value uses the server's JVM locale.",
                        "The selected language must have a matching file in assets/neotpa/lang/.",
                        "Missing keys always fall back to en_us.")
                .define("language", "");
        SPEC = builder.build();
    }
}
