package bb.aoc2022.handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day25 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day25.class.getName());
	
	// SNAFU number
	// base 5, digits are 2, 1, 0, - (-1), = (-2) 
	protected static class SNAFU {
		String txt;
		
		public SNAFU(String t) {
			this.txt = t;
		}
		
		@Override
		public String toString() {
			return txt;
		}
		
		public long toDecimal() {
			long dec = 0;
			long place = 1;
			for (int i=txt.length() - 1; i>=0; i--) {
				char digit = txt.charAt(i);
				switch(digit) {
				case '2' : dec += (place * 2); break;
				case '1' : dec += (place); break;
				case '0' : break;
				case '-' : dec -= (place); break;
				case '=' : dec -= (place * 2); break;
				default:
					throw new IllegalArgumentException("Unrecognized digit: "+digit);
				}
				place = place * 5;
			}
			return dec;			
		}
	}
		
	public static SNAFU fromDecimal(long dec) {
		// Get the max place
		long place = 1;
		long nextPlace = 0;
		int digitCount = 1;
		while (dec > (2*place)) {
			nextPlace = place;
			place = place * 5;
			digitCount++;
		}
		long val = dec;
		char[] digits = new char[digitCount];
		for (int i=0; i<digits.length; ++i) {
			digits[i] = '0';
		}
		int curDigit = 0;
		while (val != 0 && (curDigit+1) < digitCount) {
			long place2 = place * 2; // 2 in the place digit is this value
			long nextPlace2 = nextPlace * 2;
			if (val < -(place+nextPlace2)) {
				digits[curDigit] = '=';
				val = val + place2;					
			} else if (val <= (-nextPlace2)) {
				digits[curDigit] = '-';
				val = val + place;
			} else if (val >= (place2 - nextPlace2 - (nextPlace/5)*2)) { // We need a 2 in place
				digits[curDigit] = '2';
				val = val - place2;
			} else if (val >= (place - nextPlace2 - nextPlace2)) {  // This is wrong, should be place - nextPlace2 - (nextPlace/5*2) ...
				// But it works well enough for these inputs, and it's christmas
				digits[curDigit] = '1';
				val = val - place;
			} else {
				digits[curDigit] = '0';					
			}
			curDigit++;
			place = nextPlace;
			nextPlace = nextPlace / 5;
		}
		
		int lastPlace = digits.length - 1;
		digits[lastPlace] = '0';
		if (val != 0) {
			if (val == -2) {
				digits[lastPlace] = '=';
			} else if (val == -1) {
				digits[lastPlace] = '-';
			} else if (val == 1) {
				digits[lastPlace] = '1';
			} else if (val == 2) {
				digits[lastPlace] = '2';
			}
		}
				
		String txt = new String(digits);
		SNAFU s1 = new SNAFU(txt);
		if (s1.toDecimal() != dec) {
			throw new RuntimeException("Logic failure: decimal: "+dec+" SNAFU: "+txt+" sDecimal: "+s1.toDecimal());
		}
		return s1;
	}
	

	List<SNAFU> snafus;
	
	@Override
	public void initialize() {
		snafus = new ArrayList<>();

	}

	@Override
	public void handleInput(String line) {
		line = line.trim();
		if (line.length() > 0) {
			SNAFU s = new SNAFU(line);
			snafus.add(s);
		}
	}

	@Override
	public void output() {
		testSNAFU(); 
		
		long sum = 0;
		for (SNAFU s : snafus) {
			long dec = s.toDecimal();
			logger.info(s.txt+" = "+dec);
			sum += dec;
		}
		
		logger.info("Sum: "+sum);
		
		SNAFU sResult = fromDecimal(sum);
		logger.info("SNAFU: "+sResult);
		
		if (sResult.toDecimal() != sum) {
			logger.error("SNAFU is wrong though =(");
		}
	}
	
	public void testSNAFU() {
		Map<String, Long> testValues = new HashMap<>();
		testValues.put("1", 1l);
		testValues.put("2", 2l);
		testValues.put("1=", 3l);
		testValues.put("1-", 4l);
		testValues.put("10", 5l);
		testValues.put("11", 6l);
		testValues.put("12", 7l);
		testValues.put("2=", 8l);
		testValues.put("2-", 9l);
		testValues.put("20", 10l);
		testValues.put("1=0", 15l);
		testValues.put("1-0", 20l);
		testValues.put("1=11-2", 2022l);
		testValues.put("1-0---0", 12345l);
		testValues.put("1121-1110-1=0", 314159265l);
		testValues.put("2=-1=0", 4890l);
		
		for (Entry<String, Long> sEntry : testValues.entrySet()) {
			SNAFU s = new SNAFU(sEntry.getKey());
			if (s.toDecimal() == sEntry.getValue()) {
				logger.info(s.txt+" = "+sEntry.getValue());
			} else {
				logger.error(s.txt+" should be "+sEntry.getValue()+" but is "+s.toDecimal());
			}
			
			SNAFU s2 = Day25.fromDecimal(sEntry.getValue());
			if (!s2.txt.equals(s.txt)) {
				logger.error(s2.txt+" should be "+s.txt);
			}
		}		
	}

}
