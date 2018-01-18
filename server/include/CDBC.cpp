    #include "CDBC.h"

string getRepeatQMark(int n,int m)
{
    char s[n*2*m+3*n+n];
    int p=0;
    for(int i=1;i<=n;i++)
    {
        if(i>1) s[p++]=',';
        s[p++]='(';
        for(int j=1;j<=m;j++)
        {
            if(j>1) s[p++]=',';
            s[p++]='?';
        }
        s[p++]=')';
    }
    s[p]='\0';
    return s;
}

int CDBC::getColsUpdateString(const Record &record, char *cols, char *update)
{
    JSON json(record);
    int p=0,q=0,l=0,m=0; const char *col;
    cols[p++]='(';
    for(JSON::CMIt it=record.MemberBegin();it!=record.MemberEnd();it++)
    {
        if((++m)>1) cols[p++]=',',update[q++]=',';
        col=GETKey(it); l=strlen(col);

        strcpy(cols+p,col); p+=l;

        strcpy(update+q,col); q+=l;
        strcpy(update+q,"=VALUES("); q+=8;
        strcpy(update+q,col); q+=l;
        update[q++]=')';
    }
    cols[p++]=')'; cols[p]='\0';
    update[q]='\0';
    return m;
}

#define BUFSIZE 15*20

string CDBC::insertRecordlist(RecordList &recordList,const string &table,bool isUpdate)//the recordList shouldn't be used later
{
    int n=recordList.Size();
    if(n==0) return OK;

    char cols[BUFSIZE],update[BUFSIZE];
    int col_num=getColsUpdateString(recordList[0],cols,update);

    string sql="INSERT INTO "+table+" "+string(cols)+" VALUES "+getRepeatQMark(1,col_num);
    if(isUpdate) sql+=" ON DUPLICATE KEY UPDATE "+string(update);
    try
    {
        PreparedStatement *query=conn->prepareStatement(sql);
        for(int k=0,i=0;k<n;k++)
        {
            Record &record=recordList[k];
            for(JSON::CMIt it=record.MemberBegin();it!=record.MemberEnd();it++)
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
        }
        query->executeUpdate();
        delete query;
    }catch(exception &e)
    {
        return e.what();
    }
    return OK;
}

string CDBC::insertRecord(Record &record,const string &table,bool isUpdate)//the record shouldn't be used later
{
    DefRecordList(recordList);
    recordList.PushBack(record,Allocator);
    return insertRecordlist(recordList,table,isUpdate);
}

string CDBC::insertJSON(JSON &json,const string &table,bool isUpdate)//the json shouldn't be used later
{
    DefRecordList(recordList);
    recordList.PushBack(json.toValue(),Allocator);
    return insertRecordlist(recordList,table,isUpdate);
}

string CDBC::updateJSON(const JSON &json, const string &table,const string &id,const string &idAttr)
{
    char update[BUFSIZE+40]; const char *col;
    int i=0,p=0,l=0;
    for(JSON::CMIt it=json.MemberBegin();it!=json.MemberEnd();it++)
    {
        if((++i)>1) update[p++]=',';
        col=GETKey(it); l=strlen(col);
        strcpy(update+p,col); p+=l;
        update[p++]='=';
        update[p++]='?';
    }
    update[p++]='\0';

    string sql="UPDATE "+table+" SET "+string(update)+" WHERE "+idAttr+"= ?";
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
        query->setString(++i,id);
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
                value=Value(res->getUInt64(key));
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
    //cout<<sql<<endl;

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

RecordList CDBC::queryByIDs(const vector<string> &v,const string &table,const string &attr)
{
    int n=v.size();
    if(n==0) return RecordList(kArrayType);

    string conditions=attr+" IN "+getRepeatQMark(1,n);

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
    attrs+="post.userid AS userid, ";
    attrs+="user.nickname AS nickname, ";
    attrs+="post.timestamp AS timestamp, ";
    attrs+="post.text AS text, ";
    attrs+="post.format AS format, ";
    attrs+="post.love as love, ";
    attrs+="post.dislike as dislike";
    string conditions="POI_id=? AND postid<? AND post.userid=user.userid";

    Value argv(kArrayType);
    argv.PushBack(Str2Value(POI_id),Allocator);
    argv.PushBack(Int2Value(postid),Allocator);
    return selectQuery(attrs,"post,user",conditions,argv,"ORDER BY postid DESC LIMIT 20");
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
    string attrs="POI_id, MAX(timestamp) AS time";
    string conditions="userid=? AND timestamp>?";
    Value argv(kArrayType);
    argv.PushBack(Str2Value(userid),Allocator);
    argv.PushBack(Int2Value(timestamp-YEAR_SECONDS),Allocator);
    return selectQuery(attrs,"post",conditions,argv,"GROUP BY POI_id ORDER BY time DESC LIMIT 50");
}

RecordList CDBC::queryPostByTime(int timestamp)
{
    return selectQuery("POI_id,tags","post","timestamp>"+TOString(timestamp));
}

string CDBC::updatePOIPopularity(const string &POI_id,int popularity)
{
    string sql="UPDATE POI SET popularity=popularity+? WHERE POI_id=?";
    try
    {
        PreparedStatement *query=conn->prepareStatement(sql);
        query->setInt(1,popularity);
        query->setString(2,POI_id);
        query->executeUpdate();
        delete query;
    }catch(exception &e)
    {
        return e.what();
    }
    return OK;
}

string CDBC::updatePostLike(const vector<int> &ids,const string &attr,int x)
{
    int n=ids.size();
    if(n==0) return OK;

    string conditions="postid IN"+getRepeatQMark(1,n);
    string sql="UPDATE post SET "+attr+"="+attr+"+("+TOString(x)+") WHERE "+conditions;

    try
    {
        PreparedStatement *query=conn->prepareStatement(sql);
        for(int i=0;i<n;i++) query->setInt(i+1,ids[i]);
        query->executeUpdate();
        delete query;
    }catch(exception &e)
    {
        return e.what();
    }
    return OK;
}

RecordList CDBC::queryAttitude(const string &userid, const vector<int> &postids)
{
    int n=postids.size();
    if(n==0) return RecordList(kArrayType);

    string conditions="userid=? AND postid IN "+getRepeatQMark(1,n);

    Value argv(kArrayType);
    argv.PushBack(Str2Value(userid),Allocator);
    for(int i=0;i<n;i++) argv.PushBack(Int2Value(postids[i]),Allocator);
    return selectQuery("*","feedback",conditions,argv,"ORDER BY postid DESC");
}
