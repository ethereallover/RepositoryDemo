/**
 * Copyright (c) 2012-2017, www.tinygroup.org (luo_guo@icloud.com).
 * <p>
 *  Licensed under the GPL, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * <p>
 *       http://www.gnu.org/licenses/gpl.html
 * <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 */
package com.hundsun.gapsv5.svn.bean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author yanwj06282
 */
public class ExtendPropertyConverter extends AbstractCollectionConverter {

    /**
     * @param mapper
     */
    public ExtendPropertyConverter(Mapper mapper) {
        super(mapper);
    }

    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return type.equals(ExtendProperties.class); // Used by java.awt.Font in JDK 6  
    }

    @SuppressWarnings("rawtypes")
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        ExtendProperties map = (ExtendProperties) source;
        for (Iterator iterator = map.getExtendMap().entrySet().iterator(); iterator.hasNext(); ) {
            Entry entry = (Entry) iterator.next();
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, "property", Entry.class);

            writer.addAttribute("key", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ExtendProperties map = (ExtendProperties) createCollection(context.getRequiredType());
        populateMap(reader, context, map);
        return map;
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, ExtendProperties map) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object key = reader.getAttribute("key");
            Object value = reader.getAttribute("value");
            map.getExtendMap().put(key.toString(), value.toString());
            reader.moveUp();
        }
    }
} 
