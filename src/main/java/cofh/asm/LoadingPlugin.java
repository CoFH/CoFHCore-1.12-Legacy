package cofh.asm;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import codechicken.core.launch.DepLoader;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({ "cofh.asm" })
public class LoadingPlugin implements IFMLLoadingPlugin {

	public static final String MC_VERSION = "[1.7.2]";
	public static ArrayList<String> transformersList = new ArrayList<String>();
	public static boolean runtimeDeobfEnabled = false;

	// Initialize SubMod transformers
	static {
		attemptClassLoad("cofh.asm.TransformerCore", "Failed to find Main Transformer! Critical Issue!");
		attemptClassLoad("cofh.asm.PCCASMTransformer", "Failed to find Secondary Transformer! Critical Issue!");
		attemptClassLoad("cofh.asm.TransformerMasquerade", "Failed to find SubCoreMod Masquerade!");
	}

	public LoadingPlugin() {

		DepLoader.load();
	}

	public static void attemptClassLoad(String className, String failMessage) {

		try {
			// Class.forName(className);
			transformersList.add(className);
		} catch (Throwable e) {
			FMLLog.warning(failMessage);
		}
	}

	@Override
	public String getAccessTransformerClass() {

		return null;
	}

	public static void versionCheck(String reqVersion, String mod) {

		String mcVersion = (String) FMLInjectionData.data()[4];
		if (!VersionParser.parseRange(reqVersion).containsVersion(new DefaultArtifactVersion(mcVersion))) {
			String err = "This version of " + mod + " does not support Minecraft version " + mcVersion;
			System.err.println(err);

			JEditorPane ep = new JEditorPane("text/html", "<html>" + err
					+ "<br>Remove it from your coremods folder and check <a href=\"http://thermalexpansion.wikispaces.com/\">here</a> for updates" + "</html>");

			ep.setEditable(false);
			ep.setOpaque(false);
			ep.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(HyperlinkEvent event) {

					try {
						if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
							Desktop.getDesktop().browse(event.getURL().toURI());
						}
					} catch (Exception e) {
					}
				}
			});
			JOptionPane.showMessageDialog(null, ep, "Fatal error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	@Override
	public String[] getASMTransformerClass() {

		// versionCheck(MC_VERSION, "CoFHCore");
		return transformersList.toArray(new String[2]);
	}

	@Override
	public String getModContainerClass() {

		return null;
	}

	@Override
	public String getSetupClass() {

		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

		runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
		if (data.containsKey("coremodLocation")) {
			myLocation = (File) data.get("coremodLocation");
		}
	}

	public File myLocation;

}
