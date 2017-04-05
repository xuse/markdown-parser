package org.tautua.markdownpapers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.tautua.markdownpapers.ast.Document;
import org.tautua.markdownpapers.ast.Header;
import org.tautua.markdownpapers.ast.Image;
import org.tautua.markdownpapers.ast.Node;
import org.tautua.markdownpapers.ast.VisitorAdapter;
import org.tautua.markdownpapers.parser.ParseException;
import org.tautua.markdownpapers.parser.Parser;

public class Case {

	@Test
	public void testParser() throws IOException, ParseException {
		final String root=this.getClass().getResource("/").getPath();
		File file = new File(root,"ef/云数据库.md");
		
		//示例：解析markdown
		Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		Parser p = new Parser(reader);
		Document doc = p.parse();
		reader.close();
		
		//示例：图片处理
		{
			doc.accept(new VisitorAdapter() {
				public void visit(Image node) {
					if(node.getResource()!=null){
						String location=node.getResource().getLocation();
						System.out.println("处理图片，仅作示例"+location);
						File file=new File(root+"/ef",location);
						//在文字中加上图片的大小
						node.setText(node.getText()+"-"+file.length());
						
						//在这里添加你自己的处理图片的逻辑，比如上传到服务器，然后再修改node中的location。
					}
					super.visit(node);	
				}
			});	
		}
		
		//示例：切分段落(此处仅作示例)
		int splitLevel = 2;//按照几级标题来切分文档。
		List<Document> docs = new ArrayList<Document>();
		List<Node> current = new ArrayList<Node>();
		for (Node node : doc.getChildren()) {
//			print(node);
			int level = 100;
			if (node instanceof Header) {
				level = ((Header) node).getLevel();
			}
			if (level <= splitLevel) {
				if(!current.isEmpty()){
					Document d = new Document(0);
					d.setChildren(current.toArray(new Node[current.size()]));
					docs.add(d);	
					current.clear();
				}
				current.add(node);
			}else{
				current.add(node);
			}
		}
		if (current.size() > 0) {
			Document d = new Document(0);
			d.setChildren(current.toArray(new Node[current.size()]));
			current.clear();
			docs.add(d);
		}
		// ----------------------------------
		{
			for (Document d : docs) {
				System.out.println("=================================新的一篇文档");
				print(d);
			}
		}
	}
	
	/**
	 * 解析markdown，然后输出
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void testPrint() throws IOException, ParseException {
		File file = new File(this.getClass().getResource("/").getPath(), "ef/云数据库.md");
		Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
		Document doc;
		try{
			Parser p = new Parser(reader);
			doc = p.parse();	
		}finally{
			reader.close();
		}

		System.out.println("====下面将markdown直接输出到文件====");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file.getParent(), "test.md")), "UTF-8"));
		try{
			RawEmitter raw = new RawEmitter(writer);
			doc.accept(raw);
			System.out.println("文件已保存。");
		}finally{
			writer.close();
		}
		
		System.out.println("====下面将markdown渲染为HTML====");
		HtmlEmitter html=new HtmlEmitter(System.out);
		doc.accept(html);
	}
	
	void print(Node node) {
		RawEmitter raw = new RawEmitter(System.out);
		node.accept(raw);
	}
}
