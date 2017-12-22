#include <stdcpp.h>
#include <server.h>

int main()
{
    DefRecordList(v);
    DefRecord(json);
    json.AddMember("a",Value(1),Allocator);
    cout<<2<<endl;
    v.PushBack(COPYValue(json),Allocator);
    cout<<json.GetType()<<endl;
    json["a"].SetInt(2);
    cout<<0<<endl;
    v.PushBack(json,Allocator);
    
    for(int i=0;i<v.Size();i++)
        cout<<JSON(v[i]).toString()<<endl;
    return 0;
}