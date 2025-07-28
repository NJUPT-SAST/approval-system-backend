package fun.sast;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import java.io.*;
import java.util.Random;

public class ApplicationTest {
    /** 生成一个唯一的 Bucket 名称 */
    public static String generateUniqueBucketName(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return prefix + "-" + timestamp + "-" + randomNum;
    }

    public static void main(String[] args) {
        // 配置OSS参数
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        String bucketName = generateUniqueBucketName("sast-demo");
        String region = "cn-hangzhou";

        // 使用您的AccessKey（请替换为实际值）
        String accessKeyId = "";
        String accessKeySecret = "";

        // 配置签名版本
        ClientBuilderConfiguration clientConfig = new ClientBuilderConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);

        // 创建OSSClient实例
        OSS ossClient =
                OSSClientBuilder.create()
                        .endpoint(endpoint)
                        .credentialsProvider(
                                new DefaultCredentialProvider(accessKeyId, accessKeySecret))
                        .region(region)
                        .clientConfiguration(clientConfig)
                        .build();

        try {
            // 1. 创建存储空间（Bucket）
            ossClient.createBucket(bucketName);
            System.out.println("1. Bucket 创建成功: " + bucketName);

            // 2. 上传文件
            String objectName = "exampledir/exampleobject.txt";
            String content = "Hello OSS - 此文件将保留在Bucket中";
            ossClient.putObject(
                    bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
            System.out.println("2. 文件上传成功: " + objectName);

            // 3. 下载并显示文件内容
            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(ossObject.getObjectContent()))) {
                System.out.println("3. 文件内容:");
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("   " + line);
                }
            }

            // 4. 列出Bucket中的文件
            System.out.println("4. Bucket中的文件列表:");
            ObjectListing objectListing = ossClient.listObjects(bucketName);
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(
                        "   - "
                                + objectSummary.getKey()
                                + " (大小: "
                                + objectSummary.getSize()
                                + " bytes)");
            }

            // 5. 添加额外文件以便查看
            String extraFileName = "exampledir/additional_file.txt";
            String extraContent = "这是额外添加的测试文件";
            ossClient.putObject(
                    bucketName, extraFileName, new ByteArrayInputStream(extraContent.getBytes()));
            System.out.println("5. 额外文件已添加: " + extraFileName);

            // 6. 显示访问信息（重要）
            System.out.println("\n===== 重要提示 =====");
            System.out.println("所有文件已保留在Bucket中，您可以在OSS控制台查看:");
            System.out.println("Bucket名称: " + bucketName);
            System.out.println("区域: " + region);
            System.out.println(
                    "访问地址: https://oss.console.aliyun.com/bucket/oss-cn-hangzhou/"
                            + bucketName
                            + "/object");
            System.out.println("文件列表:");
            System.out.println("  1. " + objectName);
            System.out.println("  2. " + extraFileName);
            System.out.println("====================\n");

        } catch (OSSException oe) {
            System.out.println("OSS异常: " + oe.getMessage());
            oe.printStackTrace();
        } catch (ClientException | IOException ce) {
            System.out.println("客户端异常: " + ce.getMessage());
            ce.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        System.out.println("程序执行完成。所有文件已保留在Bucket中！");
    }
}
