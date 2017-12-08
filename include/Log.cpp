#include "Log.h"

void Log::set(const string &logFile)
{
    string logPath(getenv("LetsGoLogPATH")); //need to set environment variable(env) miniProgLogPATH in apache internal env with the SetEnv directive
    flog=fopen((logPath+logFile).c_str(),"a");
    fprintf(flog,"==========================\n%s\n",getDateTime().c_str());
    fflush(flog);
}

void Log::print(const string &str)const
{
    fprintf(flog,"%s\n\n",str.c_str());
    fflush(flog);
}

void Log::print(const char *str)const
{
    fprintf(flog,"%s\n\n",str);
    fflush(flog);
}
