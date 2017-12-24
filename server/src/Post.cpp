#include <stdcpp.h>
#include <server.h>

int main()
{
    INIT("Post.log");

    JSON::CMIt POI_it=jsonReq.FindMember("POI_id");
    JSON::CMIt lat_it=jsonReq.FindMember("latitude");
    JSON::CMIt lng_it=jsonReq.FindMember("longitude");
    JSON::CMIt text_it=jsonReq.FindMember("text");
    JSON::CMIt tags_it=jsonReq.FindMember("tags");
    if(POI_it==jsonReq.MemberEnd() || lat_it==jsonReq.MemberEnd() || lng_it==jsonReq.MemberEnd() || tags_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    jsonReq.RemoveMember("token");
/*    
    string POI_id=GETString(POI_it);
    Record record=cdbc.queryByID(POI_id,"POI","POI_id");
    if(record.IsNull())
        writeError("Request data error!");
    record["popularity"].SetInt(record["popularity"].GetInt()+1);
    string ret=cdbc.insertJSON(record,"POI",true);
    if(ret!=OK) writeError(ret);
*/
    int timestamp=getTimestamp();
    jsonReq.insert("timestamp",timestamp);
    string ret=cdbc.insertJSON(jsonReq,"post",false);
    if(ret!=OK) writeError(ret);

    JSON jsonRes(0);
    jsonRes.insert("postid",cdbc.getLastId());
    jsonRes.insert("timestamp",timestamp);
    sendResponse(jsonRes.toString());
    return 0;
}
