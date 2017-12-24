#include <stdcpp.h>
#include <server.h>

int main()
{
    HTTPContent http(10*1024*1024,ContentType_JPEG);
    http.read(cin);
    writeError(http.saveAsFile("test.jpeg"));
    return 0;
}