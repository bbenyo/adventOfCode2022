package bb.aoc2022.handler;

import org.apache.log4j.Logger;

public class Day21b extends Day21 {
	static private Logger logger = Logger.getLogger(Day21b.class.getName());
	
	@Override
	public void output() {
		Monkey root = monkeys.get("root");
		root.operator = "=";
		Monkey me = monkeys.get("humn");
		me.number = null;
		
		Monkey targetMonkey = root;
		Long targetValue = null;

		// We want to find one of the target monkey's arguments
		//   Eventually, one of the two should be bound and the other unbound
		//   We're figuring out what that unbound value should be to make the monkey shout the target value
		//   Then we iterate and figure out how to get that monkey to shout that value, etc, until we get to humn
		while (me.number == null) {
			anyChanged = false;
			for (Monkey m : monkeys.values()) {
				m.bindArguments();
			}
			if (targetValue == null) {
				// Root's value, it's equal to the other argument
				if (root.boundArgs[0] != null) {
					targetValue = root.boundArgs[0];
					// We now want to find what the other argument's monkey should shout
					targetMonkey = monkeys.get(root.args[1]);
				} else if (root.boundArgs[1] != null) {
					targetValue = root.boundArgs[1];
					// We now want to find what the other argument's monkey should shout
					targetMonkey = monkeys.get(root.args[0]);
				}
			}
			// If we know the target value, we want the target monkey to shout that value
			if (targetValue != null && targetMonkey.oneBound()) {
				Monkey newTarget = null;
				if (targetMonkey.boundArgs[0] != null) {
					newTarget = monkeys.get(targetMonkey.args[1]);
				} else {
					newTarget = monkeys.get(targetMonkey.args[0]);
				}
				targetValue = targetMonkey.reverseEngineer(targetValue);
				targetMonkey = newTarget;
			}
			if (me.equals(targetMonkey)) {
				me.number = targetValue;
			}
			if (!anyChanged) {
				logger.error("Deadlock, no further computation possible");
				return;
			}
		}
		logger.info("I should shout: "+me.number);
	}

}
