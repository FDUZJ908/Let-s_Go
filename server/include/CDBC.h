#pragma once

#include "stdcpp.h"
#include "Util.h"
#include "JSON.h"
#include <mysql_connection.h>
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>
#include <cppconn/prepared_statement.h>
#include "server.h"
using namespace sql;

typedef Value Record;
#define DefRecord(record) Record record(kObjectType)

typedef Value RecordList;
#define DefRecordList(recordList) RecordList recordList(kArrayType)

#define FirstOf(recordList) (recordList.Size()>0)?Value(recordList[0],Allocator):Record(kNullType)

class CDBC
{
    const string host="tcp://localhost:3306/LetsGo";
    const string user="root";
    string password;
    //const string password="1234";

    Driver *driver;
    Connection *conn;

public:
    CDBC()
    {
        string password=getenv("DatabasePassword");
        driver = get_driver_instance();
        conn = driver->connect(CDBC::host,CDBC::user,password);
    }

    int getColsUpdateString(const Record &record, char *cols, char *update);
    string insertRecordlist(RecordList &recordList,const string &table,bool isUpdate);
    string insertRecord(Record &record,const string &table,bool isUpdate);
    string insertJSON(JSON &json,const string &table,bool isUpdate=false);
    string updateJSON(const JSON &json, const string &table,const string &id,const string &idAttr);
    vector<Pair> getColumns(const ResultSet *res);
    RecordList getResultList(ResultSet *res);
    RecordList selectQuery(const string &attrs,const string &tables,const string &conditions="TRUE",
                           const Value &argv=Value(kArrayType),const string &options=""); //argv must be kArrayType
    int getLastId();
    Record queryByID(const string &id,const string &table,const string &attr);
    RecordList queryByIDs(const vector<string> &v,const string &table,const string &attr);
    Record querySystemVariable(const string &name);
    bool authenticate(const string &userid,const string &password);
    RecordList queryPostHistoryAtPOI(const string &id, int postid=0);
    RecordList queryPOINearby(const double &lat, const double &lng, const double distLimit);
    RecordList queryHistoryPOI(const string &userid,const int &timestamp);
    RecordList queryPostByTime(int timestamp);
    string updatePOIPopularity(const string &POI_id,int popularity);
    string updatePostLike(const vector<int> &ids,const string &attr,int x);
    RecordList queryAttitude(const string &userid, const vector<int> &postids);

    ~CDBC()
    {
        //remember to free ResultSet
        //remember to free Statement and PreparedStatement
        delete conn; //remmeber to free Connection
    }
};

extern CDBC cdbc;
