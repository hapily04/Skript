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
