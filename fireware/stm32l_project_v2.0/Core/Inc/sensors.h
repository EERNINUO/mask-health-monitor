#ifndef __SENSORS_H
#define __SENSORS_H

#include "main.h"
#include "IIC.h"

#include "sgp30_cmd.h"
#include "sht40_cmd.h"
#include "lps22hb.h"

#pragma pack(push, 1) // 按1字节对齐
extern struct Sensor_data{
    uint16_t co2; // CO2浓度
    uint16_t tvoc; // TVOC浓度
    uint16_t temperature; // 温度
    uint16_t humidity; // 湿度
    uint32_t pressure; // 气压
    uint16_t voltage; // 电池电压
} sensor_data;
#pragma pack(pop)

void sensors_init(void);

void SHT40_heater(void);

void SHT40_get_data(void);
void SGP30_get_data(void);
void LPS22HB_get_data(void);

#endif // __SENSORS_H