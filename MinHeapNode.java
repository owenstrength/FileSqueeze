/** The MinHeapNode Class is used for building the HuffmanTree */
public class MinHeapNode implements Comparable<MinHeapNode> {
    public char data;
    public int freq;
    public MinHeapNode left, right;

    public MinHeapNode(char data, int freq) {
        this.data = data;
        this.freq = freq;
    }

    public int compareTo(MinHeapNode other) {
        return this.freq - other.freq;
    }
}
