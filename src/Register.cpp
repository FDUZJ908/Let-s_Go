#include <stdcpp.h>
#include <server.h>

int main()
{   
    
    string jsonReq_str=getRequestData();

    JSON jsonReq(jsonReq_str);
    JSON::CMIt openid_it=jsonReq.FindMember("openid");
    JSON::CMIt rawData_it=jsonReq.FindMember("rawData");
    //JSON::CMIt Tel_it=jsonReq.FindMember("Tel");
    if(openid_it==jsonReq.MemberEnd() || rawData_it==jsonReq.MemberEnd())//|| Tel_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    JSON jsonInfo(GETString(rawData_it));
    jsonInfo.insert("openid",GETString(openid_it));
    //jsonInfo.insert("Tel",GETString(Tel_it));
    string ret=cdbc.insertJSON(jsonInfo,"user",false);
    if(ret!="OK") writeError(ret);
    
    JSON jsonRes(0);
    jsonRes.insert("sessionid",string("TESTlsh1234!"));
    sendResponse(jsonRes.toString());
    return 0;
}
