package ihh.neotpa;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NeoTPA.MODID)
public class NeoTPA {
    public static final String MODID = "neotpa";

    public NeoTPA(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, NeoTPAConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(TPACommands::registerCommands);
    }
}
