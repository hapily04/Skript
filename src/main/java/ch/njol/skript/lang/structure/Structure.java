/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.lang.structure;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Debuggable;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import ch.njol.util.NonNullPair;
import ch.njol.util.coll.iterator.ConsumingIterator;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// TODO STRUCTURE make sure options work everywhere (maybe have some structure validation in preload instead of init)
// TODO STRUCTURE javadocs (everywhere)
// TODO STRUCTURE add Structures to docs
public abstract class Structure implements SyntaxElement, Debuggable {

	// TODO STRUCTURE priorities
	public static class Priority implements Comparable<Priority> {
		private final int priority;

		public Priority(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}

		@Override
		public int compareTo(@NotNull Structure.Priority o) {
			return Integer.compare(this.priority, o.priority);
		}
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Config script;

	@Override
	public final boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		script = Objects.requireNonNull(getParser().getCurrentScript());

		StructureData structureData = getParser().getData(StructureData.class);

		Literal<?>[] literals = Arrays.copyOf(exprs, exprs.length, Literal[].class);

		StructureInfo<? extends Structure> structureInfo = structureData.structureInfo;
		assert structureInfo != null;
		if (structureInfo.skipEntryParsing) { // No validation necessary, the structure itself will handle it
			List<Node> unhandledNodes = new ArrayList<>();
			for (Node node : structureData.sectionNode) // All nodes are unhandled
				unhandledNodes.add(node);
			return init(literals, matchedPattern, parseResult, new EntryContainer(structureData.sectionNode, null, unhandledNodes));
		}

		StructureEntryValidator entryValidator = structureInfo.entryValidator;
		assert entryValidator != null;
		NonNullPair<Boolean, List<Node>> validated = entryValidator.validate(structureData.sectionNode);
		if (!validated.getFirst())
			return false;
		return init(literals, matchedPattern, parseResult, new EntryContainer(structureData.sectionNode, entryValidator, validated.getSecond()));
	}

	public abstract boolean init(Literal<?>[] args,
								 int matchedPattern,
								 ParseResult parseResult,
								 EntryContainer entryContainer);

	public void preload() {

	}

	public abstract void load();

	public void afterLoad() {

	}

	/**
	 * Called when this structure is unloaded, similar to {@link SelfRegisteringSkriptEvent#unregister(Trigger)}.
	 */
	public void unload() {

	}

	public abstract Priority getPriority();

	public void runWithScript(Consumer<Structure> consumer) {
		try {
			getParser().setCurrentScript(script);
			consumer.accept(this);
		} catch (Exception e) {
			//noinspection ThrowableNotThrown
			Skript.exception(e);
		} finally {
			getParser().setCurrentScript(null);
		}
	}

	@Override
	public String toString() {
		return toString(null, false);
	}

	@Nullable
	public static Structure parse(String expr, SectionNode sectionNode, @Nullable String defaultError) {
		Structure.setNode(sectionNode);

		Iterator<StructureInfo<? extends Structure>> iterator =
			new ConsumingIterator<>(Skript.getStructures().iterator(),
				elementInfo -> ParserInstance.get().getData(StructureData.class).structureInfo = elementInfo);

		ParseLogHandler parseLogHandler = SkriptLogger.startParseLogHandler();
		try {
			Structure structure = SkriptParser.parseStatic(expr, iterator, ParseContext.EVENT, defaultError);
			if (structure != null) {
				parseLogHandler.printLog();
				return structure;
			}
			parseLogHandler.printError();
			return null;
		} finally {
			parseLogHandler.stop();
		}
	}

	static void setNode(SectionNode sectionNode) {
		StructureData structureData = ParserInstance.get().getData(StructureData.class);
		structureData.sectionNode = sectionNode;
	}

	static {
		ParserInstance.registerData(StructureData.class, StructureData::new);
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	public static class StructureData extends ParserInstance.Data {
		private SectionNode sectionNode;
		@Nullable
		private StructureInfo<? extends Structure> structureInfo;

		public StructureData(ParserInstance parserInstance) {
			super(parserInstance);
		}

		@Nullable
		public StructureInfo<? extends Structure> getStructureInfo() {
			return structureInfo;
		}
	}

}
