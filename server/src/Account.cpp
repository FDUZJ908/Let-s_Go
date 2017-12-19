#include <stdcpp.h>
#include <server.h>

int main()
{
    INIT("Account.log");

    Record record=cdbc.queryByID(userid,"user","userid");
    if(record.IsNull())
        writeError("Request data error!");
    JSON jsonRes(record);
    jsonRes.insert("status",OK);
    sendResponse(jsonRes.toString());
}
