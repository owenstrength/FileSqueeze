# FileSqueeze
FileSqueeze is a Huffman Coding-Based File Compression Program. It takes in a file and compresses it. Then it can take that folder and decompress it back into the original file. On Average, the Compression ratio is about 0.68 (compressed file / original file). 

## How it Works
FileSqueeze utilizes the Huffman Coding Algorithm to compress files. The program first reads in the file and counts the number of occurrences of each character. Then it creates a Huffman Tree based on the character counts. The program then traverses the tree and assigns a binary code to each character. The Tree is a min heap used as a priority queue, so the characters with the lowest counts are at the top of the tree. This means that the most frequent characters will have the shortest binary codes.

The program then writes the binary code to a file. The program also writes the character counts to the file so that the file can be decompressed. The program then reads in the binary code and writes the characters to a new file based on the Huffman Tree. 


## Requirements
  - The Program is written in Java, so the JVM must be installed on your machine to run this code

## Usage
### Encoding a File

```sh
java FileSqueeze.java -e <Path to File>
```

This command will create a .filesqueeze file with the same name as the original file.
the original file is not deleted by the program.

### Decoding a File

```sh
java FileSqueeze.java -d <Path to .filesqueeze file>
```

This command will create a new file with the name of the original file + "OUT.txt"

### Example

```sh
java FileSqueeze.java -e "C:\Users\owens\OneDrive\Desktop\test.txt"

java FileSqueeze.java -d "C:\Users\owens\OneDrive\Desktop\test"
```
