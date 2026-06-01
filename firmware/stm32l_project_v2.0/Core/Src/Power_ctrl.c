#include "Power_ctrl.h"

uint32_t last_time = 0;

void get_Voltage(void){
    if (HAL_GetTick() - last_time > 1200 || last_time == 0){ // 如果距上次不到1.2s则跳过
        last_time = HAL_GetTick();

        IIC_start();

        IIC_send_data(BQ27441_ADDR | 0x00); 
        IIC_read_ack();
        IIC_send_data(BQ27441_GET_VOLATGE & 0x00ff); 
        IIC_read_ack();
        IIC_send_data((BQ27441_GET_VOLATGE & 0xff00) >> 8);
        IIC_read_ack();

        sensor_data.voltage = IIC_read_data();
        sensor_data.voltage <<= 8;
        IIC_ack(1);
        sensor_data.voltage += IIC_read_data();
        IIC_ack(1);

        IIC_end();
    }
}

void Power_off(void){
    HAL_GPIO_WritePin(Key_GPIO_Port, Key_Pin, GPIO_PIN_SET);
    HAL_GPIO_WritePin(VCCS_Ctrl_GPIO_Port, VCCS_Ctrl_Pin, GPIO_PIN_SET);

    __HAL_RCC_PWR_CLK_ENABLE();
    HAL_PWR_EnableWakeUpPin(PWR_WAKEUP_PIN1);
    __HAL_PWR_CLEAR_FLAG(PWR_FLAG_WU);
    HAL_PWR_EnterSTANDBYMode();
}