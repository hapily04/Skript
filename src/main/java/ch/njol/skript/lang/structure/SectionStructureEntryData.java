package ch.njol.skript.lang.structure;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import org.eclipse.jdt.annotation.Nullable;

public class SectionStructureEntryData extends StructureEntryData<SectionNode> {

	public SectionStructureEntryData(String key) {
		super(key);
	}

	public SectionStructureEntryData(String key, boolean optional) {
		super(key, optional);
	}

	public SectionStructureEntryData(String key, @Nullable SectionNode defaultValue) {
		super(key, defaultValue);
	}

	@Override
	@Nullable
	public SectionNode getValue(Node node) {
		assert node instanceof SectionNode;
		return (SectionNode) node;
	}

	@Override
	public boolean canCreateWith(Node node) {
		return node instanceof SectionNode && super.canCreateWith(node);
	}

}
