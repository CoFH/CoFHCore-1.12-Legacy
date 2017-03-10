package cofh.core.util.tileentity;

/**
 * Implement this interface on Tile Entities which have Redstone Control functionality. This means that a tile can be set to ignore redstone entirely, or respond to a low or high redstone state.
 *
 * @author King Lemming
 */
public interface IRedstoneControl extends IRedstoneCache {

	/**
	 * Enum for Control Modes - Disabled (Ignored), Low (False), High (True).
	 *
	 * @author King Lemming
	 */
	enum ControlMode {

		DISABLED(true), LOW(false), HIGH(true);

		private final boolean state;

		ControlMode(boolean state) {

			this.state = state;
		}

		public boolean isDisabled() {

			return this == DISABLED;
		}

		public boolean isLow() {

			return this == LOW;
		}

		public boolean isHigh() {

			return this == HIGH;
		}

		public boolean getState() {

			return state;
		}

		public static ControlMode stepForward(ControlMode curControl) {

			return curControl == DISABLED ? LOW : curControl == HIGH ? DISABLED : HIGH;
		}

		public static ControlMode stepBackward(ControlMode curControl) {

			return curControl == DISABLED ? HIGH : curControl == HIGH ? LOW : DISABLED;
		}
	}

	/**
	 * Attempt to set the Control Mode of a Tile Entity. Returns TRUE on successful change.
	 */
	boolean setControl(ControlMode control);

	/**
	 * Gets the current Control Mode of a Tile Entity.
	 */
	ControlMode getControl();

}
