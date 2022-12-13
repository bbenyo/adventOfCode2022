package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class Day13b extends Day13 implements Comparator<Day13.Packet> {
	static private Logger logger = Logger.getLogger(Day13b.class.getName());

	@Override
	public int compare(Packet p1, Packet p2) {
		PacketPair pair = new PacketPair(0);
		pair.p1 = p1;
		pair.p2 = p2;
		if (pair.correctOrder()) {
			return -1;
		} else {
			// If they're the same, it doesn't matter which order, say p2 is first
			return 1;
		}
	}
	
	List<Packet> packets = new ArrayList<>();
		
	@Override
	public void handleInput(String line) {
		line = line.trim();
		if (line.length() == 0) {
			return;
		}
		Packet p1 = new Packet(line);
		packets.add(p1);
	}
	
	@Override
	public void output() {
		Packet d1 = new Packet("[[2]]");
		Packet d2 = new Packet("[[6]]");
		packets.add(d1);
		packets.add(d2);
		
		int i1 = 0;
		int i2 = 0;
		Collections.sort(packets, this);
		for (int i=0; i<packets.size(); ++i) {
			Packet p1 = packets.get(i);
			if (p1.equals(d1)) {
				logger.info(d1+" found at index "+(i+1));
				i1 = i + 1;
			} else if (p1.equals(d2)) {
				logger.info(d2+" found at index "+(i+1));
				i2 = i + 1;
			}
		}
		
		logger.info("Decoder Key: "+i1 * i2);
	}
	
}
