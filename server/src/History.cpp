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

    vector<int> ids;
    int n=recordList.Size();
    for(int i=0;i<n;i++)
        ids.push_back(recordList[i]["postid"].GetInt());

    RecordList feedbacks=cdbc.queryAttitude(userid,ids);
    int m=feedbacks.Size();

    int i=0;
    for(int j=0;i<n && j<m;i++)
    {
        Record &record=recordList[i];
        Record &feedback=feedbacks[j];
        if(record["postid"].GetInt()>feedback["postid"].GetInt()) record.AddMember("attitude",Int2Value(0),Allocator);
        else record.AddMember("attitude",feedback["attitude"],Allocator),j++;
    }
    for(;i<n;i++)
        recordList[i].AddMember("attitude",Int2Value(0),Allocator);

    JSON jsonRes(0);
    jsonRes.insert("post_num",int(recordList.Size()));
    jsonRes.insert("posts",recordList);
    sendResponse(jsonRes.toString());
    return 0;
}
