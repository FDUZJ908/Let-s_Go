#pragma once

#include "stdcpp.h"
#include "Util.h"
#include "CDBC.h"

class Log
{
    FILE *flog;

public:
    Log(){};

    Log(const string &logFile)
    {
        set(logFile);
    }

    void set(const string &logFile);
    void print(const string &str)const;
    void print(const char *str)const;

    ~Log()
    {
        if(flog!=NULL) fclose(flog);
    }
};

extern Log logFile;
