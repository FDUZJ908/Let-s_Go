#pragma once

#include "stdcpp.h"
#include "Util.h"
#include "crypto.h"
#include "JSON.h"
#include "CDBC.h"
#include "Log.h"

const string URL="https://shiftlin.top";

const int MAXBUF=2*1024*1024;

struct ContentType
{
    static const string json;
    static const string jpeg;
};

#define ContentType_JSON "application/json"
#define ContentType_JPEG "image/jpeg"


string getRequestData();
void sendResponse(const string &response,const string &contentType=ContentType::json);
string getToken(const string &userid);
bool checkTocken(const string &token);
void writeError(const string &mesg);


#define TAGS_MAXNUM 64
#define TAGS_NUM 36
#define PARTITION_LEN 16 
#define START1 0
#define START2 16
#define START3 32
#define START4 48

void setTags(int * tags, const int offset, const char *s);
void tagsToString(int *tags,const int offset,const char *s);
void tagsArrayToRecord(int *tags, Value &record);
void tagsRecordToArray(int *tags, const Value &record);

#define INIT(logName) logFile.set(logName);\
                        \
                      string jsonReq_str=getRequestData();\
                      logFile.print(jsonReq_str);\
                        \
                      JSON jsonReq(jsonReq_str);\
                      JSON::CMIt userid_it=jsonReq.FindMember("userid");\
                      JSON::CMIt token_it=jsonReq.FindMember("token");\
                      if(userid_it==jsonReq.MemberEnd() || token_it==jsonReq.MemberEnd()) writeError("Request data error!");\
                      string userid=GETString(userid_it),token=GETString(token_it);\
                      if(!checkTocken(token)) writeError("Request data error!");
