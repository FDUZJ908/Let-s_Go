#pragma once

#include "stdcpp.h"
#include "Util.h"
#include "Poco.h"
#include "crypto.h"
#include "JSON.h"
#include "CDBC.h"
#include "Log.h"

typedef long long LL;

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
        string filepath(getenv("LetsGoFilePATH"));
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

/*
struct AccessToken
{
    string value;
    int expireTime;
    static const string name;
    static const int defaultExpires;

    AccessToken()
    {
        expireTime=0;
    }

    AccessToken(Record record)
    {
        if(!record.IsNull())
        {
            value=record["value"].GetString();
            expireTime=record["attr1"].GetInt();
        }else AccessToken();
    }

    void set(string _value, int _expireTime)
    {
        value=_value;
        expireTime=_expireTime;
    }

    JSON toJSON()
    {
        JSON json;
        json.insert("name",name);
        json.insert("value",value);
        json.insert("attr1",expireTime);
        return json;
    }
};

#define AccessToken_NAME "AccessToken"
#define AccessToken_DEFAULT_EXPIRES 5400
*/


string getRequestData();
void sendResponse(const string &response,const string &contentType=ContentType::json);
string getToken(const string &userid);
bool checkTocken(const string &token,const string &userid);
string HTTPRequestGET(const string &url_str);
string HTTPSRequestGET(const string &url_str);
HTTPContent HTTPSRequestPOST(const string &url_str,const JSON &data);
void writeError(const string &mesg);


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
                      checkTocken(token,userid)
