package com.atguigu.gulimall.product;

//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import com.aliyun.oss.*;
import com.aliyun.oss.OSSClient;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallProductApplicationTests {
	@Autowired
	BrandService brandService;
	@Autowired
	OSSClient ossClient;
	@Autowired
	CategoryService categoryService;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Test
	public void testRedis(){
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello","world_"+ UUID.randomUUID().toString());
		System.out.println("保存的数据是："+ops.get("hello"));

	}

    @Test
	public void testFindPath(){
		Long[] catelogPath = categoryService.findCatelogPath(225L);
		log.info("路径{}", Arrays.asList(catelogPath));
	}
	@Test
	public void testUpload() throws FileNotFoundException {
			InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\timg.jpg");
			ossClient.putObject("gulimall-chenxi","寡姐.jpg",inputStream);
	          ossClient.shutdown();
            System.out.println("上传完成");
	}
//    @Test
//	public void testUpload() throws Exception {
//		// Endpoint以杭州为例，其它Region请按实际情况填写。
//		String endpoint = "oss-cn-shanghai.aliyuncs.com";
//// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//		String accessKeyId = "LTAI4GCA7fjMN8SJ7SPqypxn";
//		String accessKeySecret = "hWBSez9rSzPS3iN8HdiAMLry16UBdJ";
//
//// 创建OSSClient实例。
//		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 上传文件流。
//		InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\timg.jpg");
//		ossClient.putObject("gulimall-chenxi", "555.jpg", inputStream);
//
//// 关闭OSSClient。
//		ossClient.shutdown();
//		System.out.println("上传完成");
//	}
	@Test
	void contextLoads() {
		BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("华为333");
		brandService.save(brandEntity);
	}

}
