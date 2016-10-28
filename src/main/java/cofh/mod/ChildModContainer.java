package cofh.mod;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.util.List;
import java.util.Map;

public class ChildModContainer extends FMLModContainer {

	private final String parent;

	public ChildModContainer(String className, ModCandidate container, Map<String, Object> modDescriptor) {

		super(className, container, (Map<String, Object>)((List<Object>)modDescriptor.get("mod")).get(0));

		parent = (String) modDescriptor.get("parent");
	}

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {

    	Map<String, ModContainer> list = Loader.instance().getIndexedModList();
    	l: {
    		if (!list.containsKey(parent)) {
    			break l;
    		}
    		for (ArtifactVersion info : this.getMetadata().dependencies) {
    			if (!list.containsKey(info.getLabel()) && !ModAPIManager.INSTANCE.hasAPI(info.getLabel())) {
    				break l;
    			}
    		}
	    	return super.registerBus(bus, controller);
    	}

    	return false;
    }

}
