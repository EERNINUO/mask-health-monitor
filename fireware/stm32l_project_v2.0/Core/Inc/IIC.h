#ifndef __IIC_H
#define __IIC_H

#include "main.h"
#include "gpio.h"

void IIC_start(void);
void IIC_end(void);
void IIC_send_data(uint8_t Byte);
uint8_t IIC_read_data(void);
void IIC_ack(uint8_t ack);
uint8_t IIC_read_ack(void);

#endif