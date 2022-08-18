
package com.example.sdk.hikvision.original;

import com.sun.jna.Pointer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {
    //报警信息回调函数
    public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
        return true;
    }

    public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
        String sTime;
        String MonitoringSiteID;
        //lCommand是传的报警类型
        switch (lCommand) {

            case HCNetSDK.COMM_ITS_PLATE_RESULT://交通抓拍结果(新报警信息)
                HCNetSDK.NET_ITS_PLATE_RESULT strItsPlateResult = new HCNetSDK.NET_ITS_PLATE_RESULT();
                strItsPlateResult.write();
                Pointer pItsPlateInfo = strItsPlateResult.getPointer();
                pItsPlateInfo.write(0, pAlarmInfo.getByteArray(0, strItsPlateResult.size()), 0, strItsPlateResult.size());
                strItsPlateResult.read();
                try {
                    String sLicense = new String(strItsPlateResult.struPlateInfo.sLicense, "GBK");
                    byte VehicleType = strItsPlateResult.byVehicleType;  //0-其他车辆，1-小型车，2-大型车，3- 行人触发，4- 二轮车触发，5- 三轮车触发，6- 机动车触发
                     MonitoringSiteID = new String(strItsPlateResult.byMonitoringSiteID);
                    System.out.println("车牌号：" + sLicense + ":车辆类型：" + VehicleType + ":监控点编号：" + MonitoringSiteID);
                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                /**
                 * 报警图片保存，车牌，车辆图片
                 */
                for (int i = 0; i < strItsPlateResult.dwPicNum; i++) {
                    if (strItsPlateResult.struPicInfo[i].dwDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = "../pic/" + newName + "_type[" + strItsPlateResult.struPicInfo[i].byType + "]_ItsPlate.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strItsPlateResult.struPicInfo[i].pBuffer.getByteBuffer(offset, strItsPlateResult.struPicInfo[i].dwDataLen);
                            byte[] bytes = new byte[strItsPlateResult.struPicInfo[i].dwDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case HCNetSDK.COMM_ALARM_TFS : //道路违章取证报警
                HCNetSDK.NET_DVR_TFS_ALARM strTfsAlarm = new HCNetSDK.NET_DVR_TFS_ALARM();
                strTfsAlarm.write();
                Pointer pTfsAlarm = strTfsAlarm.getPointer();
                pTfsAlarm.write(0, pAlarmInfo.getByteArray(0, strTfsAlarm.size()), 0, strTfsAlarm.size());
                strTfsAlarm.read();
                sTime = CommonUtil.parseTime(strTfsAlarm.dwAbsTime); //报警绝对时间
                int IllegalType = strTfsAlarm.dwIllegalType; // 违章类型
                MonitoringSiteID=strTfsAlarm.byMonitoringSiteID.toString(); //监控点编号
                // 车牌信息
                try {
                    String PlateInfo="车牌号："+new String(strTfsAlarm.struPlateInfo.sLicense, "GBK");
                    System.out.println("【道路违章取证报警】时间："+sTime+"违章类型："+IllegalType+"车牌信息："+PlateInfo);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //报警图片信息
                for (int i = 0; i < strTfsAlarm.dwPicNum; i++) {
                    if (strTfsAlarm.struPicInfo[i].dwDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = "../pic/" + newName + "_type[" + strTfsAlarm.struPicInfo[i].byType + "]_TfsPlate.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strTfsAlarm.struPicInfo[i].pBuffer.getByteBuffer(offset, strTfsAlarm.struPicInfo[i].dwDataLen);
                            byte[] bytes = new byte[strTfsAlarm.struPicInfo[i].dwDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case HCNetSDK.COMM_ALARM_AID_V41: //道路事件检测
                HCNetSDK.NET_DVR_AID_ALARM_V41 strAIDAlarmV41 = new HCNetSDK.NET_DVR_AID_ALARM_V41();
                strAIDAlarmV41.write();
                Pointer pstrAIDAlarmV41 = strAIDAlarmV41.getPointer();
                pstrAIDAlarmV41.write(0, pAlarmInfo.getByteArray(0, strAIDAlarmV41.size()), 0, strAIDAlarmV41.size());
                strAIDAlarmV41.read();
                sTime=CommonUtil.parseTime(strAIDAlarmV41.dwAbsTime); //报警触发绝对时间
                 MonitoringSiteID=strAIDAlarmV41.byMonitoringSiteID.toString(); //监控点编号
                int AIDType=strAIDAlarmV41.struAIDInfo.dwAIDType; //    交通事件类型
                int AIDTypeEx=strAIDAlarmV41.struAIDInfo.dwAIDTypeEx; //交通事件类型扩展
                System.out.println("【道路事件检测】"+"时间："+sTime+"监控点："+MonitoringSiteID+"交通事件类型："+AIDType+"交通事件类型扩展："+AIDTypeEx);
                //报警图片信息
                    if (strAIDAlarmV41.dwPicDataLen > 0 &&strAIDAlarmV41.pImage != null) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = "../pic/" + newName + "_AIDalarm.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strAIDAlarmV41.pImage.getByteBuffer(offset, strAIDAlarmV41.dwPicDataLen);
                            byte[] bytes = new byte[strAIDAlarmV41.dwPicDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                break;
            case HCNetSDK.COMM_ALARM_TPS_V41://交通数据统计的报警
                HCNetSDK.NET_DVR_TPS_ALARM_V41 strTPSalarmV41 = new HCNetSDK.NET_DVR_TPS_ALARM_V41();
                strTPSalarmV41.write();
                Pointer pstrTPSalarmV41 = strTPSalarmV41.getPointer();
                pstrTPSalarmV41.write(0, pAlarmInfo.getByteArray(0, strTPSalarmV41.size()), 0, strTPSalarmV41.size());
                strTPSalarmV41.read();
                sTime=CommonUtil.parseTime(strTPSalarmV41.dwAbsTime);
                MonitoringSiteID=strTPSalarmV41.byMonitoringSiteID.toString(); //监控点编号
                String StartTime=CommonUtil.parseTime(strTPSalarmV41.dwStartTime); //开始统计时间；
                String StopTime=CommonUtil.parseTime(strTPSalarmV41.dwStopTime); //结束统计时间；
                System.out.println("【交通数据统计】"+"时间："+sTime+"监控点编号："+MonitoringSiteID+"开始统计时间："+StartTime+"结束统计时间："+StopTime);
                //车道统计参数信息
                for (int i=0;i<=HCNetSDK.MAX_TPS_RULE;i++)
                {
                    byte LaneNo=strTPSalarmV41.struTPSInfo.struLaneParam[i].byLaneNo; //车道号
                    byte TrafficState=strTPSalarmV41.struTPSInfo.struLaneParam[i].byTrafficState; //车道状态 0-无效，1-畅通，2-拥挤，3-堵塞
                    int TpsType=strTPSalarmV41.struTPSInfo.struLaneParam[i].dwTpsType; //数据变化类型标志，表示当前上传的统计参数中，哪些数据有效，按位区分
                    int LaneVolume=strTPSalarmV41.struTPSInfo.struLaneParam[i].dwLaneVolume; //车道流量
                    int LaneVelocity=strTPSalarmV41.struTPSInfo.struLaneParam[i].dwLaneVelocity; //车道平均速度
                    float SpaceOccupyRation=strTPSalarmV41.struTPSInfo.struLaneParam[i].fSpaceOccupyRation;  //车道占有率，百分比计算（空间上，车辆长度与监控路段总长度的比值)
                    System.out.println("车道号："+LaneNo+"车道状态："+TrafficState+"车道流量："+LaneVolume+"车道占有率："+SpaceOccupyRation+"\n");
                }
                break;
            case HCNetSDK.COMM_ALARM_TPS_REAL_TIME: //实时过车数据数据
                HCNetSDK.NET_DVR_TPS_REAL_TIME_INFO netDvrTpsParam = new HCNetSDK.NET_DVR_TPS_REAL_TIME_INFO();
                netDvrTpsParam.write();
                Pointer pItsParkVehicle = netDvrTpsParam.getPointer();
                pItsParkVehicle.write(0, pAlarmInfo.getByteArray(0, netDvrTpsParam.size()), 0, netDvrTpsParam.size());
                netDvrTpsParam.read();
                String struTime = "" + String.format("%04d", netDvrTpsParam.struTime.wYear) +
                        String.format("%02d", netDvrTpsParam.struTime.byMonth) +
                        String.format("%02d", netDvrTpsParam.struTime.byDay) +
                        String.format("%02d", netDvrTpsParam.struTime.byDay) +
                        String.format("%02d", netDvrTpsParam.struTime.byHour) +
                        String.format("%02d", netDvrTpsParam.struTime.byMinute) +
                        String.format("%02d", netDvrTpsParam.struTime.bySecond);
                Short wDeviceID = new Short(netDvrTpsParam.struTPSRealTimeInfo.wDeviceID);//设备ID
                int channel = netDvrTpsParam.dwChan; //触发报警通道号
                String byLane = new String(String.valueOf(netDvrTpsParam.struTPSRealTimeInfo.byLane)).trim();// 对应车道号
                String bySpeed = new String(String.valueOf(netDvrTpsParam.struTPSRealTimeInfo.bySpeed)).trim();// 对应车速（KM/H)
                int dwDownwardFlow = netDvrTpsParam.struTPSRealTimeInfo.dwDownwardFlow;//当前车道 从上到下车流量
                int dwUpwardFlow = netDvrTpsParam.struTPSRealTimeInfo.dwUpwardFlow;       //当前车道 从下到上车流量
                System.out.println("通道号：" + channel + "; 时间：" + struTime + ";对应车道号：" + byLane + ";当前车道 从上到下车流量：" + dwDownwardFlow + ";dwUpwardFlow：" + dwUpwardFlow);
                break;

            case HCNetSDK.COMM_ALARM_TPS_STATISTICS: //统计过车数据
                HCNetSDK.NET_DVR_TPS_STATISTICS_INFO netDvrTpsStatisticsInfo = new HCNetSDK.NET_DVR_TPS_STATISTICS_INFO();
                netDvrTpsStatisticsInfo.write();
                Pointer pTpsVehicle = netDvrTpsStatisticsInfo.getPointer();
                pTpsVehicle.write(0, pAlarmInfo.getByteArray(0, netDvrTpsStatisticsInfo.size()), 0, netDvrTpsStatisticsInfo.size());
                netDvrTpsStatisticsInfo.read();
                int Tpschannel = netDvrTpsStatisticsInfo.dwChan; //触发报警通道号
                //统计开始时间
                String struStartTime = "" + String.format("%04d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.wYear) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.byMonth) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.byDay) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.byDay) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.byHour) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.byMinute) +
                        String.format("%02d", netDvrTpsStatisticsInfo.struTPSStatisticsInfo.struStartTime.bySecond);
                byte TotalLaneNum = netDvrTpsStatisticsInfo.struTPSStatisticsInfo.byTotalLaneNum; //有效车道总数
                System.out.println("通道号：" + Tpschannel + "; 开始统计时间：" + struStartTime + "有效车道总数：" + TotalLaneNum);
                break;
            case HCNetSDK.COMM_ITS_PARK_VEHICLE: //停车场数据上传
                HCNetSDK.NET_ITS_PARK_VEHICLE strParkVehicle = new HCNetSDK.NET_ITS_PARK_VEHICLE();
                strParkVehicle.write();
                Pointer pstrParkVehicle = strParkVehicle.getPointer();
                pstrParkVehicle.write(0, pAlarmInfo.getByteArray(0, strParkVehicle.size()), 0, strParkVehicle.size());
                strParkVehicle.read();
                try {
                    byte ParkError = strParkVehicle.byParkError; //停车异常：0- 正常，1- 异常
                    String ParkingNo = new String(strParkVehicle.byParkingNo,"UTF-8"); //车位编号
                    byte LocationStatus= strParkVehicle.byLocationStatus; //车位车辆状态 0- 无车，1- 有车
                    MonitoringSiteID=strParkVehicle.byMonitoringSiteID.toString();
                    String plateNo=new String(strParkVehicle.struPlateInfo.sLicense,"GBK"); //车牌号
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //报警图片信息
                for (int i = 0; i < strParkVehicle.dwPicNum; i++) {
                    if (strParkVehicle.struPicInfo[i].dwDataLen > 0) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String newName = sf.format(new Date());
                        FileOutputStream fout;
                        try {
                            String filename = "../pic/" + newName + "_ParkVehicle.jpg";
                            fout = new FileOutputStream(filename);
                            //将字节写入文件
                            long offset = 0;
                            ByteBuffer buffers = strParkVehicle.struPicInfo[i].pBuffer.getByteBuffer(offset, strParkVehicle.struPicInfo[i].dwDataLen);
                            byte[] bytes = new byte[strParkVehicle.struPicInfo[i].dwDataLen];
                            buffers.rewind();
                            buffers.get(bytes);
                            fout.write(bytes);
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case HCNetSDK.COMM_ALARMHOST_CID_ALARM://报警主机CID报告报警上传
                HCNetSDK.NET_DVR_CID_ALARM strCIDalarm = new HCNetSDK.NET_DVR_CID_ALARM();
                strCIDalarm.write();
                Pointer pstrCIDalarm = strCIDalarm.getPointer();
                pstrCIDalarm.write(0, pAlarmInfo.getByteArray(0, strCIDalarm.size()), 0, strCIDalarm.size());
                strCIDalarm.read();
                try {
                    String TriggerTime  = "" + String.format("%04d", strCIDalarm.struTriggerTime.wYear) +
                            String.format("%02d", strCIDalarm.struTriggerTime.byMonth) +
                            String.format("%02d", strCIDalarm.struTriggerTime.byDay) +
                            String.format("%02d", strCIDalarm.struTriggerTime.byDay) +
                            String.format("%02d", strCIDalarm.struTriggerTime.byHour) +
                            String.format("%02d", strCIDalarm.struTriggerTime.byMinute) +
                            String.format("%02d", strCIDalarm.struTriggerTime.bySecond);  //触发报警时间
                    String sCIDCode = new String(strCIDalarm.sCIDCode,"GBK"); //CID事件号
                    String sCIDDescribe = new String(strCIDalarm.sCIDDescribe,"GBK"); //CID事件名
                    byte bySubSysNo = strCIDalarm.bySubSysNo; //子系统号；
                    short wDefenceNo = strCIDalarm.wDefenceNo; //防区编号
                    System.out.println("【CID事件】"+"触发时间："+TriggerTime+"CID事件号："+sCIDCode+"CID事件名："+sCIDDescribe+"子系统号："+
                            bySubSysNo+"防区编号："+wDefenceNo);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case HCNetSDK.COMM_IPC_AUXALARM_RESULT: //PIR报警、无线报警、呼救报警信息
                System.out.println("PIR报警、无线报警、呼救报警触发");
                break;
            case HCNetSDK.COMM_ISAPI_ALARM: //ISAPI协议报警信息
                HCNetSDK.NET_DVR_ALARM_ISAPI_INFO struEventISAPI = new HCNetSDK.NET_DVR_ALARM_ISAPI_INFO();
                struEventISAPI.write();
                Pointer pEventISAPI = struEventISAPI.getPointer();
                pEventISAPI.write(0, pAlarmInfo.getByteArray(0, struEventISAPI.size()), 0, struEventISAPI.size());
                struEventISAPI.read();
                String sAlarmInfo = new String(pAlarmer.sDeviceIP);
                //报警数据类型：0- invalid，1- xml，2- json
                sAlarmInfo = "报警设备IP：" + sAlarmInfo + "：ISAPI协议报警信息, 数据格式:" + struEventISAPI.byDataType +
                        ", 图片个数:" + struEventISAPI.byPicturesNumber;
                System.out.println(sAlarmInfo);

                //报警数据保存
                SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                String curTime1 = sf1.format(new Date());
                FileOutputStream foutdata;
                try {
                    String jsonfilename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() + curTime1 + "_ISAPI_Alarm_" + ".json";
                    foutdata = new FileOutputStream(jsonfilename);
                    //将字节写入文件
                    ByteBuffer jsonbuffers = struEventISAPI.pAlarmData.getByteBuffer(0, struEventISAPI.dwAlarmDataLen);
                    byte[] jsonbytes = new byte[struEventISAPI.dwAlarmDataLen];
                    jsonbuffers.rewind();
                    jsonbuffers.get(jsonbytes);
                    foutdata.write(jsonbytes);
                    foutdata.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //图片数据保存
                for (int i = 0; i < struEventISAPI.byPicturesNumber; i++) {
                    HCNetSDK.NET_DVR_ALARM_ISAPI_PICDATA struPicData = new HCNetSDK.NET_DVR_ALARM_ISAPI_PICDATA();
                    struPicData.write();
                    Pointer pPicData = struPicData.getPointer();
                    pPicData.write(0, struEventISAPI.pPicPackData.getByteArray(i * struPicData.size(), struPicData.size()), 0, struPicData.size());
                    struPicData.read();

                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() + curTime1 +
                                "_ISAPIPic_" + i + "_" + new String(struPicData.szFilename).trim() + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struPicData.pPicData.getByteBuffer(offset, struPicData.dwPicLen);
                        byte[] bytes = new byte[struPicData.dwPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case HCNetSDK.COMM_VCA_ALARM:  // 智能检测通用报警(Json或者XML数据结构)
                sAlarmInfo = new String(pAlarmer.sDeviceIP);
                //报警数据类型：0- invalid，1- xml，2- json
                sAlarmInfo = "报警设备IP：" + sAlarmInfo;
                System.out.println(sAlarmInfo);

                SimpleDateFormat sf0 = new SimpleDateFormat("yyyyMMddHHmmss");
                String curTime0 = sf0.format(new Date());
                FileOutputStream Data;
                String jsonfile = "../pic" + new String(pAlarmer.sDeviceIP).trim() + curTime0 + "_VCA_ALARM_" + ".json";
                try {
                    Data = new FileOutputStream(jsonfile);
                    //将字节写入文件
                    ByteBuffer dataBuffer = pAlarmInfo.getByteBuffer(0, dwBufLen);
                    byte[] dataByte = new byte[dwBufLen];
                    dataBuffer.rewind();
                    dataBuffer.get(dataByte);
                    Data.write(dataByte);
                    Data.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            //行为分析信息
            case HCNetSDK.COMM_ALARM_RULE:
                HCNetSDK.NET_VCA_RULE_ALARM strVcaAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
                strVcaAlarm.write();
                Pointer pVCAInfo = strVcaAlarm.getPointer();
                pVCAInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
                strVcaAlarm.read();

                switch (strVcaAlarm.struRuleInfo.wEventTypeEx) {
                    case 1: //穿越警戒面 (越界侦测)
                        System.out.println("穿越警戒面报警发生");
                        break;
                    case 2: //目标进入区域

                        System.out.println("目标进入区域报警发生");

                        break;
                    case 3: //目标离开区域
                        System.out.println("目标离开区域报警触发");
                    case 4: //周界入侵

                        System.out.println("周界入侵报警发生");

                        break;
                    case 5: //徘徊
                        System.out.println("徘徊事件触发");

                        break;
                    case 8: //快速移动(奔跑)，
                        System.out.println("快速移动(奔跑)事件触发");
                        break;
                    case 15: //离岗
                        System.out.println("离岗事件触发");
                    case 20: //倒地检测
                        System.out.println("倒地事件触发");
                        break;
                    case 44: //玩手机

                        System.out.println("玩手机报警发生");

                        if ((strVcaAlarm.dwPicDataLen > 0) && (strVcaAlarm.byPicTransType == 0)) {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String newName = sf.format(new Date());
                            FileOutputStream fout;
                            try {
                                String filename = "../pic/" + newName + "PLAY_CELLPHONE_" + ".jpg";
                                fout = new FileOutputStream(filename);
                                //将字节写入文件
                                long offset = 0;
                                ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                                byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                                buffers.rewind();
                                buffers.get(bytes);
                                fout.write(bytes);
                                fout.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 45: //持续检测
                        System.out.println("持续检测事件触发");
                    default:
                        System.out.println("行为事件类型:"+strVcaAlarm.struRuleInfo.wEventTypeEx);
                        break;
                }
                break;
            case HCNetSDK.COMM_ALARM_ACS: //门禁主机报警信息
                HCNetSDK.NET_DVR_ACS_ALARM_INFO strACSInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
                strACSInfo.write();
                Pointer pACSInfo = strACSInfo.getPointer();
                pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
                strACSInfo.read();
                /**门禁事件的详细信息解析，通过主次类型的可以判断当前的具体门禁类型，例如（主类型：0X5 次类型：0x4b 表示人脸认证通过，
                 主类型：0X5 次类型：0x4c 表示人脸认证失败）*/
                System.out.println("【门禁主机报警信息】卡号：" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim() + "，卡类型：" +
                        strACSInfo.struAcsEventInfo.byCardType + "，报警主类型：" + Integer.toHexString(strACSInfo.dwMajor) + "，报警次类型：" + Integer.toHexString(strACSInfo.dwMinor));
                System.out.println("工号1：" + strACSInfo.struAcsEventInfo.dwEmployeeNo);
                //温度信息（如果设备支持测温功能，人脸温度信息从NET_DVR_ACS_EVENT_INFO_EXTEND_V20结构体获取）
                if (strACSInfo.byAcsEventInfoExtendV20 == 1) {
                    HCNetSDK.NET_DVR_ACS_EVENT_INFO_EXTEND_V20 strAcsInfoExV20 = new HCNetSDK.NET_DVR_ACS_EVENT_INFO_EXTEND_V20();
                    strAcsInfoExV20.write();
                    Pointer pAcsInfoExV20 = strAcsInfoExV20.getPointer();
                    pAcsInfoExV20.write(0, strACSInfo.pAcsEventInfoExtendV20.getByteArray(0, strAcsInfoExV20.size()), 0, strAcsInfoExV20.size());
                    strAcsInfoExV20.read();
                    System.out.println("实时温度值：" + strAcsInfoExV20.fCurrTemperature);
                }
                //考勤状态
                if (strACSInfo.byAcsEventInfoExtend == 1) {
                    HCNetSDK.NET_DVR_ACS_EVENT_INFO_EXTEND strAcsInfoEx = new HCNetSDK.NET_DVR_ACS_EVENT_INFO_EXTEND();
                    strAcsInfoEx.write();
                    Pointer pAcsInfoEx = strAcsInfoEx.getPointer();
                    pAcsInfoEx.write(0, strACSInfo.pAcsEventInfoExtend.getByteArray(0, strAcsInfoEx.size()), 0, strAcsInfoEx.size());
                    strAcsInfoEx.read();
                    System.out.println("考勤状态：" + strAcsInfoEx.byAttendanceStatus);
                    System.out.println("工号2：" + new String(strAcsInfoEx.byEmployeeNo).trim());
                }

                /**
                 * 报警时间
                 */
                String year = Integer.toString(strACSInfo.struTime.dwYear);
                String SwipeTime = year + strACSInfo.struTime.dwMonth + strACSInfo.struTime.dwDay + strACSInfo.struTime.dwHour + strACSInfo.struTime.dwMinute +
                        strACSInfo.struTime.dwSecond;
                if (strACSInfo.dwPicDataLen > 0) {
//                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String newName = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        /**
                         * 人脸保存路径
                         */
                        String filename = "../pic/" + SwipeTime + "_ACS_Event_" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim() + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strACSInfo.pPicData.getByteBuffer(offset, strACSInfo.dwPicDataLen);
                        byte[] bytes = new byte[strACSInfo.dwPicDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case HCNetSDK.COMM_ID_INFO_ALARM: //身份证信息
                HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM strIDCardInfo = new HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM();
                strIDCardInfo.write();
                Pointer pIDCardInfo = strIDCardInfo.getPointer();
                pIDCardInfo.write(0, pAlarmInfo.getByteArray(0, strIDCardInfo.size()), 0, strIDCardInfo.size());
                strIDCardInfo.read();
                System.out.println("报警主类型：" + Integer.toHexString(strIDCardInfo.dwMajor) + "，报警次类型：" + Integer.toHexString(strIDCardInfo.dwMinor));
                /**
                 * 身份证信息
                 */
                String IDnum = new String(strIDCardInfo.struIDCardCfg.byIDNum).trim();
                System.out.println("【身份证信息】：身份证号码：" + IDnum + "，姓名：" +
                        new String(strIDCardInfo.struIDCardCfg.byName).trim() + "，住址：" + new String(strIDCardInfo.struIDCardCfg.byAddr));

                /**
                 * 报警时间
                 */
                String year1 = Integer.toString(strIDCardInfo.struSwipeTime.wYear);
                String SwipeTime1 = year1 + strIDCardInfo.struSwipeTime.byMonth + strIDCardInfo.struSwipeTime.byDay + strIDCardInfo.struSwipeTime.byHour + strIDCardInfo.struSwipeTime.byMinute +
                        strIDCardInfo.struSwipeTime.bySecond;
                /**
                 * 保存图片
                 */
                //身份证图片
                if (strIDCardInfo.dwPicDataLen > 0 || strIDCardInfo.pPicData != null) {
//                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String nowtime = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + SwipeTime1 + "_" + IDnum + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strIDCardInfo.pPicData.getByteBuffer(offset, strIDCardInfo.dwPicDataLen);
                        byte[] bytes = new byte[strIDCardInfo.dwPicDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (strIDCardInfo.dwCapturePicDataLen > 0 || strIDCardInfo.pCapturePicData != null) {
//                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String nowtime = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        /**
                         * 人脸图片保存路径
                         */
                        String filename = "../pic/" + SwipeTime1 + "_CapturePic_" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strIDCardInfo.pCapturePicData.getByteBuffer(offset, strIDCardInfo.dwCapturePicDataLen);
                        byte[] bytes = new byte[strIDCardInfo.dwCapturePicDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;

            case HCNetSDK.COMM_ALARM_VIDEO_INTERCOM: //可视对讲报警信息
                System.out.println("可视对讲报警触发");



                break;
            case HCNetSDK.COMM_UPLOAD_VIDEO_INTERCOM_EVENT: //可视对讲事件记录信息
                System.out.println("可视对讲事件记录报警触发");



                break;

            case HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT: //实时人脸抓拍上传
                System.out.println("UPLOAD_FACESNAP_Alarm");
                HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
                strFaceSnapInfo.write();
                Pointer pFaceSnapInfo = strFaceSnapInfo.getPointer();
                pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, strFaceSnapInfo.size()), 0, strFaceSnapInfo.size());
                strFaceSnapInfo.read();
                
                //事件时间
                int dwYear = (strFaceSnapInfo.dwAbsTime>>26)+2000;
                int dwMonth = (strFaceSnapInfo.dwAbsTime>>22)&15;
                int dwDay = (strFaceSnapInfo.dwAbsTime>>17)&31;
                int dwHour = (strFaceSnapInfo.dwAbsTime>>12)&31;
                int dwMinute = (strFaceSnapInfo.dwAbsTime>>6)&63;
                int dwSecond = (strFaceSnapInfo.dwAbsTime>>0)&63;
                
                String strAbsTime = "" + String.format("%04d", dwYear) +
                        String.format("%02d", dwMonth) +
                        String.format("%02d", dwDay) +
                        String.format("%02d", dwHour) +
                        String.format("%02d", dwMinute) +
                        String.format("%02d", dwSecond);
     
                //人脸属性信息
                String sFaceAlarmInfo = "Abs时间:" + strAbsTime + ",年龄:" + strFaceSnapInfo.struFeature.byAge + 
                		",性别：" + strFaceSnapInfo.struFeature.bySex + ",是否戴口罩：" +
                        strFaceSnapInfo.struFeature.byMask + ",是否微笑：" + strFaceSnapInfo.struFeature.bySmile;
                System.out.println("人脸信息：" + sFaceAlarmInfo);
                
                //人脸测温信息
                if (strFaceSnapInfo.byAddInfo == 1) {
                    HCNetSDK.NET_VCA_FACESNAP_ADDINFO strAddInfo = new HCNetSDK.NET_VCA_FACESNAP_ADDINFO();
                    strAddInfo.write();
                    Pointer pAddInfo = strAddInfo.getPointer();
                    pAddInfo.write(0, strFaceSnapInfo.pAddInfoBuffer.getByteArray(0, strAddInfo.size()), 0, strAddInfo.size());
                    strAddInfo.read();

                    String sTemperatureInfo = "测温是否开启：" + strAddInfo.byFaceSnapThermometryEnabled + "人脸温度：" + strAddInfo.fFaceTemperature + "温度是否异常"
                            + strAddInfo.byIsAbnomalTemperature + "报警温度阈值：" + strAddInfo.fAlarmTemperature;
                    System.out.println("人脸温度信息:" + sTemperatureInfo);

                }

                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
                    String time = df.format(new Date());// new Date()为获取当前系统时间

                    //人脸图片写文件
                    FileOutputStream small = new FileOutputStream("../pic/" + time + "small.jpg");
                    FileOutputStream big = new FileOutputStream("../pic/" + time + "big.jpg");
                    try {
                        small.write(strFaceSnapInfo.pBuffer1.getByteArray(0, strFaceSnapInfo.dwFacePicLen), 0, strFaceSnapInfo.dwFacePicLen);
                        small.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        big.write(strFaceSnapInfo.pBuffer2.getByteArray(0, strFaceSnapInfo.dwBackgroundPicLen), 0, strFaceSnapInfo.dwBackgroundPicLen);
                        big.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;

            case HCNetSDK.COMM_SNAP_MATCH_ALARM:    //人脸黑名单比对报警
                HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
                strFaceSnapMatch.write();
                Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
                pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0, strFaceSnapMatch.size());
                strFaceSnapMatch.read();
                //比对结果，0-保留，1-比对成功，2-比对失败
                String sFaceSnapMatchInfo="比对结果："+strFaceSnapMatch.byContrastStatus+",相似度："+strFaceSnapMatch.fSimilarity;
                System.out.println(sFaceSnapMatchInfo);
                if (strFaceSnapMatch.struBlockListInfo.dwFDIDLen > 0) {
                    long offset1 = 0;
                    ByteBuffer buffers1 = strFaceSnapMatch.struBlockListInfo.pFDID.getByteBuffer(offset1, strFaceSnapMatch.struBlockListInfo.dwFDIDLen);
                    byte[] bytes1 = new byte[strFaceSnapMatch.struBlockListInfo.dwFDIDLen];
                    buffers1.get(bytes1);
                    System.out.println("人脸库ID:" + new String(bytes1));
                }
                if (strFaceSnapMatch.struBlockListInfo.dwPIDLen > 0) {
                    long offset2 = 0;
                    ByteBuffer buffers2 = strFaceSnapMatch.struBlockListInfo.pPID.getByteBuffer(offset2, strFaceSnapMatch.struBlockListInfo.dwPIDLen);
                    byte[] bytes2 = new byte[strFaceSnapMatch.struBlockListInfo.dwPIDLen];
                    buffers2.get(bytes2);
                    System.out.println("图片ID：" + new String(bytes2));
                }
                //抓拍库附加信息解析（解析人脸测温温度,人脸温度存放在附件信息的XML报文中，节点：  <currTemperature> ）
                SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                String curTime2 = sf2.format(new Date());
                FileOutputStream AddtionData;
                String AddtionFile = "../pic" + new String(pAlarmer.sDeviceIP).trim() + curTime2 + "_FCAdditionInfo_" + ".xml";
                try {
                    Data = new FileOutputStream(AddtionFile);
                    //将字节写入文件
                    ByteBuffer dataBuffer = strFaceSnapMatch.struBlockListInfo.struBlockListInfo.pFCAdditionInfoBuffer.getByteBuffer(0, strFaceSnapMatch.struBlockListInfo.struBlockListInfo.dwFCAdditionInfoLen );
                    byte[] dataByte = new byte[dwBufLen];
                    dataBuffer.rewind();
                    dataBuffer.get(dataByte);
                    Data.write(dataByte);
                    Data.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //人脸比对报警图片保存，图片格式二进制
                if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + newName + "_pSnapPicBuffer" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
                        byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + newName + "_struSnapInfo_pBuffer1" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strFaceSnapMatch.struSnapInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
                        byte[] bytes = new byte[strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + newName + "_fSimilarity_" + strFaceSnapMatch.fSimilarity + "_struBlackListInfo_pBuffer1" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen);
                        byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //人脸比对报警图片保存，图片格式URL格式
                if ((strFaceSnapMatch.dwSnapPicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {

                    long offset = 0;
                    ByteBuffer buffers = strFaceSnapMatch.pSnapPicBuffer.getByteBuffer(offset, strFaceSnapMatch.dwSnapPicLen);
                    byte[] bytes = new byte[strFaceSnapMatch.dwSnapPicLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    String SnapPicUrl = new String(bytes);
                    System.out.println("抓拍图URL：" + SnapPicUrl);
                }
                if ((strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {

                    long offset = 0;
                    ByteBuffer buffers = strFaceSnapMatch.struSnapInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen);
                    byte[] bytes = new byte[strFaceSnapMatch.struSnapInfo.dwSnapFacePicLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    String SnapPicUrl = new String(bytes);
                    System.out.println("抓拍人脸子图URL：" + SnapPicUrl);
                }
                if ((strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen > 0) && (strFaceSnapMatch.byPicTransType == 1)) {

                    long offset = 0;
                    ByteBuffer buffers = strFaceSnapMatch.struBlockListInfo.pBuffer1.getByteBuffer(offset, strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen);
                    byte[] bytes = new byte[strFaceSnapMatch.struBlockListInfo.dwBlockListPicLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    String SnapPicUrl = new String(bytes);
                    System.out.println("人脸库人脸图的URL：" + SnapPicUrl);
                }


                break;
            //  客流量报警信息
            case HCNetSDK.COMM_ALARM_PDC:
                HCNetSDK.NET_DVR_PDC_ALRAM_INFO strPDCResult = new HCNetSDK.NET_DVR_PDC_ALRAM_INFO();
                strPDCResult.write();
                Pointer pPDCInfo = strPDCResult.getPointer();
                pPDCInfo.write(0, pAlarmInfo.getByteArray(0, strPDCResult.size()), 0, strPDCResult.size());
                strPDCResult.read();
                // byMode=0-实时统计结果(联合体中struStatFrame有效)，
                if (strPDCResult.byMode == 0) {
                    strPDCResult.uStatModeParam.setType(HCNetSDK.NET_DVR_STATFRAME.class);
                    String sAlarmPDC0Info = "实时客流量统计，进入人数：" + strPDCResult.dwEnterNum + "，离开人数：" + strPDCResult.dwLeaveNum +
                            ", byMode:" + strPDCResult.byMode + ", dwRelativeTime:" + strPDCResult.uStatModeParam.struStatFrame.dwRelativeTime +
                            ", dwAbsTime:" + strPDCResult.uStatModeParam.struStatFrame.dwAbsTime;
                }
                // byMode=1-周期统计结果(联合体中struStatTime有效)，
                if (strPDCResult.byMode == 1) {
                    strPDCResult.uStatModeParam.setType(HCNetSDK.NET_DVR_STATTIME.class);
                    String strtmStart = "" + String.format("%04d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwYear) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwMonth) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwDay) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwHour) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwMinute) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmStart.dwSecond);
                    String strtmEnd = "" + String.format("%04d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwYear) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwMonth) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwDay) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwHour) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwMinute) +
                            String.format("%02d", strPDCResult.uStatModeParam.struStatTime.tmEnd.dwSecond);
                    String sAlarmPDC1Info = "周期性客流量统计，进入人数：" + strPDCResult.dwEnterNum + "，离开人数：" + strPDCResult.dwLeaveNum +
                            ", byMode:" + strPDCResult.byMode + ", tmStart:" + strtmStart + ",tmEnd :" + strtmEnd;
                }
                break;
            case HCNetSDK.COMM_ALARM_V30:  //移动侦测、视频丢失、遮挡、IO信号量等报警信息(V3.0以上版本支持的设备)
                HCNetSDK.NET_DVR_ALARMINFO_V30 struAlarmInfo = new HCNetSDK.NET_DVR_ALARMINFO_V30();
                struAlarmInfo.write();
                Pointer pAlarmInfo_V30 = struAlarmInfo.getPointer();
                pAlarmInfo_V30.write(0, pAlarmInfo.getByteArray(0, struAlarmInfo.size()), 0, struAlarmInfo.size());
                struAlarmInfo.read();
                System.out.println("报警类型：" + struAlarmInfo.dwAlarmType);  // 3-移动侦测
                break;
            case HCNetSDK.COMM_ALARM_V40: //移动侦测、视频丢失、遮挡、IO信号量等报警信息，报警数据为可变长
                HCNetSDK.NET_DVR_ALARMINFO_V40 struAlarmInfoV40 = new HCNetSDK.NET_DVR_ALARMINFO_V40();
                struAlarmInfoV40.write();
                Pointer pAlarmInfoV40 = struAlarmInfoV40.getPointer();
                pAlarmInfoV40.write(0, pAlarmInfo.getByteArray(0, struAlarmInfoV40.size()), 0, struAlarmInfoV40.size());
                struAlarmInfoV40.read();
                System.out.println("报警类型:" + struAlarmInfoV40.struAlarmFixedHeader.dwAlarmType); //3-移动侦测
                break;
            case HCNetSDK.COMM_THERMOMETRY_ALARM:  //温度报警信息
                HCNetSDK.NET_DVR_THERMOMETRY_ALARM struTemInfo = new HCNetSDK.NET_DVR_THERMOMETRY_ALARM();
                struTemInfo.write();
                Pointer pTemInfo = struTemInfo.getPointer();
                pTemInfo.write(0, pAlarmInfo.getByteArray(0, struTemInfo.size()), 0, struTemInfo.size());
                struTemInfo.read();
                String sThermAlarmInfo = "规则ID:" + struTemInfo.byRuleID + "预置点号：" + struTemInfo.wPresetNo + "报警等级：" + struTemInfo.byAlarmLevel + "报警类型：" +
                        struTemInfo.byAlarmType + "当前温度：" + struTemInfo.fCurrTemperature;
                System.out.println(sThermAlarmInfo);
                //可见光图片保存
                if ((struTemInfo.dwPicLen > 0) && (struTemInfo.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + struTemInfo.fCurrTemperature + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struTemInfo.pPicBuff.getByteBuffer(offset, struTemInfo.dwPicLen);
                        byte[] bytes = new byte[struTemInfo.dwPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if ((struTemInfo.dwThermalPicLen > 0) && (struTemInfo.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + "_ThermalPiC" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struTemInfo.pThermalPicBuff.getByteBuffer(offset, struTemInfo.dwThermalPicLen);
                        byte[] bytes = new byte[struTemInfo.dwThermalPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case HCNetSDK.COMM_THERMOMETRY_DIFF_ALARM: //温差检测报警
                HCNetSDK.NET_DVR_THERMOMETRY_DIFF_ALARM strThermDiffAlarm = new HCNetSDK.NET_DVR_THERMOMETRY_DIFF_ALARM();
                strThermDiffAlarm.write();
                Pointer pTemDiffInfo = strThermDiffAlarm.getPointer();
                pTemDiffInfo.write(0, pAlarmInfo.getByteArray(0, strThermDiffAlarm.size()), 0, strThermDiffAlarm.size());
                strThermDiffAlarm.read();
                String sThremDiffInfo = "通道号：" + strThermDiffAlarm.dwChannel + ",报警规则：" + strThermDiffAlarm.byAlarmRule + "，当前温差：" + strThermDiffAlarm.fCurTemperatureDiff;
                System.out.println(sThremDiffInfo);
                //可见光图片保存
                if ((strThermDiffAlarm.dwPicLen > 0) && (strThermDiffAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + strThermDiffAlarm.fCurTemperatureDiff + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strThermDiffAlarm.pPicBuff.getByteBuffer(offset, strThermDiffAlarm.dwPicLen);
                        byte[] bytes = new byte[strThermDiffAlarm.dwPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //热成像图片保存
                if ((strThermDiffAlarm.dwThermalPicLen > 0) && (strThermDiffAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + "_ThermalDiffPiC" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strThermDiffAlarm.pThermalPicBuff.getByteBuffer(offset, strThermDiffAlarm.dwThermalPicLen);
                        byte[] bytes = new byte[strThermDiffAlarm.dwThermalPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case HCNetSDK.COMM_ALARM_SHIPSDETECTION: //船只检测报警
                HCNetSDK.NET_DVR_SHIPSDETECTION_ALARM struShipAlarm = new HCNetSDK.NET_DVR_SHIPSDETECTION_ALARM();
                struShipAlarm.write();
                Pointer pShipAlarm = struShipAlarm.getPointer();
                pShipAlarm.write(0, pAlarmInfo.getByteArray(0, struShipAlarm.size()), 0, struShipAlarm.size());
                struShipAlarm.read();
                String sShipAlarm = "绝对时间：" + struShipAlarm.dwAbsTime + ",正跨越检测线的船只数:" + struShipAlarm.byShipsNum + ",船头检测的船只数 :" + struShipAlarm.byShipsNumHead
                        + ", 船尾检测的船只数 :" + struShipAlarm.byShipsNumEnd;
                System.out.println(sShipAlarm);
                //可见光图片保存
                if ((struShipAlarm.dwPicLen > 0) && (struShipAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_ShipAlarm" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struShipAlarm.pPicBuffer.getByteBuffer(offset, struShipAlarm.dwPicLen);
                        byte[] bytes = new byte[struShipAlarm.dwPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //热成像图片保存
                if ((struShipAlarm.dwThermalPicLen > 0) && (struShipAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + "_ThermalShipAlarm" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struShipAlarm.pThermalPicBuffer.getByteBuffer(offset, struShipAlarm.dwThermalPicLen);
                        byte[] bytes = new byte[struShipAlarm.dwThermalPicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;

            case HCNetSDK.COMM_FIREDETECTION_ALARM://烟火检测
                HCNetSDK.NET_DVR_FIREDETECTION_ALARM struFireDecAlarm = new HCNetSDK.NET_DVR_FIREDETECTION_ALARM();
                struFireDecAlarm.write();
                Pointer pFireDecAlarm = struFireDecAlarm.getPointer();
                pFireDecAlarm.write(0, pAlarmInfo.getByteArray(0, struFireDecAlarm.size()), 0, struFireDecAlarm.size());
                struFireDecAlarm.read();
                String sFireDecAlarmInfo = "绝对时间：" + struFireDecAlarm.dwAbsTime + ",报警子类型：" + struFireDecAlarm.byAlarmSubType + ",火点最高温度 :" +
                        struFireDecAlarm.wFireMaxTemperature + ",火点目标距离：" + struFireDecAlarm.wTargetDistance;
                System.out.println(sFireDecAlarmInfo);
                //可见光图片保存
                if ((struFireDecAlarm.dwVisiblePicLen > 0) && (struFireDecAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_FireDecAlarm" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struFireDecAlarm.pVisiblePicBuf.getByteBuffer(offset, struFireDecAlarm.dwVisiblePicLen);
                        byte[] bytes = new byte[struFireDecAlarm.dwVisiblePicLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //热成像图片保存
                if ((struFireDecAlarm.dwPicDataLen > 0) && (struFireDecAlarm.byPicTransType == 0)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newName = sf.format(new Date());
                    FileOutputStream fout;

                    try {
                        String filename = "../pic/" + newName + "_" + "_ThermalFireAlarm" + ".jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struFireDecAlarm.pBuffer.getByteBuffer(offset, struFireDecAlarm.dwPicDataLen);
                        byte[] bytes = new byte[struFireDecAlarm.dwPicDataLen];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                break;
            case HCNetSDK.COMM_UPLOAD_AIOP_VIDEO: //AI开放平台接入视频检测报警信息
                System.out.println("AI开放平台接入视频检测报警上传");
                HCNetSDK.NET_AIOP_VIDEO_HEAD struAIOPVideo = new HCNetSDK.NET_AIOP_VIDEO_HEAD();
                struAIOPVideo.write();
                Pointer pAIOPVideo = struAIOPVideo.getPointer();
                pAIOPVideo.write(0, pAlarmInfo.getByteArray(0, struAIOPVideo.size()), 0, struAIOPVideo.size());
                struAIOPVideo.read();
                System.out.println("视频任务ID" + new String(struAIOPVideo.szTaskID));
                System.out.println("通道号：" + struAIOPVideo.dwChannel);
                System.out.println("检测模型包ID" + new String(struAIOPVideo.szMPID));
                String strTime = String.format("%04d", struAIOPVideo.struTime.wYear) +
                        String.format("%02d", struAIOPVideo.struTime.wMonth) +
                        String.format("%02d", struAIOPVideo.struTime.wDay) +
                        String.format("%02d", struAIOPVideo.struTime.wHour) +
                        String.format("%02d", struAIOPVideo.struTime.wMinute) +
                        String.format("%02d", struAIOPVideo.struTime.wSecond) +
                        String.format("%03d", struAIOPVideo.struTime.wMilliSec);
                //AIOPData数据
                if (struAIOPVideo.dwAIOPDataSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + strTime + "_VideoData.json";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struAIOPVideo.pBufferAIOPData.getByteBuffer(offset, struAIOPVideo.dwAIOPDataSize);
                        byte[] bytes = new byte[struAIOPVideo.dwAIOPDataSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //图片数据保存
                if (struAIOPVideo.dwPictureSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + strTime + "_VideoPic.jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struAIOPVideo.pBufferPicture.getByteBuffer(offset, struAIOPVideo.dwPictureSize);
                        byte[] bytes = new byte[struAIOPVideo.dwPictureSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case HCNetSDK.COMM_UPLOAD_AIOP_PICTURE: //AI开放平台接入图片检测报警信息
                System.out.println("AI开放平台接入图片检测报警上传");
                HCNetSDK.NET_AIOP_PICTURE_HEAD struAIOPPic = new HCNetSDK.NET_AIOP_PICTURE_HEAD();
                struAIOPPic.write();
                Pointer pAIOPPic = struAIOPPic.getPointer();
                pAIOPPic.write(0, pAlarmInfo.getByteArray(0, struAIOPPic.size()), 0, struAIOPPic.size());
                struAIOPPic.read();
                System.out.println("图片ID：" + new String(struAIOPPic.szPID));
                System.out.println("检测模型包ID：" + new String(struAIOPPic.szMPID));
                String strPicTime = "" + String.format("%04d", struAIOPPic.struTime.wYear) +
                        String.format("%02d", struAIOPPic.struTime.wMonth) +
                        String.format("%02d", struAIOPPic.struTime.wDay) +
                        String.format("%02d", struAIOPPic.struTime.wHour) +
                        String.format("%02d", struAIOPPic.struTime.wMinute) +
                        String.format("%02d", struAIOPPic.struTime.wSecond) +
                        String.format("%03d", struAIOPPic.struTime.wMilliSec);
                //AIOPData数据
                if (struAIOPPic.dwAIOPDataSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + strPicTime + "_AIO_PicData.json";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = struAIOPPic.pBufferAIOPData.getByteBuffer(offset, struAIOPPic.dwAIOPDataSize);
                        byte[] bytes = new byte[struAIOPPic.dwAIOPDataSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;

            //AI开放平台接入轮询抓图检测报警信息
            case HCNetSDK.COMM_UPLOAD_AIOP_POLLING_SNAP:
                System.out.println("AI开放平台接入轮询抓图检测报警事件上传");
                HCNetSDK.NET_AIOP_POLLING_SNAP_HEAD strAiopPollingPic = new HCNetSDK.NET_AIOP_POLLING_SNAP_HEAD();
                strAiopPollingPic.write();
                Pointer pAiopPollingPic = strAiopPollingPic.getPointer();
                pAiopPollingPic.write(0, pAlarmInfo.getByteArray(0, strAiopPollingPic.size()), 0, strAiopPollingPic.size());
                strAiopPollingPic.read();
                System.out.println("通道号：" + strAiopPollingPic.dwChannel);
                System.out.println("轮询抓图任务ID：" + new String(strAiopPollingPic.szTaskID));
                String strPollingPicTime = "" + String.format("%04d", strAiopPollingPic.struTime.wYear) +
                        String.format("%02d", strAiopPollingPic.struTime.wMonth) +
                        String.format("%02d", strAiopPollingPic.struTime.wDay) +
                        String.format("%02d", strAiopPollingPic.struTime.wHour) +
                        String.format("%02d", strAiopPollingPic.struTime.wMinute) +
                        String.format("%02d", strAiopPollingPic.struTime.wSecond) +
                        String.format("%03d", strAiopPollingPic.struTime.wMilliSec);
                //AIOPData数据保存
                if (strAiopPollingPic.dwAIOPDataSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + strPollingPicTime + "_PollingPicData.json";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strAiopPollingPic.pBufferAIOPData.getByteBuffer(offset, strAiopPollingPic.dwAIOPDataSize);
                        byte[] bytes = new byte[strAiopPollingPic.dwAIOPDataSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //轮询抓图图片保存
                if (strAiopPollingPic.dwPictureSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + strPollingPicTime + "_PollingPic.jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strAiopPollingPic.pBufferPicture.getByteBuffer(offset, strAiopPollingPic.dwPictureSize);
                        byte[] bytes = new byte[strAiopPollingPic.dwPictureSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                break;
            //AI开放平台接入轮询视频检测报警信息
            case HCNetSDK.COMM_UPLOAD_AIOP_POLLING_VIDEO:
                System.out.println("AI开放平台接入轮询视频检测报警事件上传");
                HCNetSDK.NET_AIOP_POLLING_VIDEO_HEAD strAiopPollingVideo = new HCNetSDK.NET_AIOP_POLLING_VIDEO_HEAD();
                strAiopPollingVideo.write();
                Pointer pAiopPollingVideo = strAiopPollingVideo.getPointer();
                pAiopPollingVideo.write(0, pAlarmInfo.getByteArray(0, strAiopPollingVideo.size()), 0, strAiopPollingVideo.size());
                strAiopPollingVideo.read();
                System.out.println("通道号：" + strAiopPollingVideo.dwChannel);
                System.out.println("轮询视频任务ID：" + new String(strAiopPollingVideo.szTaskID));
                String AiopPollingVideoTime = "" + String.format("%04d", strAiopPollingVideo.struTime.wYear) +
                        String.format("%02d", strAiopPollingVideo.struTime.wMonth) +
                        String.format("%02d", strAiopPollingVideo.struTime.wDay) +
                        String.format("%02d", strAiopPollingVideo.struTime.wHour) +
                        String.format("%02d", strAiopPollingVideo.struTime.wMinute) +
                        String.format("%02d", strAiopPollingVideo.struTime.wSecond) +
                        String.format("%03d", strAiopPollingVideo.struTime.wMilliSec);
                //AIOPData数据保存
                if (strAiopPollingVideo.dwAIOPDataSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + AiopPollingVideoTime + "_PollingVideoData.json";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strAiopPollingVideo.pBufferAIOPData.getByteBuffer(offset, strAiopPollingVideo.dwAIOPDataSize);
                        byte[] bytes = new byte[strAiopPollingVideo.dwAIOPDataSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                //对应分析图片数据
                if (strAiopPollingVideo.dwPictureSize > 0) {
                    FileOutputStream fout;
                    try {
                        String filename = "../pic/" + new String(pAlarmer.sDeviceIP).trim() +
                                "_" + AiopPollingVideoTime + "_PollingVideo.jpg";
                        fout = new FileOutputStream(filename);
                        //将字节写入文件
                        long offset = 0;
                        ByteBuffer buffers = strAiopPollingVideo.pBufferPicture.getByteBuffer(offset, strAiopPollingVideo.dwPictureSize);
                        byte[] bytes = new byte[strAiopPollingVideo.dwPictureSize];
                        buffers.rewind();
                        buffers.get(bytes);
                        fout.write(bytes);
                        fout.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            default:
                System.out.println("报警类型" + Integer.toHexString(lCommand));
                break;
        }
    }
}





