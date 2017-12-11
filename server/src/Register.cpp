#include <stdcpp.h>
#include <server.h>

int main()
{ 
    logFile.set("Register.log");
    
    string jsonReq_str=getRequestData();
    logFile.print(jsonReq_str);

    JSON jsonReq(jsonReq_str);
    JSON::CMIt userid_it=jsonReq.FindMember("userid");
    JSON::CMIt password_it=jsonReq.FindMember("password");
    JSON::CMIt nickname_it=jsonReq.FindMember("nickname");
    JSON::CMIt gender_it=jsonReq.FindMember("gender");
    if(userid_it==jsonReq.MemberEnd() || password_it==jsonReq.MemberEnd()
        || nickname_it==jsonReq.MemberEnd() || gender_it==jsonReq.MemberEnd())//|| Tel_it==jsonReq.MemberEnd())
        writeError("Request data error!");
    JSON::CMIt Tel_it=jsonReq.FindMember("Tel");

    string userid=GETString(userid_it);
    string password=GETString(password_it);
    string nickname=GETString(nickname_it);
    int gender=GETInt(gender_it);

    JSON json;
    json.insert("userid",userid);
    json.insert("password",sha1(password));
    json.insert("nickname",nickname);
    json.insert("gender",gender);
    if(Tel_it!=jsonReq.MemberEnd())
        json.insert("Tel",GETString(Tel_it));
    string ret=cdbc.insertJSON(json,"user",false);
    if(ret!=OK) writeError(ret);

    JSON jsonRes(0);
    jsonRes.insert("token",getToken(userid));
    string jsonRes_str=jsonRes.toString();
    sendResponse(jsonRes_str);
    
    logFile.print(jsonRes_str);
    return 0;
}
