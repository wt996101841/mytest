package com.itheima.lucene;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.lucene.dao.BookDao;
import com.itheima.lucene.dao.BookDaoImpl;
import com.itheima.lucene.pojo.Book;

/**
 * Lucene的入门程序
 * 
 * 创建索引
 * 搜索索引
 * @author lx
 *
 */
public class FirstLucene {

	//	创建索引
	// 1: 原始数据 从Mysql数据来  已经完成
	// 2: 获取数据 连接数据库   Dao接口 Dao实现类（jdbc连接数据库并查询结果 返回结果集）  直接复制文档代码就可以了
	// 3:创建文档对象(文档对象中 添加域
	// 4:分析文档
	// 5:索引文档（保存索引及文档二部分到索引库中）
	@Test
	public void testAdd() throws Exception {
		BookDao bookDao = new BookDaoImpl();
		//结果集  5
		List<Book> books = bookDao.queryBookList();
		
		//分词器 分析文档 
//		Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		
		//索引库 位置对象
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//保存索引配置对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		//
		IndexWriter indexWriter  = new IndexWriter(directory, config);
		
		for (Book book : books) {
			
			//创建文档对象
			Document doc = new Document();
			//域 ID
			Field idField = new TextField("id", "" + book.getId(), Store.YES);
			//域 名称
			Field nameField = new TextField("name",book.getName(),Store.YES);
			//价格
			Field priceField = new TextField("price",String.valueOf(book.getPrice()),Store.YES);
			//图片
			Field picField = new TextField("pic",book.getPic(),Store.YES);
			//描述
			Field descField = new TextField("desc",book.getDesc(),Store.NO);
			
			doc.add(idField);
			doc.add(nameField);
			doc.add(priceField);
			doc.add(picField);
			doc.add(descField);
			//保存索引
			indexWriter.addDocument(doc);
			
		}
		//关闭资源
		indexWriter.close();
		
	}

//	搜索索引
	@Test
	public void testSearchIndex() throws Exception {
		//创建索引
		Query query = new TermQuery(new Term("name","lucene"));
		
		//索引库 位置对象
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		//执行查询
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//执行  5条的文档ID
		TopDocs topDocs = indexSearcher.search(query, 5);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docID = scoreDoc.doc;
			Document doc = indexSearcher.doc(docID);
			System.out.println("ID:" + doc.get("id"));
			System.out.println("名称:" + doc.get("name"));
			System.out.println("价格:" + doc.get("price"));
			System.out.println("图片:" + doc.get("pic"));
			System.out.println("描述:" + doc.get("desc"));
		}
		indexReader.close();
	}

	
	
	
	
	
}
