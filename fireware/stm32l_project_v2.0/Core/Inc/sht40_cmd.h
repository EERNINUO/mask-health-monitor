#ifndef __SHT40_CMD_H
#define __SHT40_CMD_H

#define SHT40_ADDRESS 0x88

#define READ_STATUS_REGISTER 0x89 // 读状态寄存器
#define SHT40_HIGH_PRECISION_MEASURE 0xFD // 高精度测量
#define SHT40_MEDIUM_PRECISION_MEASURE 0xF6 // 中精度测量
#define SHT40_LOW_PRECISION_MEASURE 0xE0

#define activate_heater_with_110mW_for_1s 0x2F

#endif // __SHT40_CMD_H