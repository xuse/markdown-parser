package org.tautua.markdownpapers.ast;

public abstract class VisitorAdapter implements Visitor{
	public void visit(CharRef node) {
		visitChildren(node);
	}

	public void visit(Code node) {
		visitChildren(node);
	}

	public void visit(CodeSpan node) {
		visitChildren(node);
	}

	public void visit(CodeText node) {
		visitChildren(node);
	}

	public void visit(Comment node) {
		visitChildren(node);
	}

	public void visit(Document node) {
		visitChildren(node);
	}

	public void visit(Emphasis node) {
		visitChildren(node);
	}

	public void visit(EmptyTag node) {
		visitChildren(node);
	}

	public void visit(EndTag node) {
		visitChildren(node);
	}

	public void visit(Header node) {
		visitChildren(node);
	}

	public void visit(Image node) {
		visitChildren(node);
	}

	public void visit(Line node) {
		visitChildren(node);
	}

	public void visit(LineBreak node) {
		visitChildren(node);
	}

	public void visit(Link node) {
		visitChildren(node);
	}

	public void visit(List node) {
		visitChildren(node);
	}

	public void visit(InlineUrl node) {
		visitChildren(node);
	}

	public void visit(Item node) {
		visitChildren(node);
	}

	public void visit(Paragraph node) {
		visitChildren(node);
	}

	public void visit(Quote node) {
		visitChildren(node);
	}

	public void visit(ResourceDefinition node) {
		visitChildren(node);
	}

	public void visit(Ruler node) {
		visitChildren(node);
	}

	public void visit(SimpleNode node) {
		visitChildren(node);
	}

	public void visit(Tag node) {
		visitChildren(node);
	}

	public void visit(TagAttribute node) {
		visitChildren(node);
	}

	public void visit(TagAttributeList node) {
		visitChildren(node);
	}

	public void visit(TagBody node) {
		visitChildren(node);
	}
	
	public void visit(Text node) {
		visitChildren(node);
	}

	public void visit(StartTag node) {
		visitChildren(node);
	}
	
    protected void visitChildren(Node node){
        int count = node.jjtGetNumChildren();
        for(int i = 0; i < count; i++) {
            node.jjtGetChild(i).accept(this);
        }
    }
}
