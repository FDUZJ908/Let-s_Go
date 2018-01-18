#include <stdcpp.h>
#include <server.h>
#include <sys/stat.h>
#include <unistd.h>

string saveFile(const string &userid,const string imageName,const string &image_str)
{
    string filepath(getenv("LetsGoFilePATH"));
    filepath+="/"+userid;
    if(access(filepath.c_str(),0)==-1 && mkdir(filepath.c_str(),0755)==-1)
        return "Fail to create the user's directory!";

    filepath+="/"+imageName;
    FILE *fout=fopen(filepath.c_str(),"w");
    char buf[MAXBUF];
    int len=base64Decoder(image_str,buf);
    fwrite(buf,len,1,fout);
    fclose(fout);

    return OK;
}

int main()
{
    INIT("Post.log");

    JSON::CMIt POI_it=jsonReq.FindMember("POI_id");
    JSON::CMIt lat_it=jsonReq.FindMember("latitude");
    JSON::CMIt lng_it=jsonReq.FindMember("longitude");
    JSON::CMIt text_it=jsonReq.FindMember("text");
    JSON::CMIt tags_it=jsonReq.FindMember("tags");
    if(POI_it==jsonReq.MemberEnd() || lat_it==jsonReq.MemberEnd() || lng_it==jsonReq.MemberEnd() || tags_it==jsonReq.MemberEnd())
        writeError("Request data error!");

    JSON::CMIt image_it=jsonReq.FindMember("image");
    string image_str="",imageName;
    if(image_it!=jsonReq.MemberEnd())
    {
        JSON::CMIt format_it=jsonReq.FindMember("format");
        if(format_it!=jsonReq.MemberEnd()) imageName="."+GETString(format_it);
        else imageName=".jpeg";
        image_str=GETString(image_it);
        jsonReq.RemoveMember("image");
    }
    jsonReq.RemoveMember("token");

    int timestamp=getTimestamp();
    jsonReq.insert("timestamp",timestamp);
    string ret=cdbc.insertJSON(jsonReq,"post",false);
    if(ret!=OK) writeError(ret);

    if(image_str.size()>0)
    {
        imageName=TOString(cdbc.getLastId())+imageName;
        ret=saveFile(userid,imageName,image_str);
        if(ret!=OK) writeError(ret);
    }

    JSON jsonRes(0);
    jsonRes.insert("postid",cdbc.getLastId());
    jsonRes.insert("timestamp",timestamp);
    sendResponse(jsonRes.toString());
    return 0;
}
