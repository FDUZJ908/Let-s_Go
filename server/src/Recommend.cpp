#include <stdcpp.h>
#include <server.h>

#define TAGS_MAXNUM 64
#define TAGS_NUM 30

struct POI{
    int tags[TAGS_MAXNUM];
};

int timestamp=0;


vector<POI> getPoiInfo(const vector<string> &ids)
{
    RecordList recordList=cdbc.queryByIDs(ids);

    vector<POI> v; v.clear();
    int n=records.Size();
    for(int i=0;i<n;i++) v.push_back(POI(recordList[i]));
    return v;
}

vector<POI> setUser(const RecordList &recordList)
{
    vector<string> ids; ids.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++)
        ids.push_back(recordList[i]["POI_id"].GetString());
    vector<POI> v=getPoiInfo(ids);

    for(int i=0;i<n;i++)
    {
        int delta=timestamp-recordList[i]["time"].GetInt();
        if(delta<MON_SECONDS) v[i].ts=1;
        else if((delta<<1)<YEAR_SECONDS) v[i].ts=0.6
        else v[i].ts=0.3
    }
    return v;
}

vector<POI> setCand(const RecordList &recordList)
{
    vector<string> ids; ids.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++)
        ids.push_back(recordList[i]["POI_id"].GetString());
    return getPoiInfo(ids);
}



RecordList recommendGenerally()
{
}

RecordList recommendByHistory(const vector<POI> &candPOIs,const vector<POI> &userPOIs)
{
    int n=candPOIs.size(),m=userPOIs.size();
    Pair res[n];
    for(int i=0;i<n;i++)
    {
        double Smax=0;
        for(int j=0;j<m;j++)
            Smax=max(Smax,ScorebyHistory(candPOIs[i],userPOIs[j]));
        res[i]=Pair(i,Smax);
    }
    sort(res,res+n);
    int tot=min(n,REC_NUM);
    DefRecordList(recordList);
    for(int i=0;i<tot;i++)
        recordList.PushBack(candPOIs[i].value);
    return recordList;
}

int main()
{
    INIT("Recommend.log");

    JSON::CMIt lat_it=jsonReq.FindMember("latitude");
    JSON::CMIt lng_it=jsonReq.FindMember("longitude");
    JSON::CMIt tags_it=jsonReq.FindMember("tags");
    if(lat_it==jsonReq.MemberEnd() || lng_it==jsonReq.MemberEnd() || tags_it==jsonReq.MemberEnd())
        writeError("Request data error!");
    
    timestamp=getTimestamp();
    double lat=GETDouble(lat_it);
    double lng=GETDouble(lng_it);
    LL tags=GETLong(tags_it);

    vector<POI> candPOIs=setCand(cdbc.queryPOI(lat,lng,DISTHIGH));
    vector<POI> userPOIs=setUser(cdbc.queryHistoryPOI(user_id,timestamp));
    if(userPOIs.size()<THRESHOLD)
    {
        results=recommendGenerally(candPOIs,tags)
    }else
    {
        results=recommendByHistory(candPOIs,userPOIs);
    }

    JSON jsonRes(0);
    jsonRes.insert("POI_num",int(results.Size()));
    jsonRes.insert("POIs",results);
    sendResponse(jsonRes.toString());    
    return 0;
}

//user poi_id tags timestamp
//cand poi_id tags popularity