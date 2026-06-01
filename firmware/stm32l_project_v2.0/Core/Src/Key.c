#include "Key.h"

uint16_t KeyPress_Event = 0;
uint16_t Key_state = KEY_NOPRESS;
static uint32_t start_time = 0;

void Check_Key(void){
    if (KeyPress_Event == 1 && Key_state == KEY_NOPRESS){
        HAL_Delay(10 - 1);
        if (HAL_GPIO_ReadPin(Key_GPIO_Port, Key_Pin) == GPIO_PIN_RESET){
            Key_state = KEY_PRESS;
            KeyPress_Event = 0;
            start_time = HAL_GetTick();
        }
    }
    if (Key_state == KEY_PRESS){
        if (HAL_GPIO_ReadPin(Key_GPIO_Port, Key_Pin) == GPIO_PIN_SET){
            if (HAL_GetTick() - start_time > 1000){
                Key_state = KEY_LONGPRESS;

                __HAL_RCC_PWR_CLK_ENABLE();
                HAL_PWR_EnableWakeUpPin(PWR_WAKEUP_PIN1);
                __HAL_PWR_CLEAR_FLAG(PWR_FLAG_WU);
                HAL_PWR_EnterSTANDBYMode();
            } 
            else {
                Key_state = KEY_NOPRESS;
            }
        }
    }
}