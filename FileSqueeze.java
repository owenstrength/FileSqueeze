import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;

/**
 * File Squeeze is a HuffmanCoding Based File Compression Program. It takes in a
 * file and compresses it into a
 * folder with a table and encoded bytes. Then it can take that folder and
 * decompress it back into the original file.
 * On Average the Compression ratio is about 0.68. (compressed file / original
 * file)
 * 
 * @author Owen Strength
 * @version 1.0 9/9/2023
 * @category File Compression
 */
public class FileSqueeze {

    // opted to use static variables instead of passing them around
    private static Map<Character, String> codes = new HashMap<>();
    private static Map<String, Character> codesIn = new HashMap<>();
    private static Map<Character, Integer> freq = new HashMap<>();
    private static PriorityQueue<MinHeapNode> minHeap = new PriorityQueue<>();
    private static String encodedString = "";
    private static byte[] encodedBytes;
    private static String decodedString = "";
    private static String str = "";

    private static String inputFileName = "";
    private static String inputFolderName = "";

    // main method
    public static void main(String[] args) {

        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("HELP:");
            System.out.println("Usage For Encoding: java FileSqueeze.java -e <String of path to filename>");
            System.out.println("Usage For Decoding: java FileSqueeze.java -d <String of path to foldername>");
            System.exit(0);
        }

        if (args[0].equalsIgnoreCase("-e")) {
            inputFileName = args[1];

            try {
                inputStringToCompress(inputFileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }

            calcFreq(str);
            HuffmanCodes(str.length());

            buildEncodedString();

            exportTableToFile(minHeap);

            System.out.println("Compression Completed!");
            System.out.println("To Decompress Run (in this directory): java FileSqueeze.java -d " + inputFolderName);
            System.exit(0);

        } else if (args[0].equalsIgnoreCase("-d")) {
            inputFolderName = args[1];

            try {
                inputFileToTable(inputFolderName);
                // importTableFromFile2(inputFolderName);
                decode();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputToFile();

            System.out.println("Decompression Completed!");
            System.out.println("Data has been outputted to: " + inputFolderName + "OUT.txt");

        } else {
            System.out.println(args[0] + " is not a valid argument");
            System.out.println(args[0] == "-e");
            System.out.println("Usage For Encoding: java FileSqueeze.java -e <String of path to filename>");
            System.out.println("Usage For Decoding: java FileSqueeze.java -d <String of path to foldername>");
            System.exit(0);
        }
    }

    /** creating Huffman tree */
    private static void HuffmanCodes(int size) {
        for (Entry<Character, Integer> entry : freq.entrySet()) {
            // System.out.println(entry.getKey() + " " + entry.getValue());
            minHeap.add(new MinHeapNode(entry.getKey(), entry.getValue()));
        }
        while (minHeap.size() != 1) {
            MinHeapNode left = minHeap.poll();
            MinHeapNode right = minHeap.poll();
            MinHeapNode top = new MinHeapNode('$', left.freq + right.freq);
            top.left = left;
            top.right = right;
            minHeap.add(top);
        }
        storeCodes(minHeap.peek(), "");
    }

    /** building encoded string */
    private static void buildEncodedString() {
        for (char c : str.toCharArray()) {
            encodedString += codes.get(c);
        }
        encodedBytes = BinaryString.packBinaryString(encodedString);
    }

    /** calculating frequency of each character */
    private static void calcFreq(String str) {
        for (char c : str.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
    }

    /** storing codes */
    private static void storeCodes(MinHeapNode root, String str) {
        if (root == null) {
            return;
        }
        if (root.data != '$') {
            codes.put(root.data, str);
        }
        storeCodes(root.left, str + "0");
        storeCodes(root.right, str + "1");
    }

    /** exporting table to file */
    private static void exportTableToFile(PriorityQueue<MinHeapNode> minHeapIn) {
        inputFolderName = inputFileName.substring(0, inputFileName.lastIndexOf("."));

        File theDir = new File(inputFolderName + "/");

        if (Files.notExists(theDir.toPath())) {
            try {
                Files.createDirectories(theDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            theDir.mkdirs();

        }

        System.out.println(theDir.exists());

        File tableFile = new File(theDir, "table.txt");
        File bytesFile = new File(theDir, "encodedBytes.bin");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile))) {
            FileOutputStream fos = new FileOutputStream(bytesFile);
            fos.write(encodedBytes.length);
            fos.write(encodedBytes);
            fos.close();

            StringBuilder tableBuilder = new StringBuilder();

            for (var entry : codes.entrySet()) {
                if (entry.getKey() == "\n".charAt(0)) {
                    writer.write("\\n" + " " + entry.getValue());
                    writer.newLine();
                    tableBuilder.append("\\n").append(" ").append(entry.getValue()).append("\n");
                    continue;
                }
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
                tableBuilder.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
            }

            writer.close();

            String tableString = tableBuilder.toString();
            byte[] tableBytes = BinaryString.packBinaryString(tableString);

            byte[] combinedBytes = combineBytes(encodedBytes, tableBytes);
            combinedBytes = combineBytes(BinaryString.packBinaryString(Integer.toString(encodedBytes.length)),
                    combinedBytes);

            File combinedFile = new File(theDir, "combined.bin");

            try (RandomAccessFile raf = new RandomAccessFile(combinedFile, "rw")) {
                raf.write(combinedBytes);

                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void exportTableToFile2(PriorityQueue<MinHeapNode> minHeapIn) {
        inputFolderName = inputFileName.substring(0, inputFileName.lastIndexOf("."));

        File theDir = new File(inputFolderName + "/");

        if (!theDir.exists()) {
            theDir.mkdirs();
        } else {
            theDir.delete();
        }

        // create combined file
        File combinedFile = new File(theDir, "combined.bin");

        System.out.println(combinedFile);

        StringBuilder tableBuilder = new StringBuilder();
        for (var entry : codes.entrySet()) {
            if (entry.getKey() == "\n".charAt(0)) {
                tableBuilder.append(Integer.toBinaryString('\\')).append(Integer.toBinaryString('n')).append(" ")
                        .append(entry.getValue()).append("\n");
            } else {
                tableBuilder.append(Integer.toBinaryString(entry.getKey())).append(" ").append(entry.getValue())
                        .append("\n");
            }
        }

        String tableString = tableBuilder.toString();
        byte[] tableBytes = BinaryString.packBinaryString(tableString);

        System.out.println(tableBytes.toString());

        try (RandomAccessFile raf = new RandomAccessFile(combinedFile, "rw")) {
            raf.writeInt(tableBytes.length);
            raf.write(tableBytes);
            raf.write(encodedBytes);

            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** inputing string to compress */
    private static void inputStringToCompress(String filename) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while (line != null) {
                str += line + "\n";
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    /** inputting table to decode frome file */
    private static void inputFileToTable(String folderename) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(folderename + "/table.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                if (line.charAt(0) == '\\') {
                    char c = "\n".charAt(0);
                    String code = line.substring(3, line.length());
                    codesIn.put(code, c);
                    line = reader.readLine();
                    continue;
                }
                char c = line.charAt(0);
                String code = line.substring(2, line.length());
                codesIn.put(code, c);
                line = reader.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading huffman table file", e);
        }
    }

    private static void importTableFromFile2(String inputFolderName) {
        try {
            File theDir = new File(inputFolderName + "/");
            File combinedFile = new File(theDir, "combined.bin");

            try (RandomAccessFile raf = new RandomAccessFile(combinedFile, "r")) {
                int tableLength = raf.readInt();
                byte[] tableBytes = new byte[tableLength];
                raf.read(tableBytes);

                String tableString = BinaryString.unpackBinaryString(tableBytes);
                String[] tableEntries = tableString.split("\n");

                System.out.println(tableString);

                codes = new HashMap<>();
                for (String entry : tableEntries) {
                    String[] parts = entry.split(" ");
                    BigInteger bigIntegerKey = new BigInteger(parts[0], 2);
                    char key = (char) bigIntegerKey.intValue();
                    String value = parts[1];
                    codes.put(key, value);
                }

                long encodedLength = raf.length() - raf.getFilePointer();
                encodedBytes = new byte[(int) encodedLength];
                raf.read(encodedBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** decoding encoded string from bin file */
    private static void decode() throws IOException {
        encodedBytes = null;

        File theDir = new File(inputFolderName + "/");
        if (!theDir.exists()) {
            throw new IOException("No Folder with Name: " + inputFileName, null);
        }

        // InputStream inputStream = new FileInputStream(theDir + "/encodedBytes.bin");
        // encodedBytes = inputStream.readAllBytes();
        // inputStream.close();

        // this kinda works but not really, might be easier to read entire file in
        // and then parse it
        InputStream inputStream = new FileInputStream(theDir + "/combined.bin");
        byte[] encodedLengthBytes = inputStream.readNBytes(2);
        int encodedLength = Integer.parseInt(BinaryString.unpackBinaryString(encodedLengthBytes), 2);
        System.out.println(encodedLengthBytes);
        System.out.println(encodedLength);
        encodedBytes = inputStream.readNBytes(encodedLength * 4 + 16);

        inputStream.close();

        encodedString = BinaryString.unpackBinaryString(encodedBytes);

        int i = 0;
        int j = 1;
        while (i + j < encodedString.length()) {
            String sub = encodedString.substring(i, i + j);
            if (codesIn.containsKey(sub)) {
                char c = codesIn.get(sub);
                decodedString += c;
                i += j;
                j = 1;
            } else {
                j++;
            }
        }
    }

    /** outputting decoded string to file */
    private static void outputToFile() {
        File output = new File(inputFolderName + "OUT.txt");

        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(decodedString.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static byte[] combineBytes(byte[] arr1, byte[] arr2) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(arr1);
            outputStream.write(arr2);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}

/** The MinHeapNode Class is used for building the HuffmanTree */
class MinHeapNode implements Comparable<MinHeapNode> {
    char data;
    int freq;
    MinHeapNode left, right;

    MinHeapNode(char data, int freq) {
        this.data = data;
        this.freq = freq;
    }

    public int compareTo(MinHeapNode other) {
        return this.freq - other.freq;
    }
}

/** BinaryString class for packing and unpacking binary strings */
class BinaryString {

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
