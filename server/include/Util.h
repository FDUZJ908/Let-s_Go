#pragma once

#include "stdcpp.h"
#include <boost/lexical_cast.hpp> 
#include <sys/time.h>

#define ZERO 1e-6
#define INF 2000000000

#define TOString(x) (boost::lexical_cast<string>(x))

struct Pair
{
    string x,y;

    Pair(){}

    Pair(const string &_x,const string &_y)
    {
        x=_x; y=_y;
    }
};

struct Triple
{
    int x,y,z;

    Triple(){}

    Triple(const int _x,const int _y,const int _z)
    {
        x=_x; y=_y; z=_z;
    }
};

const int DAY_SECONDS=24*60*60;
const int MON_SECONDS=30*DAY_SECONDS;
const int YEAR_SECONDS=6*MON_SECONDS;

#define TIMEONLY 1
#define DATEONLY 2
#define DATETIME 3
#define DATEWEEK 4
#define DATEMONTH 5
#define DATEYEAR 6

string toDateTime(time_t timestamp, int selection=DATETIME); //local time

time_t toTimestamp(string str, int selection=DATETIME); //local time to unix timestamp

string getDateTime(int selection=DATETIME); //local time

const string WEEKDAYS[7]={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

int weekdayToNumber(const string &str);

inline int getTimestamp() //unix timestamp
{
    return time(NULL);
/*
    struct timeval t;
    gettimeofday(&t,NULL);
    return t.tv_sec+1.0*t.tv_usec/1000000;
*/
}

const double Radius=6371;
const double PI=atan2(0,-1);
#define ToRadian(x) (x*PI/180.0)


#define DISTLOW 1
#define DISTHIGH 3

double distance(double lat1,double lng1,double lat2,double lng2);

const double degPerKm=180.0/(PI*Radius);
#define distTolat(dist,lat) (dist*degPerKm)
#define distTolng(dist,lat) (lat<90?(dist*degPerKm/cos(ToRadian(lat))):0)

string getLocStr(double lat,double lng);

string removeChar(string str,char ch);

unsigned short Hex2Byte(const char c);
unsigned int Hex2Int(const char *s);