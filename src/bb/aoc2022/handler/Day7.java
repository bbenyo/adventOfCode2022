package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;

public class Day7 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day7.class.getName());
	
	protected class File {
		String name;
		long size;
		
		public File(String n, long sz) {
			this.name = n;
			this.size = sz;
		}
		
		public String toString(String indent) {
			StringBuffer sb = new StringBuffer(indent);
			sb.append("- "+name);
			sb.append(" (file, size="+size);
			sb.append(")");
			return sb.toString();
		}
		
		public long size() {
			return size;
		}
		
		@Override
		public String toString() {
			return toString("");
		}
	}
	
	protected class Directory extends File {
		
		List<File> files;
		Directory parent;
		boolean listed = false; // Have we done an ls yet?
		
		public Directory(String n) {
			super(n, 0);
			files = new ArrayList<>();
			parent = null;
		}
		
		public Directory cd(String subdir) {
			for (File f : files) {
				if (f.name.equals(subdir) && f instanceof Directory) {
					return (Directory)f;
				}
			}
			logger.error("Unable to find subdirectory "+subdir+" of "+name);
			return this;
		}
		
		public void addFile(String lsOutput) {
			if (lsOutput.startsWith("dir ")) {
				Directory d = new Directory(lsOutput.substring(4));
				d.parent = this;
				files.add(d);
				logger.info("Adding directory "+d.name+" of "+name);
				dirs.add(d);
			} else {
				String[] ls1 = lsOutput.split(" ");
				if (ls1.length != 2) {
					logger.info("Can not parse ls output: "+lsOutput);
				} else {
					try {
						long size = Long.parseLong(ls1[0]);
						File f = new File(ls1[1], size);
						files.add(f);
					} catch (NumberFormatException ex) {
						logger.error(ex.toString(), ex);
					}
				}
			}
		}
		
		@Override
		public long size() {
			long total = 0;
			for (File f : files) {
				total += f.size();
			}
			return total;
		}
		
		@Override
		public String toString(String indent) {
			StringBuffer sb = new StringBuffer(indent);
			sb.append("- "+name);
			sb.append(" (dir)");
			String nIndent = indent + "  ";
			for (File f : files) {
				sb.append(System.lineSeparator());
				sb.append(f.toString(nIndent));
			}
			return sb.toString();
		}
	}
	
	Directory root = new Directory("/");
	Directory cwd = root;
	HashSet<Directory> dirs = new HashSet<>();
	
	String lastCommand = null;
	List<String> cmdOutput = new ArrayList<>();

	@Override
	public void handleInput(String line) {
		// Initilization
		if (dirs.isEmpty()) {
			dirs.add(root);
		}
		line = line.trim();
		if (line.startsWith("$")) {
			if (lastCommand != null) {
				handleMultiLineCommand();
			}
			lastCommand = null;
			String[] cmd = line.split(" ");
			if (cmd.length > 1) { // $ cmd [args]
				String command = cmd[1];
				switch (command) {
				case "ls" : 
					cmdOutput.clear();
					lastCommand = "ls";
					break;				
				case "cd" : handleCD(cmd); break;
				default :
					logger.error("Unrecognized command: "+command);
				}
			} else {
				logger.error("Invalid command: "+line);
			}
		} else {
			cmdOutput.add(line);
		}
	}
	
	protected void handleCD(String[] cmd) {
		if (cmd.length < 3) {
			logger.error("cd command with no argument: "+cmd);
			return;
		}
		switch (cmd[2]) {
		case "/": cwd = root; break;
		case "..": 
			if (cwd.parent != null) {
				cwd = cwd.parent;
			} else {
				logger.error("Error, no parent directory for "+cwd.name);
			}
			break;
		
		default:
			if (cwd.listed) {
				cwd = cwd.cd(cmd[2]);
			} else {
				logger.error("Trying to cd to a subdirectory we haven't listed yet: "+cwd.name);
			}
		}
	}
	
	protected void handleMultiLineCommand() {
		switch(lastCommand) {
		case "ls" : handleLS(); break;
		default:
			logger.error("Unrecognized command: "+lastCommand);
		}
	}
	
	protected void handleLS() {
		for (String line : cmdOutput) {
			cwd.addFile(line);
			cwd.listed = true;
		}
	}
	
	
	@Override
	public void output() {
		if (lastCommand != null) {
			handleMultiLineCommand();
		}
		logger.info(root.toString(""));
		long total = 0;
		for (Directory d : dirs) {
			long sz = d.size();
			if (sz <= 100000) {
				logger.info(d.name+" size is <= 100000");
				total += sz;				
			}
		}
		
		logger.info("Total Size: "+total);
	}
	
}
