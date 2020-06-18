package com.open.capacity.oss.service.impl;

import com.open.capacity.common.util.UUIDUtils;
import com.open.capacity.oss.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.FdfsClientConfig;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.open.capacity.oss.dao.FileDao;
import com.open.capacity.oss.model.FileInfo;
import com.open.capacity.oss.model.FileType;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * fastdfs存储文件
 * @author pm 1280415703@qq.com
 * @date 2019/8/11 16:22
 */
@Import(FdfsClientConfig.class)
@Service("fastDfsOssServiceImpl")
@Slf4j
public class FastDfsOssServiceImpl extends AbstractFileService {

	@Autowired
	private FileDao fileDao;
	
	@Autowired
    private FastFileStorageClient storageClient;

	@Override
	protected FileDao getFileDao() {
		return fileDao;
	}
 
	/**
	 * nginx安装了fastdfs的地址
	 */
	@Value("${fdfs.oss.domain:}")
	private String domain;
	 

	@Override
	protected FileType fileType() {
		return FileType.FASTDFS;
	}

	 @Override
     protected void uploadFile(MultipartFile file, FileInfo fileInfo) throws Exception {
         StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
         fileInfo.setUrl(domain+ storePath.getFullPath());
         fileInfo.setPath(storePath.getFullPath());
     }

     @Override
     protected boolean deleteFile(FileInfo fileInfo) {
         if (fileInfo != null && StrUtil.isNotEmpty(fileInfo.getPath())) {
             StorePath storePath = StorePath.parseFromUrl(fileInfo.getPath());
             storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
         }
         return true;
     }

	/**
	 * 上传大文件
	 * 分片上传 每片一个临时文件
	 *
	 * @param guid
	 * @param chunk
	 * @param file
	 * @param chunks
	 * @return
	 */
	@Override
	protected void chunkFile(String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath)throws Exception {

	}


	/**
	 * 合并分片文件
	 * 每一个小片合并一个完整文件
	 *
	 * @param guid
	 * @param fileName
	 * @param filePath
	 * @return
	 */
	@Override
	protected void mergeFile(String guid, String fileName, String filePath) throws Exception {

		// 得到 destTempFile 就是最终的文件
		log.info("guid:{},fileName:{}",guid,fileName);

		File parentFileDir = new File(filePath + File.separator + guid);

		try {
			int index = fileName.lastIndexOf(".");

			// 文件扩展名
			String fileSuffix = fileName.substring(index);
			String suffix = "/" + LocalDate.now().toString() + "/"  + UUIDUtils.getGUID32() + fileSuffix;

			File destTempFile = new File(filePath , suffix);

			FileUtil.saveBigFile(guid, parentFileDir, destTempFile);

			// TODO: 2020/6/17 保存到数据库中 LOCAL
			FileInputStream fileInputStream = new FileInputStream(destTempFile);
			MultipartFile multipartFile = new MockMultipartFile(destTempFile.getName(), destTempFile.getName(),
					ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

			FileInfo fileInfo = FileUtil.getFileInfo(multipartFile);
			fileInfo.setName(fileName);
			FileInfo oldFileInfo = getFileDao().getById(fileInfo.getId());

			if (oldFileInfo != null) {
				return;
			}

			StorePath storePath = storageClient.uploadFile(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), null);
			fileInfo.setUrl(domain+ storePath.getFullPath());
			fileInfo.setPath(storePath.getFullPath());
			fileInfo.setSource(fileType().name());// 设置文件来源
			getFileDao().save(fileInfo);// 将文件信息保存到数据库

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 删除临时目录中的分片文件
			try {
				FileUtils.deleteDirectory(parentFileDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
