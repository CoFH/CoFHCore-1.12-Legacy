package cofh.asm;

import cofh.mod.ChildMod;
import cofh.mod.ChildModContainer;
import cofh.repack.codechicken.lib.asm.ASMInit;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModContainerFactory;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.Side;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;

@IFMLLoadingPlugin.TransformerExclusions({ "cofh.asm." })
@IFMLLoadingPlugin.SortingIndex(1001)
public class LoadingPlugin implements IFMLLoadingPlugin {

	public static final String MC_VERSION = "[1.7.10]";
	public static ArrayList<String> transformersList = new ArrayList<String>();
	public static boolean runtimeDeobfEnabled = false;
	public static ASMDataTable ASM_DATA = null;
	public static LaunchClassLoader loader = null;

	public static final String currentMcVersion;
	public static final File minecraftDir;
	public static final boolean obfuscated;

	// Initialize SubMod transformers
	static {

		boolean obf = true;
		try {
			obf = Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
		} catch (IOException e) {
		}
		obfuscated = obf;
		if (!obfuscated) {
			try {
				CoFHAccessTransformer.initForDeobf();
			} catch (IOException e) {
			}
		}
		currentMcVersion = (String) FMLInjectionData.data()[4];
		versionCheck(MC_VERSION, "CoFHCore");
        minecraftDir = (File) FMLInjectionData.data()[6];
		loader = Launch.classLoader;
		attemptClassLoad("cofh.asm.CoFHClassTransformer", "Failed to find Class Transformer! Critical Issue!");
		ASMInit.init();
	}

	public static void versionCheck(String reqVersion, String mod) {

		String mcVersion = currentMcVersion;
		if (!VersionParser.parseRange(reqVersion).containsVersion(new DefaultArtifactVersion(mcVersion))) {
			String err = "This version of " + mod + " does not support Minecraft version " + mcVersion;
			System.err.println(err);

			JEditorPane ep = new JEditorPane("text/html", "<html>" + err
					+ "<br>Remove it from your mods folder and check <a href=\"http://teamcofh.com/\">here</a> for updates" + "</html>");

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
						// pokemon!
					}
				}
			});
			JOptionPane.showMessageDialog(null, ep, "Fatal error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		l: if (obfuscated && System.class.getPackage().getSpecificationVersion().compareTo("1.8") < 0) {
			// create always-on-top modal dialogue in a separate thread so initialization can continue (but the user has to respond anyway)
			if (FMLLaunchHandler.side() == Side.SERVER) {
				FMLLog.log(Level.WARN, "*************************************************************************");
				for (int i = 0; i < 5; ++i) {
					FMLLog.log(Level.WARN, "*************************************************************************");
					FMLLog.log(Level.WARN, "* You are using an old Java version, and should update to 1.8 or newer. *");
					FMLLog.log(Level.WARN, "*************************************************************************");
				}
				FMLLog.log(Level.WARN, "*************************************************************************");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				break l;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {

					JEditorPane ep = new JEditorPane("text/html", "<html>You are using an old Java version, and should update to 1.8 or newer.</html>");

					ep.setEditable(false);
					ep.setOpaque(false);

					final JFrame frame = new JFrame();
					frame.setFocusable(false);
					frame.setUndecorated(true);
					frame.setAlwaysOnTop(true);
					Rectangle rect = frame.getRootPane().getGraphicsConfiguration().getBounds();
					frame.setLocation((int) rect.getCenterX(), (int) rect.getCenterY());
					frame.setTitle("Warning");
					frame.setVisible(true);
					JOptionPane.showMessageDialog(frame, ep, "Warning", JOptionPane.WARNING_MESSAGE);
					frame.setVisible(false);
				}

			}, "Message Thread").start();
		}
	}

	// public LoadingPlugin() {
	//
	// // DepLoader.load();
	// }

	public static void attemptClassLoad(String className, String failMessage) {

		try {
			Class.forName(className, false, LoadingPlugin.class.getClassLoader());
			transformersList.add(className);
		} catch (Throwable e) {
			FMLLog.warning(failMessage);
		}
	}

	@Override
	public String getAccessTransformerClass() {

		return CoFHAccessTransformer.class.getName();
	}

	@Override
	public String[] getASMTransformerClass() {

		return transformersList.toArray(new String[1]);
	}

	@Override
	public String getModContainerClass() {

		return CoFHDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass() {

		return CoFHDummyContainer.class.getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {

		runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
		if (data.containsKey("coremodLocation")) {
			myLocation = (File) data.get("coremodLocation");
		}
	}

	public File myLocation;

	public static class CoFHDummyContainer extends DummyModContainer implements IFMLCallHook {

		public CoFHDummyContainer() {

			super(new ModMetadata());
			ModMetadata md = getMetadata();
			md.autogenerated = true;
			md.modId = "<CoFH ASM>";
			md.name = md.description = "CoFH ASM";
			md.parent = "CoFHCore";
			md.version = "000";
		}

		@Override
		public boolean registerBus(EventBus bus, LoadController controller) {

			bus.register(this);
			return true;
		}

		@Subscribe
		public void construction(FMLConstructionEvent evt) {

			ASM_DATA = evt.getASMHarvestedData();
			CoFHClassTransformer.scrapeData(ASM_DATA);

			//for (ModCandidate t : ASM_DATA.getCandidatesFor("cofh.api.energy"));

		}

		@Override
		public void injectData(Map<String, Object> data) {

			loader = (LaunchClassLoader) data.get("classLoader");
		}

		@Override
		public Void call() throws Exception {

			scanMods();
			ModContainerFactory.instance().registerContainerType(Type.getType(ChildMod.class), ChildModContainer.class);
			return null;
		}

		private void scanMods() {

			File modsDir = new File(minecraftDir, "mods");
			for (File file : modsDir.listFiles()) {
				scanMod(file);
			}
			File versionModsDir = new File(minecraftDir, "mods/"+currentMcVersion);
			if (versionModsDir.exists()) {
				for (File file : versionModsDir.listFiles()) {
					scanMod(file);
				}
			}
		}

		private void scanMod(File file) {

			{
				String name = file.getName().toLowerCase();
				if (file.isDirectory() || !name.endsWith(".jar") && !name.endsWith(".zip")) {
					return;
				}
			}

			try {
				JarFile jar = new JarFile(file);
				try {
					l: {
					Manifest manifest = jar.getManifest();
					if (manifest == null) {
						break l;
					}
					Attributes attr = manifest.getMainAttributes();
					if (attr == null) {
						break l;
					}

					String transformers = attr.getValue("CoFHAT");
					if (transformers != null) {
						for (String t : transformers.split(" ")) {
							ZipEntry at = jar.getEntry("META-INF/" + t);
							if (at != null) {
								FMLLog.log("CoFHASM", Level.DEBUG, "Adding CoFHAT: " + t + " from: " + file.getName());
								CoFHAccessTransformer.processATFile(new InputStreamReader(jar.getInputStream(at)));
							}
						}
					}
				}
				} finally {
					jar.close();
				}
			} catch(Exception e) {
				// todo log at debug?
			}
		}
	}

}
