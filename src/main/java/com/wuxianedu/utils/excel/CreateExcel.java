package com.wuxianedu.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wuxianedu.domain.questions.Questions;

public class CreateExcel {
	public static Integer oneSearchExcel = 360000;
	public static Integer oneSearchPageExcel = 60000;
	public static Integer oneBatchNum = 10000;

	public static Integer oneFlushNum = 200;

	/**
	 * 吧所有试题生成为一个xlsx文件
	 * 
	 * @param filePath
	 *            生成的xlsx的路径
	 * @param questions
	 *            生成文件的数据
	 * @param index
	 *            生成几个sheet
	 * @throws IOException
	 */
	public static void Excel2007AboveOperateQuestions(String filePath, List<Questions> questions, Integer index)
			throws IOException {
		/*
		 * XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new
		 * File(filePath))); SXSSFWorkbook sxssfWorkbook = new
		 * SXSSFWorkbook(workbook1, 100); Sheet first =
		 * sxssfWorkbook.getSheetAt(0);
		 */
		Workbook workbook1;
		try {
			CreateExcelFirst(filePath, index);
			workbook1 = WorkbookFactory.create(new FileInputStream(new File(filePath)));
			Sheet first = workbook1.getSheetAt(0); // 示意访问sheet
			for (int i = 0; i <= questions.size(); i++) {
				Row row = first.createRow(i);
				for (int j = 0; j < 8; j++) {
					if (i == 0) {
						// 首行
						row.createCell(j).setCellValue(setHead(j));
					} else {
						CellUtil.createCell(row, j, String.valueOf(setBody(j, questions.get(i - 1))));
					}
				}
			}
			FileOutputStream out = new FileOutputStream(filePath);
			workbook1.write(out);
			/* sxssfWorkbook.write(out); */
			out.close();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 把传入的oneSearchExcel条数据按每页oneSearchPageExcel条存进xlsx
	 * 
	 * @param questions
	 *            要保存的数据
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public static void Excel2007AboveOperateQuestionsOther(List<Questions> questions)
			throws IOException, InvalidFormatException {
		String filePath = "file/questions" + System.currentTimeMillis() + ".xlsx";
		Integer allnum = questions.size();
		if (allnum % oneSearchPageExcel == 0) {
			CreateExcel.CreateExcelFirst(filePath, allnum / oneSearchPageExcel);
		} else {
			CreateExcel.CreateExcelFirst(filePath, allnum / oneSearchPageExcel + 1);
		}
		XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));
		SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook1, oneFlushNum);
		Sheet sheet = sxssfWorkbook.getSheetAt(0);
		Row row;
		Integer beginnum = 1;
		for (int i = 0; i < allnum; i++) {
			// 每oneSearchPageExcel条数据数据存到另外一个sheet里面
			if (i % oneSearchPageExcel == 0) {
				// 每个sheet开始的行号计数器
				beginnum = 1;
				sheet = sxssfWorkbook.getSheetAt(i / oneSearchPageExcel);
				row = sheet.createRow(0);
				for (int j = 0; j < 8; j++) {
					row.createCell(j).setCellValue(setHead(j));
				}
			}
			row = sheet.createRow(beginnum);
			for (int j = 0; j < 8; j++) {
				CellUtil.createCell(row, j, String.valueOf(setBody(j, questions.get(i))));
			}
			beginnum++;

		}

		FileOutputStream out = new FileOutputStream(filePath);
		sxssfWorkbook.write(out);
		out.close();
	}

	/**
	 * 读取xlsx或者xls文件把数据存到数据库内
	 * 
	 * @param fileName
	 * @param jdbcTemplate
	 * @throws IOException
	 */
	public static void read(String fileName, JdbcTemplate jdbcTemplate) throws IOException {
		System.out.println("---------------->");
		InputStream stream = new FileInputStream(new File(fileName));
		Workbook wb = null;
		String prefixof = fileName.substring(fileName.lastIndexOf(".") + 1);
		if ("xls".equals(prefixof)) {
			wb = new HSSFWorkbook(stream);
		} else if ("xlsx".equals(prefixof)) {
			System.out.println("---------------->");
			wb = new XSSFWorkbook(stream);
			System.out.println("---------------->");
		} else {
			System.out.println("您输入的excel格式不正确");
		}
		Sheet sheet1;
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheet1 = wb.getSheetAt(i);
			String prefix = "INSERT INTO questions(createdate,optiona,optionb,optionc,optiond,question,tqtitle,type_tid) VALUES \n";
			StringBuffer suffix = new StringBuffer("('");
			for (Row row : sheet1) {
				// 第几行
				Integer rowstart = 1;
				for (Cell cell : row) {
					if (rowstart == 7) {
						suffix.append(cell.getNumericCellValue() + "',");
					} else if (rowstart == 8) {
						suffix.append(cell.getStringCellValue() + ")\n,");
					} else {
						suffix.append(cell.getStringCellValue() + "','");
					}
				}
				if (rowstart == oneBatchNum) {
					String sql = prefix + suffix.substring(0, suffix.length() - 2);
					System.out.println(sql);
					jdbcTemplate.update(sql);
					suffix = new StringBuffer("('");
					rowstart = 0;
				}
				rowstart++;
			}
			// 没到oneBatchNum道试题的试题提交掉
			if (!"('".equals(suffix.toString())) {
				String sql = prefix + suffix.substring(0, suffix.length() - 2);
				jdbcTemplate.update(sql);
			}
		}
		stream.close();
		System.out.println("---------------->");

	}

	// poi读取Excel存入数据库
	public static void showExcel(String fileName, JdbcTemplate jdbcTemplate) {
		try {
			long beginTime1 = System.currentTimeMillis();
			String prefixof = fileName.substring(fileName.lastIndexOf(".") + 1);
			Workbook wb = null;
			InputStream stream = new FileInputStream(new File(fileName));
			if ("xls".equals(prefixof)) {
				wb = new HSSFWorkbook(stream);
			} else if ("xlsx".equals(prefixof)) {
				wb = new XSSFWorkbook(stream);
			} else {
				System.out.println("您输入的excel格式不正确");
				if (stream != null) {
					stream.close();
				}
				return;
			}
			Sheet sheet;
			System.out.println("一共"+wb.getNumberOfSheets()+"个表");
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {// 获取每个Sheet表
				sheet = wb.getSheetAt(i);
				String prefix = "INSERT INTO questions(tqtitle,optiona,optionb,optionc,optiond,question,type_tid,createdate) VALUES \n";
				StringBuffer suffix = new StringBuffer();
				Integer countoneBatchNum=1;//每个sheet中第几次提交增加数据到数据库
				for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {// 获取每行
					Row row = sheet.getRow(j);
					if (j != 0) {// 第一行忽略
						for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {// 获取每个单元格
							if (k==0) {
								suffix.append("('"+row.getCell(k)+"','");
							}else if (k==5) {
								suffix.append( row.getCell(k)+ "',");
							}else if (k == 6) {
								suffix.append(transType(row.getCell(k).toString())+ ",'");// 把类型文字转换为数字存到数据库
							} else if (k == 7) {
								suffix.append(row.getCell(k)+ "')\n,");
							}else {
								suffix.append(row.getCell(k)+"','");
							}
						}
						if (j ==oneBatchNum*countoneBatchNum||j==sheet.getPhysicalNumberOfRows()) {
							String sql = prefix + suffix.substring(0, suffix.length() - 2);
							jdbcTemplate.update(sql);
							suffix = new StringBuffer();
							countoneBatchNum++;
						}
					}
				}
				System.out.println("---Sheet表" + i + "处理完毕---");
			}
			long endTime1 = System.currentTimeMillis();
			System.out.println("一共花费 : " + (endTime1 - beginTime1));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String setHead(Integer integer) {
		if (integer == 0) {
			return "标题";
		} else if (integer == 1) {
			return "选项A";
		} else if (integer == 2) {
			return "选项B";
		} else if (integer == 3) {
			return "选项C";
		} else if (integer == 4) {
			return "选项D";
		} else if (integer == 5) {
			return "答案";
		} else if (integer == 6) {
			return "类型";
		} else if (integer == 7) {
			return "创建时间";
		}
		return null;
	}

	public static String setBody(Integer integer, Questions questions) {
		if (integer == 0) {
			return questions.getTqtitle();
		} else if (integer == 1) {
			return questions.getOptiona();
		} else if (integer == 2) {
			return questions.getOptionb();
		} else if (integer == 3) {
			return questions.getOptionc();
		} else if (integer == 4) {
			return questions.getOptiond();
		} else if (integer == 5) {
			return questions.getQuestion();
		} else if (integer == 6) {
			return questions.getType().getTname();
		} else if (integer == 7) {
			return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(questions.getCreatedate());
		}
		return null;
	}

	/**
	 * 创建index个sheet的xlsx文件
	 * 
	 * @param filePath
	 *            创建的文件路径
	 * @param index
	 *            创建多少个sheet
	 */
	public static void CreateExcelFirst(String filePath, Integer index) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// 新创建的xls需要新创建新的工作簿，offine默认创建的时候会默认生成三个sheet
			for (int i = 0; i < index; i++) {
				workbook.createSheet(getSheetNameByIndex(i));
			}
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			out.close();
			System.out.println("createWorkBook success");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getSheetNameByIndex(Integer index) {

		return "第" + (index + 1) + "张表";
	}

	public static Integer transType(String typename) {
		if ("java".equalsIgnoreCase(typename)) {
			return 1;
		} else if ("ios".equalsIgnoreCase(typename)) {
			return 2;
		} else if ("php".equalsIgnoreCase(typename)) {
			return 3;
		} else if ("html5".equalsIgnoreCase(typename)) {
			return 4;
		}
		return 0;
	}
}
