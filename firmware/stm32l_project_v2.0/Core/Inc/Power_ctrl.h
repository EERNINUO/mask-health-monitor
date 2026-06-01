#ifndef __POWER_CTRL_H__
#define __POWER_CTRL_H__

#include "main.h"
#include "IIC.h"
#include "sensors.h"

#define BQ27441_ADDR 0xAA

#define BQ27441_GET_VOLATGE (uint16_t)0x0504

void get_Voltage(void);
void Power_off(void);

#endif 