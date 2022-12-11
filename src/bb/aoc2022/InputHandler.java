package bb.aoc2022;

public interface InputHandler {

	// Initialization
	public void initialize();
	
	// Handle 1 input line
	public void handleInput(String line);
	
	// Write output to the console
	public void output();
	
}
