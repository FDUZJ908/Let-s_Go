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

    string insertJSON(const JSON &json,const string &table,bool isUpdate=false);
    vector<Pair> getColumns(const ResultSet *res);
    RecordList getResultList(ResultSet *res);
    RecordList selectQuery(const string &attrs,const string &tables,const string &conditions="TRUE",
                           const Value &argv=Value(kArrayType),const string &options=""); //argv must be kArrayType
    int getLastId();
    Record queryByID(const string &id,const string &table,const string &attr);
    bool authenticate(const string &userid,const string &password);

    ~CDBC()
    {
        //remember to free ResultSet
        //remember to free Statement and PreparedStatement
        delete conn; //remmeber to free Connection
    }
};

extern CDBC cdbc;
