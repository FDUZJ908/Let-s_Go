#include "Util.h"

string toDateTime(time_t timestamp,int selection)
{
    char str[32];
    struct tm *t=localtime(&timestamp);
    switch(selection)
    {
        case DATETIME:
            strftime(str,sizeof(str),"%Y-%m-%d %H:%M:%S",t);
            break;
        case TIMEONLY:
            strftime(str,sizeof(str),"%H:%M:%S",t);
            break;
        case DATEONLY:
            strftime(str,sizeof(str),"%Y-%m-%d",t);
            break;
        case DATEWEEK:
            strftime(str,sizeof(str),"%Y-%m-%d %A",t);
            break;
        case DATEMONTH:
            strftime(str,sizeof(str),"%Y-%m",t);
            break;
        case DATEYEAR:
            strftime(str,sizeof(str),"%Y",t);
            break;
        default:
            str[0]='\0';
            break;
    }
    return string(str);
}

time_t toTimestamp(string str, int selection)
{
    switch(selection)
    {
        case DATEONLY:
            str+=" 00:00:00";
            break;
        case DATEMONTH:
            str+="-01 00:00:00";
            break;
        case DATEYEAR:
            str+="-01-01 00:00:00";
            break;
    }
    struct tm* t= (tm*)malloc(sizeof(tm));
    strptime(str.c_str(),"%Y-%m-%d %H:%M:%S",t);
    return mktime(t);
}

string getDateTime(int selection)
{
    char str[32];
    time_t timestamp=time(NULL);
    return toDateTime(timestamp,selection);
}

int weekdayToNumber(const string &str)
{
    for(int i=0;i<7;i++)
        if(str==WEEKDAYS[i]) return i;
    return -1;
}

double distance(double lat1,double lng1,double lat2,double lng2)
{
    lat1=ToRadian(lat1); lng1=ToRadian(lng1);
    lat2=ToRadian(lat2); lng2=ToRadian(lng2);
    double C=cos(lat1)*cos(lat2)*cos(lng1-lng2)+sin(lat1)*sin(lat2);
    return Radius*acos(C);
}

string getLocStr(double lat,double lng)
{
    return "("+TOString(lat)+","+TOString(lng)+")";
}

string removeChar(string str,char ch)
{
    int n=str.size(),m=0;
    char* s;
    s=(char *)malloc(sizeof(char)*n);
    for(int i=0;i<n;i++)
        if(str[i]!=ch) s[m++]=str[i];
    free(s);
    return s;
}