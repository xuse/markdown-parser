/*
 * Copyright 2011, TAUTUA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tautua.markdownpapers.ast;

import java.util.*;

/**
 * @author Larry Ruiz
 */
public class Tag extends SimpleNode {
    protected String name;

    public Tag(int i) {
        super(i);
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public TagAttributeList getAttributeList() {
        for(int i = 0; i < children.length; i++) {
            if(children[i] instanceof TagAttributeList) {
                return (TagAttributeList) children[i];
            }
        }
        return null;
    }

    public TagBody getBody() {
        for(int i = 0; i < children.length; i++) {
            if(children[i] instanceof TagBody) {
                return (TagBody) children[i];
            }
        }
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
