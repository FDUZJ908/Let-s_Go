#pragma once

#include "stdcpp.h"
#include "Util.h"
#include <rapidjson/document.h>
#include <rapidjson/prettywriter.h>
#include <rapidjson/error/error.h>
#include <rapidjson/error/en.h>
using namespace rapidjson;

extern Document::AllocatorType& Allocator;

#define GETKey(it) string((it)->name.GetString())

#define ISString(it) ((it)->value.IsString())
#define ISInt(it) ((it)->value.IsInt())
#define ISUInt(it) ((it)->value.IsUint())
#define ISLong(it) ((it)->value.IsInt64())
#define ISULong(it) ((it)->value.IsUint64())
#define ISDouble(it) ((it)->value.IsDouble())
#define ISBool(it) ((it)->value.IsBool())
#define ISObject(it) ((it)->value.IsObject())
#define ISArray(it) ((it)->value.IsArray())
#define ISNull(it) ((it)->value.IsNull())

#define GETString(it) string((it)->value.GetString())
#define GETInt(it) ((it)->value.GetInt())
#define GETUInt(it) ((it)->value.GeUint())
#define GETLong(it) ((it)->value.Getint64())
#define GETULong(it) ((it)->value.GetUint64())
#define GETDouble(it) ((it)->value.GetDouble())
#define GETBool(it) ((it)->value.GetBool())
//#define GETValue(it) (Value().CopyFrom((it)->value,Allocator))

#define COPYValue(v) (Value(v,Allocator)) //copyConstStrings???
#define Str2Value(str) (Value().SetString((str).c_str(),(str).size(),Allocator))
#define CharStr2Value(str) (Value().SetString(str,Allocator))
#define Int2Value(x) Value(x)

const string OK="OK"; //status=0
const string ERROR="ERROR"; //status=1

const Value NullValue=Value(kNullType);

class JSON
{
    Document dict;
    Document::AllocatorType& alloc=dict.GetAllocator();

public:
    typedef Value::ConstMemberIterator CMIt;
    typedef Value::ConstValueIterator CVIt;

    JSON()
    {
        dict.SetObject();
    }

    JSON(const int status)
    {
        dict.SetObject();
        insert("status",status?ERROR:OK);
    }

    JSON(const string &json_str)
    {
        ParseResult result=dict.Parse(json_str.c_str());
        if(!result) dict.SetObject();
    }

    JSON(const Value &value)
    {
        dict.CopyFrom(value,alloc);
    }

    inline void clear()
    {
        dict.SetObject();
    }
    
    inline void insertError(const string &mesg)
    {
        insert("status",ERROR);
        insert("message",mesg);
    }

    inline Value toValue()
    {
        return COPYValue(Value(dict.GetObject()));
    }

    inline CMIt FindMember(const string &key) const
    {
        return dict.FindMember(key.c_str());
    }

    inline bool RemoveMember(const char* key)
    {
        return dict.RemoveMember(key);
    }

    inline CMIt MemberBegin() const
    {
        return dict.MemberBegin();
    }

    inline CMIt MemberEnd() const
    {
        return dict.MemberEnd();
    }

    int size();
    void insert(const string &key,const string &value);
    void insert(const string &key,const int value);
    void insert(const string &key,const double value);
    void insert(const string &key,const bool value);
    void insert(const string &key,const Value &value);
    string toString() const;
    Value& operator[](const string &key);
};

