package com.wuxianedu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.wuxianedu.dao.questions.QuestionsRepository;
import com.wuxianedu.dao.type.TypeRepository;
import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.QuestionService;
import com.wuxianedu.utils.batchaddandupdate.BatchAdding;
import com.wuxianedu.utils.csv.CSVUtils;
import com.wuxianedu.utils.excel.CreateExcel;
import com.wuxianedu.utils.filetozip.MyZipCompressing;
import com.wuxianedu.utils.md5.MD5Util;
import com.wuxianedu.utils.stringcode.ShowCode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootDay071ApplicationTests {
	@Autowired
	QuestionService questionService;
	@Autowired
	QuestionsRepository questionsRepository;
	@Autowired
	TypeRepository TypeRepository;
	@Autowired
	BatchAdding batchAdding;
	@Autowired
	JdbcTemplate jdbcTemplate;
	/* 通过totalId查询试题 */
	@Test
	public void test5() {
		System.out.println("一共：");
	}

	/**
	 * 测试MD5
	 */
	@Test
	public void test6() {

		System.out.println("EncoderByMd5:" + MD5Util.EncoderByMd5("bbb"));
		System.out.println("EncoderByMd5:" + MD5Util.EncoderByMd5("bbb"));
		System.out.println(MD5Util.string2MD5("t843tu"));
		System.out.println(MD5Util.string2MD5("t843tu"));
		System.out.println(MD5Util.string2MD5("bbb"));
		System.out.println(MD5Util.convertMD5("bbb"));
		System.out.println(MD5Util.convertMD5(MD5Util.convertMD5("bbb")));
	}

	/*
	 * 数据库到Excle操作poi 1000提交一次 Cast time1 : 2865 Cast time2 : 8355 100提交一次 Cast
	 * time1 : 2239 Cast time2 : 7869
	 * 
	 */
	@Test
	public void test9() throws IOException {
		/* hasFileOrNot("E:/14.xlsx"); */

		/*
		 * Workbook wb = new XSSFWorkbook(); Sheet sheet1 = wb.createSheet(
		 * "new sheet"); FileOutputStream fileOut = new
		 * FileOutputStream("E:/workbook.xlsx"); wb.write(fileOut);
		 * fileOut.close();
		 */

		// 可以表示xls和xlsx格式文件的类
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// 新创建的xls需要新创建新的工作簿，offine默认创建的时候会默认生成三个sheet
			Sheet sheet = workbook.createSheet("first sheet");
			FileOutputStream out = new FileOutputStream("E:/createWorkBook.xlsx");
			workbook.write(out);
			out.close();
			System.out.println("createWorkBook success");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * long beginTime1 = System.currentTimeMillis(); List<Questions>
		 * questions = questionService.findAll(); long endTime1 =
		 * System.currentTimeMillis(); System.out.println("Cast time1 : " +
		 * (endTime1 - beginTime1)); long beginTime =
		 * System.currentTimeMillis();
		 * ExcelController.Excel2007AboveOperateQuestions("E:/14.xlsx",
		 * questions); WriteExcel.writeExcel(questions, 8, "E:/13.xlsx"); long
		 * endTime = System.currentTimeMillis(); System.out.println(
		 * "Cast time2 : " + (endTime - beginTime));
		 */
	}

	/* 添加10万条数据到数据库 每一万条四分钟 */
	@Test
	public void test10() throws IOException {
		long beginTime = System.currentTimeMillis();
		int j = 0;
		/* 共多少条记录 */
		int i = 100000;
		while (true) {
			int temp = j + 10000;
			if (temp <= i) {
				List<Questions> questions = new ArrayList<>(10000);
				for (; j < temp; j++) {
					Questions questions2 = getQuestion(j);
					questions.add(questions2);
					System.out.println("第" + j + "条记录");
				}
				long beginTime1 = System.currentTimeMillis();
				questionService.AddmanyQuestions(questions);
				long endTime1 = System.currentTimeMillis();
				System.out.println("从第" + (temp - 10000) + "条到第" + temp + "条一共花了 : " + (endTime1 - beginTime1));

			} else {
				List<Questions> questions = new ArrayList<>(10000);
				for (; j < i; j++) {
					Questions questions2 = getQuestion(j);
					questions.add(questions2);
					System.out.println("第" + j + "条记录");
				}
				long beginTime1 = System.currentTimeMillis();
				questionService.AddmanyQuestions(questions);
				long endTime1 = System.currentTimeMillis();
				System.out.println("从第" + (temp - 10000) + "条到第" + temp + "条一共花了 : " + (endTime1 - beginTime1));
				break;
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("一共花了 : " + (endTime - beginTime));
	}

	/* 删除记录 */
	@Test
	public void tes11() throws IOException {
		List<String> list = new ArrayList<>();
		for (int i = 1961; i < 336338; i++) {
			list.add(i + "");
		}
		long beginTime1 = System.currentTimeMillis();
		questionService.deleteAll(list);
		long endTime1 = System.currentTimeMillis();
		System.out.println("一共花了 : " + (endTime1 - beginTime1));
	}
/**
 * 批量插入300万两分钟
 * @throws IOException
 */
	@Test
	public void insertit() throws IOException {
		List<Questions> questionslist1 = new ArrayList<>(100000);
		for (int i = 0; i < 100000; i++) {
			questionslist1.add(getQuestion(i));
		}
		long beginTime = System.currentTimeMillis();
		int j = 0;
		/* 共多少条记录 */
		int i = 3000000;
		while (true) {
			int temp = j + 10000;
			List<Questions> questionslist2 = new ArrayList<>(10000);
			if (temp <= i) {
				for (; j < temp; j++) {
					questionslist2.add(questionslist1.get(1));
				}
				long beginTime1 = System.currentTimeMillis();
				batchAdding.updateWinnerList(questionslist2);
				long endTime1 = System.currentTimeMillis();
				System.out.println("从第" + (temp - 10000) + "条到第" + temp + "条一共花了 : " + (endTime1 - beginTime1));
				
			} else {
					for (; j < i; j++) {
						questionslist2.add(questionslist1.get(1));
					}
					long beginTime1 = System.currentTimeMillis();
					batchAdding.updateWinnerList(questionslist2);
					long endTime1 = System.currentTimeMillis();
					System.out.println("从第" + (temp - 10000) + "条到第" + temp + "条一共花了 : " + (endTime1 - beginTime1));
					break;
				}
			}
		long endTime = System.currentTimeMillis();
		System.out.println("一共花了 : " + (endTime - beginTime) + "秒");
	}

	

	/* jdbc多线程 添加10万条记录 ms */
	@Test
	public void tes13() throws IOException {
		long beginTime = System.currentTimeMillis();
		for (int i = 1; i <= 10; i++) {
			/* new MyQuestionsAddThread().start(); */
		}
		long endTime = System.currentTimeMillis();
		System.out.println("------------------------------------>");
		System.out.println("一共花了 : " + (endTime - beginTime) + "秒");
		System.out.println("------------------------------------>");
	}

	/* 把记录存到csv里面 */
	@Test
	public void tes14() throws IOException {
		long beginTime1 = System.currentTimeMillis();
		List<Questions> questions = questionService.findAll();
		long endTime1 = System.currentTimeMillis();
		System.out.println("Cast time1 : " + (endTime1 - beginTime1));

		Questions questions2 = new Questions();
		questions2.setTqtitle("标题");
		questions2.setOptiona("选项A");
		questions2.setOptionb("选项B");
		questions2.setOptionc("选项C");
		questions2.setOptiond("选项D");
		questions2.setQuestion("答案");
		Type type = new Type();
		type.setTname("类型");
		questions2.setType(type);
		long beginTime2 = System.currentTimeMillis();
		CSVUtils.createOneCSVFile((Object) questions2, questions, "E:/tempcsv/thesecond1sdf1234.csv");
		long endTime2 = System.currentTimeMillis();
		System.out.println("Cast time2 : " + (endTime2 - beginTime2));
	}

	/* 删除csv文件 */
	@Test
	public void test17() throws IOException {
		File file = new File("thesecond.csv");
		file.delete();
	}

	/* 删除所有记录 */
	@Test
	public void tes15() throws IOException {
		questionsRepository.deleteAllQuestion();
	}

	/* csv到数据库 */
	@Test
	public void tes16() throws IOException {
		try {
			questionsRepository.CsvToMysql2("E:/tempcsv/" + "thesecond1sdf11.csv");
		} catch (Exception e) {
			/* e.printStackTrace(); */
		}
	}

	/* 查找所有questions数据库 */
	@Test
	public void tes17() throws IOException {
		questionsRepository.getQuestionThenToCsv();
	}

	/* 查看文件字符编码 */
	@Test
	public void tes18() throws IOException {

		System.out.println(ShowCode.getFileEncode("E:/tempcsv/thesecond1sdf11.csv"));
		System.out.println(ShowCode.getFileEncode("E:/tempcsv/thesecond1sdf.csv"));
		System.out.println(ShowCode.getFileEncode("E:/tempcsv/textofit.csv"));
		System.out.println(ShowCode.getFileEncode("E:/tempcsv/ksdafsa.csv"));
		System.out.println(ShowCode.getFileEncode("E:/tempcsv/firstcsv1483083903473.csv"));
		System.out.println("--------------------->");
		System.out.println(ShowCode
				.getFileStringByInputStream2(new FileInputStream(new File("E:/tempcsv/thesecond1sdf11.csv")), true));
		System.out.println(ShowCode
				.getFileStringByInputStream2(new FileInputStream(new File("E:/tempcsv/thesecond1sdf.csv")), true));
		System.out.println(
				ShowCode.getFileStringByInputStream2(new FileInputStream(new File("E:/tempcsv/textofit.csv")), true));
		System.out.println(
				ShowCode.getFileStringByInputStream2(new FileInputStream(new File("E:/tempcsv/ksdafsa.csv")), true));
		System.out.println(ShowCode.getFileStringByInputStream2(
				new FileInputStream(new File("E:/tempcsv/firstcsv1483083903473.csv")), true));
	}

	/* 大文件分解 文件压缩 */
	@Test
	public void tes19() throws IOException {

		CSVUtils.readCdvAndSplitIt(getQuestionHead(), "file/questions1483932479210.csv");
		try {
			MyZipCompressing.zip("file/filezip.zip", new File("file/"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test20() {
		deleteFile(new File("file"));
		new File("file").mkdirs();
	}

	@Test
	public void test21() {
		System.out.println(new Date());
	}
//	读取xlsx文件并把数据存到数据库
	@Test
	public void test22() {
	/*	try {
			CreateExcel.read("E:/temp/questions1484531103262.xlsx", jdbcTemplate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		CreateExcel.showExcel("E:/temp/questions1484531159480.xlsx",jdbcTemplate);
	}
	@Test
	public void test23() {
		String var = "\"df";
		System.out.println(var);
		System.out.println(var.replace('"', '\''));
	}

	public Questions getQuestionHead() {
		Questions questions2 = new Questions();
		questions2.setTqtitle("标题");
		questions2.setOptiona("选项A");
		questions2.setOptionb("选项B");
		questions2.setOptionc("选项C");
		questions2.setOptiond("选项D");
		questions2.setQuestion("答案");
		Type type = new Type();
		type.setTname("类型");
		questions2.setType(type);
		return questions2;
	}

	Questions getQuestion(Integer i) {
		Questions questions2 = new Questions();
		questions2.setTqtitle("java试题");
		questions2.setOptiona("A.11");
		questions2.setOptionb("B.22");
		questions2.setOptionc("C.33");
		questions2.setOptiond("D.44");
		questions2.setQuestion("D");

		try {
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date newdate = new Date();
			String createdate = bartDateFormat.format(newdate);
			Date dateinput;
			dateinput = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(createdate);
			questions2.setCreatedate(dateinput);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (i % 4 == 0) {
			Type type = new Type();
			type.setTid(1);
			type.setTname("java");
			questions2.setType(type);
		} else if (i % 4 == 1) {
			Type type = new Type();
			type.setTid(2);
			type.setTname("ios");
			questions2.setType(type);
		} else if (i % 4 == 2) {
			Type type = new Type();
			type.setTid(3);
			type.setTname("php");
			questions2.setType(type);
		} else if (i % 4 == 3) {
			Type type = new Type();
			type.setTid(4);
			type.setTname("html5");
			questions2.setType(type);
		}
		return questions2;
	}

	/* 判断有没有文件，没有新建 */
	public void hasFileOrNot(String filename) {
		try {
			File csvFile = null;
			csvFile = new File(filename);
			File parent = csvFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			csvFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 递归删除文件夹
	private void deleteFile(File file) {
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				file.delete();// 删除文件
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					this.deleteFile(files[i]);// 把每个文件用这个方法进行迭代
				}
				file.delete();// 删除文件夹
			}
		} else {
			System.out.println("所删除的文件不存在");
		}
	}

	public String isCsvOrXlsx(String filename){
		String [] fileSplit = filename.split(".");
		return fileSplit[0];
		/*if ("csv".equals(fileSplit[1])) {
			return "csv";
		}else if ("xlsx".equals(fileSplit[1])) {
			return "xlsx";
		}
		return "error";*/
	}
}