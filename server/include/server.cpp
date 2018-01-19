#include "server.h"

CDBC cdbc;
Log logFile;

const string ContentType::json=ContentType_JSON;
const string ContentType::jpeg=ContentType_JPEG;

string getRequestData()
{
    char buf[MAXBUF],ch;
    int len=0;
    while((ch=getchar())!=EOF) buf[len++]=ch;
    buf[len]='\0';
    return string(buf);
}

void sendResponse(const string &response,const string &contentType)
{
    string header="Content-type: "+contentType+"\n";
    printf("%s\n%s\n",header.c_str(),response.c_str());
    logFile.print(response);
}

string getToken(const string &userid)
{
    string str=userid+TOString(getTimestamp());
    return sha1(polyhash(str))+str;
}

bool checkTocken(const string &token)
{
    return sha1(polyhash(token.substr(40)))==token.substr(0,40);
}

void writeError(const string &mesg)
{
    JSON json(1);
    json.insert("message",mesg);
    sendResponse(json.toString());
    exit(0);
}

void setTags(int * tags, const int offset, const char *s)
{
    if(strlen(s)==0)
    {
        for(int i=0;i<PARTITION_LEN;i++) tags[offset+i]=0;
        return;
    }
    for(int i=0;i<PARTITION_LEN;i++)
        tags[offset+i]=Hex2Int(s+(i<<3)); //bigend
}

void tagsRecordToArray(int *tags, const Record &record)
{
    const char* s1=record["tags1"].GetString();
    const char* s2=record["tags2"].GetString();
    const char* s3=record["tags3"].GetString();    
    const char* s4=record["tags4"].GetString();

    setTags(tags,START1,s1);
    setTags(tags,START2,s2);
    setTags(tags,START3,s3);
    setTags(tags,START4,s4);
}

void tagsToString(int *tags,const int offset, char *s)
{
    for(int i=0;i<PARTITION_LEN;i++)
        Int2Hex(tags[offset+i],s+(i<<3));
    s[PARTITION_LEN<<3]='\0';
}

void tagsArrayToRecord(int *tags,Record &record)
{
    int len=(PARTITION_LEN<<3); char s[len+1];
    tagsToString(tags,START1,s);
    record["tags1"].SetString(s,len,Allocator);
    tagsToString(tags,START2,s);
    record["tags2"].SetString(s,len,Allocator);
    tagsToString(tags,START3,s);
    record["tags3"].SetString(s,len,Allocator);
    tagsToString(tags,START4,s);
    record["tags4"].SetString(s,len,Allocator);
}
