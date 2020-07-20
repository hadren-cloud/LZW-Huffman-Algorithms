import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

	public static String defaultInPathName;
	public static String defaultOutPathName;
	public StringBuilder stringBuilder = new StringBuilder();

	public void byteRead(String inPathName) {
		File file = new File(inPathName);

		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
			int singleCharInt;
			char singleChar;
			while((singleCharInt = bufferedInputStream.read()) != -1) {
				singleChar = (char) singleCharInt;
				stringBuilder.append(singleChar);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void fastWrite(String stringToWrite, String out) throws IOException {
		File file = new File(out);
		try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
			 DataOutputStream os = new DataOutputStream(bufferedOutputStream);) {
			os.writeBytes(stringToWrite);
		}
	}

	public void writeToFile(String binaryString) throws IOException {
		BitOutputStream bos = new BitOutputStream(defaultOutPathName);
		for (int j = 0; j < binaryString.length(); j++) {
			bos.writeBit(Character.getNumericValue(binaryString.charAt(j)));
		}
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 4){
			throw new ExceptionInInitializerError();
		}
		Instant start = Instant.now();
		defaultInPathName = args[2];
		defaultOutPathName = args[3];
		Test test = new Test();

		if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-c")) {
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			test.byteRead(defaultInPathName);
			String stringCompressed = HUFFcompressor.compress(test.stringBuilder.toString(),defaultOutPathName);
			test.writeToFile(stringCompressed);
		}
		else if((args[0].equals("-huff") || args[0].equals("-opt")) && args[1].equals("-d")) {
			HuffmanCompressor HUFFcompressor = new HuffmanCompressor();
			String stringDecompressed = HUFFcompressor.decompress(defaultInPathName,defaultOutPathName);
			test.fastWrite(stringDecompressed,defaultOutPathName);
		}
		else if(args[0].equals("-lzw") && args[1].equals("-c")) {
			LZWCompressor LZWcompressor = new LZWCompressor();
			LZWcompressor.compress(defaultInPathName,defaultOutPathName);
		}
		else if(args[0].equals("-lzw") && args[1].equals("-d")) {
			LZWCompressor LZWcompressor = new LZWCompressor();
			LZWcompressor.decompress(defaultInPathName,defaultOutPathName);
		}

		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		System.out.println("Operation done in: " + timeElapsed.toMillis() + " milliseconds!");
	}
}
