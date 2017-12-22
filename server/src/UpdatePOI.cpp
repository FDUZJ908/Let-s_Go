#include <stdcpp.h>
#include <server.h>

struct Info
{
    int popularity;
    int tags[TAGS_MAXNUM];

    Info()
    {
        popularity=0;
        for(int i=0;i<TAGS_MAXNUM;i++) tags[i]=0;
    }

    void Add(int x, uLL _tags)
    {
        popularity+=x; 
        uLL mask=1;
        for(int i=0;i<TAGS_MAXNUM;i++,mask<<=1)
            if(_tags&mask) tags[i]++;
    }
};

int getLastUpdateTime()
{
    cdbc.querySystemVariable("lastUpdateTime");
    return 0;
}

int main()
{
    logFile.set("UpdatePOI.log");

    int timestamp=getTimestamp();
    RecordList recordList=cdbc.queryPostByTime(getLastUpdateTime());
    map<string,Info> count; count.clear();
    
    int n=recordList.Size();
    for(int i=0;i<n;i++)
    {
        Record &record=recordList[i];
        const string &POI_id=record["POI_id"].GetString();
        map<string,Info>::iterator it=count.find(POI_id);
        if(it==count.end()) it=count.insert(it,pair<string,Info>(POI_id,Info()));
        (it->second).Add(1,record["tags"].GetUint64());
    }

    vector<string> ids; ids.clear();
    for(map<string,Info>::iterator it=count.begin();it!=count.end();it++)
    {
        ids.push_back(it->first);
        //cdbc.updateAttr(it->first,"POI","POI_id",(it->second).popularity,"popularity");
    }

    recordList=cdbc.queryByIDs(ids,"POITags","POI_id");
    n=recordList.Size();
    int tmp[TAGS_MAXNUM];
    for(int i=0;i<n;i++)
    {
        Record &record=recordList[i];
        const string &POI_id=record["POI_id"].GetString();
        map<string,Info>::iterator it=count.find(POI_id);

        int *tags=(it->second).tags;
        tagsRecordToArray(tmp,record);
        for(int i=0;i<TAGS_NUM;i++) tags[i]+=tmp[i];
        tagsArrayToRecord(tags,record);
        cdbc.insertJSON(JSON(record),"POITags",true);

        count.erase(it);
    }

    for(map<string,Info>::iterator it=count.begin();it!=count.end();it++)
    {
        const string &POI_id=it->first;
        DefRecord(record);
        record.AddMember("POI_id",Str2Value(POI_id),Allocator);
        record.AddMember("tags1","",Allocator);
        record.AddMember("tags2","",Allocator);
        record.AddMember("tags3","",Allocator);
        record.AddMember("tags4","",Allocator);
        tagsArrayToRecord((it->second).tags,record);
    }
//****
    return 0;
}
