#include <stdcpp.h>
#include <server.h>

int main()
{
    INIT("UpdatePOI.log");

    int timestamp=getTimestamp();
    RecordList recordList=queryPost(timestamp-DAY_SECONDS);
    map<string,int> count; count.clear();
    int n=recordList.Size();
    for(int i=0;i<n;i++)
    {

    }

    
    return 0;
}
