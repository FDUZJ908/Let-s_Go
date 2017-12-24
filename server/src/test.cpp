#include <stdcpp.h>
#include <server.h>

int main()
{
    DefRecordList(v);
    DefRecord(json);
    json.AddMember("a",Value(1),Allocator);
    
    v.PushBack(json,Allocator);
    cout<<json.GetType()<<endl;
    json["a"].SetInt(2);
    
    v.PushBack(json,Allocator);
    
    for(int i=0;i<v.Size();i++)
        cout<<JSON(v[i]).toString()<<endl;
    return 0;
}