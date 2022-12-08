package bb.aoc2022.handler;

import java.util.List;

import org.apache.log4j.Logger;

public class Day8b extends Day8 {
	static private Logger logger = Logger.getLogger(Day8b.class.getName());
	
	protected int getScenicScore(int i, int j) {
		int cell = getCell(i, j);
		// Path up
		int upScore = 0;
		for (int k=i-1; k>-1; --k) {
			upScore++;
			if (getCell(k,j) >= cell) {
				break;
			}
		}
		
		// Down
		int dScore = 0;
		for (int k=i+1; k<grid.size(); ++k) {
			dScore++;
			if (getCell(k,j) >= cell) {
				break;
			}
		}
		// Left
		int lScore = 0;
		for (int k=j-1; k>-1; --k) {
			lScore++;
			if (getCell(i,k) >= cell) {
				break;
			}
		}
		// Right
		int rScore = 0;
		List<Integer> row = grid.get(i);
		for (int k=j+1; k<row.size(); ++k) {
			rScore++;
			if (getCell(i,k) >= cell) {
				break;
			}
		}
		
		return upScore * dScore * lScore * rScore;
	}
	
	@Override
	public void output() {
		logger.info(displayGrid());
		int maxScore = -1;
		int maxI = -1, maxJ = -1;
		for (int i=1; i<grid.size()-1; ++i) {
			for (int j=1; j<grid.get(i).size()-1; ++j) {
				int score = getScenicScore(i, j);
				logger.info("Scenic Score for "+i+","+j+" = "+score);
				if (score > maxScore) {
					maxScore = score;
					maxI = i;
					maxJ = j;
				}
			}
		}
		logger.info("Best Scenic Score: "+maxScore+" at "+maxI+","+maxJ);
	}
}
