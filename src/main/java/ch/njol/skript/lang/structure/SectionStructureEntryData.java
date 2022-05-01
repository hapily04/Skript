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
