#include <stdcpp.h>
#include <server.h>

const int POINumLimit=20;

struct Data
{
    int x; double d;

    Data(int _x=0,double _d=0)
    {
        x=_x; d=_d;
    }

    bool operator<(const Data &rhs)const
    {
        return d<rhs.d;
    }
};

int main()
{
    INIT("Search.log");

    JSON::CMIt lat_it=jsonReq.FindMember("latitude");
    JSON::CMIt lng_it=jsonReq.FindMember("longitude");
    if(lat_it==jsonReq.MemberEnd() || lng_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    double lat=GETDouble(lat_it);
    double lng=GETDouble(lng_it);
    RecordList recordList=cdbc.queryPOINearby(lat,lng,DISTLOW);

    vector<Data> v;
    int n=recordList.Size();
    for(int i=0;i<n;i++)
    {
        Record &record=recordList[i];
        double lat_poi=record["latitude"].GetDouble();
        double lng_poi=record["longitude"].GetDouble();
        v.push_back(Data(i,distance(lat,lng,lat_poi,lng_poi)));
    }
    sort(v.begin(),v.end());

    DefRecordList(results);
    n=min(n,POINumLimit);
    for(int i=0;i<n;i++)
    {
        Record &record=recordList[v[i].x];
        results.PushBack(record,Allocator);
    }

    JSON jsonRes(0);
    jsonRes.insert("POI_num",int(results.Size()));
    jsonRes.insert("POIs",results);
    sendResponse(jsonRes.toString());
    return 0;
}
