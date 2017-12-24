#include <stdcpp.h>
#include <server.h>

int main(int argc,char *argv[])
{
    string jsonReq_str=getRequestData();
    JSON jsonReq(jsonReq_str);

    int cnt=0;
    string message="";
    try
    {
        int n=jsonReq["POI_num"].GetInt();
        string category=jsonReq["category"].GetString();
        Value &POIs=jsonReq["POIs"];
        for(int i=0;i<n;i++)
        {
            string ret=OK;
            Value &p=POIs[i];
            string POI_id=p["POI_id"].GetString();
            Record res=cdbc.queryByID(POI_id,"POI","POI_id");
            if(res.IsNull())
            {
                p.AddMember("category",Str2Value(category),Allocator);
                ret=cdbc.insertRecord(p,"POI",false);
            }else
            {
                string categories=res["category"].GetString();
                if(categories.find(category)==-1)
                {
                    categories+=","+category;
                    p.AddMember("category",Str2Value(categories),Allocator);
                    ret=cdbc.insertRecord(p,"POI",true);
                }else ret="Inserted";
            }
            if(ret==OK) cnt++; else message=ret;
        }
    }catch (Exception e)
    {
        writeError(e.what());
    }

    JSON jsonRes(0);
    jsonRes.insert("cnt",cnt);
    jsonRes.insert("message",message);
    sendResponse(jsonRes.toString());
    return 0;
}
