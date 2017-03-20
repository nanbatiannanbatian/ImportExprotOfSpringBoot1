package com.wuxianedu.utils.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.questions.UpDownLoadPosition;
import com.wuxianedu.service.QuestionService;

/**
 * 
 * CSV文件导出工具类
 * 
 */
public class CSVUtils {

	@Autowired
	QuestionService questionService;
	// 导入大CSV是从数据库每次取几个
	public static Integer OnePageNum = 500000;
	public static Integer OnePageFileNum = 50000;
	public static Integer oneBatchNum = 10000;

	/**
	 * CSV文件生成文件
	 * 
	 * @param head
	 * @param questions
	 * @param outPutPath
	 * @param filename
	 * @return
	 */
	public static File createOneCSVFile(Object head, List<Questions> questions, String filename) {

		File csvFile = null;
		BufferedWriter csvWtriter = null;
		try {
			hasFileOrNot(filename);
			csvFile = new File(filename);

			// GB2312使正确读取分隔符","
			csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), "GBK"), 1024);
			// 写入文件头部
			writeRowhead(head, csvWtriter);

			// 写入文件内容
			for (Object row : questions) {
				writeRow1(row, csvWtriter);
			}
			csvWtriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvWtriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}

	/**
	 * CSV文件生成文件多个
	 * 
	 * @param head
	 * @param questions
	 * @param outPutPath
	 * @param filename
	 * @return
	 */
	public static File createOneCSVFileOther(Object head, List<Questions> questions) {

		File csvFile = null;
		BufferedWriter csvWtriter = null;
		File filename;
		Integer index = 1;
		try {
			for (int i = 0; i < questions.size(); i++) {
				if (index % CSVUtils.OnePageFileNum == 1) {
					filename = new File("file/question" + System.currentTimeMillis() + ".csv");
					filename.createNewFile();
					// GB2312使正确读取分隔符","
					csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true), "GBK"),
							1024);
					// 写入文件头部
					writeRowhead(head, csvWtriter);
				}
				writeRow1(questions.get(i), csvWtriter);
				System.out.println("index:" + index);
				if (index % CSVUtils.OnePageFileNum == 0) {
					csvWtriter.flush();
				}
				index++;
			}
			csvWtriter.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvWtriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvFile;
	}

	/**
	 * 
	 * @param head
	 *            写入CSV的头信息
	 * @param filename
	 *            要分解的大文件CSV
	 */
	public static void readCdvAndSplitIt(Object head, String filename) {
		File csvFile = null;
		BufferedReader bufferedReader = null;
		BufferedWriter csvWtriter = null;
		try {
			if (new File(filename).exists()) {
				csvFile = new File(filename);
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "GBK"), 1024);
				String lineTxt;
				/* 取消第一行 */
				if (bufferedReader.readLine() == null) {
					return;
				}
				;
				Integer index = 1;
				File readOne;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (index % CSVUtils.OnePageFileNum == 1) {
						// 必须关闭每个文件的流否则被程序占用导致无法删除文件而把残留文件打包到下次下载
						if (csvWtriter != null) {
							csvWtriter.close();
						}
						readOne = new File("file/question" + System.currentTimeMillis() + ".csv");
						readOne.createNewFile();
						// GB2312使正确读取分隔符","
						csvWtriter = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(readOne, true), "GBK"), 1024);
						// 写入文件头部
						writeRowhead(head, csvWtriter);
					}
					csvWtriter.write(lineTxt);
					csvWtriter.newLine();
					System.out.println("index:" + index);
					if (index % CSVUtils.OnePageFileNum == 0) {
						csvWtriter.flush();
					}
					index++;
				}
				csvWtriter.flush();
				csvWtriter.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader!=null) {
					bufferedReader.close();
					bufferedReader=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 把传入的csv文件数据存到数据库中
	 * @param head
	 *            写入CSV的头信息
	 * @param filename
	 *            要分解的大文件CSV
	 */
	public static void saveQuestionToMysql(String filename, JdbcTemplate jdbcTemplate,HttpServletRequest request) {
		long beginTime = System.currentTimeMillis();
		File csvFile = null;
		BufferedReader bufferedReader = null;
		BufferedReader bufferedReadernum = null;
		try {
			if (new File(filename).exists()) {
				csvFile = new File(filename);
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "utf-8"), 1024);
				bufferedReadernum = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "utf-8"), 1024);
				String lineTxt;
//				获取一共多少行
				Integer allrow=0;
				while ((lineTxt = bufferedReadernum.readLine()) != null) {
					allrow++;
				}
				if (bufferedReadernum!=null) {
					bufferedReadernum.close();
					bufferedReadernum=null;
				}
				
				UpDownLoadPosition upDownLoadPosition=(UpDownLoadPosition) request.getSession().getAttribute("UpDownLoadPosition");
				HttpSession session = request.getSession();
				session.setAttribute("UpDownLoadPosition", upDownLoadPosition);
				upDownLoadPosition.setBeginUpLoadOrOnt("Uploading");
				upDownLoadPosition.setUpLoadAllrow(allrow);
			
				/* 取消第一行 */
				if (bufferedReader.readLine() == null){
					return;
				}
				Integer index = 1;
				Integer batchnum = 1;
				String prefix = "INSERT INTO questions(optiona,optionb,optionc,optiond,question,tqtitle,type_tid,createdate) VALUES \n";
				StringBuffer suffix = new StringBuffer();
				while ((lineTxt = bufferedReader.readLine()) != null) {
					lineTxt.replaceAll("\"", "\'");
					lineTxt = lineTxt.substring(0, lineTxt.length()-1);
					System.out.println(index+":"+lineTxt);
					suffix.append("(").append(lineTxt).append(")\n,");
					if (index == CSVUtils.oneBatchNum * batchnum) {
						String sql = prefix + suffix.substring(0, suffix.length() - 2);
						jdbcTemplate.update(sql);
						batchnum++;
						suffix = new StringBuffer();
					}
					upDownLoadPosition.setUpLoadDealrow(index);
					index++;
				}
				upDownLoadPosition.setBeginUpLoadOrOnt("final");
				String sql = prefix + suffix.substring(0, suffix.length() - 2);
				jdbcTemplate.update(sql);
				System.out.println("数据导入完成");
				long endTime = System.currentTimeMillis();
				System.out.println("一共花了 : " + (endTime - beginTime) + "秒");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader!=null) {
					bufferedReader.close();
					bufferedReader=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写一行数据方法
	 * 
	 * @param row
	 * @param csvWriter
	 * @throws IOException
	 */
	private static void writeRow1(Object row, BufferedWriter csvWriter) throws IOException {
		// 写入文件头部
		Questions questions = (Questions) row;
		StringBuffer sb = new StringBuffer();
		/* sb.append(questions.getTqid()).append(","); */
		sb.append("\"").append(questions.getOptiona()).append("\",");
		sb.append("\"").append(questions.getOptionb()).append("\",");
		sb.append("\"").append(questions.getOptionc()).append("\",");
		sb.append("\"").append(questions.getOptiond()).append("\",");
		sb.append("\"").append(questions.getQuestion()).append("\",");
		/* sb.append(questions.getThedesc()).append(","); */
		sb.append("\"").append(questions.getTqtitle()).append("\",");
		sb.append("").append(questions.getType().getTid()).append(",");
		String rowStr = sb.append("\"")
				.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(questions.getCreatedate())).append("\",")
				.toString();
		csvWriter.write(rowStr);
		csvWriter.newLine();
	}

	/**
	 * 写一行数据方法
	 * 
	 * @param row
	 * @param csvWriter
	 * @throws IOException
	 */
	private static void writeRowhead(Object row, BufferedWriter csvWriter) throws IOException {
		// 写入文件头部
		if (row != null) {
			Questions questions = (Questions) row;
			StringBuffer sb = new StringBuffer();
			/* sb.append("\"").append("id").append("\","); */
			sb.append("\"").append(questions.getOptiona()).append("\",");
			sb.append("\"").append(questions.getOptionb()).append("\",");
			sb.append("\"").append(questions.getOptionc()).append("\",");
			sb.append("\"").append(questions.getOptiond()).append("\",");
			sb.append("\"").append(questions.getQuestion()).append("\",");
			sb.append("\"").append(questions.getTqtitle()).append("\",");
			sb.append("\"").append(questions.getType().getTname()).append("\",");
			String rowStr = sb.append("\"").append("创建日期").append("\",").toString();
			csvWriter.write(rowStr);
			csvWriter.newLine();
		}
	}

	public static void hasFileOrNot(String filename) {
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

}