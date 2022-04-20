package ch.njol.skript.lang.structure;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.lang.structure.StructureEntryValidator.StructureEntryValidatorBuilder;
import org.eclipse.jdt.annotation.Nullable;

public abstract class KeyValueStructureEntryData<T> extends StructureEntryData<T> {

	public KeyValueStructureEntryData(String key) {
		super(key);
	}

	public KeyValueStructureEntryData(String key, @Nullable T defaultValue) {
		super(key, defaultValue);
	}

	public KeyValueStructureEntryData(String key, boolean optional) {
		super(key, optional);
	}

	@Nullable
	@Override
	public final T getValue(Node node) {
		assert node instanceof SimpleNode;
		String key = node.getKey();
		if (key == null)
			return null;
		return getValue(ScriptLoader.replaceOptions(key).substring(getKey().length() + getSeparator().length()));
	}

	@Nullable
	public abstract T getValue(String value);

	// TODO by default this should probably use the entry separator specified in builder
	public String getSeparator() {
		return StructureEntryValidatorBuilder.DEFAULT_ENTRY_SEPARATOR;
	}

	@Override
	public boolean canCreateWith(Node node) {
		return node instanceof SimpleNode && super.canCreateWith(node);
	}

}
