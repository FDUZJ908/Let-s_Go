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
            else if(ISDouble(it)) query->setDouble(i,GETDouble(it));
            else if(ISBool(it)) query->setBoolean(i,GETBool(it));
            else if(ISObject(it) || ISArray(it)) query->setString(i,JSON(GETValue(it)).toString());
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
            if(type.find("INT")!=-1) 
                value=Value(res->getInt(key));
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
