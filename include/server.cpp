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
}

string getSessionID(const string &openid)
{
    int timestamp=getTimestamp();
    srand((unsigned)timestamp);
    string time_str=TOString(timestamp),sessionid,ret="";
    JSON json;
    json.insert("openid",openid);
    json.insert("sessionid","");
    for(int cnt=0;cnt<10;cnt++)
    {
        int id=rand();
        sessionid=time_str+TOString(id);
        json["sessionid"].SetString(sessionid.c_str(),sessionid.size(),Allocator);
        ret=cdbc.insertJSON(json,"session",true);
        if(ret==OK) return sessionid;
    }
    return ERROR;
}

bool checkSessionID(const string &openid,const string &sessionid)
{
    RecordList res=cdbc.selectQuery("*","session","openid='"+openid+"' and sessionid='"+sessionid+"'");
    if(res.Size()!=1) writeError("Request data error!");
    return true;
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
