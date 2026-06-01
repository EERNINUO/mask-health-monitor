#ifndef __SGP30_CMD_H
#define __SGP30_CMD_H

#define SGP30_ADDRESS 0xB0 // SGP30的I2C地址

#define SGP30_IAQ_INIT 0x2003 // 初始化空气质量补偿
#define SGP30_MEASURE_IAQ 0x2008 // 测量空气质量
#define SGP30_MEASURE_RAW 0x2050 // 测量原始数据
#define SGP30_SET_ABSOLUTE_HUMIDITY 0x2061 // 设置绝对湿度 发送数据格式：（数据）+ （校验和）
#define SGP30_GET_ID 3682 // 获取芯片ID

#endif /* __SGP30_CMD_H */