#include <stdcpp.h>
#include <server.h>

#define REC_NUM 30
#define REQUIRE_DATA_NUM 10 //****

#define ALPHA 0.5
#define BETA 0.2

int timestamp=0,CATE_NUM=0;
map< string,vector<int> > cateTags;
map< string,double > cateFreq;

struct POI{
    string id;
    int popularity;
    double ts;
    Record info;
    int tags[TAGS_MAXNUM]; //will be used to multiply, so remember to cast it to long long
    vector<string> categories;
    double MD;
    double CF;
    double Pop;

    POI(const Record &record)
    {
        info=COPYValue(record);
        id=info["POI_id"].GetString();
        popularity=info["popularity"].GetInt();
        for(int i=0;i<TAGS_MAXNUM;i++) tags[i]=0;
        categories=splitString(info["category"].GetString(),',');
    }

    void setTags()
    {
        for(int k=0;k<categories.size();k++)
        {
            string &category=categories[k];
            vector<int> &v=cateTags[category];
            for(int i=0;i<v.size();i++) tags[i]+=v[i];
        }
    }

    void setMDCF(const uLL _tags)
    {
        LL sum=0,total=0; uLL mask=1;
        for(int j=0;j<TAGS_NUM;j++,mask<<=1,total+=tags[j])
            if(_tags&mask) sum+=tags[j];
        MD=(1.0*sum/total)+1; //total>0

        CF=0;
        for(int k=0;k<categories.size();k++) CF+=cateFreq[categories[k]];
        CF=log(1+CF)/log(2)+1;
    }
};

void setTags(vector<POI> &v)
{
    int n=v.size();
    vector<string> ids; ids.clear();
    for(int i=0;i<n;i++) ids.push_back(v[i].id);

    RecordList recordList=cdbc.queryByIDs(ids,"POITags","POI_id"); //the results should be sorted yet

    int m=recordList.Size(),i=0;
    for(int j=0;i<n && j<m;i++)
    {
        if(v[i].id<recordList[j]["POI_id"].GetString()) v[i].setTags();
        else tagsRecordToArray(v[i].tags,recordList[j]),j++;
    }
    for(;i<n;i++) v[i].setTags();
}

vector<POI> setUser(const RecordList &recordList,const uLL tags)
{
    vector<string> ids; ids.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++)
        ids.push_back(recordList[i]["POI_id"].GetString());
    RecordList POIs=cdbc.queryByIDs(ids,"POI","POI_id"); //the results should be sorted yet

    vector<POI> v; v.clear();
    n=POIs.Size();
    for(int i=0;i<n;i++) v.push_back(POI(POIs[i])); 
    setTags(v);

    for(int i=0;i<n;i++) v[i].setMDCF(tags);
/*    for(int i=0;i<n;i++)
    {
        int delta=timestamp-recordList[i]["time"].GetInt();
        if(delta<MON_SECONDS) v[i].ts=1;
        else if((delta<<1)<YEAR_SECONDS) v[i].ts=0.6;
        else v[i].ts=0.3;
    }*/
    return v;
}

vector<POI> setCand(const RecordList &POIs, const uLL tags, double latitude,double longitude)
{
    vector<POI> v; v.clear();
    int n=POIs.Size();
    for(int i=0;i<n;i++)
    {
        const Record &record=POIs[i];
        const double lat=record["latitude"].GetDouble(),lng=record["longitude"].GetDouble();
        double d=distance(lat,lng,latitude,longitude);
        if(DISTLOW<d && d<DISTHIGH) v.push_back(POI(record));
    }
    setTags(v);

    n=v.size();
    int Pmax=0;
    for(int i=0;i<n;i++)
    {
        Pmax=max(Pmax,v[i].popularity);
        v[i].setMDCF(tags);
    }
    for(int i=0;i<n;i++)
        v[i].Pop=v[i].popularity/(Pmax+1);
    for(int i=0;i<n;i++)
        v[i].Pop=log(1+v[i].Pop)/log(2)+1;
    return v;
}

double ScorebyHistory(const POI &cand, const POI &user, const uLL tags)
{
    LL scalar=0,norm1=0,norm2=0;
   
    for(int i=0;i<TAGS_NUM;i++)
    {
        scalar+=((LL)cand.tags[i])*((LL)user.tags[i]);
        norm1+=((LL)cand.tags[i])*((LL)cand.tags[i]);
        norm2+=((LL)user.tags[i])*((LL)user.tags[i]);
    }
    double cosine=1.0*scalar/(sqrt(1.0*norm1)*sqrt(1.0*norm2));
    double scores=cosine*user.MD*cand.CF*cand.Pop;
    if(cand.id==user.id) scores*=BETA;
    return scores;
}

bool cmp(const pair<int,double> &a,const pair<int,double> &b)
{
    return a.second>b.second;
}

RecordList recommendGenerally(vector<POI> &candPOIs, const uLL tags)
{
    int n=candPOIs.size();
    pair<int,double> res[n];
    for(int i=0;i<n;i++)
    {
        const POI &cand=candPOIs[i];
        double scores=cand.MD*cand.CF*cand.Pop;
        res[i]=make_pair(i,scores);
    }
    sort(res,res+n,cmp);
    int tot=min(n,REC_NUM);
    DefRecordList(recordList);
    for(int i=0;i<tot;i++)
    {
        candPOIs[res[i].first].info.AddMember("scores",res[i].second,Allocator);
        recordList.PushBack(candPOIs[res[i].first].info,Allocator);
        //cout<<res[i].second<<endl;
    }
    return recordList;
}

RecordList recommendByHistory(vector<POI> &candPOIs, vector<POI> &userPOIs, const uLL tags)
{
    int n=candPOIs.size(),m=userPOIs.size();
    pair<int,double> res[n];
    for(int i=0;i<n;i++)
    {
        double Smax=0;
        for(int j=0;j<m;j++)
            Smax=max(Smax,ScorebyHistory(candPOIs[i],userPOIs[j],tags));
        res[i]=make_pair(i,Smax);
    }
    sort(res,res+n,cmp);
    int tot=min(n,REC_NUM);
    DefRecordList(recordList);
    for(int i=0;i<tot;i++)
    {
        candPOIs[res[i].first].info.AddMember("scores",res[i].second,Allocator);
        recordList.PushBack(candPOIs[res[i].first].info,Allocator);
        //cout<<res[i].second<<endl;
    }
    return recordList;
}

void readCategoryTags()
{
    string resrcPath(getenv("LetsGoResrcPATH"));
    FILE *fin=fopen((resrcPath+"category.txt").c_str(),"r");
    int x; double y;
    char category[64];
    vector<int> v;
    cateTags.clear();
    for(CATE_NUM=0;fscanf(fin," %s",category)!=EOF;CATE_NUM++)
    {
        v.clear();
        for(int i=0;i<TAGS_NUM;i++)
        {
            fscanf(fin," %d",&x);
            v.push_back(x);
        }
        cateTags[string(category)]=v;

        fscanf(fin," %lf",&y);
        cateFreq[string(category)]=y;
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

    vector<POI> candPOIs=setCand(cdbc.queryPOINearby(lat,lng,DISTHIGH),tags,lat,lng); //sortbydistance
    vector<POI> userPOIs=setUser(cdbc.queryHistoryPOI(userid,timestamp),tags);
    RecordList results;
    if(userPOIs.size()<REQUIRE_DATA_NUM)
    {
        results=recommendGenerally(candPOIs,tags);
    }else
    {
        results=recommendByHistory(candPOIs,userPOIs,tags);
    }

    JSON jsonRes(0);
    jsonRes.insert("POI_num",int(results.Size()));
    jsonRes.insert("POIs",results);
    sendResponse(jsonRes.toString());    
    return 0;
}
