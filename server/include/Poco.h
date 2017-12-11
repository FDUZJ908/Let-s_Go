#ifndef POCO_H
#define POCO_H

//network
#include <Poco/Net/HTTPClientSession.h>
#include <Poco/Net/HTTPSClientSession.h>
#include <Poco/Net/HTTPRequest.h>
#include <Poco/Net/HTTPResponse.h>
#include <Poco/StreamCopier.h>
#include <Poco/Net/NetException.h>
#include <Poco/Net/HTMLForm.h>
#include <Poco/URI.h>

/*
//json
#include <Poco/Dynamic/Var.h>
#include <Poco/SharedPtr.h>
#include <Poco/Json/Parser.h>
#include <Poco/JSON/Object.h>
#include <Poco/JSON/Array.h>
using namespace Poco::JSON;

Parser parser;
Object::Ptr json=parser.parse(json_str).extract<Object::Ptr>();
string code=json->getValue<string>("code");
string url=wxLogin+"appid="+APPID+"&secret="+APPSECRET+"&js_code="+code+"&grant_type=authorization_code";
string code=json->getValue<String>('code')
json_str=HTTPSRequestGet(url);
*/

using namespace Poco;
using namespace Poco::Net;

#endif
