package cofh.asm;

public class HooksCore {

	public static void getObjectClass(Object aObject) {

		System.out.println("INJECT OBJ: " + aObject.getClass().getName());
	}

}
