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

package org.tautua.markdownpapers;

import org.tautua.markdownpapers.ast.*;

import java.io.IOException;

import static org.tautua.markdownpapers.util.Utils.*;
/**
 * <p>HTML generator.</p>
 *
 * @author Larry Ruiz
 */
public class RawEmitter implements Visitor {
    private Appendable buffer;

    public RawEmitter(Appendable buffer) {
        this.buffer = buffer;
    }

    public void visit(CharRef node) {
        append(node.getValue());
    }

    public void visit(Code node) {
        visitChildrenAndAppendSeparator(node, EOL);
        append(EOL);
    }

    public void visit(CodeSpan node) {
    	append('`');
        escapeAndAppend(node.getText());
        append('`');
    }

    public void visit(CodeText node) {
        escapeAndAppend(node.getValue());
    }

    public void visit(Comment node) {
        append(node.getText());
    }

    public void visit(Document node) {
        visitChildrenAndAppendSeparator(node, EOL);
    }

    public void visit(Emphasis node) {
        switch (node.getType()) {
            case ITALIC:
            	append('*');
                node.childrenAccept(this);
            	append('*');
                break;
            case BOLD:
            	append("**");
                node.childrenAccept(this);
                append("**");
                break;
            case ITALIC_AND_BOLD:
            	append("***");
                node.childrenAccept(this);
                append("***");
                break;
        }
    }

    public void visit(EmptyTag node) {
        TagAttributeList attributes = node.getAttributeList();
        append("<");
        append(node.getName());

        if(attributes != null) {
            attributes.accept(this);
        }
        append("/>");
    }

    public void visit(EndTag node) {
        append("</");
        append(node.getName());
        append(">");
    }

    public void visit(Header node) {
    	for(int i=0;i<node.getLevel();i++){
    		append('#');
    	}
        node.childrenAccept(this);
        append(EOL);
    }

    public void visit(Image node) {
    	
        Resource resource = node.getResource();
        if (resource == null) {
            if(node.getReference() != null){
                append("![");
                if (node.getText() != null) {
                    escapeAndAppend(node.getText());
                }
                append("][");
                escapeAndAppend(node.getReference());
                append("]");
            } else {
                append("![img]( \"");
                if (node.getText() != null) {
                    escapeAndAppend(node.getText());
                }
                append("\")");
            }
        } else {
            append("![");
            append(node.getText());
            append("](");
            escapeAndAppend(resource.getLocation());
            if (resource.getHint() != null) {
                append(" \"");
                escapeAndAppend(resource.getHint());
                append("\"");
            }
            append(')');
        }
    }

    public void visit(InlineUrl node) {
        append('[');
        escapeAndAppend(node.getUrl());
        append("](");
        escapeAndAppend(node.getUrl());
        append(')');
    }

    public void visit(Item node) {
    	if(node.isOrdered()){
    		append("1. ");	
    	}else{
    		append("* ");
    	}
        node.childrenAccept(this);
        append(EOL);
    }

    public void visit(Line node) {
        node.childrenAccept(this);
    }

    @Override
    public void visit(LineBreak node) {
        Line l = (Line) node.jjtGetParent();
        if(!l.isEnding()) {
            append("\r\n");
        }
    }

    public void visit(Link node) {
        Resource resource = node.getResource();
        if (resource == null) {
            if (node.isReferenced()) {
                append("[");
                node.childrenAccept(this);
                append("]");
                if (node.getReference() != null) {
                    if (node.hasWhitespaceAtMiddle()) {
                        append(' ');
                    }
                    append("[");
                    append(node.getReference());
                    append("]");
                }
            } else {
                append("[");
                node.childrenAccept(this);
                append("]()");
            }
        } else {
            append("[");
            node.childrenAccept(this);
            
            append("](");
            escapeAndAppend(resource.getLocation());
            if (resource.getHint() != null) {
                append(" \"");
                escapeAndAppend(resource.getHint());
                append("\"");
            }
            append(")");
        }
    }

    public void visit(ResourceDefinition node) {
        // do nothing
    }

    public void visit(List node) {
//        if (node.isOrdered()) {
//            append("1. ");
//            append(EOL);
//        
//        } else {
//            append("* ");
//            append(EOL);
//            node.childrenAccept(this);
//        }
        node.childrenAccept(this);
       // append(EOL);
    }

    public void visit(Paragraph node) {
        Node parent = node.jjtGetParent();
        if(parent instanceof Item) {
            if (!((Item)parent).isLoose()) {
                visitChildrenAndAppendSeparator(node, EOL);
                return;
            }
        }
        visitChildrenAndAppendSeparator(node, EOL);
        append(EOL);
    }

    public void visit(Ruler node) {
        append("----");
        append(EOL);
    }

    public void visit(Quote node) {
        append("<blockquote>");
        append(EOL);
        node.childrenAccept(this);
        append("</blockquote>");
        append(EOL);
    }

    public void visit(SimpleNode node) {
        throw new IllegalArgumentException("can not process this element");
    }

    public void visit(Tag node) {
        TagAttributeList attributes = node.getAttributeList();
        TagBody body = node.getBody();

        append("<");
        append(node.getName());

        if(attributes != null) {
            attributes.accept(this);
        }

        if(body == null) {
            if(isEmptyTag(node.getName())) {
                append("/>");
            } else {
                append("></");
                append(node.getName());
                append(">");
            }
        } else {
            append(">");
            body.accept(this);
            append("</");
            append(node.getName());
            append(">");
        }
    }

    @Override
    public void visit(TagAttribute node) {
        append(SPACE);
        append(node.getName());
        append("=\"");
        append(node.getValue());
        append("\"");
    }

    @Override
    public void visit(TagAttributeList node) {
        node.childrenAccept(this);
    }

    @Override
    public void visit(TagBody node) {
        node.childrenAccept(this);
    }

    public void visit(Text node) {
        if(node.jjtGetParent() instanceof TagBody) {
            append(node.getValue());
        } else {
            escapeAndAppend(node.getValue());
        }
    }

    @Override
    public void visit(StartTag node) {
        TagAttributeList attributes = node.getAttributeList();
        append(node.getName());

        if(attributes != null) {
            attributes.accept(this);
        }
    }

    protected void visitChildrenAndAppendSeparator(Node node, char separator){
        int count = node.jjtGetNumChildren();
        for(int i = 0; i < count; i++) {
            node.jjtGetChild(i).accept(this);
            if(i < count - 1) {
                append(separator);
            }
        }
    }

    protected void visit(Node[] nodes) {
        for (Node n : nodes) {
            n.accept(this);
        }
    }

    protected void escapeAndAppend(String val) {
        for(char character : val.toCharArray()) {
            append(escape(character));
        }
    }

    protected void append(String val) {
        try {
            buffer.append(val);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void append(char val) {
        try {
            buffer.append(val);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
