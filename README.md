> 对海康威视网络设备SDK进行封装，以方便项目调用


### 海康设备网络SDK版本
```
CH-HCNetSDKV6.1.9.4_build20220412_win64_20220419185136.zip
CH-HCNetSDKV6.1.9.4_build20220413_linux64_20220419184908.zip
```

### 使用方法
1. 将 hikvision/lib/jar 目录中的jna.jar和examples.jar安装到本地仓库
```shell
mvn install:install-file -DgroupId=net.java.jna -DartifactId=jna -Dversion=1.0.0 -Dpackaging=jar -Dfile=E:\Projects\Hikvision\lib\jars\jna.jar


mvn install:install-file -DgroupId=net.java.examples -DartifactId=examples -Dversion=1.0.0 -Dpackaging=jar -Dfile=E:\Projects\Hikvision\lib\jars\examples.jar
```

2. 放置动态链接库
```shell
1) window环境: 将 hikvision/lib/win 目录下的文件全部复制到 c:\hikvision\lib  目录下

2) linux环境: 将 hikvision/lib/linux 目录下的文件全部复制到 /mnt/data/hikivision/lib  目录下

3) 确保程序对上述目录有读写权限
```

3.调用方法举例
```java
        // 1.初始化
        HikVision.NET_DVR_Init();

        // 2.设置回调函数
        SdkActionResult sdkActionResult = HikVision.NET_DVR_SetDVRMessageCallBack_V31(new FMSGCallBack_V31());

        // 3. 注册登录
        // 需提供设备ip, 设备端口、设备登录账号、设备登录密码
        sdkActionResult = HikVision.NET_DVR_LOGIN_v40("192.168.0.118", (short) 8000, "admin", "123456");

        int userId = sdkActionResult.getRetValue();

        // 4. 报警布防
        sdkActionResult = HikVision.NET_DVR_SetupAlarmChan_V41("1", userId);



        // 报警监听
        // SdkActionResult sdkActionResult = HikVision.NET_DVR_StartListen_V30(null, (short)5060, new FMSGCallBack_V31());




        while (true) {
            //这里加入控制台输入控制，是为了保持连接状态，当输入Y表示布防结束
            System.out.print("请选择是否撤出布防(Y/N)：");
            Scanner input = new Scanner(System.in);
            String str = input.next();
            if (str.equals("Y")) {
                break;
            }
        }

        // 停止 报警监听
        // HikVision.NET_DVR_StopListen_V30();

        
        // 停止 报警布防
        HikVision.NET_DVR_CloseAlarmChan("1");
        // 注销用户
        HikVision.NET_DVR_Logout(userId);
        
        // 释放SDK资源
        HikVision.NET_DVR_Cleanup();


```

4.SpringBoot调用
```java
1) 实现自己业务逻辑的回调函数 HCNetSDK.FMSGCallBack_V31 如：
@Component
public class HikVisionCallback implements HCNetSDK.FMSGCallBack_V31 {

    @Override
    public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        return false;
    }
}

2) 回调函数方法实现可参考 com.example.sdk.hikvision.original.FMSGCallBack_V31

3) 在项目启动时，进行报警布防或者报警监听
@Component
public class AlarmInit implements CommandLineRunner {

    @Resource
    private HikVisionCallback hikVisionCallback;

    @Override
    public void run(String... args) throws Exception {
        String serverIp = "192.168.0.153";
        short serverPort = 5060;

        boolean initResult = HikVision.NET_DVR_Init();
        if (!initResult) {
            log.error("HikVision SDK 初始化失败.");
            return;
        }

        log.info("HikVision SDK 初始化成功.");
        
        // 报警-监听
        SdkActionResult actionResult = HikVision.NET_DVR_StartListen_V30(serverIp, serverPort, hikVisionCallback);
        if (!actionResult.isSuccess()) {
            log.info("HikVision 报警监听设置失败，错误码:{}", actionResult.getErrorCode());
            return;
        }
        log.info("HikVision 报警监听设置成功");

    }
}

```
