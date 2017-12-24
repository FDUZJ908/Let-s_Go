#include <stdcpp.h>
#include <server.h>

double delta=0.01;
double myLat=31.196835;
double myLng=121.60084;

string categories[10]={"餐饮美食", "教育学校", "文化艺术", "旅游景点", "购物商场",
                       "休闲娱乐", "政府机关", "医疗卫生", "住宅小区", "生活服务"};

map< string,vector<string> > Map;
string resrcPath(getenv("LetsGoResrcPATH"));

int timestamp=getTimestamp()-2000;

char buffer[MAXBUF+1];

void getPOIs()
{
    FILE *fin=fopen((resrcPath+"POIs.txt").c_str(),"r");
    int len=fread(buffer,1,MAXBUF,fin);
    buffer[len]='\0';
    fclose(fin);
    string tmp=buffer;
    JSON json(tmp);

    vector<string> ids;
    RecordList &POIs=json["POIs"];
    int n=POIs.Size();
    for(int i=0;i<n;i++)
    {
        Record &POI=POIs[i];
        vector<string> v=splitString(POI["category"].GetString(),',');
        for(int j=0;j<v.size();j++)
        {
            /*if(Map.find(categories[j])==Map.end())
                Map[categories[j]]=vector<>*/
            Map[v[j]].push_back(POI["POI_id"].GetString());
        }
    }
}

string Register(int k, uLL tags)
{
    string nickname="test"+TOString(k);
    string userid=nickname+"@l63.com";
    string password=sha1("123456");
    int gender=0;
    JSON json;
    json.insert("userid",userid);
    json.insert("tags",(uint64_t)tags);
    json.insert("nickname",nickname);
    json.insert("password",password);
    json.insert("gender",gender);
    cdbc.insertJSON(json,"user",true);
    return userid;
}

void insert(const string &category, const string &userid, uLL tags)
{
    const vector<string> &v=Map[category];
    if(v.size()==0) return;
    int x=rand()%v.size();
    JSON json;
    json.insert("POI_id",v[x]);
    json.insert("userid",userid);
    json.insert("tags",(uint64_t)tags);
    json.insert("latitude",myLat);
    json.insert("longitude",myLng);
    json.insert("timestamp",timestamp++);
    json.insert("text","Post by "+userid+".");
    cdbc.insertJSON(json,"post");
}

int main()
{
    srand((unsigned)time(NULL));

    getPOIs();

    FILE *fin=fopen((resrcPath+"users.txt").c_str(),"r");
    int k=0,x,age,cstl;
    while(fscanf(fin, " %d",&age)!=EOF)
    {
        fscanf(fin," %d",&cstl);
        LL tags=0,item=1<<15;
        tags|=(1<<(age-1));
        tags|=(1<<(cstl+3));
        for(int i=0;i<20;i++,item<<=1)
        {
            fscanf(fin," %d",&x);
            if(x==1) tags|=item;
        }
        string userid=Register(++k, tags);
        for(int i=0;i<10;i++)
        {
            fscanf(fin," %d",&x);
            if(x==1)
            {
                    insert(categories[i], userid, tags);
                    insert(categories[i], userid, tags);
            }
        }
    }
    fclose(fin);
    return 0;
}
