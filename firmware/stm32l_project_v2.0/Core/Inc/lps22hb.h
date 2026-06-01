#ifndef __LPS22HB_H__
#define __LPS22HB_H__

#define LPS22HB_ADDRESS 0xB8

#define LPS22HB_WHO_AM_I 0x0F

#define LPS22HB_CTRL_REG1 0x10 // 0x0A
#define LPS22HB_CTRL_REG2 0x11 // 0x11
#define LPS22HB_STATUS 0x27 // if 0x03
#define LPS22HB_PRESS_OUT_XL 0x28 
#define LPS22HB_TEMP_OUT_L 0x2B

#endif /* __LPS22HB_H__ */
