package ch.njol.skript.lang.structure.util;

import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.structure.KeyValueStructureEntryData;
import ch.njol.skript.registrations.Classes;
import org.eclipse.jdt.annotation.Nullable;

public class LiteralStructureEntryData<T> extends KeyValueStructureEntryData<T> {

	private final Class<T> type;

	public LiteralStructureEntryData(String key, String separator, Class<T> type) {
		super(key, separator);
		this.type = type;
	}

	public LiteralStructureEntryData(String key, T defaultValue, String separator, Class<T> type) {
		super(key, defaultValue, separator);
		this.type = type;
	}

	public LiteralStructureEntryData(String key, boolean optional, String separator, Class<T> type) {
		super(key, optional, separator);
		this.type = type;
	}

	@Override
	@Nullable
	public T getValue(String value) {
		return Classes.parse(value, type, ParseContext.DEFAULT);
	}

}
