#pragma once

#include "stdcpp.h"
#include "Util.h"
#include "Poco.h"
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


class HTTPContent
{
    char *buffer;
    int bufferSize,length; 

public:
    string type;

    HTTPContent(int _bufferSize,const string _type)
    {
        bufferSize=_bufferSize;
        buffer=(char*)malloc(bufferSize*sizeof(char));
        type=_type;
    }

    int read(istream& is)
    {
        is.read(buffer,bufferSize);
        length=is.gcount();
        return length;
    }

    void sendResponse()
    {
        string header="Content-type: "+type+"\n";
        printf("%s\n",header.c_str());
        fflush(stdout);
        write(STDOUT_FILENO,buffer,length);
    }

    string saveAsFile(const string &filename)
    {
        string url=URL+"/Files/"+filename;
        string filepath(getenv("LetsGoResrcPATH"));
        FILE* fout=fopen((filepath+filename).c_str(),"w");
        fwrite(buffer,length,1,fout);
        fclose(fout);
        return url;
    }

    string toString()
    {
        int len=strlen(buffer);
        while(len>0 && buffer[len-1]!='}') len--;
        buffer[len]='\0';
        return string(buffer);
    }

    ~HTTPContent()
    {
        free(buffer);
    }
};

string getRequestData();
void sendResponse(const string &response,const string &contentType=ContentType::json);
string getToken(const string &userid);
bool checkTocken(const string &token);
string HTTPRequestGET(const string &url_str);
string HTTPSRequestGET(const string &url_str);
HTTPContent HTTPSRequestPOST(const string &url_str,const JSON &data);
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
