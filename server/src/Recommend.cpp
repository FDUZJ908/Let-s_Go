#include <stdcpp.h>
#include <server.h>

#define REC_NUM 10
#define REQUIRE_DATA_NUM 10 //****

#define ALPHA 0.7
#define BETA 0.6

int timestamp=0,CATE_NUM=0,Pmax=0;
map< string,vector<int> > cateTags;

struct POI{
    string id;
    int popularity;
    double ts;
    Record info;
    int tags[TAGS_MAXNUM]; //will be used to multiply, so remember to cast it to long long

    POI(const Record &record)
    {
        info=COPYValue(record);
        id=info["POI_id"].GetString();
        popularity=info["popularity"].GetInt();
    }

    void setTags()
    {
        vector<int> &v=cateTags[string(info["category"].GetString())];
        for(int i=0;i<v.size();i++) tags[i]=v[i];
        for(int i=v.size();i<TAGS_MAXNUM;i++) tags[i]=0;
    }
};

vector<POI> getPoiInfo(const vector<string> &ids)
{
    RecordList recordList=cdbc.queryByIDs(ids,"POI","POI_id"); //the results should be sorted yet

    vector<POI> v; v.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++) v.push_back(POI(recordList[i])); 
    
    recordList=cdbc.queryByIDs(ids,"POITags","POI_id"); //the results should be sorted yet
    int m=recordList.Size();
    for(int i=0,j=0;i<n && j<m;i++)
    {
        if(v[i].id<recordList[j]["POI_id"].GetString()) v[i].setTags();
        else tagsRecordToArray(v[i].tags,recordList[j]),j++;
    }
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
        else if((delta<<1)<YEAR_SECONDS) v[i].ts=0.6;
        else v[i].ts=0.3;
    }
    return v;
}

vector<POI> setCand(const RecordList &recordList)
{
    vector<string> ids; ids.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++)
        ids.push_back(recordList[i]["POI_id"].GetString());
    vector<POI> v=getPoiInfo(ids);

    Pmax=0;
    for(int i=0;i<n;i++) Pmax=max(Pmax,v[i].popularity);
    return v;
}

double ScorebyHistory(const POI &cand, const POI &user)
{
    LL scalar=0,norm1=0,norm2=0;
    for(int i=0;i<TAGS_NUM;i++)
    {
        scalar+=((LL)cand.tags[i])*((LL)user.tags[i]);
        norm1+=((LL)cand.tags[i])*((LL)cand.tags[i]);
        norm2+=((LL)user.tags[i])*((LL)user.tags[i]);
    }
    double cosine=1.0*scalar/(sqrt(1.0*norm1)*sqrt(1.0*norm2));
    double scores=ALPHA*user.ts*cosine+(1-ALPHA)*2*cand.popularity/(Pmax+1);
    if(cand.id==user.id) scores*=BETA;
    return scores;
}

bool cmp(const pair<int,double> &a,const pair<int,double> &b)
{
    return a.second>b.second;
}

RecordList recommendGenerally(vector<POI> &candPOIs, uLL tags)
{
    int n=candPOIs.size();
    pair<int,double> res[n];
    for(int i=0;i<n;i++)
    {
        const POI &cand=candPOIs[i];
        LL sum=0,total=0; uLL mask=1;
        for(int j=0;j<TAGS_NUM;j++,mask<<=1,total+=cand.tags[j])
            if(tags&mask) sum+=cand.tags[j];
        double scores=1.0*sum/total+2*cand.popularity/(Pmax+1);
        res[i]=make_pair(i,scores);
    }
    sort(res,res+n,cmp);
    int tot=min(n,REC_NUM);
    DefRecordList(recordList);
    for(int i=0;i<tot;i++)
        recordList.PushBack(candPOIs[res[i].first].info,Allocator);
    return recordList;
}

RecordList recommendByHistory(vector<POI> &candPOIs, vector<POI> &userPOIs)
{
    int n=candPOIs.size(),m=userPOIs.size();
    pair<int,double> res[n];
    for(int i=0;i<n;i++)
    {
        double Smax=0;
        for(int j=0;j<m;j++)
            Smax=max(Smax,ScorebyHistory(candPOIs[i],userPOIs[j]));
        res[i]=make_pair(i,Smax);
    }
    sort(res,res+n,cmp);
    int tot=min(n,REC_NUM);
    DefRecordList(recordList);
    for(int i=0;i<tot;i++)
        recordList.PushBack(candPOIs[res[i].first].info,Allocator);
    return recordList;
}

#define BUFSIZE 1024

void readCategoryTags()
{
    string resrcPath(getenv("LetsGoResrcPATH"));
    FILE *fin=fopen((resrcPath+"CategoryTags.txt").c_str(),"r");
    int x;
    char buf[BUFSIZE],category[64];
    vector<int> v;
    CATE_NUM=0; cateTags.clear();
    while(fgets(buf,BUFSIZE,fin)!=NULL)
    {
        v.clear();
        sscanf(buf," %s",category);
        for(int i=0;i<TAGS_NUM;i++)
        {
            sscanf(buf," %d",&x);
            v.push_back(x);
        }
        cateTags[string(category)]=v;
        CATE_NUM++;
    }
    fclose(fin);
}

int main()
{
    INIT("Recommend.log");

    JSON::CMIt lat_it=jsonReq.FindMember("latitude");
    JSON::CMIt lng_it=jsonReq.FindMember("longitude");
    JSON::CMIt tags_it=jsonReq.FindMember("tags");
    if(lat_it==jsonReq.MemberEnd() || lng_it==jsonReq.MemberEnd() || tags_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    readCategoryTags();
    timestamp=getTimestamp();

    double lat=GETDouble(lat_it);
    double lng=GETDouble(lng_it);
    uLL tags=GETULong(tags_it);

    vector<POI> candPOIs=setCand(cdbc.queryPOINearby(lat,lng,DISTHIGH));
    vector<POI> userPOIs=setUser(cdbc.queryHistoryPOI(userid,timestamp));
    RecordList results;
    if(userPOIs.size()<REQUIRE_DATA_NUM)
    {
        results=recommendGenerally(candPOIs,tags);
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
