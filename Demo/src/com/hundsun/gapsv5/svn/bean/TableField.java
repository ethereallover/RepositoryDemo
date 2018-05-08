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
package com.hundsun.gapsv5.svn.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author luoguo
 */
@XStreamAlias("table-field")
public class TableField extends Field {
    @XStreamAsAttribute
    @XStreamAlias("auto-increase")
    private boolean autoIncrease;

    /**
     * 备注
     */
    @XStreamAsAttribute
    private String comment;

    /**
     * 表字段的默认值，引用默认值唯一标识
     */
    @XStreamAsAttribute
    @XStreamAlias("default")
    private String defaultValue;

    public boolean isAutoIncrease() {
        return autoIncrease;
    }

    public void setAutoIncrease(boolean autoIncrease) {
        this.autoIncrease = autoIncrease;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
