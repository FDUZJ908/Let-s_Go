#include <stdcpp.h>
#include <server.h>

int main()
{
    logFile.set("Login.log");//

    string jsonReq_str=getRequestData();
    logFile.print(jsonReq_str);//

    JSON jsonReq(jsonReq_str);
    JSON::CMIt userid_it=jsonReq.FindMember("userid");
    JSON::CMIt password_it=jsonReq.FindMember("password");
    if(userid_it==jsonReq.MemberEnd() || password_it==jsonReq.MemberEnd())
        writeError("Request data error!");
    string userid=GETString(userid_it);
    string password=GETString(password_it);

    if(!cdbc.authenticate(userid,sha1(password)))
        writeError("用户名或密码错误！");

    JSON jsonRes(0);
    jsonRes.insert("token",getToken(userid));
    string jsonRes_str=jsonRes.toString();
    sendResponse(jsonRes_str);

    logFile.print(jsonRes_str);
    return 0;
}
