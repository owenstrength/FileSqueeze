# FileSqueeze
File Squeeze is a Huffman Coding-Based File Compression Program. It takes in a file and compresses it into a folder with a table and encoded bytes. Then it can take that folder and decompress it back into the original file. On Average, the Compression ratio is about 0.68. (compressed file / original file)

## Requirements
  - The Program is written in Java, so the JVM must be installed on your machine to run this code

## Usage
### Encoding a File

```sh
java FileSqueeze.java -e <Path to File>
```

This command will create a folder with 2 files of the compressed data. The Folder will have the same name as the original file.
Currently, the original file is not deleted by the program.

### Decoding a File

```sh
java FileSqueeze.java -d <Path to the folder created by compressing the original file>
```

This command will create a new file with the name of the original file + "OUT.txt"

### Example

```sh
java FileSqueeze.java -e "C:\Users\owens\OneDrive\Desktop\test.txt"

java FileSqueeze.java -d "C:\Users\owens\OneDrive\Desktop\test"
```
