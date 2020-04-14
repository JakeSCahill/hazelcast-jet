/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.sql.impl.connector.kafka;

import com.hazelcast.jet.sql.SqlConnector;
import com.hazelcast.jet.sql.impl.schema.JetTable;
import com.hazelcast.sql.impl.type.QueryDataType;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.type.SqlTypeName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import static com.hazelcast.jet.impl.util.Util.toList;

/**
 * {@link Table} implementation for IMap.
 */
public class KafkaTable extends JetTable {

    private final String topicName;
    private final List<Entry<String, QueryDataType>> fields;
    private final String keyClassName;
    private final String valueClassName;

    private final List<String> fieldNames;
    private final List<QueryDataType> fieldTypes;
    private final Properties kafkaProperties;

    public KafkaTable(
            @Nonnull SqlConnector sqlConnector,
            @Nonnull String topicName,
            @Nonnull List<Entry<String, QueryDataType>> fields,
            @Nullable String keyClassName,
            @Nullable String valueClassName,
            @Nonnull Properties kafkaProperties
    ) {
        super(sqlConnector);
        this.topicName = topicName;
        this.fields = fields;
        this.keyClassName = keyClassName;
        this.valueClassName = valueClassName;
        this.kafkaProperties = kafkaProperties;

        fieldNames = toList(fields, Entry::getKey);
        fieldTypes = toList(fields, Entry::getValue);
    }

    @Override
    public boolean isStream() {
        return true;
    }

    @Override
    public List<QueryDataType> getPhysicalRowType() {
        return fieldTypes;
    }

    public String getTopicName() {
        return topicName;
    }

    public List<Entry<String, QueryDataType>> getFields() {
        return fields;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        RelDataTypeFactory.Builder builder = typeFactory.builder();
        for (Entry<String, QueryDataType> field : fields) {
            RelDataType type = typeFactory.createSqlType(SqlTypeName.ANY);

            builder.add(field.getKey(), type)
                .nullable(true);
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{mapName=" + topicName + '}';
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public String getKeyClassName() {
        return keyClassName;
    }

    public String getValueClassName() {
        return valueClassName;
    }

    public Properties getKafkaProperties() {
        return kafkaProperties;
    }
}
