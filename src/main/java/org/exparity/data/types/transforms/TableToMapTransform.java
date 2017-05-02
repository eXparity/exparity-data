package org.exparity.data.types.transforms;

import java.util.HashMap;
import java.util.Map;

import org.exparity.data.types.Row;
import org.exparity.data.types.Table;
import org.exparity.data.types.Table.TableTransform;

/**
 * Implementation of a {@link TableTransform} to convert a {@link Table} into a {@link Map}
 */
public class TableToMapTransform implements TableTransform<Map<String, String>> {

    private final int keyIndex;
    private final int valueIndex;

    public TableToMapTransform(final int keyIndex, final int valueIndex) {
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
    }

    @Override
    public Map<String, String> apply(final Table table) {
        Map<String, String> map = new HashMap<>();
        for (Row row : table.rowIterator()) {
            map.put(row.getValueAsString(keyIndex), row.getValueAsString(valueIndex));
        }
        return map;
    }

}
