#include <stdcpp.h>
#include <server.h>

#define LIKE 1
#define CANCEL_LIKE -1
#define DISLIKE 2
#define CANCEL_DISLIKE -2
#define REPORT 3

int main()
{
    INIT("Feedback.log");

    JSON::CMIt feedbacks_it=jsonReq.FindMember("feedbacks");
    if(feedbacks_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    const RecordList &feedbacks=feedbacks_it->value;
    if(!feedbacks.IsArray())
        writeError("Request data error!");

    JSON jsonRes(0);
    sendResponse(jsonRes.toString());

    DefRecordList(vJson);
    vector<int> vLike,vDislike,vCancelLike,vCancelDisLike;
    int n=feedbacks.Size();
    for(int i=0;i<n;i++)
    {
        Record feedback=COPYValue(feedbacks[i]);
        int id=feedback["postid"].GetInt();
        int att=feedback["attitude"].GetInt();
        switch(att)
        {
            case LIKE:
                vLike.push_back(id);
                break;
            case DISLIKE:
                vDislike.push_back(id);
                break;
            case CANCEL_LIKE:
                vCancelLike.push_back(id);
                feedback["attitude"].SetInt(0);
                break;
            case CANCEL_DISLIKE:
                vCancelDisLike.push_back(id);
                feedback["attitude"].SetInt(0);
                break;
            case REPORT:
                break;
        }
        feedback.AddMember("userid",Str2Value(userid),Allocator);
        vJson.PushBack(feedback,Allocator);
    }
    string ret=cdbc.insertRecordlist(vJson,"feedback",true);

    cdbc.updatePostLike(vLike,"love",1);
    cdbc.updatePostLike(vDislike,"dislike",1);
    cdbc.updatePostLike(vCancelLike,"love",-1);
    cdbc.updatePostLike(vCancelDisLike,"dislike",-1);

    return 0;
}

//post nickname attitude