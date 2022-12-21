package bb.aoc2022;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.handler.Day10;

public class OpCode {
	static private Logger logger = Logger.getLogger(OpCode.class.getName());
	
	String cmd = null;
	
	List<String> args = new ArrayList<>();
	
	public OpCode(String line) {
		this(line, " ");
	}
	
	public OpCode(String line, String separator) {
		String[] split = line.split(separator);
		if (split.length == 0) {
			logger.error("Unable to parse OpCode: "+line);
		}
		cmd = split[0];
		for (int i=1; i<split.length; ++i) {
			args.add(split[i].trim());
		}
	}
	
	public int argCount() {
		return args.size();
	}
	
	public Integer argAsInteger(int index) {
		if (args.size() <= index) {
			logger.error("OpCode "+cmd+" doesn't have "+index+" args");
			return null;
		}
		try {
			return Integer.parseInt(args.get(index));
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
		}
		return null;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

}
