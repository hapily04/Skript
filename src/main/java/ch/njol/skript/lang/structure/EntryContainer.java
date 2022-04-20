package ch.njol.skript.lang.structure;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class EntryContainer {

	private final SectionNode source;
	@Nullable
	private final StructureEntryValidator entryValidator;
	private final List<Node> unhandledNodes;

	EntryContainer(SectionNode source, @Nullable StructureEntryValidator entryValidator, List<Node> unhandledNodes) {
		this.source = source;
		this.entryValidator = entryValidator;
		this.unhandledNodes = unhandledNodes;
	}

	public SectionNode getSource() {
		return source;
	}

	public List<Node> getUnhandledNodes() {
		return unhandledNodes;
	}

	@Nullable
	public Object @NonNull [] getEntryValues() {
		if (entryValidator == null)
			return new Object[0];
		return entryValidator.getValues(source);
	}

}
