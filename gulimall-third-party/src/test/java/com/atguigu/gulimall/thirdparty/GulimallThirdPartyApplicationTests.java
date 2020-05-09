package com.atguigu.gulimall.thirdparty;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	public void testUpload() throws Exception {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
		String endpoint = "oss-cn-shanghai.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
		String accessKeyId = "LTAI4GCA7fjMN8SJ7SPqypxn";
		String accessKeySecret = "hWBSez9rSzPS3iN8HdiAMLry16UBdJ";

// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
		InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\63e862164165f483.jpg");
		ossClient.putObject("gulimall-chenxi", "ff.jpg", inputStream);

// 关闭OSSClient。
		ossClient.shutdown();
		System.out.println("上传完成");
	}
}
