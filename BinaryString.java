import java.util.ArrayList;
import java.util.List;

/** BinaryString class for packing and unpacking binary strings */
public class BinaryString {

    /** Pack a binary string into a byte array */
    public static byte[] packBinaryString(String binaryString) {
        List<Byte> packedData = new ArrayList<>();
        byte currentByte = 0;
        int bitCount = 0;

        for (char bit : binaryString.toCharArray()) {
            currentByte |= (bit - '0') << (7 - bitCount);
            bitCount++;

            if (bitCount == 8) {
                packedData.add(currentByte);
                currentByte = 0;
                bitCount = 0;
            }
        }

        // Add the last byte if it's not complete
        if (bitCount > 0) {
            packedData.add(currentByte);
        }

        byte[] packedArray = new byte[packedData.size()];
        for (int i = 0; i < packedData.size(); i++) {
            packedArray[i] = packedData.get(i);
        }

        return packedArray;
    }

    /** Unpack a binary string from a byte array */
    public static String unpackBinaryString(byte[] packedData) {
        StringBuilder binaryString = new StringBuilder();

        for (byte packedByte : packedData) {
            for (int i = 7; i >= 0; i--) {
                binaryString.append((packedByte >> i) & 1);
            }
        }

        return binaryString.toString();
    }
}
