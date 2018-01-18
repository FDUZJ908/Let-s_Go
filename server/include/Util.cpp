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

vector<string> splitString(const char* st,char ch)
{
    int len=strlen(st);
    char buf[len+10];
    vector<string> v; v.clear();
    for(int i=0,j=0;i<len;i=j+1)
    {
        j=i;
        while(j<len && st[j]!=ch) j++;
        v.push_back(string(st+i,j-i));
    }
    return v;
}

unsigned short Hex2Byte(char c)
{
    if('0'<=c && c<='9') return c-'0';
    if('a'<=c && c<='f') return c-'a'+10;
    return c-'A'+10;
}

unsigned int Hex2Int(const char *s)
{
    unsigned int x=0;
    for(int j=0;j<8;j++)
        x=(x<<4)+Hex2Byte(s[j]);
    return x;
}

char Byte2Hex(short x)
{
    if(0<=x && x<=9) return '0'+x;
    return 'a'+x-10;
}

char* Int2Hex(unsigned int x, char *s)
{
    int i=7;
    for(;i>=0 && x>0;i--,x>>=4) s[i]=Byte2Hex(x%16);
    while(i>=0) s[i--]='0';
    return s;
}
