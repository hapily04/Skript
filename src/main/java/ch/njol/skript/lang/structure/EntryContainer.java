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
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class EntryContainer {

	private final SectionNode source;
	@Nullable
	private final StructureEntryValidator entryValidator;
	@Nullable
	private final Map<String, Node> handledNodes;
	private final List<Node> unhandledNodes;

	EntryContainer(
		SectionNode source, @Nullable StructureEntryValidator entryValidator, @Nullable Map<String, Node> handledNodes, List<Node> unhandledNodes)
	{
		this.source = source;
		this.entryValidator = entryValidator;
		this.handledNodes = handledNodes;
		this.unhandledNodes = unhandledNodes;
	}

	public SectionNode getSource() {
		return source;
	}

	public List<Node> getUnhandledNodes() {
		return unhandledNodes;
	}

	@Nullable
	public Object getParsed(String key) {
		if (entryValidator == null || handledNodes == null)
			return null;

		StructureEntryData<?> entryData = null;
		for (StructureEntryData<?> data : entryValidator.entryDataMap) {
			if (data.getKey().equals(key)) {
				entryData = data;
				break;
			}
		}
		if (entryData == null)
			return null;

		Node node = handledNodes.get(key);
		if (node == null)
			return entryData.getDefaultValue();

		return entryData.getValue(node);
	}

	public Object getNonNullParsed(String key) {
		Object parsed = getParsed(key);
		if (parsed == null)
			throw new RuntimeException("Null value for asserted non-null value");
		return parsed;
	}

}
