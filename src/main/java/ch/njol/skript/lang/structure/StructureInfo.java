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

import ch.njol.skript.lang.SyntaxElementInfo;
import org.eclipse.jdt.annotation.Nullable;

public class StructureInfo<E extends Structure> extends SyntaxElementInfo<E> {

	public final boolean skipEntryParsing;
	@Nullable
	public final StructureEntryValidator entryValidator;

	public StructureInfo(String[] patterns, Class<E> c, String originClassPath) throws IllegalArgumentException {
		super(patterns, c, originClassPath);
		skipEntryParsing = true;
		entryValidator = null;
	}

	public StructureInfo(String[] patterns, Class<E> c, String originClassPath, StructureEntryValidator entryValidator) throws IllegalArgumentException {
		super(patterns, c, originClassPath);
		skipEntryParsing = false;
		this.entryValidator = entryValidator;
	}

}
