package com.example.sdk.hikvision.biz;

import com.example.sdk.hikvision.bo.SdkActionResult;
import com.example.sdk.hikvision.original.HCNetSDK;
import com.example.sdk.hikvision.original.OsSelect;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对海康威视SDK包装
 */
public class HikVision {

    /**
     * window环境动态链接库存放目录
     */
    private static final String DLL_WIN_PATH = "C:\\hikvision\\lib\\";

    /**
     * linux环境动态链接库存放目录
     */
    private static final String DLL_LINUX_PATH = "/mnt/data/hikvision/lib/";

    private static final String SDK_LOG_WIN_PATH = "C:\\hikvision\\log\\";
    private static final String SDK_LOG_LINUX_PATH = "/mnt/data/hikvision/log/";

    private static final int ERROR = -1;

    /**
     * 报警-布防句柄
     * key为设备标识，value为该设备布防成功时返回的值
     */
    private static Map<String, Integer> alarmChanMap = new ConcurrentHashMap<>();


    /**
     * 报警-监听句柄
     */
    private static int lListenHandle = -1;


    private volatile static HCNetSDK hCNetSDK;


    /**
     * 实例化 HCNetSDK
     * @return
     */
    private static boolean instanceHCNetSDK() {
        /**
         * 创建 HCNetSDK，并调用初始化方法
         */
        boolean isCreated = false;
        String dllPath = "";
        if (OsSelect.isWindows()) {
            dllPath = DLL_WIN_PATH + "HCNetSDK.dll";
        } else if (OsSelect.isLinux()) {
            dllPath = DLL_LINUX_PATH + "libhcnetsdk.so";
        } else {
            System.out.println("未能识别操作系统, 无法获取动态链接库路径");
        }
        if (dllPath != null && !"".equals(dllPath)) {
            hCNetSDK = (HCNetSDK)Native.loadLibrary(dllPath, HCNetSDK.class);
            isCreated = true;
            System.out.println("HCNetSDK create success.");
        }

        /**
         * hCNetSDK初始化
         */
        if (isCreated) {

            if (OsSelect.isLinux()) {
                HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
                HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
                //这里是库的绝对路径，请根据实际情况修改，注意该路径必须有访问权限
                String strPath1 = DLL_LINUX_PATH + "libcrypto.so.1.1";
                String strPath2 = DLL_LINUX_PATH + "libssl.so.1.1";

                System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
                ptrByteArray1.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());

                System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
                ptrByteArray2.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());

                String strPathCom = DLL_LINUX_PATH;
                HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
                System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
                struComPath.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
            }

            // sdk加载日志
            if (OsSelect.isWindows()) {
                hCNetSDK.NET_DVR_SetLogToFile(3, SDK_LOG_WIN_PATH, false);
            } else {
                hCNetSDK.NET_DVR_SetLogToFile(3, SDK_LOG_LINUX_PATH, false);
            }

            hCNetSDK.NET_DVR_Init();
            System.out.println("hCNetSDK init success.");

            NET_DVR_SetConnectTime(2000, 1);
            NET_DVR_SetReconnect(10000, true);

            DVR_LOCAL_GENERAL_CFG();

        }
        return isCreated;
    }


    /**
     * 获取 HCNetSDK
     * @return
     */
    private static boolean initHCNetSDK() {
        boolean isInit = true;
        if (hCNetSDK == null) {
            synchronized(HikVision.class) {
                if (hCNetSDK == null) {
                    isInit = instanceHCNetSDK();
                }
            }
        }
        return isInit;
    }



    /**
     * 初始化化
     * @return
     */
    public static boolean NET_DVR_Init() {
        return initHCNetSDK();

    }

    /**
     * 设置连接参数
     * @param waitTime 等待时间
     * @param tryTimes  重试次数
     */
    public static boolean NET_DVR_SetConnectTime(int waitTime, int tryTimes) {
        return hCNetSDK.NET_DVR_SetConnectTime(waitTime, tryTimes);
    }

    /**
     * 设置重连参数
     * @param interval  重连间隔
     * @param enableReconnect 是否重连
     * @return
     */
    public static boolean NET_DVR_SetReconnect(int interval, boolean enableReconnect) {
        return hCNetSDK.NET_DVR_SetReconnect(interval, enableReconnect);
    }

    /**
     * 注册登录
     * @param deviceIp 设备ip地址
     * @param devicePort 设备端口
     * @param username  设备登录账号
     * @param password  设备登录密码
     * @return
     */
    public static SdkActionResult NET_DVR_LOGIN_v40(String deviceIp, short devicePort, String username, String password) {
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        String m_sDeviceIP = deviceIp;//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        String m_sUsername = username;//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        String m_sPassword = password;//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = devicePort;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        // m_strLoginInfo.byLoginMode=1;  //ISAPI登录
        m_strLoginInfo.write();

        /**
         * 异步登录的状态、用户ID和设备信息通过NET_DVR_USER_LOGIN_INFO结构体中设置的回调函数(fLoginResultCallBack)返回。
         * 对于同步登录，接口返回-1表示登录失败，其他值表示返回的用户ID值。
         * 用户ID具有唯一性，后续对设备的操作都需要通过此ID实现
         */
        int ret = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        return buildSdkActionResult(ret);
    }

    /**
     * 设置回调函数
     * @return true-设置回调函数成功，false-设置回调函数失败
     */
    public static SdkActionResult NET_DVR_SetDVRMessageCallBack_V31(HCNetSDK.FMSGCallBack_V31 fmsgCallBack_v31) {
        boolean ret = hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fmsgCallBack_v31, null);
        return buildSdkActionResult(ret);
    }


    /**
     * 设置通用参数
     */
    public static boolean DVR_LOCAL_GENERAL_CFG() {
    /** 设备上传的报警信息是COMM_VCA_ALARM(0x4993)类型，
         在SDK初始化之后增加调用NET_DVR_SetSDKLocalCfg(enumType为NET_DVR_LOCAL_CFG_TYPE_GENERAL)设置通用参数NET_DVR_LOCAL_GENERAL_CFG的byAlarmJsonPictureSeparate为1，
         将Json数据和图片数据分离上传，这样设置之后，报警布防回调函数里面接收到的报警信息类型为COMM_ISAPI_ALARM(0x6009)，
         报警信息结构体为NET_DVR_ALARM_ISAPI_INFO（与设备无关，SDK封装的数据结构），更便于解析。
     */
        HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struNET_DVR_LOCAL_GENERAL_CFG = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
        struNET_DVR_LOCAL_GENERAL_CFG.byAlarmJsonPictureSeparate = 1;   //设置JSON透传报警数据和图片分离
        struNET_DVR_LOCAL_GENERAL_CFG.write();
        Pointer pStrNET_DVR_LOCAL_GENERAL_CFG = struNET_DVR_LOCAL_GENERAL_CFG.getPointer();

        // enumType=17 代表通用参数配置
        return hCNetSDK.NET_DVR_SetSDKLocalCfg(17, pStrNET_DVR_LOCAL_GENERAL_CFG);
    }


    /**
     * 报警-布防形式
     * @param deviceId 设备编号
     * @param userId 注册登录成功返回的userId
     * @return
     */
    public static SdkActionResult NET_DVR_SetupAlarmChan_V41(String deviceId, int userId) {

        //报警布防参数设置
        HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
        m_strAlarmInfo.byLevel = 0;  //布防等级
        m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
        m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
        m_strAlarmInfo.byFaceAlarmDetection = 0; //人脸抓拍报警，上传COMM_UPLOAD_FACESNAP_RESULT类型报警信息
        m_strAlarmInfo.write();
        int ret = hCNetSDK.NET_DVR_SetupAlarmChan_V41(userId, m_strAlarmInfo);
        alarmChanMap.put(deviceId, ret);
        return buildSdkActionResult(ret);
    }


    /**
     * 报警-监听方式
     * @param serverIp  服务端监听ip
     * @param serverPort 服务端监听端口
     * @param fmsgCallBack_v31 回调函数
     * @return
     */
    public static SdkActionResult NET_DVR_StartListen_V30(String serverIp, short serverPort, HCNetSDK.FMSGCallBack_V31 fmsgCallBack_v31) {
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(serverIp, serverPort, fmsgCallBack_v31, null);
        return buildSdkActionResult(lListenHandle);
    }

    /**
     * 报警-停止监听
     * @return
     */
    public static boolean NET_DVR_StopListen_V30() {
        boolean ret = true;
        if (lListenHandle != ERROR) {
            ret = hCNetSDK.NET_DVR_StopListen_V30(lListenHandle);
        }
        return ret;
    }

    /**
     * 报警-停止布防
     * @param deviceId
     * @return
     */
    public static boolean NET_DVR_CloseAlarmChan(String deviceId) {
        boolean ret = true;
        Integer value = alarmChanMap.get(deviceId);
        if (value != null && value != ERROR) {
            ret = hCNetSDK.NET_DVR_CloseAlarmChan(value);
        }
        return ret;
    }


    /**
     * 注销用户
     * @param userId
     * @return
     */
    public static boolean NET_DVR_Logout(int userId) {
        return hCNetSDK.NET_DVR_Logout(userId);
    }

    /**
     * 释放SDK资源
     * @return
     */
    public static boolean NET_DVR_Cleanup() {
        return hCNetSDK.NET_DVR_Cleanup();
    }

    /**
     * 获取最后一次错误码
     * @return
     */
    public static int NET_DVR_GetLastError() {
        return hCNetSDK.NET_DVR_GetLastError();
    }








    private static SdkActionResult buildSdkActionResult(int ret) {
        SdkActionResult result = new SdkActionResult();
        result.setRetValue(ret);
        if (ret == ERROR) {
            result.setSuccess(false);
            result.setErrorCode(hCNetSDK.NET_DVR_GetLastError());
        } else {
            result.setSuccess(true);
        }
        return result;
    }

    private static SdkActionResult buildSdkActionResult(boolean ret) {
        SdkActionResult result = new SdkActionResult();
        if (!ret) {
            result.setSuccess(false);
            result.setErrorCode(hCNetSDK.NET_DVR_GetLastError());
        } else {
            result.setSuccess(true);
        }
        return result;
    }








}
