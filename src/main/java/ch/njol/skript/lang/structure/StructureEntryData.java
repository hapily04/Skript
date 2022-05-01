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

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import org.eclipse.jdt.annotation.Nullable;

public abstract class StructureEntryData<T> {

	private final String key;
	@Nullable
	private final T defaultValue;
	private final boolean optional;

	public StructureEntryData(String key) {
		this(key, null, false);
	}

	public StructureEntryData(String key, T defaultValue) {
		this(key, defaultValue, true);
	}

	public StructureEntryData(String key, boolean optional) {
		this(key, null, optional);
	}

	private StructureEntryData(String key, @Nullable T defaultValue, boolean optional) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.optional = optional;
	}

	public String getKey() {
		return key;
	}

	@Nullable
	public T getDefaultValue() {
		return defaultValue;
	}

	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	public boolean isOptional() {
		return optional;
	}

	@Nullable
	public abstract T getValue(Node node);

	public boolean canCreateWith(Node node) {
		String key = node.getKey();
		if (key == null)
			return false;
		return key.equalsIgnoreCase(ScriptLoader.replaceOptions(key));
	}

}
