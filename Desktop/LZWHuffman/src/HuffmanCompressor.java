import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class HuffmanCompressor {

	private static Map<Character, String> huffmanPrefix = new HashMap<>();
	static HuffmanNode root;

	private static HuffmanNode buildTree(Map<Character, Integer> freqCodes) {

		PriorityQueue<HuffmanNode> prioQueue = new PriorityQueue<>();
		Set<Character> keySet = freqCodes.keySet();
		for (Character c : keySet) {
			HuffmanNode huffmanNode = new HuffmanNode();
			huffmanNode.data = c;
			huffmanNode.frequency = freqCodes.get(c);
			huffmanNode.left = null;
			huffmanNode.right = null;
			prioQueue.offer(huffmanNode);
		}

		while (prioQueue.size() > 1) {
			HuffmanNode x = prioQueue.peek();
			prioQueue.poll();
			HuffmanNode y = prioQueue.peek();
			prioQueue.poll();
			HuffmanNode sum = new HuffmanNode();
			sum.frequency = x.frequency + y.frequency;
			sum.data = '-';
			sum.left = x;
			sum.right = y;
			root = sum;
			prioQueue.offer(sum);
		}

		return prioQueue.poll();
	}

	private static void setPrefixCodes(HuffmanNode nodePrefix, StringBuilder prefixCode) {
		if (nodePrefix != null) {
			if (nodePrefix.left == null && nodePrefix.right == null) {
				huffmanPrefix.put(nodePrefix.data, prefixCode.toString());
			} else {
				prefixCode.append('0');
				setPrefixCodes(nodePrefix.left, prefixCode);
				prefixCode.deleteCharAt(prefixCode.length() - 1);

				prefixCode.append('1');
				setPrefixCodes(nodePrefix.right, prefixCode);
				prefixCode.deleteCharAt(prefixCode.length() - 1);
			}
		}
	}

	public String compress(String fileString,String fileStringOut) {
		HashMap<Character, Integer> freqCodes = new HashMap<>();
		for (int i = 0; i < fileString.length(); i++) {
			if (!freqCodes.containsKey(fileString.charAt(i))) {
				freqCodes.put(fileString.charAt(i), 1);
			} else {
				freqCodes.put(fileString.charAt(i), freqCodes.get(fileString.charAt(i)) + 1);
			}
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(fileStringOut + ".dat");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(freqCodes);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}

		root = buildTree(freqCodes);

		System.out.println("Character Frequency Map = " + freqCodes + "\n");

		setPrefixCodes(root, new StringBuilder());
		System.out.println("Character Prefix Map = " + huffmanPrefix);
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < fileString.length(); i++) {
			char c = fileString.charAt(i);
			s.append(huffmanPrefix.get(c));
		}

		return s.toString();
	}

	public String decompress(String compressedFileString, String out) {

		StringBuilder compressedFile = new StringBuilder();
		BitInputStream bis = new BitInputStream(compressedFileString);

		StringBuilder sBuilder = new StringBuilder();
		HashMap<Character, Integer> freqCodes = new HashMap<>();

		try {
			FileInputStream fileIn = new FileInputStream(compressedFileString + ".dat");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			freqCodes = (HashMap) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
		}

		int bit = 0;

		while (bit != -1) {
			bit = bis.readBit();
			if (bit == 0 || bit == 1) {
				sBuilder.append(bit);
			}
		}
		bis.close();

		String s = sBuilder.toString();

		HuffmanNode decodeRoot = buildTree(freqCodes);

		for (int i = 0; i < s.length(); i++) {
			int j = Integer.parseInt(String.valueOf(s.charAt(i)));
			if (j == 0) {
				decodeRoot = decodeRoot.left;
				if (decodeRoot.left == null && decodeRoot.right == null) {
					compressedFile.append(decodeRoot.data);
					decodeRoot = root;
				}
			}
			if (j == 1) {
				decodeRoot = decodeRoot.right;
				if (decodeRoot.left == null && decodeRoot.right == null) {
					compressedFile.append(decodeRoot.data);
					decodeRoot = root;
				}
			}
		}

		// System.out.println("Decoded string is " + stringBuilder.toString());
		return compressedFile.toString();
	}

	static class HuffmanNode implements Comparable<HuffmanNode> {
		int frequency;
		char data;
		HuffmanNode left, right;

		public int compareTo(HuffmanNode node) {
			return frequency - node.frequency;
		}
	}
}
