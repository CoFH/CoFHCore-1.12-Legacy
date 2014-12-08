package irclib;

public class Base64 {

	private static final String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final int splitLinesAt = 76;

	public static byte[] zeroPad(int length, byte[] bytes) {

		byte[] padded = new byte[length];
		System.arraycopy(bytes, 0, padded, 0, bytes.length);
		return padded;
	}

	public static String encode(String string) {

		String encoded = "";
		byte[] stringArray;
		try {
			stringArray = string.getBytes("UTF-8");
		} catch (Exception ignored) {
			stringArray = string.getBytes();
		}

		int paddingCount = (3 - stringArray.length % 3) % 3;

		stringArray = zeroPad(stringArray.length + paddingCount, stringArray);

		for (int i = 0; i < stringArray.length; i += 3) {
			int j = ((stringArray[i] & 0xFF) << 16) + ((stringArray[i + 1] & 0xFF) << 8) + (stringArray[i + 2] & 0xFF);

			encoded = encoded + base64code.charAt(j >> 18 & 0x3F)
					+ base64code.charAt(j >> 12 & 0x3F)
					+ base64code.charAt(j >> 6 & 0x3F)
					+ base64code.charAt(j & 0x3F);
		}

		return splitLines(encoded.substring(0, encoded.length() - paddingCount) + "==".substring(0, paddingCount));
	}

	public static String splitLines(String string) {

		String lines = "";
		for (int i = 0; i < string.length(); i += splitLinesAt) {
			lines = lines + string.substring(i, Math.min(string.length(), i + splitLinesAt));
			lines = lines + "\r\n";
		}

		return lines;
	}

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			System.err.println("encoding \"" + args[i] + "\"");
			System.out.println(encode(args[i]));
		}
	}

}
