package cofh.mod.updater;

import org.apache.logging.log4j.Logger;

public interface IUpdatableMod {

    public String getModId();

    public String getModName();

    public String getModVersion();

    public Logger getLogger();

}
