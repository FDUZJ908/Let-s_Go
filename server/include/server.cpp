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

bool checkTocken(const string &token,const string &userid)
{
    return (sha1(polyhash(token.substr(40)))==token.substr(0,40) && (token.substr(40).find(userid)!=-1));
}

string HTTPRequestGET(const string &url_str) //need to append '/' at the end of url_str when you request for the default index.html
{
    URI url(url_str);
    HTTPClientSession clientSession(url.getHost(),url.getPort());
    HTTPRequest request(HTTPRequest::HTTP_GET,url.getPathAndQuery());
    clientSession.sendRequest(request);
    HTTPResponse response;
    istream& is=clientSession.receiveResponse(response);
    string result;
    StreamCopier::copyToString(is,result);
    return result;
}

string HTTPSRequestGET(const string &url_str)
{
    URI url(url_str);
    HTTPSClientSession clientSession(url.getHost(),url.getPort());
    HTTPRequest request(HTTPRequest::HTTP_GET,url.getPathAndQuery());
    clientSession.sendRequest(request);
    HTTPResponse response;
    istream& is=clientSession.receiveResponse(response);
    string result;
    StreamCopier::copyToString(is,result);
    return result;
}

HTTPContent HTTPSRequestPOST(const string &url_str,const JSON &data)
{
    URI url(url_str);
    HTTPSClientSession clientSession(url.getHost(),url.getPort());
    HTTPRequest request(HTTPRequest::HTTP_POST,url.getPathAndQuery());
    string data_str=data.toString();
    request.setContentType("application/json");
    request.setContentLength(data_str.length());
    ostream& os=clientSession.sendRequest(request);
    os<<data_str;
    HTTPResponse response;
    istream& is=clientSession.receiveResponse(response);
    HTTPContent result(response.getContentLength(),response.getContentType());
    result.read(is);
    return result;
}

/*
string getAccessToken(const bool refresh)
{
    int timestamp=getTimestamp();
    AccessToken token(cdbc.querySystemVariable(AccessToken::name));
    if(refresh || token.expireTime<timestamp)
    {
        string url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET;
        string jsonRes_str=HTTPSRequestGET(url);
        JSON jsonRes(jsonRes_str);

        JSON::CMIt accessToken_it=jsonRes.FindMember("access_token");
        if(accessToken_it==jsonRes.MemberEnd())
            return "error";
        string accessToken=GETString(accessToken_it);

        JSON::CMIt expiresIn_it=jsonRes.FindMember("expires_in");
        int expiresIn=(expiresIn_it==jsonRes.MemberEnd())?GETInt(expiresIn_it):AccessToken::defaultExpires;

        token.set(accessToken,timestamp+expiresIn);
        cdbc.insertJSON(token.toJSON(),"sysvar",true);
    }
    return token.value;
}
*/

void writeError(const string &mesg)
{
    JSON json(1);
    json.insert("message",mesg);
    sendResponse(json.toString());
    logFile.print(mesg);
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
