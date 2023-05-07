package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.jdt.annotation.Nullable;

@Name("Execute Times")
@Description("Execute an effect x amount of times with an optional interval between each run.\n " +
	"There is no delay before the effect is ran.")
@Examples({"command /clearchat:",
	"\tpermission: skript.clearchat",
	"\ttrigger:",
	"\t\tbroadcast \" \" 300 times",
	"\t\tbroadcast \"The chat has been cleared by %player%!\"",
	"",
	"command /tntparade:",
	"\tpermission: skript.tntparade",
	"\ttrigger:",
	"\t\tbroadcast \"A TNT Parade has been activated by %player%! You will receive 1 tnt/sec over the next 10 seconds!\"",
	"\t\tgive 1 tnt to all players 10 times with interval 1 second"})
@Since("INSERT VERSION")
public class EffExecuteTimes extends Effect {

	static {
		Skript.registerEffect(EffExecuteTimes.class, "[(repeat|run|execute)] <.+> " +
			"(times:%-number% time[s]|:twice|:thrice) [(with interval|every) %-timespan%]");
	}

	@Nullable
	private Effect effect;
	@Nullable
	private Expression<Number> times;
	@Nullable
	private Expression<Timespan> timespan;
	private int mark;
	private int timesRan;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		mark = parseResult.mark;
		String eff = parseResult.regexes.get(0).group();
		effect = Effect.parse(eff, "Can't understand this effect: " + eff);
		if (effect instanceof EffExecuteTimes) {
			Skript.error("Cannot use the execute times effect within itself.");
			return false;
		}
		if (parseResult.hasTag("times")) {
			mark = 0;
			times = (Expression<Number>) exprs[0];
			if (times instanceof Literal) {
				int amount = ((Literal<Number>) times).getSingle().intValue();
				if (amount == 0) {
					Skript.warning("Looping zero times makes the code inside of the loop useless");
				}
				else if (amount == 1) {
					Skript.warning("Looping once makes no sense in this effect as you could effectively remove the execute times portion instead.");
				}
				else if (amount < 0) {
					Skript.error("You cannot loop a negative amount of times.");
					return false;
				}
			}
			timespan = (Expression<Timespan>) exprs[1];
		}
		else {
			if (parseResult.hasTag("twice")) {
				mark = 2;
			}
			else {
				mark = 3;
			}
			timespan = (Expression<Timespan>) exprs[0];
		}
		return effect != null;
	}

	@Override
	protected void execute(Event event) {
		int howMany;
		if (mark == 0) {
			assert times != null;
			Number t = times.getSingle(event); // Using this approach as opposed to ternary for readability and less getSingle calls
			if (t == null) {
				howMany = 0; // If it's null nothing should run
			}
			else {
				howMany = t.intValue();
			}
		}
		else {
			howMany = mark;
		}
		assert effect != null; // Shouldn't be null because it'll never run this effect if it's null according to the init method
		Timespan timespan = this.timespan == null ? null : this.timespan.getSingle(event);
		if (timespan == null) {
			for (int i = 0; i < howMany; i++) {
				effect.run(event);
			}
		}
		else {
			Object localVariables = Variables.copyLocalVariables(event);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (timesRan >= howMany) {
						cancel();
					}
					else {
						if (localVariables != null) {
							Variables.setLocalVariables(event, localVariables);
						}
						effect.run(event);
						timesRan++;
						Variables.removeLocals(event);
					}
				}
			}.runTaskTimer(Skript.getInstance(), 0, timespan.getTicks_i());
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String timesData;
		if (mark == 0) {
			timesData = (times == null ? 0 : times.toString(event, debug)) + " times";
		}
		else if (mark == 2) {
			timesData = "twice";
		}
		else {
			timesData = "thrice";
		}
		return effect.toString(event, debug) + " " + timesData + (timespan == null ? "" : " with interval " + timespan.toString(event, debug));
	}

}
