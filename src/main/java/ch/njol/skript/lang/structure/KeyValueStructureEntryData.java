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
