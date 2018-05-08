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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yanwj06282
 */
@XStreamConverter(value = ExtendPropertyConverter.class)
@XStreamAlias("extend-properties")
public class ExtendProperties {

    Map<String, String> extendMap;

    public Map<String, String> getExtendMap() {
        if (extendMap == null) {
            extendMap = new LinkedHashMap<String, String>();
        }
        return extendMap;
    }

    public void setExtendMap(Map<String, String> extendMap) {
        this.extendMap = extendMap;
    }

}
