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

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.util.NonNullPair;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StructureEntryValidator {

	public static StructureEntryValidatorBuilder builder() {
		return new StructureEntryValidatorBuilder();
	}

	private final List<StructureEntryData<?>> entryDataMap;
	private final boolean allowUnknownEntries, allowUnknownSections;

	private StructureEntryValidator(List<StructureEntryData<?>> entryDataMap, boolean allowUnknownEntries, boolean allowUnknownSections) {
		this.entryDataMap = entryDataMap;
		this.allowUnknownEntries = allowUnknownEntries;
		this.allowUnknownSections = allowUnknownSections;
	}

	public boolean allowsUnknownEntries() {
		return allowUnknownEntries;
	}

	public boolean allowsUnknownSections() {
		return allowUnknownSections;
	}

	public NonNullPair<Boolean, List<Node>> validate(SectionNode sectionNode) {

		List<StructureEntryData<?>> entries = new ArrayList<>(entryDataMap);
		List<Node> unhandledNodes = new ArrayList<>();

		boolean ok = true;
		for (Node node : sectionNode) {
			if (node.getKey() == null)
				continue;

			StructureEntryData<?> entryData = null;
			for (StructureEntryData<?> data : entryDataMap) {
				if (data.canCreateWith(node)) { // Determine if it's a match
					entryData = data;
					break;
				}
			}

			if (entryData == null) { // Key isn't registered with the validator
				if ((!allowUnknownEntries && node instanceof SimpleNode) ||
					(!allowUnknownSections && node instanceof SectionNode)
				) {
					ok = false;
					Skript.error("Unexpected entry '" + node.getKey() + "'. Check whether it's spelled correctly or remove it");
				} else {
					unhandledNodes.add(node);
				}
			} else { // We have checked this data successfully
				entries.remove(entryData);
			}
		}

		for (StructureEntryData<?> entryData : entries) { // Check for missing required entries
			if (!entryData.isOptional() && !entryData.hasDefaultValue()) { // It's required, and there's no default value to use as a backup
				Skript.error("Required entry '" + entryData.getKey() + "' is missing");
				ok = false;
			}
		}

		return new NonNullPair<>(ok, unhandledNodes);
	}

	@Nullable
	public Object @NonNull [] getValues(SectionNode sectionNode) {
		Object[] values = new Object[entryDataMap.size()];

		List<Node> nodes = new ArrayList<>();
		for (Node node : sectionNode)
			nodes.add(node);

		int pos = 0;
		for (StructureEntryData<?> entryData : entryDataMap) {
			String key = entryData.getKey();

			Node foundNode = null;
			for (Node node : nodes) {
				if (key.equalsIgnoreCase(node.getKey())) {
					foundNode = node;
					break;
				}
			}

			if (foundNode != null) {
				values[pos] = entryData.getValue(foundNode);
				nodes.remove(foundNode);
			} else {
				values[pos] = null;
			}

			pos++;
		}

		return values;
	}

	public static class StructureEntryValidatorBuilder {

		/**
		 * The default separator used for all {@link KeyValueStructureEntryData}.
		 */
		public static final String DEFAULT_ENTRY_SEPARATOR = ": ";

		private StructureEntryValidatorBuilder() { }

		private final List<StructureEntryData<?>> entryData = new ArrayList<>();
		private String entrySeparator = DEFAULT_ENTRY_SEPARATOR;
		private boolean allowUnknownEntries, allowUnknownSections;

		public StructureEntryValidatorBuilder entrySeparator(String separator) {
			this.entrySeparator = separator;
			return this;
		}

		public StructureEntryValidatorBuilder allowUnknownEntries() {
			allowUnknownEntries = true;
			return this;
		}

		public StructureEntryValidatorBuilder allowUnknownSections() {
			allowUnknownSections = true;
			return this;
		}

		public StructureEntryValidatorBuilder addEntry(String key) {
			return addEntry(key, false);
		}

		public StructureEntryValidatorBuilder addEntry(String key, String defaultValue) {
			entryData.add(new KeyValueStructureEntryData<String>(key, defaultValue) {
				@Override
				public String getValue(String value) {
					return value;
				}

				@Override
				public String getSeparator() {
					return entrySeparator;
				}
			});
			return this;
		}

		public StructureEntryValidatorBuilder addEntry(String key, boolean optional) {
			entryData.add(new KeyValueStructureEntryData<String>(key, optional) {
				@Override
				public String getValue(String value) {
					return value;
				}

				@Override
				public String getSeparator() {
					return entrySeparator;
				}
			});
			return this;
		}

		public StructureEntryValidatorBuilder addSection(String key) {
			return addSection(key, false);
		}

		public StructureEntryValidatorBuilder addSection(String key, boolean optional) {
			entryData.add(new SectionStructureEntryData(key, optional));
			return this;
		}

		public StructureEntryValidatorBuilder addEntryData(StructureEntryData<?> entryData) {
			this.entryData.add(entryData);
			return this;
		}

		public StructureEntryValidator build() {
			return new StructureEntryValidator(entryData, allowUnknownEntries, allowUnknownSections);
		}

	}

}
