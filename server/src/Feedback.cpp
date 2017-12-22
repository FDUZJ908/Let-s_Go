#include <stdcpp.h>
#include <server.h>

int main()
{
    JSON jsonRes(0);
    sendResponse(jsonRes.toString());
    return 0;
}
