#ifndef __KEY_H__
#define __KEY_H__

#include "main.h"

enum{
    KEY_NOPRESS = 0,
    KEY_PRESS,
    KEY_LONGPRESS,
};

extern uint16_t KeyPress_Event;

void Check_Key(void);

#endif