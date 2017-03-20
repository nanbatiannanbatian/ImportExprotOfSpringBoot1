package com.wuxianedu.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wuxianedu.dao.questions.QuestionsRepository;
import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.questions.UpDownLoadPosition;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.QuestionService;
import com.wuxianedu.utils.csv.CSVUtils;
import com.wuxianedu.utils.excel.CreateExcel;
import com.wuxianedu.utils.filetozip.MyZipCompressing;

@Controller
@RequestMapping("/fileup")
public class FileUploadController {
	@Autowired
	QuestionsRepository questionsRepository;
	@Autowired
	QuestionService questionService;
	@Autowired
	JdbcTemplate jdbcTemplate;

	// 访问路径为：http://127.0.0.1:8080/file
	@RequestMapping("/file")
	public String file() {
		return "/file";
	}

	/**
	 * 文件上传具体实现方法;
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		if (!file.isEmpty()) {
			try {
				/*
				 * 这段代码执行完毕之后，图片上传到了工程的跟路径； 大家自己扩散下思维，如果我们想把图片上传到
				 * d:/files大家是否能实现呢？ 等等;
				 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如： 1、文件路径； 2、文件名；
				 * 3、文件格式; 4、文件大小的限制;
				 */
				
				/**
				 * 标记文件文件状态
				 */
				UpDownLoadPosition upDownLoadPosition=new UpDownLoadPosition();
				upDownLoadPosition.setBeginUpLoadOrOnt("begin");
				upDownLoadPosition.setUpLoadDealrow(0);
				request.getSession().setAttribute("UpDownLoadPosition", upDownLoadPosition);
				
				System.out.println(file.getOriginalFilename());
				File csvFile = null;
				String simpfilename = file.getOriginalFilename();
				String prefixof = simpfilename.substring(simpfilename.lastIndexOf(".") + 1);
				String filename = "E:/tempcsv/" + simpfilename;
				csvFile = new File(filename);
				File fileparent = csvFile.getParentFile();
				if (fileparent.exists()) {
				} else {
					fileparent.mkdirs();
				}

				if ("csv".equals(prefixof)) {
					if (csvFile.exists()) {
						csvFile.delete();
						csvFile.createNewFile();
					} else {
						csvFile.createNewFile();
					}
				} else {
					Addmessage(request, "格式错误请输入csv格式文件");
					return "格式错误请输入csv格式文件";
				}

				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(csvFile));
				byte[] dates = file.getBytes();
				System.out.println(dates.length);
				byte[] transdate = new String(dates, "GB2312").getBytes("utf-8");
				out.write(transdate);
				out.flush();
				out.close();
//				questionsRepository.CsvToMysql2(filename);
				CSVUtils.saveQuestionToMysql(filename, jdbcTemplate,request);
				Addmessage(request, "添加成功");
				csvFile.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "上传失败," + e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return "上传失败," + e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				File csvFile = new File(file.getOriginalFilename());
				if (csvFile.exists()) {
					csvFile.delete();
				}
				Addmessage(request, "上传成功！");
				return "上传成功111";
			}
			Addmessage(request, "上传成功！");
			return "上传成功";
		} else {
			Addmessage(request, "上传失败，因为文件是空的.");
			return "上传失败，因为文件是空的.";
		}
	}

	/**
	 * 下载 每次加载50万条allnum:2908122 下载话费时间 : 207970 allnum:2908122 每次100万条数据 下载话费时间
	 * : 260000 每次25万条数据allnum:2908122 下载话费时间 : 330358 第1次递归 压缩完成 下载话费时间 :
	 * 671890
	 * 
	 * @return
	 */
	@RequestMapping(value = "/testDownload", method = RequestMethod.POST)
	public void testDownloadPart(HttpServletRequest request, HttpServletResponse res, String totalId, String type)
			throws IOException {
		long beginTime1 = System.currentTimeMillis();
		request.getSession().setAttribute("finalUpLoadOrNot", "true");

		// 删除file文件内所有文件
		deleteFile(new File("file"));
		new File("file").mkdirs();

		String filename;
		try {
			if (type.equals("csv")) {
				filename = "file/questions" + System.currentTimeMillis() + ".csv";
			} else {
				filename = "file/questions" + System.currentTimeMillis() + ".xlsx";
			}
			if (totalId == null || totalId.equals("")) {
				transformQuestionsAll(type, filename);
				// 使用这个方法可以不产生数据库所有数据整合的大CSV文件数据
				/* transformQuestionsAllOther(type, filename); */
				dealDownloadFile(request, res, filename, MyZipCompressing.zipName, MyZipCompressing.zipofname);
			} else {
				transformQuestionsPart(totalId, type, filename);
				dealDownloadFile(request, res, filename, filename, filename);
			}
			/* 告诉前台下载完毕 */
			request.getSession().setAttribute("finalUpLoadOrNot", "false");
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime1 = System.currentTimeMillis();
		System.out.println("下载话费时间 : " + (endTime1 - beginTime1));
	}

	/**
	 * 查询是否完成上传
	 * 
	 * @return
	 */
	@RequestMapping(value = "/finalUploadOrNot", method = RequestMethod.GET)
	@ResponseBody
	public String finalUploadOrNot(HttpServletRequest request) {
		UpDownLoadPosition upDownLoadPosition = (UpDownLoadPosition) request.getSession().getAttribute("UpDownLoadPosition");
		String json = JSON.toJSONString(upDownLoadPosition, SerializerFeature.DisableCircularReferenceDetect);
		System.out.println(json);
		return json;
	}
	/**
	 * 查询是否完成下载
	 * 
	 * @return
	 */
	@RequestMapping(value = "/finalDownloadOrNot", method = RequestMethod.GET)
	@ResponseBody
	public String finalDownloadOrNot(HttpServletRequest request) {
		String mString = (String) request.getSession().getAttribute("finalUpLoadOrNot");
		return mString;
	}

	/////////////////// 多文件上传.///////////////////////////
	// 访问：http://127.0.0.1:8080/mutifile
	@RequestMapping(value = "/mutifile", method = RequestMethod.GET)
	public String provideUploadInfo1() {
		return "/mutifile";
	}

	/**
	 * 多文件具体上传时间，主要是使用了MultipartHttpServletRequest和MultipartFile
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload1(HttpServletRequest request) {
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		MultipartFile file = null;
		BufferedOutputStream stream = null;
		for (int i = 0; i < files.size(); ++i) {
			file = files.get(i);
			if (!file.isEmpty()) {
				try {
					byte[] bytes = file.getBytes();
					stream = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
					stream.write(bytes);
					stream.close();
				} catch (Exception e) {
					stream = null;
					return "You failed to upload " + i + " => " + e.getMessage();
				}
			} else {
				return "You failed to upload " + i + " because the file was empty.";
			}
		}
		return "upload successful";
	}

	/**
	 * 把提示信息加入session
	 */
	@RequestMapping("/addmessage")
	public void Addmessage(HttpServletRequest request, String message) {
		try {
			if (message != null) {
				request.getSession().setAttribute("message", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Questions getQuestions() {
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

	public void transformQuestionsPart(String totalId, String type, String filename) {
		List<String> lists = Arrays.asList(totalId.split("_"));
		List<Questions> questions = new ArrayList<>();
		for (String list : lists) {
			questions.add(questionService.findByTqid(Integer.valueOf(list)));
		}
		Questions questions2 = getQuestions();
		if (type.equals("csv")) {
			CSVUtils.createOneCSVFile((Object) questions2, questions, filename);

		} else if (type.equals("Excel")) {
			try {
				CreateExcel.Excel2007AboveOperateQuestions(filename, questions, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 下载全部数据
	 * 
	 * @param type
	 *            csv或者xlsx格式的文件
	 * @param filename
	 *            打包成的大csv文件的文件地址
	 */
	public void transformQuestionsAll(String type, String filename) {
		Questions questions2 = getQuestions();
		if (type.equals("csv")) {
			Integer index = 0;
			Integer allnum = questionService.getQuestionsNumByqueryinfo();
			while (allnum > index) {
				if (index == 0) {// 把头写入
					if (allnum - index > 0 && allnum - index < CSVUtils.OnePageNum) {
						index = createOneCsv(index, allnum - index, questions2, filename);
					} else {
						index = createOneCsv(index, CSVUtils.OnePageNum, questions2, filename);
					}
				} else {// 不把头写入
					if (allnum - index > 0 && allnum - index < CSVUtils.OnePageNum) {
						index = createOneCsv(index, allnum - index, null, filename);

					} else {
						index = createOneCsv(index, CSVUtils.OnePageNum, null, filename);
					}
				}
			}
			CSVUtils.readCdvAndSplitIt(questions2, filename);
		} else if (type.equals("Excel")) {
			try {
				Integer index = 0;
				Integer allnum = questionService.getQuestionsNumByqueryinfo();
				while (allnum > index) {
					if (allnum - index > 0 && allnum - index < CreateExcel.oneSearchExcel) {
						index = createOneXlsxOther(index, allnum - index);
					} else {
						index = createOneXlsxOther(index, CreateExcel.oneSearchExcel);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			MyZipCompressing.zip(MyZipCompressing.zipName, new File("file/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载全部数据读取50万条直接写入5万的CSV文件内，不产生300万数据的大文件CSV
	 * 
	 * @param type
	 *            csv或者xlsx格式的文件
	 * @param filename
	 *            打包成的大csv文件的文件地址
	 */
	public void transformQuestionsAllOther(String type, String filename) {
		Questions questions2 = getQuestions();
		if (type.equals("csv")) {
			Integer index = 0;
			Integer allnum = questionService.getQuestionsNumByqueryinfo();
			while (allnum > index) {
				if (index == 0) {// 把头写入
					if (allnum - index > 0 && allnum - index < CSVUtils.OnePageNum) {
						index = createOneCsvOther(index, allnum - index, questions2);
					} else {
						index = createOneCsvOther(index, CSVUtils.OnePageNum, questions2);
					}
				} else {// 不把头写入
					if (allnum - index > 0 && allnum - index < CSVUtils.OnePageNum) {
						index = createOneCsvOther(index, allnum - index, questions2);

					} else {
						index = createOneCsvOther(index, CSVUtils.OnePageNum, questions2);
					}
				}
			}

		} else if (type.equals("Excel")) {
			try {
				Integer index = 0;
				Integer allnum = questionService.getQuestionsNumByqueryinfo();
				while (allnum > index) {
					if (allnum - index > 0 && allnum - index < CreateExcel.oneSearchExcel) {
						index = createOneXlsxOther(index, allnum - index);
					} else {
						index = createOneXlsxOther(index, CreateExcel.oneSearchExcel);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			MyZipCompressing.zip(MyZipCompressing.zipName, new File("file/"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	/**
	 * 把压缩文件上传给用户
	 * 
	 * @param request
	 * @param res
	 * @param downloadfile
	 *            传到客户端的文件路径名
	 * @param zipPath
	 *            压缩文件地址
	 * @param zipname
	 *            用户收到的文件名
	 */
	public void dealDownloadFile(HttpServletRequest request, HttpServletResponse res, String downloadfile,
			String zipPath, String zipname) {
		try {
			res.setHeader("content-type", "application/octet-stream");
			res.setContentType("application/octet-stream");
			res.setHeader("Content-Disposition", "attachment;filename=" + zipname);
			File file1 = new File(zipPath);

			OutputStream os;
			os = res.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);

			InputStream is = null;

			is = new FileInputStream(zipPath);
			BufferedInputStream bis = new BufferedInputStream(is);

			int length = 0;
			byte[] temp = new byte[1 * 1024 * 1024 * 50];

			while ((length = bis.read(temp)) != -1) {
				bos.write(temp, 0, length);
			}
			bos.flush();
			bis.close();
			bos.close();
		
			res.setContentLengthLong(file1.length());
			// 删除file文件内所有文件
			deleteFile(new File("file"));
			new File("file").mkdirs();

			Addmessage(request, "下载成功!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 把tonum个 数据导入filename中
	 * 
	 * @param index
	 *            开始查询条数
	 * @param tonum
	 *            查多少条
	 * @param questions2
	 *            插入的表头
	 * @param filename
	 *            要插入的文件地址
	 * @return
	 */
	public Integer createOneCsv(Integer index, Integer tonum, Questions questions2, String filename) {
		List<Questions> questions1;
		questions1 = questionsRepository.getQuestionsByLimitTqid(index, tonum);
		System.out.println(questions1.size());
		CSVUtils.createOneCSVFile(questions2, questions1, filename);
		System.out.println("tonum:" + tonum);
		index += CSVUtils.OnePageNum;
		questions1 = null;
		System.gc();
		return index;
	}

	/**
	 * 把tonum个 数据导入filename中，不产生大文件CSV
	 * 
	 * @param index
	 *            开始查询条数
	 * @param tonum
	 *            查多少条
	 * @param questions2
	 *            插入的表头
	 * @return
	 */
	public Integer createOneCsvOther(Integer index, Integer tonum, Questions questions2) {
		List<Questions> questions1;
		questions1 = questionsRepository.getQuestionsByLimitTqid(index, tonum);
		CSVUtils.createOneCSVFileOther(questions2, questions1);
		System.out.println("tonum:" + tonum);
		index += CSVUtils.OnePageNum;
		questions1 = null;
		System.gc();
		return index;
	}

	/**
	 * 把tonum个 数据导入到xlsx当中，
	 * 
	 * @param index
	 *            开始查询条数
	 * @param tonum
	 *            查多少条
	 * @param questions2
	 *            插入的表头
	 * @return
	 */
	public Integer createOneXlsxOther(Integer index, Integer tonum) {
		List<Questions> questions1;

		try {
			questions1 = questionsRepository.getQuestionsByLimitTqid(index, tonum);
			CreateExcel.Excel2007AboveOperateQuestionsOther(questions1);
			System.out.println("index:" + (index + CreateExcel.oneSearchExcel - 1));
			index += CreateExcel.oneSearchExcel;
			System.gc();
			return index;
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public String isCsvOrXlsx(String filename) {
		String[] fileSplit = filename.split(".");
		if ("csv".equals(fileSplit[1])) {
			return "csv";
		} else if ("xlsx".equals(fileSplit[1])) {
			return "xlsx";
		}
		return "error";
	}
}
