package bb.aoc2022.handler;

public class Day20b extends Day20 {

	protected int decryptionKey = 811589153;
	
	@Override
	public void handleInput(String line) {
		Long i = Long.parseLong(line.trim());
		nums.add(i * decryptionKey);
	}
	
	@Override
	protected void mix() {
		for (int i=0; i<10; ++i) {
			super.mix();
		}
	}
	
}
