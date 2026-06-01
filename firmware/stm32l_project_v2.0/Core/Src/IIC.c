#include "IIC.h"

__attribute__((optnone)) void IIC_delay(){
    for (uint8_t i= 0; i < 5; i++){
        __NOP();
    }
}

void SDA_write(uint8_t BitValue){
	HAL_GPIO_WritePin(GPIOB, SDA_Pin, (GPIO_PinState)BitValue);
	IIC_delay();
}

void SCL_write(uint8_t BitValue){
	HAL_GPIO_WritePin(GPIOB, SCL_Pin, (GPIO_PinState)BitValue);
	IIC_delay();
}

uint8_t SDA_read(void){
	uint8_t BitValue;
	BitValue = HAL_GPIO_ReadPin(GPIOB, SDA_Pin);
	return BitValue;
}

void IIC_start(void){
	SDA_write(1);
	SCL_write(1);
    SDA_write(0);
	SCL_write(0);
}

void IIC_end(void){
	SDA_write(0);
	SCL_write(1);	
	SDA_write(1);
}

void IIC_send_data(uint8_t Byte){
	for(uint8_t i = 0; i < 8; i ++){
		SDA_write(Byte & (0x80 >> i));
		SCL_write(1);
		SCL_write(0);
	}
}

uint8_t IIC_read_ack(void){
	uint8_t AckBit;
	SDA_write(1);
	SCL_write(1);
	AckBit = SDA_read();
	SCL_write(0);
	return AckBit;
}

uint8_t IIC_read_data(void){
	uint8_t dat = 0;
	for(uint8_t i=0; i<8; i++){
		SCL_write(1);
		if (SDA_read()) dat++;
		if (i != 7) dat <<= 1;
		SCL_write(0);
	}
	return dat;
}

void IIC_ack(uint8_t ack){
	if (ack) SDA_write(0);
	else SDA_write(1);
	SCL_write(1);
	SCL_write(0);
	SDA_write(1);
}
