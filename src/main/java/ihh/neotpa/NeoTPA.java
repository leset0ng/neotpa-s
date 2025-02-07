package ihh.neotpa;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = NeoTPA.MODID)
public class NeoTPA {
    public static final String MODID = "neotpa";

    public NeoTPA() {
        NeoForge.EVENT_BUS.addListener(TPACommands::registerCommands);
    }
}
