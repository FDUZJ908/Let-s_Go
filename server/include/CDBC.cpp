#include "CDBC.h"

string CDBC::insertJSON(const JSON &json,const string &table,bool isUpdate)
{
    string sql="INSERT INTO "+table+"(",values="",update=" ON DUPLICATE KEY UPDATE ";
    int i=0; string col;
    for(JSON::CMIt it=json.MemberBegin();it!=json.MemberEnd();it++)
    {
        if((++i)>1) sql+=",",values+=",",update+=",";
        col=GETKey(it);
        sql+=col;
        values+="?";
        update+=col+"=VALUES("+col+")";
    }
    sql+=") VALUES("+values+")";
    if(isUpdate) sql+=update;
    try
    {
        PreparedStatement *query=conn->prepareStatement(sql);
        i=0;
        for(JSON::CMIt it=json.MemberBegin();it!=json.MemberEnd();it++)
        {
            i++;
            if(ISString(it)) query->setString(i,GETString(it));
            else if(ISInt(it)) query->setInt(i,GETInt(it));
            else if(ISULong(it)) query->setUInt64(i,GETULong(it));
            else if(ISDouble(it)) query->setDouble(i,GETDouble(it));
            else if(ISBool(it)) query->setBoolean(i,GETBool(it));
            else if(ISObject(it) || ISArray(it)) query->setString(i,JSON(it->value).toString());
            else if(ISNull(it)) query->setNull(i,DataType::UNKNOWN);
        }
        query->executeUpdate();
        delete query;
    }catch(exception &e)
    {
        return e.what();
    }
    return OK;
}

vector<Pair> CDBC::getColumns(const ResultSet *res)
{
    ResultSetMetaData *res_meta=res->getMetaData();
    int colsNum=res_meta->getColumnCount();
    vector<Pair> cols;
    for(int i=1;i<=colsNum;i++)
        cols.push_back(Pair(res_meta->getColumnLabel(i),res_meta->getColumnTypeName(i)));
     //delete res_meta; //No need to free res_meta. It will be reaped automatically.
    return cols;
}

RecordList CDBC::getResultList(ResultSet *res)
{
    vector<Pair> cols=getColumns(res);
    int colsNum=cols.size();
    DefRecordList(recordList);
    while(res->next())
    {
        DefRecord(record); Value value;
        for(int i=0;i<colsNum;i++)
        {
            const string &key=cols[i].x;
            const string &type=cols[i].y;
            if(type=="CHAR" || type=="DATETIME" || type=="TEXT" || type=="JSON")
            {
                string str=res->getString(key);
                value=Str2Value(str);
            }
            else 
            if(type=="INT" || type=="TINYINT") 
                value=Value(res->getInt(key));
            else
            if(type=="BIGINT UNSIGNED")
                value=Value(res->getUInt64(key));//
            else 
            if(type=="FLOAT" || type=="DOUBLE")
                value=Value(double(res->getDouble(key)));
            record.AddMember(Str2Value(key),value,Allocator);
        }
        recordList.PushBack(record,Allocator);
    }
    return recordList;
}

RecordList CDBC::selectQuery(const string &attrs,const string &tables,const string &conditions,
                             const Value &argv,const string &options)
{
    string sql="SELECT "+attrs+" FROM "+tables+" WHERE "+conditions+" "+options;
    PreparedStatement *query=conn->prepareStatement(sql);
    int argc=argv.Size();
    for(int i=0;i<argc;i++)
    {
        if(argv[i].IsString()) query->setString(i+1,argv[i].GetString());
        else if(argv[i].IsInt()) query->setInt(i+1,argv[i].GetInt());
        else if(argv[i].IsDouble()) query->setDouble(i+1, argv[i].GetDouble());
        else if(argv[i].IsBool()) query->setBoolean(i+1,argv[i].GetBool());
    }
    ResultSet *res=query->executeQuery();
    RecordList recordList=getResultList(res);
    delete res;
    delete query;
    return recordList;
}

int CDBC::getLastId()
{
    string sql="SELECT LAST_INSERT_ID() AS id";
    Statement *query=conn->createStatement();
    ResultSet *res=query->executeQuery(sql);
    RecordList recordList=getResultList(res);
    delete res;
    delete query;
    if(recordList.Size()>0) return recordList[0]["id"].GetInt();
    else return 0;
}

Record CDBC::queryByID(const string &id,const string &table,const string &attr)
{
    Value argv(kArrayType);
    argv.PushBack(Str2Value(id),Allocator);
    RecordList recordList=selectQuery("*",table,attr+"=?",argv,"LIMIT 1");
    return FirstOf(recordList);
}

Record CDBC::queryByIDs(const vector<string> &v,const string &table,const string &attr)
{
    int n=v.size();
    char conds[4*n]; int m=0;
    conds[m++]='(';
    for(int i=0;i<n;i++)
    {
        if(i>0) conds[m++]=',';
        conds[m++]='?';
    }
    conds[m++]=')';
    conds[m]='\0';

    string conditions=attr+" in"+string(conds);

    Value argv(kArrayType);
    for(int i=0;i<n;i++) argv.PushBack(Str2Value(v[i]),Allocator);
    return selectQuery("*",table,conditions,argv,"ORDER BY "+attr);
}

Record CDBC::querySystemVariable(const string &name)
{
    Value argv(kArrayType);
    argv.PushBack(Str2Value(name),Allocator);
    RecordList recordList=selectQuery("*","sysvar","name=?",argv);
    return FirstOf(recordList);
}

bool CDBC::authenticate(const string &userid,const string &password)
{
    RecordList res=selectQuery("*","user","userid='"+userid+"' AND password='"+password+"'");
    return (res.Size()==1);
}

RecordList CDBC::queryPostHistoryAtPOI(const string &POI_id, int postid)
{
    if(postid==0) postid=INF;
    string attrs="";
    attrs+="post.postid AS postid, ";
    attrs+="user.nickname AS nickname, ";
    attrs+="post.timestamp AS timestamp, ";
    attrs+="post.text AS text, ";
    attrs+="post.imageUrl AS imageUrl, ";
    attrs+="post.like as like, ";
    attrs+="post.dislike as dislike";
    string conditions="POI_id=? AND postid<? AND post.userid=user.userid";

    Value argv(kArrayType);
    argv.PushBack(Str2Value(POI_id),Allocator);
    argv.PushBack(Int2Value(postid),Allocator);
    return selectQuery(attrs,"post,user",conditions,argv,"ORDER BY postid DESC LIMIT 10");
}

RecordList CDBC::queryPOINearby(const double &lat, const double &lng, const double distLimit)
{
    double dlat=distTolat(distLimit,lat),dlng=distTolng(distLimit,lat);
    string conditions=TOString(lat-dlat)+"<latitude and latitude<"+TOString(lat+dlat)+" and ";
    conditions+=TOString(lng-dlng)+"<longitude and longitude<"+TOString(lng+dlng);
    return selectQuery("*","POI",conditions);
}

RecordList CDBC::queryHistoryPOI(const string &userid,const int &timestamp)
{
    string attrs="POI_id, MAX(timestamp) AS time,";
    string conditions="userid=? AND timestamp>?";
    Value argv(kArrayType);
    argv.PushBack(Str2Value(userid),Allocator);
    argv.PushBack(Int2Value(timestamp-YEAR_SECONDS),Allocator);
    return selectQuery(attrs,"post",conditions,argv,"GROUP BY POI_id ORDER BY time DESC LIMIT 50");
    //*****
}

RecordList CDBC::queryPostByTime(int timestamp)
{
    return selectQuery("POI_id,tags","post","timestamp>"+TOString(timestamp));
}

string CDBC::updatePOIPopularity(const string &POI_id,int popularity)
{
    string sql="UPDATE POI SET popularity=? WHERE POI_id=?";
    try
    {
        PreparedStatement *query=conn->prepareStatement(sql);
        query->setInt(1,popularity);
        query->setString(2,POI_id);
        delete query;
    }catch(exception &e)
    {
        return e.what();
    }
    return OK;
}
