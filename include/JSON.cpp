#include "JSON.h"

static Document document;
Document::AllocatorType& Allocator = document.GetAllocator();

int JSON::size()
{
    return dict.Size();
}

void JSON::insert(const string &key,const string &value)
{
    dict.AddMember(Str2Value(key),Str2Value(value),alloc);
}

void JSON::insert(const string &key,const int value)
{
    dict.AddMember(Str2Value(key),Value(value).Move(),alloc);
}

void JSON::insert(const string &key,const double value)
{
    dict.AddMember(Str2Value(key),Value(value).Move(),alloc);
}

void JSON::insert(const string &key,const bool value)
{
    dict.AddMember(Str2Value(key),Value(value).Move(),alloc);
}

void JSON::insert(const string &key,const Value &value)
{
    dict.AddMember(Str2Value(key),COPYValue(value),alloc); //Note: COPYValue!!!
}

string JSON::toString() const
{
    StringBuffer sb;
    PrettyWriter<StringBuffer> writer(sb);
    dict.Accept(writer);
    return sb.GetString();
}

Value& JSON::operator[](const string &key)
{
    return dict[key.c_str()];
}
