package cofh.updater;

import org.apache.logging.log4j.Logger;

public interface IUpdateableMod {

	public String getModId();

	public String getModName();

	public String getModVersion();

	public Logger getLogger();
}
