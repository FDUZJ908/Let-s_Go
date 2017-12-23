#include <stdcpp.h>
#include <server.h>

int main()
{
    INIT("Account.log");
    jsonReq.RemoveMember("token");
    jsonReq.RemoveMember("userid");

    Record record=cdbc.queryByID(userid,"user","userid");
    if(record.IsNull())
        writeError("Request data error!");

    bool modified=false;
    for(JSON::CMIt it=jsonReq.MemberBegin();it!=jsonReq.MemberEnd();it++)
    {
        const char* key=GETKey(it);
        JSON::MIt jt=record.FindMember(key);
        if(jt!=record.MemberEnd())
        {
            if(strcmp(key,"password")==0)
            {
                string hash=sha1((it->value).GetString());
                (jt->value)=Str2Value(hash);
            }
            else jt->value=COPYValue(it->value);
            modified=true;
        }
    }
    if(modified)
    {
        string ret=cdbc.insertRecord(record,"user",true);
        if(ret!=OK) writeError(ret);
    }

    JSON jsonRes(record);
    jsonRes.insert("status",OK);
    sendResponse(jsonRes.toString());
    return 0;
}
