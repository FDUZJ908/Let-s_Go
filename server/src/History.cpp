#include <stdcpp.h>
#include <server.h>

int main()
{
    INIT("History.log");

    JSON::CMIt POI_it=jsonReq.FindMember("POI_id");
    JSON::CMIt postid_it=jsonReq.FindMember("postid");
    if(POI_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    string POI_id=GETString(POI_it);
    int postid=(postid_it!=jsonReq.MemberEnd())?GETInt(postid_it):0;
    RecordList recordList=cdbc.queryPostHistoryAtPOI(POI_id,postid);

    JSON jsonRes(0);
    jsonRes.insert("post_num",int(recordList.Size()));
    jsonRes.insert("posts",recordList);
    sendResponse(jsonRes.toString());
    return 0;
}
