#include <stdcpp.h>
#include <server.h>

#define LIKE 1
#define DISLIKE 2
#define REPORT 3

int main()
{
    INIT("Feedback.log");

    JSON::CMIt feedbacks_it=jsonReq.FindMember("feedbacks");
    if(feedbacks_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    RecordList &feedbacks=feedbacks_it->value;
    if(!feedbacks.IsArray())
        writeError("Request data error!");

    int n=feedbacks.Size();
    for(int i=0;i<n;i++)
    {
        Record &feedback=feedback[i];
        int id=feedback["postid"].GetInt();
        int att=feedback["attitude"].GetInt();
        switch(att)
        {
            case LIKE:
                vLike.push_back(id);
                jsonLike["postid"].setInt(id);
                jsonLike["attitude"].setInt(id);
                vJson.PushBack(COPYValue(jsonLike));
                break;
            case DISLIKE:
                vDislike.push_back(id);
                jsonLike["postid"].setInt(id);
                vJson.PushBack(COPYValue(jsonLike));
                break;
            case REPORT:
                break;
        }
    }

    JSON jsonRes(0);
    sendResponse(jsonRes.toString());
    return 0;
}

//post nickname attitude