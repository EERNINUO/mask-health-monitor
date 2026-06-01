#include "sensors.h"

#pragma pack(push, 1) // 按1字节对齐
struct Sensor_data sensor_data;
#pragma pack(pop)

void SHT40_start_measure(void);
void SGP30_init(void);
void SGP30_start_measure(void);
void LPS22HB_init(void);
void LPS22HB_start_measure(void);

void sensors_init(void) {
    SHT40_heater();
    SGP30_init();
    LPS22HB_init();

    SHT40_start_measure();
    SGP30_start_measure();
    LPS22HB_start_measure();
}

void SGP30_init(){
    IIC_start();
    
	IIC_send_data(SGP30_ADDRESS | 0x00); 
    IIC_read_ack();
    IIC_send_data((SGP30_IAQ_INIT & 0xff00) >> 8);
    IIC_read_ack();
    IIC_send_data(SGP30_IAQ_INIT & 0x00ff);
    IIC_read_ack();
    IIC_end();
}

void SGP30_set_abslute_humidity(uint16_t absolute_humidity){
    ;// TODO
}

void SGP30_start_measure(void){
    IIC_start();
    
	IIC_send_data(SGP30_ADDRESS | 0x00); 
    IIC_read_ack();
    IIC_send_data((SGP30_MEASURE_IAQ & 0xff00) >> 8);
    IIC_read_ack();
    IIC_send_data(SGP30_MEASURE_IAQ & 0x00ff);
    IIC_read_ack();
    IIC_end();
}

void SGP30_get_data(){
    IIC_start();

	IIC_send_data(SGP30_ADDRESS | 0x01); // 发送从机地址
    IIC_read_ack();

    // 测量CO2浓度
    sensor_data.co2 = IIC_read_data();
    IIC_ack(1);
    sensor_data.co2 <<= 8;
    sensor_data.co2 += IIC_read_data();
    IIC_ack(1);
    IIC_read_data(); // 校验位
    IIC_ack(1);

    // 测量TVOC浓度
    sensor_data.tvoc = IIC_read_data();
    sensor_data.tvoc <<= 8;
    IIC_ack(1);
    sensor_data.tvoc += IIC_read_data();
    IIC_ack(1);
    IIC_read_data(); // 校验位
    IIC_ack(1);

    IIC_end();

    SGP30_start_measure();
}

void SHT40_start_measure(void){
    IIC_start();
    
	IIC_send_data(SHT40_ADDRESS | 0x00); 
    IIC_read_ack();
    IIC_send_data(SHT40_HIGH_PRECISION_MEASURE);
    IIC_read_ack();
    IIC_end();
}

void SHT40_heater(void){
    IIC_start();
    
	IIC_send_data(SHT40_ADDRESS | 0x00); 
    IIC_read_ack();
    IIC_send_data(activate_heater_with_110mW_for_1s);
    IIC_read_ack();
    IIC_end();
}

void SHT40_get_data(){
    IIC_start();
	IIC_send_data(SHT40_ADDRESS | 0x01);
    IIC_read_ack();

    sensor_data.temperature = IIC_read_data() << 8;
    IIC_ack(1);
    sensor_data.temperature += IIC_read_data();
    IIC_ack(1);
    IIC_read_data(); // 校验位
    IIC_ack(1);

    sensor_data.humidity  = IIC_read_data() << 8;
    IIC_ack(1);
    sensor_data.humidity += IIC_read_data();
    IIC_ack(1);
    IIC_read_data(); // 校验位
    IIC_ack(1);
    IIC_end();

    SHT40_start_measure();
}

void LPS22HB_init(void){
    IIC_start();

	IIC_send_data(LPS22HB_ADDRESS | 0x00);
    IIC_read_ack();
    IIC_send_data(LPS22HB_CTRL_REG1);
    IIC_read_ack();
    IIC_send_data(0x0A);
    IIC_read_ack();

    IIC_end();
}

void LPS22HB_start_measure(void){
    IIC_start();

    IIC_send_data(LPS22HB_ADDRESS | 0x00);
    IIC_read_ack();
    IIC_send_data(LPS22HB_CTRL_REG2);
    IIC_read_ack();
    IIC_send_data(0x11);
    IIC_read_ack();

    IIC_end();
}

void LPS22HB_get_data(){
    // 读取状态寄存器
    IIC_start();
	IIC_send_data(LPS22HB_ADDRESS | 0x00);
    IIC_read_ack();
    IIC_send_data(LPS22HB_STATUS);
    IIC_read_ack();
    IIC_end();

    IIC_start();
	IIC_send_data(LPS22HB_ADDRESS | 0x01);
    IIC_read_ack();
    uint8_t status = IIC_read_data();
    IIC_ack(0);
    IIC_end();

    if ((status & 0x03) == 0x03){
        IIC_start();
        IIC_send_data(LPS22HB_ADDRESS | 0x00);
        IIC_read_ack();
        IIC_send_data(LPS22HB_PRESS_OUT_XL);
        IIC_read_ack();
        IIC_end();

        IIC_start();
        IIC_send_data(LPS22HB_ADDRESS | 0x01);
        IIC_read_ack();
        sensor_data.pressure = IIC_read_data();
        IIC_ack(1);
        sensor_data.pressure += IIC_read_data() << 8;
        IIC_ack(1);
        sensor_data.pressure += IIC_read_data() << 16;
        IIC_ack(0);
        IIC_end();

        LPS22HB_start_measure();
    }
}
