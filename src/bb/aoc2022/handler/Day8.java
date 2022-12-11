package bb.aoc2022.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import bb.aoc2022.InputHandler;
import bb.aoc2022.Utilities;

public class Day8 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day8.class.getName());
	
	// Allow for non square grids, we don't know the number of rows until we're done parsing
	List<List<Integer>> grid = new ArrayList<>();
	
	@Override
	public void handleInput(String line) {
		ArrayList<Integer> l = new ArrayList<>();
		line.chars().forEach(c -> l.add(c - '0'));
		grid.add(l);
	}
	
	protected String displayGrid() {
		StringBuffer sb = new StringBuffer(System.lineSeparator());
		grid.forEach(r -> {
			sb.append(Utilities.listToString(r, ""));
			sb.append(System.lineSeparator());
		});
		return sb.toString();
	}
	
	protected int getCell(int i, int j) {
		if (i < grid.size()) {
			List<Integer> row = grid.get(i);
			if (j < row.size()) {
				return row.get(j);
			}
		}
		logger.error("Invalid cell: "+i+","+j);
		return -1;
	}
	
	// Is cell i,j visible?  It's visible if there's no number equal or greater on a straight line path outside the grid
	protected boolean isVisible(int i, int j) {
		int cell = getCell(i, j);
		// Path up
		boolean vis = true;
		for (int k=i-1; k>-1; --k) {
			if (getCell(k,j) >= cell) {
				vis = false;
				break;
			}
		}
		if (vis) return true;
		
		// Down
		vis = true;
		for (int k=i+1; k<grid.size(); ++k) {
			if (getCell(k,j) >= cell) {
				vis = false;
				break;
			}
		}
		if (vis) return true;
		// Left
		vis = true;
		for (int k=j-1; k>-1; --k) {
			if (getCell(i,k) >= cell) {
				vis = false;
				break;
			}
		}
		if (vis) return true;
		// Right
		vis = true;
		List<Integer> row = grid.get(i);
		for (int k=j+1; k<row.size(); ++k) {
			if (getCell(i,k) >= cell) {
				vis = false;
				break;
			}
		}
		return vis;
	}

	@Override
	public void output() {
		logger.info(displayGrid());
		int visCount = 0;
		for (int i=0; i<grid.size(); ++i) {
			for (int j=0; j<grid.get(i).size(); ++j) {
				if (isVisible(i, j)) {
					logger.info(i+","+j+" is visible");
					visCount++;
				} else {
					logger.info(i+","+j+" is NOT visible");
				}
			}
		}
		logger.info("Visible Count: "+visCount);
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
